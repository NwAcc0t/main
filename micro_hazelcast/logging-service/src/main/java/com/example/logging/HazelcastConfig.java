package com.example.logging;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    /**
     * Цей бін надає свою Hazelcast Config для автоконфігурації.
     * Ми спеціально не називаємо його "hazelcastInstance", 
     * щоб не перезаписувати бін із автоконфігурації Spring Boot.
     */
    @Bean
    public Config hazelcastXmlConfig() {
        return new ClasspathXmlConfig("hazelcast.xml");
    }
}
