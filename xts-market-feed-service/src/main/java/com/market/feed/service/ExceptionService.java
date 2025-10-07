/*
package com.market.feed.service;

import com.market.feed.service.emailService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sf.xts.api.sdk.ConfigurationProvider;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ExceptionService {


    private EmailService emailService;

    List<String> emails = Arrays.stream("bhargava.g@pagesolutions.co.uk,bhargav.m@pagesolutions.co.uk,kpdasari@gmail.com,kp@pagesolutions.co.uk,rajesh@quantlabalgo.com,bhargavmoparthi@gmail.com".split(",")).toList();

    public ExceptionService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void createLoginException( String message , boolean status){

        if (!status){
            ConfigurationProvider.loginCount ++;
            if (ConfigurationProvider.loginCount == 1){

                emails.stream().map(email ->{
                    LocalTime now = LocalTime.now();

                    // Format the time if needed
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                    String formattedTime = now.format(formatter);
                    System.out.println("email sent for the email: " + email);
                  String Message =  emailService.getLoginFailureEmailTemplate(email,"","XTS Login Faild",formattedTime,"1",message,"next step");
                    emailService.sendEmail(email,"Xts Login Faild ",Message);
                    return email;
                });
            }
        }else {
            for (String email : emails) {
                LocalTime now = LocalTime.now();

                // Format the time if needed
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                String formattedTime = now.format(formatter);
                System.out.println("email sent for the email: " + email);

             String Message=   emailService.getLoginSuccessfulTemplate("Xts Login Success ",formattedTime);
             emailService.sendEmail(email,"Xts Login Success ",Message);

            }
        }
    }
}
*/
