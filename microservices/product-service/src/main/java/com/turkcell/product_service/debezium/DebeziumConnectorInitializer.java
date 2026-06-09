package com.turkcell.product_service.debezium;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DebeziumConnectorInitializer {

    @Value("${debezium.connector.url:http://localhost:8083}")
    private String kafkaConnectUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void registerConnector() throws Exception {
        String connectorConfig = """
            {
              "name": "product-outbox-connector",
              "config": {
                "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
                "database.hostname": "product-db",
                "database.port": "5432",
                "database.user": "postgres",
                "database.password": "test12345",
                "database.dbname": "products",
                "topic.prefix": "product-service",
                "table.include.list": "public.outbox",
                "plugin.name": "pgoutput"
              }
            }
            """;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(kafkaConnectUrl + "/connectors"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(connectorConfig))
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("✅ Debezium connector kaydedildi!");
    }
}
