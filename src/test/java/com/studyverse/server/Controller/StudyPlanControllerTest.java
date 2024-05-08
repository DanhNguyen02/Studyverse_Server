package com.studyverse.server.Controller;

import com.studyverse.server.DAO.StudyPlanDAO;
import com.studyverse.server.Model.StudyPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(StudyPlanController.class)
public class StudyPlanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudyPlanDAO studyPlanDAO;

    @Test
    public void testGetAllStudyPlans() throws Exception {
        Integer familyId = 1;
        Map<Integer, Map<Integer, List<StudyPlan>>> studyPlans = new HashMap<>();
        studyPlans.put(1, new HashMap<>() {{
            put(1, Arrays.asList(new StudyPlan(), new StudyPlan()));
        }});

        when(studyPlanDAO.getAllStudyPlans(familyId)).thenReturn(studyPlans);

        mockMvc.perform(get("/studyPlan/{familyId}", familyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1.1.length()").value(2));
    }

    @Test
    public void testCreateStudyPlan() throws Exception {
        HashMap<String, Object> body = new HashMap<>();

        body.put("name", "New Study Plan");
        body.put("startDate", "11/05/2024");
        body.put("endDate", "11/08/2024");
        body.put("subjectId", 1);
        body.put("childrenIds", Arrays.asList(4, 5));

        Map<String, Object> milestone1 = new HashMap<>();
        milestone1.put("name", "Milestone 1");
        milestone1.put("content", "Milestone 1");
        milestone1.put("startDate", "15/05/2024");
        milestone1.put("endDate", "25/05/2024");
        milestone1.put("testId", null);

        Map<String, Object> milestone2 = new HashMap<>();
        milestone2.put("name", "Milestone 2");
        milestone2.put("content", "Milestone 2");
        milestone2.put("startDate", "26/05/2024");
        milestone2.put("endDate", "03/06/2024");
        milestone2.put("testId", null);

        Map<String, Object> milestone3 = new HashMap<>();
        milestone3.put("name", "Milestone 3");
        milestone3.put("content", "Milestone 3");
        milestone3.put("startDate", "04/06/2024");
        milestone3.put("endDate", "14/06/2024");
        milestone3.put("testId", 1);

        body.put("milestones", Arrays.asList(milestone1, milestone2, milestone3));

        when(studyPlanDAO.createStudyPlan(eq(body))).thenReturn(true);

        String jsonBody = """
        {
            "name": "New Study Plan",
            "startDate": "11/05/2024",
            "endDate": "11/08/2024",
            "subjectId": 1,
            "childrenIds": [4,5],
            "milestones": [
                {
                    "name": "Milestone 1",
                    "content": "Milestone 1",
                    "startDate": "15/05/2024",
                    "endDate": "25/05/2024",
                    "testId": null
                },
                {
                    "name": "Milestone 2",
                    "content": "Milestone 2",
                    "startDate": "26/05/2024",
                    "endDate": "03/06/2024",
                    "testId": null
                },
                {
                    "name": "Milestone 3",
                    "content": "Milestone 3",
                    "startDate": "04/06/2024",
                    "endDate": "14/06/2024",
                    "testId": 1
                }
            ]
        }
        """;

        mockMvc.perform(post("/studyPlan/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateStudyPlan() throws Exception {
        Integer id = 1;
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "Updated Study Plan");
        body.put("startDate", "01/06/2024");
        body.put("endDate", "01/09/2024");
        body.put("subjectId", 5);
        body.put("childrenIds", Arrays.asList(4, 5));

        when(studyPlanDAO.updateStudyPlan(eq(id), eq(body))).thenReturn(true);

        String jsonBody = """
        {
            "name": "Updated Study Plan",
            "startDate": "01/06/2024",
            "endDate": "01/09/2024",
            "subjectId": 5,
            "childrenIds": [4, 5]
        }
        """;

        mockMvc.perform(put("/studyPlan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testDeleteStudyPlan() throws Exception {
        Integer id = 1;

        when(studyPlanDAO.deleteStudyPlan(id)).thenReturn(true);

        mockMvc.perform(delete("/studyPlan/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}

