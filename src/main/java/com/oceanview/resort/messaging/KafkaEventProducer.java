package com.oceanview.resort.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanview.resort.config.AppConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public final class KafkaEventProducer {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static volatile KafkaProducer<String, String> producer;

    private KafkaEventProducer() {
    }

    public static void sendReservationEmailEvent(ReservationEmailEvent event) {
        if (!AppConfig.getBoolean("kafka.enabled", false)) {
            return;
        }
        if (event == null) {
            return;
        }
        KafkaProducer<String, String> kafkaProducer = getProducer();
        if (kafkaProducer == null) {
            return;
        }
        try {
            String topic = AppConfig.getProperty("kafka.topic.reservation.email", "reservation-email");
            String key = event.getReservationNo() == null || event.getReservationNo().isBlank()
                    ? String.valueOf(event.getReservationId())
                    : event.getReservationNo();
            String payload = MAPPER.writeValueAsString(event);
            kafkaProducer.send(new ProducerRecord<>(topic, key, payload), (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Failed to publish reservation email event: " + exception.getMessage());
                }
            });
        } catch (Exception ex) {
            System.err.println("Failed to serialize reservation email event: " + ex.getMessage());
        }
    }

    public static void close() {
        KafkaProducer<String, String> kafkaProducer = producer;
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
    }

    private static KafkaProducer<String, String> getProducer() {
        if (producer != null) {
            return producer;
        }
        synchronized (KafkaEventProducer.class) {
            if (producer == null) {
                String bootstrapServers = AppConfig.getProperty("kafka.bootstrap.servers");
                if (bootstrapServers == null || bootstrapServers.isBlank()) {
                    System.err.println("Kafka bootstrap servers not configured.");
                    return null;
                }
                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.ACKS_CONFIG, "all");
                props.put(ProducerConfig.LINGER_MS_CONFIG, "5");
                producer = new KafkaProducer<>(props);
            }
        }
        return producer;
    }
}
