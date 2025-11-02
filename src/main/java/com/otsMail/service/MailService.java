package com.otsMail.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.otsMail.config.EmailConfig;
import com.otsMail.dao.EnrollRepository;
import com.otsMail.model.Enroll;
import com.otsMail.model.Recipient;
import com.otsMail.util.EmailHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Log4j2
@RequiredArgsConstructor
public class MailService {

    private final @NonNull EmailConfig mailConfig;
    private final @NonNull EmailHelper emailHelper;
    private final @NonNull EmailHistoryService emailHistoryService;
    private final @NonNull EnrollRepository enrollRepository;
    @Autowired
    private TemplateEngine templateEngine;

    private JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getHost());
        mailSender.setPort(Integer.parseInt(mailConfig.getPort()));
        mailSender.setUsername(mailConfig.getUsername());
        mailSender.setPassword(mailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // Set to true if you want email logs

        return mailSender;
    }

    @Async
    public void sendEmailToRecipient(Recipient recipient) {
        this.sendEmail(recipient);
    }

    public void sendEmail(Recipient recipient) {
        try {
            JavaMailSender mailSender = getMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailConfig.getFrom());
            helper.setTo(recipient.getEmail());
            helper.setSubject(mailConfig.getSubject());

            boolean useHtml = Boolean.TRUE.equals(mailConfig.getUseHtmlTemplate());

            String emailContent = useHtml
                    ? generateHtmlContent(recipient)
                    : emailHelper.getEmailContent(emailHelper.getSalutationValue(recipient));

            //emailContent = emailHelper.getEmailContent(emailHelper.getSalutationValue(recipient));
            helper.setText(emailContent, true);
            ClassPathResource attachmenLocation = new ClassPathResource("Ankit_Resume_Java.pdf");
            if (attachmenLocation.exists()) {
                helper.addAttachment(mailConfig.getAttachmentname(), attachmenLocation);
            } else {
                throw new IOException("Referred file not found in classpath.");
            }
            LocalDateTime timestamp = LocalDateTime.now();
            mailSender.send(message);
            emailHelper.updateCounterForRecipient(recipient);
            emailHistoryService.createEmailHistory(recipient, timestamp);
            log.info("mail sent: {}", recipient.getEmail());
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            log.info("Fail to send mail to :{}", recipient.getEmail());
        }

    }

    public void sendEmailsToAllActiveandSubscribedRecipients() {
        List<Recipient> recipients = emailHelper.getActiveEnrollments()
                .stream()
                .map(enroll -> Recipient.builder()
                        .email(enroll.getTo())
                        .salutation(enroll.getSalutation())
                        .build())
                .collect(Collectors.toList());
        for (Recipient recipient : recipients) {
            sendEmailToRecipient(recipient);
        }

    }

    public List<Enroll> setStatusToInactive(List<String> emailIds) {
        List<Enroll> enrollList = emailHelper.getEnrollments(emailIds);
        enrollList.stream().forEach(enroll -> enroll.setStatus("inactive"));
        return enrollRepository.saveAll(enrollList);

    }

    private String generateHtmlContent(Recipient recipient) {
        Context context = new Context();
        context.setVariable("recipientName", recipient.getSalutation());
        context.setVariable("email", recipient.getEmail());
        context.setVariable("date", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        context.setVariable("role", "Java Developer");
        context.setVariable("experience", "3+ years");
        context.setVariable("githubLink", "https://github.com/yourusername/yourproject");
        context.setVariable("portfolioLink", "https://ankitjavaarena.github.io/Ankit-portfolio/");


        // template name corresponds to /templates/email-template.html
        return templateEngine.process("email-template", context);
    }

}
