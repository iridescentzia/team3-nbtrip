package org.scoula.mypage;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] rawPasswords = {
                "hashedpassword1@",
                "hashedpassword2@",
                "hashedpassword3@",
                "hashedpassword4@",
                "pass12345!",
                "pass56789!",
                "pass91013!",
                "pass141516!"
        };

        for (String rawPassword : rawPasswords) {
            String hashed = encoder.encode(rawPassword);
            System.out.println("원본: " + rawPassword);
            System.out.println("해시: " + hashed);
            System.out.println("----------------------------------");
        }
    }
}