package com.studyverse.server.Controller;

import com.studyverse.server.DAO.FamilyDAO;
import com.studyverse.server.Model.Family;
import com.studyverse.server.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(FamilyController.class)
public class FamilyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FamilyDAO familyDAO;

    @Test
    public void testGetFamilyById_whenFamilyExists() throws Exception {
        Integer familyId = 1;
        Family family = new Family(); // Assume Family is a class with appropriate fields
        family.setId(familyId);
        family.setName("Happy Family");

        when(familyDAO.getFamilyById(familyId)).thenReturn(family);

        mockMvc.perform(get("/family/{id}", familyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"))
                .andExpect(jsonPath("$.data.name").value("Happy Family"));
    }

    @Test
    public void testGetFamilyById_whenFamilyDoesNotExist() throws Exception {
        Integer familyId = 5;
        when(familyDAO.getFamilyById(familyId)).thenReturn(null);

        mockMvc.perform(get("/family/{id}", familyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("0"));
    }

    @Test
    public void testCreateFamily() throws Exception {
        String email = "studyverse@gmail.com";
        int newFamilyId = 10;

        when(familyDAO.handleCreateFamily(email)).thenReturn(newFamilyId);

        mockMvc.perform(post("/family/createFamily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"))
                .andExpect(jsonPath("$.familyId").value("10"));
    }

    @Test
    public void testCheckExistFamily() throws Exception {
        String email = "abc@gmail.com";
        when(familyDAO.handleCheckExistFamily(email)).thenReturn(true);

        mockMvc.perform(get("/family/checkUserInFamily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testLinkFamily() throws Exception {
        String email = "bunny@gmail.com";
        String familyEmail = "happyfamily@gmail.com";
        when(familyDAO.handleLinkFamily(email, familyEmail)).thenReturn(true);

        mockMvc.perform(post("/family/linkFamily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"familyEmail\":\"" + familyEmail + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUnlinkFamily() throws Exception {
        String email = "bunny@gmail.com";
        when(familyDAO.handleUnlinkFamily(email)).thenReturn(true);

        mockMvc.perform(post("/family/unlinkFamily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testGetFamilyMembers() throws Exception {
        Integer familyId = 1;
        List<User> familyMembers = Collections.singletonList(new User()); // Assume User class is properly defined

        when(familyDAO.getFamilyMembers(familyId.toString())).thenReturn(familyMembers);

        mockMvc.perform(get("/family/getFamilyMembers/{familyId}", familyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetPendingMembers() throws Exception {
        String familyId = "1";
        String email = "father@gmail.com";
        List<User> pendingUsers = Collections.singletonList(new User()); // Assume User class is properly defined

        when(familyDAO.getPendingUsers(familyId, email)).thenReturn(pendingUsers);

        mockMvc.perform(post("/family/getPendingUsers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyId\":\"" + familyId + "\", \"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testApproveLinkFamily() throws Exception {
        String email = "bunny@gmail.com";
        String familyId = "1";
        String code = "0";
        when(familyDAO.handleApproveLinkFamily(email, familyId, code)).thenReturn(true);

        mockMvc.perform(post("/family/approveLinkFamily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"familyId\":\"" + familyId + "\", \"code\":\"" + code + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testKickMember() throws Exception {
        String userEmail = "mother@gmail.com";
        String memberEmail = "fakebunny@gmail.com";
        String familyId = "1";
        when(familyDAO.handleKickMember(userEmail, memberEmail, familyId)).thenReturn(true);

        mockMvc.perform(post("/family/kickMember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"" + userEmail + "\", \"memberEmail\":\"" + memberEmail + "\", \"familyId\":\"" + familyId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testOutFamily() throws Exception {
        String email = "jenny@example.com";
        when(familyDAO.handleOutFamily(email)).thenReturn(true);

        mockMvc.perform(post("/family/outFamily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateFamilyName() throws Exception {
        String id = "1";
        String name = "Superhero Family";
        when(familyDAO.updateFamilyName(id, name)).thenReturn(true);

        mockMvc.perform(post("/family/updateName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + id + "\", \"name\":\"" + name + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}
