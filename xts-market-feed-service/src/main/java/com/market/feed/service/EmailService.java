package com.market.feed.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    JavaMailSender mailSender;

    @Value("classpath:templates/failed-attempt.html")
    private Resource template1;


    @Value("classpath:templates/login-successful.html")
    private Resource template2;

    public boolean sendEmail(String status,int attempt,String description) {
        try {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedDate = dateTime.format(dateFormatter);
            String formattedTime = dateTime.format(timeFormatter);
            String body = "";
            if(status.equalsIgnoreCase("success"))
                 body = getLoginSuccessfulTemplate(formattedDate, formattedTime);
            else if(status.equalsIgnoreCase("failure"))
                 body = getLoginFailureEmailTemplate(formattedDate, formattedTime,attempt,description);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("mohammedimran0266@gmail.com");
            List<String> recipients = List.of("pangavijayKumar9966@gmail.com", "rsriwastava@gmail.com","kpdasari@gmail.com","mahiragent47@gmail.com");
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject("Login Notification");
            helper.setText(body, true);
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: " + "pangavijayKumar9966@gmail.com");
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send email to: " + "pangavijayKumar9966@gmail.com", e);
            return false;
        }
    }


    public String getLoginFailureEmailTemplate(String date, String time, int attempt,String reason) {
        try (InputStream inputStream = template1.getInputStream()) {
            String message = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            message = message.replace("{{date}}", date);
            message = message.replace("{{time}}", time);
            message = message.replace("{{attempt}}", String.valueOf(attempt));
            message = message.replace("{{reason}}", reason );
            return message;
        } catch (Exception e) {
            System.out.println("Error generating email content.");
            e.printStackTrace();
            return null;
        }
    }
    public String getLoginSuccessfulTemplate(String date, String time) {
        try (InputStream inputStream = template2.getInputStream()) {
            String message = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            message = message.replace("{{date}}", date);
           message = message.replace("{{time}}", time);
            return message;
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

}



