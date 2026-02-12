package com.oceanview.resort.messagingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.messaging.KafkaEmailConsumer;
import com.oceanview.resort.messaging.ReservationEmailEvent;
import com.oceanview.resort.service.EmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for KafkaEmailConsumer.
 */
public class KafkaEmailConsumerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private KafkaConsumer<String, String> mockConsumer;

    private KafkaEmailConsumer consumer;
    private Properties testProperties;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Set up test properties for AppConfig
        Field propsField = AppConfig.class.getDeclaredField("PROPERTIES");
        propsField.setAccessible(true);
        testProperties = (Properties) propsField.get(null);
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");
        testProperties.setProperty("kafka.consumer.group", "test-group");
        testProperties.setProperty("kafka.topic.reservation.email", "test-topic");

        // Create consumer with mocked KafkaConsumer using reflection
        consumer = new KafkaEmailConsumer(emailService);

        // Replace the internal KafkaConsumer with our mock
        Field consumerField = KafkaEmailConsumer.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        consumerField.set(consumer, mockConsumer);
    }

    @Test
    public void run_processesMessages() throws Exception {
        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setReservationId(1L);
        event.setGuestEmail("test@example.com");

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(event);

        ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", payload);
        ConsumerRecords<String, String> records = new ConsumerRecords<>(Collections.singletonMap(
                new org.apache.kafka.common.TopicPartition("test-topic", 0),
                Collections.singletonList(record)
        ));

        when(mockConsumer.poll(any(Duration.class))).thenReturn(records).thenThrow(new WakeupException());

        // Use a separate thread to stop the consumer after processing
        Thread consumerThread = new Thread(() -> {
            try {
                consumer.run();
            } catch (Exception ignored) {
            }
        });
        consumerThread.start();

        // Wait a bit for processing
        Thread.sleep(100);

        // Shutdown to stop the loop
        consumer.shutdown();
        consumerThread.join(1000);

        verify(emailService, times(1)).sendReservationEmail(any(ReservationEmailEvent.class));
    }

    @Test
    public void run_handlesInvalidJson_continuesProcessing() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", "invalid-json");
        ConsumerRecords<String, String> records = new ConsumerRecords<>(Collections.singletonMap(
                new org.apache.kafka.common.TopicPartition("test-topic", 0),
                Collections.singletonList(record)
        ));

        when(mockConsumer.poll(any(Duration.class))).thenReturn(records).thenThrow(new WakeupException());

        Thread consumerThread = new Thread(() -> {
            try {
                consumer.run();
            } catch (Exception ignored) {
            }
        });
        consumerThread.start();

        Thread.sleep(100);
        consumer.shutdown();
        consumerThread.join(1000);

        // Should not call emailService for invalid JSON
        verify(emailService, never()).sendReservationEmail(any());
    }

    @Test
    public void run_handlesWakeupException_gracefully() throws Exception {
        when(mockConsumer.poll(any(Duration.class))).thenThrow(new WakeupException());

        Thread consumerThread = new Thread(() -> {
            try {
                consumer.run();
            } catch (Exception ignored) {
            }
        });
        consumerThread.start();

        Thread.sleep(100);
        consumer.shutdown();
        consumerThread.join(1000);

        verify(mockConsumer, atLeastOnce()).poll(any(Duration.class));
        verify(mockConsumer, times(1)).close();
    }

    @Test
    public void run_handlesGeneralException_gracefully() throws Exception {
        when(mockConsumer.poll(any(Duration.class))).thenThrow(new RuntimeException("Test exception"));

        Thread consumerThread = new Thread(() -> {
            try {
                consumer.run();
            } catch (Exception ignored) {
            }
        });
        consumerThread.start();

        Thread.sleep(100);
        consumer.shutdown();
        consumerThread.join(1000);

        verify(mockConsumer, atLeastOnce()).poll(any(Duration.class));
        verify(mockConsumer, times(1)).close();
    }

    @Test
    public void shutdown_setsRunningFalseAndWakesUpConsumer() {
        consumer.shutdown();

        verify(mockConsumer, times(1)).wakeup();
    }
}
