package com.example.eventstream.order.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresContainerConfig.class)
class OrderApplicationIT {
    @Test
    void contextLoads() {
    }

}