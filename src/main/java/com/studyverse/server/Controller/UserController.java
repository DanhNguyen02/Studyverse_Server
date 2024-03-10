package com.studyverse.server.Controller;

import com.studyverse.server.DAO.UserDAO;
import com.studyverse.server.Model.User;
import com.studyverse.server.Service.EmailSenderService;
import com.studyverse.server.Service.OTPNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EmailSenderService senderService;
    private OTPNumberService otpNumberService = new OTPNumberService();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @PostMapping("/login")
    public Map<String, Object> logIn(@RequestBody HashMap<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String email = body.get("email");
        String password = body.get("password");

        User user = userDAO.handleLogIn(email, password);
        if (user == null) response.put("msg", "0");
        else {
            response.put("msg", "1");
            response.put("data", user);
        }

        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logOut(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");

        response.put("msg", userDAO.handleLogOut(email) ? "1" : "0");

        return response;
    }

    @PostMapping("/signup")
    public Map<String, String> signUp(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();
        response.put("msg", userDAO.handleSignUp(body) ? "1" : "0");
        return response;
    }

    @PostMapping("/confirmEmail")
    public Map<String, String> confirmEmail(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();
        response.put("msg", userDAO.checkUserExists(body.get("email")) ? "1" : "0");
        return response;
    }

    @PostMapping("/sendOTP")
    public Map<String, String> sendOTP(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();
        String toEmail = body.get("email");

        int otpNumber = otpNumberService.getOTPNumber();

        userDAO.updateOTPNumber(toEmail, otpNumber);

        senderService.sendEmail(toEmail,
                "Xác thực OTP",
                "Mã OTP của bạn là: " + otpNumber);

        response.put("msg", "1");
        return response;
    }

    @PostMapping("/confirmOTP")
    public Map<String, String> confirmOTP(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();
        String email = body.get("email");
        String otp = body.get("otp");

        String checkOTP = String.valueOf(userDAO.getOTPNumber(email));
        response.put("msg", otp.equals(checkOTP) ? "1" : "0");

        return response;
    }

    @PostMapping("/newPassword")
    public Map<String, String> newPassword(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");
        String newPassword = body.get("newPassword");
        userDAO.updateNewPassword(email, newPassword);

        response.put("msg", "1");
        return response;
    }

    @PostMapping("/updateInfo")
    public Map<String, String> updateInfo(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", userDAO.updateUserInfo(body) ? "1" : "0");
        return response;
    }
}
