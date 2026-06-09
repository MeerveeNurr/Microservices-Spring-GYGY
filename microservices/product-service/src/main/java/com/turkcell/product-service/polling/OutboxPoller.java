package com.turkcell.product_service.polling;
import java.util.List;
import com.turkcell.product_service.entity.OutboxEvent;
import com.turkcell.product_service.entity.OutboxStatus;
import com.turkcell.product_service.repository.OutboxRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPoller {
    private final OutboxRepository outboxRepository;
    private final StreamBridge streamBridge;

    public OutboxPoller(OutboxRepository outboxRepository, StreamBridge streamBridge) {
        this.outboxRepository = outboxRepository;
        this.streamBridge = streamBridge;
    }
     // ÖDEV: Burayı CDC ile (Debezium) değiştir
     // bende retrycount niye artmıyo???

     //userservis*prodcuct servis mesaj alışberişi??? userdan istek atınca product karşılığı vs vs hocanın yaptığı gibi

     @Scheduled(fixedDelay = 20000) // Her 20 saniyede bir çalışır
     @Transactional
     public void publishPendingEvents(){
        List<OutboxEvent> events = outboxRepository.findPublishable(10); // 10 kere denemiş ve hala gönderilememiş event'leri getir
        for (OutboxEvent event : events) {
            try {
                // Kafka'ya gönder
                streamBridge.send(event.getEventType() + "-out-0", event.getPayload());
                // Başarılı ise durumu güncelle
                event.setStatus(OutboxStatus.SENT);
            } catch (Exception e) {
                // Hata durumunda retry count'u artır
                if(event.getRetryCount() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                   
                } else {
                event.setRetryCount(event.getRetryCount() + 1);
            }
        }
            outboxRepository.save(event); // Durumu güncelle
        }

     }
}
