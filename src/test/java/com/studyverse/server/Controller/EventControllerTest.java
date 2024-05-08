package com.studyverse.server.Controller;

import com.studyverse.server.DAO.EventDAO;
import com.studyverse.server.Model.Event;
import com.studyverse.server.SafeConvert;
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

@WebMvcTest(EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventDAO eventDAO;

    @Test
    public void testGetEventsUserJoin() throws Exception {
        Integer userId = 1;
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(eventDAO.getEventsUserJoin(userId)).thenReturn(events);

        mockMvc.perform(get("/event/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(events.size()));
    }

    @Test
    public void testCreateEvent() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", "Test event");
        body.put("day", "03/06/2024");
        body.put("timeStart", "06:50");
        body.put("timeEnd", "10:37");
        body.put("loopMode", "2");
        body.put("endDate", "03/09/2024");
        body.put("isRemind", "true");
        body.put("remindTime", "15");
        body.put("note", "Test note");
        body.put("userId", "1");
        body.put("tagUsers", "[3,4]");

        when(eventDAO.createEvent(body)).thenReturn(true);

        mockMvc.perform(post("/event/createEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateEvents() throws Exception {
        Integer id = 1;
        HashMap<String, String> body = new HashMap<>();
        body.put("name", "Updated Event");
        body.put("date", "04/06/2024");
        body.put("timeStart", "06:50");
        body.put("timeEnd", "10:37");
        body.put("isRemind", "true");
        body.put("remindTime", "15");
        body.put("note", "Test update event note");
        body.put("isLoop", "false");
        body.put("userId", "1");
        body.put("tagUsers", "[3]");

        when(eventDAO.updateEvents(id, body)).thenReturn(true);

        mockMvc.perform(put("/event/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        Integer id = 1;

        when(eventDAO.deleteEvent(id, true)).thenReturn(true);

        mockMvc.perform(delete("/event/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deleteLoop\":\"true\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Integer id = 1;

        when(eventDAO.updateStatus(id)).thenReturn(true);

        mockMvc.perform(put("/event/{id}/updateStatus", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}

