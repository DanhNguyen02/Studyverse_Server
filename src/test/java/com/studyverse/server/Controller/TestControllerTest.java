package com.studyverse.server.Controller;

import com.studyverse.server.DAO.TestDAO;
import com.studyverse.server.Model.Test;
import com.studyverse.server.SafeConvert;
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

@WebMvcTest(TestController.class)
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestDAO testDAO;

    @org.junit.jupiter.api.Test
    public void testGetFamilyTests() throws Exception {
        Integer familyId = 1;
        Map<Integer, List<Test>> tests = new HashMap<>();
        tests.put(1, Arrays.asList(new Test(), new Test()));

        when(testDAO.getAllTests(familyId)).thenReturn(tests);

        mockMvc.perform(get("/test/{familyId}", familyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1.length()").value(tests.get(1).size()));
    }

    @org.junit.jupiter.api.Test
    public void testCreateTest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Final Test");
        body.put("description", "Description");
        body.put("time", 90);
        body.put("questionCountToPass", 1);
        body.put("parentId", 1);
        body.put("childrenIds", "[4,5]");
        body.put("tags", Arrays.asList(1, 13));
        body.put("startDate", "2024-04-07T11:07:06.971Z");
        body.put("endDate", "2024-04-24T17:00:00.000Z");

        Map<String, Object> question1 = new HashMap<>();
        question1.put("name", "Question 1");
        question1.put("suggest", "");
        question1.put("image", null);
        question1.put("answerId", 1);
        question1.put("type", 1);
        question1.put("tags", Arrays.asList(1, 13));

        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("content", "Choice 1");
        choice1.put("image", null);

        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("content", "Choice 2");
        choice2.put("image", null);

        Map<String, Object> choice3 = new HashMap<>();
        choice3.put("content", "Choice 3");
        choice3.put("image", null);

        Map<String, Object> choice4 = new HashMap<>();
        choice4.put("content", "Choice 4");
        choice4.put("image", null);

        question1.put("choices", Arrays.asList(choice1, choice2, choice3, choice4));

        Map<String, Object> question2 = new HashMap<>();
        question2.put("name", "Question 2");
        question2.put("suggest", "");
        question2.put("image", null);
        question2.put("answerId", 2);
        question2.put("type", 1);
        question2.put("tags", Arrays.asList(1, 13));
        question2.put("choices", Arrays.asList(choice1, choice2, choice3, choice4));

        Map<String, Object> question3 = new HashMap<>();
        question3.put("name", "Question 3");
        question3.put("suggest", "");
        question3.put("image", null);
        question3.put("answerId", -1);
        question3.put("type", 2);
        question3.put("tags", Arrays.asList(1, 13));
        question3.put("choices", null);

        body.put("questions", Arrays.asList(question1, question2, question3));

        when(testDAO.createTest(eq(body))).thenReturn(true);

        mockMvc.perform(post("/test/createTest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @org.junit.jupiter.api.Test
    public void testSubmitTest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("startDate", "2024-04-07T11:07:06.971Z");
        body.put("endDate", "2024-04-24T17:00:00.000Z");
        body.put("testId", 1);
        body.put("childrenId", 4);
        body.put("time", 3600);

        Map<String, Object> question1 = new HashMap<>();
        question1.put("id", 1);
        question1.put("choiceId", 1);
        question1.put("answer", null);

        Map<String, Object> question2 = new HashMap<>();
        question2.put("id", 2);
        question2.put("choiceId", -1);
        question2.put("answer", null);

        Map<String, Object> question3 = new HashMap<>();
        question3.put("id", 3);
        question3.put("choiceId", null);
        question3.put("answer", "Answer");

        body.put("questions", Arrays.asList(question1, question2, question3));

        when(testDAO.submitTest(eq(body))).thenReturn(true);

        mockMvc.perform(post("/test/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @org.junit.jupiter.api.Test
    public void testUpdateTest() throws Exception {
        Integer id = 1;
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "Updated Test");
        body.put("description", "New description");
        body.put("time", 90);
        body.put("questionCountToPass", 3);
        body.put("childrenIds", Arrays.asList(4, 5));
        body.put("tags", Arrays.asList(1, 13, 14));
        body.put("startDate", "2024-04-02T12:34:56");
        body.put("endDate", "2024-04-09T12:34:56");

        when(testDAO.updateTest(id, body)).thenReturn(true);

        mockMvc.perform(put("/test/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @org.junit.jupiter.api.Test
    public void testDeleteTest() throws Exception {
        Integer id = 1;

        when(testDAO.deleteTest(id)).thenReturn(true);

        mockMvc.perform(delete("/test/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}

