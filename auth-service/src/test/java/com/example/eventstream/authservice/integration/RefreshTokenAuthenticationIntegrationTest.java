package com.example.eventstream.authservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RefreshTokenAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void rotatesRevokesAndMeasuresRefreshTokenLifecycle() throws Exception {
        String originalToken = loginAndGetRefreshToken();
        String rotatedToken = refreshAndGetToken(originalToken);

        refresh(originalToken).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + rotatedToken + "\"}"))
                .andExpect(status().isOk());
        refresh(rotatedToken).andExpect(status().isUnauthorized());

        assertThat(meterRegistry.get("auth_refresh_success_total").counter().count()).isEqualTo(1);
        assertThat(meterRegistry.get("auth_refresh_failure_total").counter().count()).isEqualTo(2);
        assertThat(meterRegistry.get("auth_logout_total").counter().count()).isEqualTo(1);
    }

    @Test
    void protectsTheActiveSessionsEndpoint() throws Exception {
        mockMvc.perform(get("/auth/sessions"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetRefreshToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return body(result).get("refreshToken").asText();
    }

    private String refreshAndGetToken(String token) throws Exception {
        MvcResult result = refresh(token).andExpect(status().isOk()).andReturn();
        return body(result).get("refreshToken").asText();
    }

    private org.springframework.test.web.servlet.ResultActions refresh(String token) throws Exception {
        return mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + token + "\"}"));
    }

    private JsonNode body(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
