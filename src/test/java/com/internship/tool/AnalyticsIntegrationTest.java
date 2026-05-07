package com.internship.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AnalyticsRecord;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class AnalyticsIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ CREATE TEST
    @Test
    void testCreateRecord() throws Exception {

        AnalyticsRecord record = new AnalyticsRecord();

        record.setTitle("Container Test");
        record.setDescription("Testing PostgreSQL container");
        record.setStatus("active");
        record.setScore(99);

        mockMvc.perform(post("/api/analytics/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Container Test"));
    }

    // ✅ GET TEST
    @Test
    void testGetAllRecords() throws Exception {

        mockMvc.perform(get("/api/analytics/all"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }
}