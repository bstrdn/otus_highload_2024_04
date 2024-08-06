package ru.bstrdn.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.bstrdn.data.model.PostNotification;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String server;

  @Bean
  public ProducerFactory<String, PostNotification> producerFactoryPostNotification() {
    return new DefaultKafkaProducerFactory<>(deviceProducerConfigs());
  }

  @Bean
  public KafkaTemplate<String, PostNotification> kafkaTemplatePostNotification() {
    KafkaTemplate<String, PostNotification> template = new KafkaTemplate<>(
        producerFactoryPostNotification());
    template.setMessageConverter(new StringJsonMessageConverter());
    return template;
  }

  @Bean
  public Map<String, Object> deviceProducerConfigs() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
    configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return configs;
  }

  @Bean
  public ProducerFactory<String, String> stringProducerConfig() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
    configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(configs);
  }

  @Bean
  public KafkaTemplate<String, String> stringKafkaTemplate() {
    return new KafkaTemplate<>(stringProducerConfig());
  }
}