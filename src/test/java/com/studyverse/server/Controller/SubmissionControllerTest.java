package com.studyverse.server.Controller;

import com.studyverse.server.DAO.SubmissionDAO;
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
import java.util.List;

@WebMvcTest(SubmissionController.class)
public class SubmissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmissionDAO submissionDAO;

    @Test
    public void testScoringTest() throws Exception {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", 1);
        body.put("questions", List.of(
                new HashMap<String, Object>() {{
                    put("id", 3);
                    put("isPass", false);
                }}
        ));

        when(submissionDAO.scoringTest(eq(body))).thenReturn(true);

        String jsonBody = """
        {
            "id": 1,
            "questions": [
                {
                    "id": 3,
                    "isPass": false
                }
            ]
        }
        """;

        mockMvc.perform(post("/submission/scoring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}
