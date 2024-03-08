package com.studyverse.server.Service;

import java.util.Random;

public class OTPNumberService {
    public int getOTPNumber() {
        Random random = new Random();

        int otpNumber = 1000 + random.nextInt(9000);

        System.out.println("Mã OTP là: " + otpNumber);

        return otpNumber;
    }
}
