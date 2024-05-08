package com.studyverse.server.Controller;

import com.studyverse.server.DAO.UserDAO;
import com.studyverse.server.Model.User;
import com.studyverse.server.SafeConvert;
import com.studyverse.server.Service.EmailSenderService;
import com.studyverse.server.Service.OTPNumberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private EmailSenderService senderService;

    @MockBean
    private OTPNumberService otpNumberService;

    @Test
    public void testLogIn() throws Exception {
        String email = "huudanhnguyen02@gmail.com";
        String password = "Studyverse";
        User user = new User();

        when(userDAO.handleLogIn(email, password)).thenReturn(user);

        Map<String, Object> mockBody = new HashMap<>();
        mockBody.put("email", email);
        mockBody.put("password", password);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(mockBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));

        String wrongPassword = "wrong password";
        when(userDAO.handleLogIn(email, wrongPassword)).thenReturn(null);

        Map<String, Object> wrongMockBody = new HashMap<>();
        wrongMockBody.put("email", email);
        wrongMockBody.put("password", wrongPassword);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SafeConvert.convertMapToString(wrongMockBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("0"));
    }

    @Test
    public void testLogOut() throws Exception {
        String email = "huudanhnguyen02@gmail.com";

        when(userDAO.handleLogOut(email)).thenReturn(true);

        mockMvc.perform(post("/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"huudanhnguyen02@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testSendOTP() throws Exception {
        String email = "huudanhnguyen02@gmail.com";
        int otp = 1234;

        when(otpNumberService.getOTPNumber()).thenReturn(otp);
        doNothing().when(senderService).sendEmail(anyString(), anyString(), anyString());
        doNothing().when(userDAO).updateOTPNumber(email, otp);

        mockMvc.perform(post("/user/sendOTP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"huudanhnguyen@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testConfirmOTP() throws Exception {
        String email = "user@example.com";
        String otp = "1234";
        String storedOTP = "1234";

        when(userDAO.getOTPNumber(email)).thenReturn(Integer.valueOf(storedOTP));

        mockMvc.perform(post("/user/confirmOTP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"otp\":\"" + otp + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));

        String wrongOTP = "4321";

        mockMvc.perform(post("/user/confirmOTP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"otp\":\"" + wrongOTP + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("0"));
    }

    @Test
    public void testNewPassword() throws Exception {
        String email = "huudanhnguyen02@gmail.com";
        String newPassword = "newPassword";

        doNothing().when(userDAO).updateNewPassword(email, newPassword);

        mockMvc.perform(post("/user/newPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"huudanhnguyen02@gmail.com\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateInfo() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("email", "huudanhnguyen02@gmail.com");
        body.put("name", "Study Verse");

        when(userDAO.updateUserInfo(body)).thenReturn(true);

        mockMvc.perform(post("/user/updateInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"huudanhnguyen02@gmail.com\",\"name\":\"Study Verse\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        String email = "huudanhnguyen02@gmail.com";
        String status = "Studying";

        when(userDAO.updateStatus(email, status)).thenReturn(true);

        mockMvc.perform(post("/user/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"huudanhnguyen02@gmail.com\",\"status\":\"Studying\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("1"));
    }
}
