package com.turkcell.product_service.controller;
import java.time.Instant;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkcell.product_service.entity.OutboxEvent;
import com.turkcell.product_service.entity.OutboxStatus;
import com.turkcell.product_service.event.TestEvent;
import com.turkcell.product_service.repository.OutboxRepository;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
  /*   @GetMapping("/product/hello")

    public String hello(){
        return "Hello Product Service...";
    } */
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public ProductController(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }
   
    @GetMapping
    public String test(@RequestParam String message){
        // asla!
        UUID id= UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        var event = new TestEvent(eventId, message, UUID.randomUUID());
        //streamBridge.send("test-event-out-0", event); // test-event-out-0: şu anlama gelir: application.yml dosyasında tanımladığımız output binding'in adıdır. Yani bu event'i hangi kanala göndermek istediğimizi belirtiriz.

        // Kafkaya bir mesaj gidecekse önce kayıt altına alınacak.
        //Outbox,XEvent,XTarihi,XTopic,XPayload

        // Daha sonra bir mekanizma bu kayıtları okuyacak ve kafkaya gönderecek.
        // Polling-> Belirli aralıklarla veritabanına bak, gönderilecek bir event var mı ? 
        // Her 20 swn de bir Select * from outbox where status='Pending'  and retryCount < 3

        //CDC(Change Data Capture) -> Veritabanındaki değişiklikleri dinler ve bu değişiklikleri Kafka'ya gönderir.

        // Debezium gibi bir mekanizma 
        // Debezium -> Veritabanındaki değişiklikleri dinler ve bu değişiklikleri Kafka'ya gönderir. Outbox tablosuna yeni bir kayıt eklendiğinde, Debezium bu kaydı okuyacak ve Kafka'ya gönderecektir.

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(eventId);
        outboxEvent.setAggregateType("Product");
        outboxEvent.setAggregateId(id.toString()); // Aggregate -> İlgili nesne
        outboxEvent.setEventType("TestEvent");
        outboxEvent.setPayload(toJson(event)); // toJson -> event'i JSON formatına çevirir. Bu, Kafka'ya gönderirken kullanışlı olacaktır.
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setCreatedAt(Instant.now());
        outboxRepository.save(outboxEvent); // outboxEvent'i veritabanına kaydederiz. Bu sayede, kafkaya gönderilmek üzere sıraya alınmış oluruz.
        return "Başarılı";

    }
    // toJson -> event'i JSON formatına çevirir. Bu, Kafka'ya gönderirken kullanışlı olacaktır.
    //Aşağıda yapılan toJson metodu, herhangi bir nesneyi JSON formatına çevirebilir. ObjectMapper sınıfı, Jackson kütüphanesinin bir parçasıdır ve Java nesnelerini JSON formatına çevirmek için kullanılır.
     private String toJson(Object o)
    {
        try { return objectMapper.writeValueAsString(o);}
        catch(Exception e) { throw new RuntimeException(e); }
    }
}
