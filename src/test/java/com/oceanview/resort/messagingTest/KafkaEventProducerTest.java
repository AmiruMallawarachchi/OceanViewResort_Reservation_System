package com.oceanview.resort.messagingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.messaging.KafkaEventProducer;
import com.oceanview.resort.messaging.ReservationEmailEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for KafkaEventProducer.
 */
@SuppressWarnings("unchecked")
public class KafkaEventProducerTest {

    private Properties testProperties;
    private Field producerField;

    @Before
    public void setUp() throws Exception {
        // Reset static producer field
        producerField = KafkaEventProducer.class.getDeclaredField("producer");
        producerField.setAccessible(true);
        producerField.set(null, null);

        // Set up test properties for AppConfig
        Field propsField = AppConfig.class.getDeclaredField("PROPERTIES");
        propsField.setAccessible(true);
        testProperties = (Properties) propsField.get(null);
    }

    @After
    public void tearDown() throws Exception {
        // Clean up producer
        KafkaEventProducer.close();
        producerField.set(null, null);
    }

    @Test
    public void sendReservationEmailEvent_kafkaDisabled_doesNothing() throws Exception {
        testProperties.setProperty("kafka.enabled", "false");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");

        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setReservationId(1L);

        KafkaEventProducer.sendReservationEmailEvent(event);

        // Verify no producer was created
        Assert.assertNull(producerField.get(null));
    }

    @Test
    public void sendReservationEmailEvent_nullEvent_doesNothing() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");

        KafkaEventProducer.sendReservationEmailEvent(null);

        // Verify no producer was created
        Assert.assertNull(producerField.get(null));
    }

    @Test
    public void sendReservationEmailEvent_noBootstrapServers_doesNothing() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.remove("kafka.bootstrap.servers");

        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setReservationId(1L);

        KafkaEventProducer.sendReservationEmailEvent(event);

        // Verify no producer was created
        Assert.assertNull(producerField.get(null));
    }

    @Test
    public void sendReservationEmailEvent_validEvent_sendsToKafka() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");
        testProperties.setProperty("kafka.topic.reservation.email", "test-topic");

        KafkaProducer<String, String> mockProducer = mock(KafkaProducer.class);
        producerField.set(null, mockProducer);

        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setReservationId(123L);
        event.setReservationNo("RES-001");
        event.setGuestName("John Doe");
        event.setGuestEmail("john@example.com");

        KafkaEventProducer.sendReservationEmailEvent(event);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(mockProducer, times(1)).send(recordCaptor.capture(), any());

        ProducerRecord<String, String> sentRecord = recordCaptor.getValue();
        Assert.assertEquals("test-topic", sentRecord.topic());
        Assert.assertEquals("RES-001", sentRecord.key());

        // Verify payload contains event data
        String payload = sentRecord.value();
        ObjectMapper mapper = new ObjectMapper();
        ReservationEmailEvent deserialized = mapper.readValue(payload, ReservationEmailEvent.class);
        Assert.assertEquals(123L, deserialized.getReservationId());
        Assert.assertEquals("RES-001", deserialized.getReservationNo());
    }

    @Test
    public void sendReservationEmailEvent_nullReservationNo_usesReservationIdAsKey() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");
        testProperties.setProperty("kafka.topic.reservation.email", "test-topic");

        KafkaProducer<String, String> mockProducer = mock(KafkaProducer.class);
        producerField.set(null, mockProducer);

        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setReservationId(456L);
        event.setReservationNo(null);

        KafkaEventProducer.sendReservationEmailEvent(event);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(mockProducer, times(1)).send(recordCaptor.capture(), any());

        ProducerRecord<String, String> sentRecord = recordCaptor.getValue();
        Assert.assertEquals("456", sentRecord.key());
    }

    @Test
    public void sendReservationEmailEvent_blankReservationNo_usesReservationIdAsKey() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");
        testProperties.setProperty("kafka.topic.reservation.email", "test-topic");

        KafkaProducer<String, String> mockProducer = mock(KafkaProducer.class);
        producerField.set(null, mockProducer);

        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setReservationId(789L);
        event.setReservationNo("   ");

        KafkaEventProducer.sendReservationEmailEvent(event);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(mockProducer, times(1)).send(recordCaptor.capture(), any());

        ProducerRecord<String, String> sentRecord = recordCaptor.getValue();
        Assert.assertEquals("789", sentRecord.key());
    }

    @Test
    public void close_withProducer_closesProducer() throws Exception {
        KafkaProducer<String, String> mockProducer = mock(KafkaProducer.class);
        producerField.set(null, mockProducer);

        KafkaEventProducer.close();

        verify(mockProducer, times(1)).close();
        // Note: close() doesn't set the field to null, it just closes the producer
        // The field remains set until next initialization or explicit null assignment
    }

    @Test
    public void close_withoutProducer_doesNothing() throws Exception {
        producerField.set(null, null);

        KafkaEventProducer.close();

        // Should not throw exception
        Assert.assertNull(producerField.get(null));
    }
}
