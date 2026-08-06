package com.example.eventstream.notification.replay;

import com.example.eventstream.common.command.SendNotificationCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EventTypeConfiguration {
    @Bean
    public EventTypeRegistry eventTypeRegistry(){
        EventTypeRegistry eventTypeRegistry = new EventTypeRegistry();
        eventTypeRegistry.register(
                SendNotificationCommand.class.getSimpleName(),
                SendNotificationCommand.class
        );
        return eventTypeRegistry;
    }
}
