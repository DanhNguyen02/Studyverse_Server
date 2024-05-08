package com.studyverse.server.Controller;

import com.studyverse.server.DAO.StatisticDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

@WebMvcTest(StatisticController.class)
public class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticDAO statisticDAO;

    @Test
    public void testGetStatistics() throws Exception {
        Integer id = 1;

        Map<String, Object> testStats = new HashMap<>();
        testStats.put("pass", 5);
        testStats.put("count", 10);

        Map<String, Object> answerStats = new HashMap<>();
        answerStats.put("correct", 50);
        answerStats.put("count", 100);

        Map<String, Object> subjectStats = new HashMap<>();
        subjectStats.put("1", new HashMap<String, Object>() {{
            put("correct", 30);
            put("count", 50);
        }});

        Map<String, Map<String, Object>> statistics = new HashMap<>();
        statistics.put("test", testStats);
        statistics.put("answer", answerStats);
        statistics.put("subject", subjectStats);

        when(statisticDAO.getStatistics(id)).thenReturn(statistics);

        mockMvc.perform(get("/stats/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.test.pass").value(5))
                .andExpect(jsonPath("$.test.count").value(10))
                .andExpect(jsonPath("$.answer.correct").value(50))
                .andExpect(jsonPath("$.answer.count").value(100))
                .andExpect(jsonPath("$.subject.1.correct").value(30))
                .andExpect(jsonPath("$.subject.1.count").value(50));
    }
}
