package com.example.logging;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoggingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingApplication.class, args);
    }

    /**
     * Конфігурація Embedded Hazelcast:
     * - Ідентична у всіх екземплярів (cluster name, multicast join тощо)
     */
    @Bean
    public Config hazelcastConfig() {
        Config cfg = new Config();
        cfg.setClusterName("hz-micro-log-cluster");

        // Використовуємо multicast для автоматичного знаходження інших вузлів:
        JoinConfig join = cfg.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(true)
            .setMulticastGroup("224.2.2.4")
            .setMulticastPort(54328);
        join.getTcpIpConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);

        // (За потреби можна вказати network.port, але Hazelcast підбере випадково вільний)
        return cfg;
    }
}
