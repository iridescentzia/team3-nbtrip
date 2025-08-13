package org.scoula.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private int port;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;

    // Gmail SMTP 설정으로 JavaMailSender Bean 생성
    @Bean
    public JavaMailSender getJavaMailSender() {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalStateException(
                    "Gmail SMTP 설정이 필요합니다!\n" +
                            "application.properties에서 다음을 설정해주세요:\n" +
                            "mail.smtp.username=your-email@gmail.com\n" +
                            "mail.smtp.password=your-16-digit-app-password"
            );
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");

        // Gmail SMTP 속성 설정
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", 2000);
        props.put("mail.smtp.timeout", 3000);
        props.put("mail.smtp.writetimeout", 2000);

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }
}
