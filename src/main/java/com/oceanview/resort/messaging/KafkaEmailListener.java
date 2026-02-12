package com.oceanview.resort.messaging;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.service.EmailService;
import com.oceanview.resort.config.DatabaseConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Servlet context listener that starts the Kafka email consumer on app startup
 * and shuts it down (and closes the producer) on app destroy.
 * Lives in messaging so all Kafka/email wiring is in one place.
 */
public class KafkaEmailListener implements ServletContextListener {
    private Thread consumerThread;
    private KafkaEmailConsumer consumer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (!AppConfig.getBoolean("kafka.enabled", false) || !AppConfig.getBoolean("email.enabled", false)) {
            return;
        }
        String bootstrap = AppConfig.getProperty("kafka.bootstrap.servers");
        if (bootstrap == null || bootstrap.isBlank()) {
            System.err.println("Kafka bootstrap servers not configured, email consumer not started.");
            return;
        }
        consumer = new KafkaEmailConsumer(new EmailService());
        consumerThread = new Thread(consumer, "kafka-email-consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (consumer != null) {
            consumer.shutdown();
        }
        if (consumerThread != null) {
            try {
                consumerThread.join(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        KafkaEventProducer.close();
        DatabaseConnection.shutdown();
    }
}
