package com.oceanview.resort.messagingTest;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.messaging.KafkaEmailConsumer;
import com.oceanview.resort.messaging.KafkaEmailListener;
import com.oceanview.resort.messaging.KafkaEventProducer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import javax.servlet.ServletContextEvent;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 * Tests for KafkaEmailListener.
 */
public class KafkaEmailListenerTest {

    private KafkaEmailListener listener;
    private ServletContextEvent mockEvent;
    private Properties testProperties;

    @Before
    public void setUp() throws Exception {
        listener = new KafkaEmailListener();
        mockEvent = mock(ServletContextEvent.class);

        // Set up test properties for AppConfig
        Field propsField = AppConfig.class.getDeclaredField("PROPERTIES");
        propsField.setAccessible(true);
        testProperties = (Properties) propsField.get(null);
    }

    @After
    public void tearDown() throws Exception {
        // Clean up any threads
        Field consumerField = KafkaEmailListener.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        KafkaEmailConsumer consumer = (KafkaEmailConsumer) consumerField.get(listener);
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    @Test
    public void contextInitialized_kafkaDisabled_doesNotStartConsumer() throws Exception {
        testProperties.setProperty("kafka.enabled", "false");
        testProperties.setProperty("email.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");

        listener.contextInitialized(mockEvent);

        Field consumerField = KafkaEmailListener.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        KafkaEmailConsumer consumer = (KafkaEmailConsumer) consumerField.get(listener);

        Assert.assertNull(consumer);
    }

    @Test
    public void contextInitialized_emailDisabled_doesNotStartConsumer() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("email.enabled", "false");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");

        listener.contextInitialized(mockEvent);

        Field consumerField = KafkaEmailListener.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        KafkaEmailConsumer consumer = (KafkaEmailConsumer) consumerField.get(listener);

        Assert.assertNull(consumer);
    }

    @Test
    public void contextInitialized_noBootstrapServers_doesNotStartConsumer() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("email.enabled", "true");
        testProperties.remove("kafka.bootstrap.servers");

        listener.contextInitialized(mockEvent);

        Field consumerField = KafkaEmailListener.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        KafkaEmailConsumer consumer = (KafkaEmailConsumer) consumerField.get(listener);

        Assert.assertNull(consumer);
    }

    @Test
    public void contextInitialized_blankBootstrapServers_doesNotStartConsumer() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("email.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "   ");

        listener.contextInitialized(mockEvent);

        Field consumerField = KafkaEmailListener.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        KafkaEmailConsumer consumer = (KafkaEmailConsumer) consumerField.get(listener);

        Assert.assertNull(consumer);
    }

    @Test
    public void contextDestroyed_withConsumer_shutsDownConsumer() throws Exception {
        testProperties.setProperty("kafka.enabled", "true");
        testProperties.setProperty("email.enabled", "true");
        testProperties.setProperty("kafka.bootstrap.servers", "localhost:9092");

        listener.contextInitialized(mockEvent);

        Field consumerField = KafkaEmailListener.class.getDeclaredField("consumer");
        consumerField.setAccessible(true);
        KafkaEmailConsumer consumer = (KafkaEmailConsumer) consumerField.get(listener);

        Assert.assertNotNull(consumer);

        try (MockedStatic<KafkaEventProducer> mockedProducer = mockStatic(KafkaEventProducer.class);
             MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {

            listener.contextDestroyed(mockEvent);

            mockedProducer.verify(KafkaEventProducer::close, times(1));
            mockedDb.verify(DatabaseConnection::shutdown, times(1));
        }
    }

    @Test
    public void contextDestroyed_withoutConsumer_onlyClosesProducer() {
        try (MockedStatic<KafkaEventProducer> mockedProducer = mockStatic(KafkaEventProducer.class);
             MockedStatic<DatabaseConnection> mockedDb = mockStatic(DatabaseConnection.class)) {

            listener.contextDestroyed(mockEvent);

            mockedProducer.verify(KafkaEventProducer::close, times(1));
            mockedDb.verify(DatabaseConnection::shutdown, times(1));
        }
    }
}
