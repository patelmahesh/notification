package com.ae.dubaipolice.configuration;

import com.ae.dubaipolice.util.ParticipantRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootApplication
@ComponentScan({"com.ae.dubaipolice"})
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

    @Bean
    @Description("Keeps connected users")
    public ParticipantRepository participantRepository() {
        return new ParticipantRepository();
    }

  
}
