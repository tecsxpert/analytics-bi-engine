package com.internship.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.repository.AnalyticsRecordRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AnalyticsRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnalyticsRecordRepository repository;

    @BeforeEach
    void setup() {

        repository.deleteAll();

        AnalyticsRecord record = new AnalyticsRecord();

        record.setTitle("Test Task");
        record.setDescription("Test Description");
        record.setStatus("active");
        record.setScore(90);

        repository.save(record);
    }

    // ✅ TEST GET ALL
    @Test
    void testGetAll() throws Exception {

        mockMvc.perform(get("/api/analytics/all"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    // ✅ TEST CREATE
    @Test
    void testCreate() throws Exception {

        AnalyticsRecord record = new AnalyticsRecord();

        record.setTitle("New Task");
        record.setDescription("Created from test");
        record.setStatus("active");
        record.setScore(95);

        mockMvc.perform(post("/api/analytics/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    // ✅ TEST UPDATE
   @Test
void testUpdate() throws Exception {

    AnalyticsRecord existing = repository.findAll().get(0);

    AnalyticsRecord updated = new AnalyticsRecord();

    updated.setId(existing.getId());

    updated.setTitle("Updated Task");
    updated.setDescription("Updated Description");
    updated.setStatus("active");
    updated.setScore(99);

    mockMvc.perform(put("/api/analytics/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updated)))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Task"));
}

    // ✅ TEST STATS
    @Test
    void testStats() throws Exception {

        mockMvc.perform(get("/api/analytics/stats"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists());
    }
}