package com.example.eventstream.notification.replay;


import java.util.HashMap;
import java.util.Map;

public class EventTypeRegistry {

    private final Map<String, Class<?>> registry = new HashMap<>();

    public void register(String eventType, Class<?> clazz) {
        registry.put(eventType, clazz);
    }

    public Class<?> resolve(String eventType) {
       Class<?> clazz = registry.get(eventType);
       if (clazz == null) {
           throw new IllegalArgumentException("Unsupported replay event: " + eventType);
       }
        return clazz;
    }
}
