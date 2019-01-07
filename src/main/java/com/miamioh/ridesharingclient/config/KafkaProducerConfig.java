package com.miamioh.ridesharingclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.miamioh.ridesharingclient.config.params.KafkaProducerConfigParams;
import com.miamioh.ridesharingclient.model.request.RideSharingRequest;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
	
	@Autowired
	private KafkaProducerConfigParams kafkaProducerConfigParams;
	
	@Bean
	@DependsOn("kafkaProducerConfigParams")
    public ProducerFactory<String, RideSharingRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfigParams.getProducerConfig());
    }
 
    @Bean
    public KafkaTemplate<String, RideSharingRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
