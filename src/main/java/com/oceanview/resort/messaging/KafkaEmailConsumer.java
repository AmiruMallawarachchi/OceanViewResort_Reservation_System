package com.oceanview.resort.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanview.resort.service.EmailService;
import com.oceanview.resort.config.AppConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaEmailConsumer implements Runnable {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final KafkaConsumer<String, String> consumer;
    private final EmailService emailService;
    private volatile boolean running = true;

    public KafkaEmailConsumer(EmailService emailService) {
        this.emailService = emailService;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.getProperty("kafka.bootstrap.servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfig.getProperty("kafka.consumer.group", "reservation-email-service"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        this.consumer = new KafkaConsumer<>(props);
        String topic = AppConfig.getProperty("kafka.topic.reservation.email", "reservation-email");
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                records.forEach(record -> handleMessage(record.value()));
            }
        } catch (WakeupException ex) {
            if (running) {
                System.err.println("Kafka consumer interrupted: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("Kafka consumer error: " + ex.getMessage());
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        running = false;
        consumer.wakeup();
    }

    private void handleMessage(String payload) {
        try {
            ReservationEmailEvent event = MAPPER.readValue(payload, ReservationEmailEvent.class);
            emailService.sendReservationEmail(event);
        } catch (Exception ex) {
            System.err.println("Failed to process reservation email event: " + ex.getMessage());
        }
    }
}
