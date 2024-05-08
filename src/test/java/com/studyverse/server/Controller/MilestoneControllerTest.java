package com.studyverse.server.Controller;

import com.studyverse.server.DAO.MilestoneDAO;
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

@WebMvcTest(MilestoneController.class)
public class MilestoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MilestoneDAO milestoneDAO;

    @Test
    public void testAddMilestone() throws Exception {
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "Milestone 4");
        body.put("content", "Content of Milestone 4");
        body.put("startDate", "21/05/2024");
        body.put("endDate", "31/07/2024");
        body.put("testId", null);

        when(milestoneDAO.addMilestone(eq(body))).thenReturn(true);

        String jsonBody = """
        {
            "name": "Milestone 4",
            "content": "Content of Milestone 4",
            "startDate": "21/05/2024",
            "endDate": "31/07/2024",
            "testId": null
        }
        """;

        mockMvc.perform(post("/milestone/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateMilestone() throws Exception {
        Integer id = 4;
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "Updated Milestone");
        body.put("content", "Updated content of Milestone");
        body.put("startDate", "18/06/2024");
        body.put("endDate", "02/07/2024");
        body.put("testId", null);

        when(milestoneDAO.updateMilestone(eq(id), eq(body))).thenReturn(true);

        String jsonBody = """
        {
            "name": "Updated Milestone",
            "content": "Updated content of Milestone",
            "startDate": "18/06/2024",
            "endDate": "02/07/2024",
            "testId": null
        }
        """;

        mockMvc.perform(put("/milestone/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testDeleteMilestone() throws Exception {
        Integer id = 1;

        when(milestoneDAO.deleteMilestone(id)).thenReturn(true);

        mockMvc.perform(delete("/milestone/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testCompleteMilestone() throws Exception {
        when(milestoneDAO.completeMilestone(eq(1), eq(4))).thenReturn(true);

        String jsonBody = """
        {
            "id": 1,
            "childrenId": 4
        }
        """;

        mockMvc.perform(post("/milestone/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}
