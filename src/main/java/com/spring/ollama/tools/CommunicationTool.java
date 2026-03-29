package com.spring.ollama.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommunicationTool {

    @Autowired
    private JavaMailSender mailSender;

    @Tool(description = "Send email to any user")
    public String sendEmail(String to, String subject, String body) {
        try {
            log.info("calling sendemail");
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Email sent successful!");
        } catch (Exception exception) {
            log.error(exception.getLocalizedMessage());
        }

        return "Email sent to " + to;
    }
}
