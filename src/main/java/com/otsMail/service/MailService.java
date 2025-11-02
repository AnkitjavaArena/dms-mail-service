package com.otsMail.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.lowagie.text.DocumentException;
//import com.lowagie.text.Row;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

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
        context.setVariable("githubLink", "https://github.com/AnkitjavaArena/dms-mail-service");
        context.setVariable("portfolioLink", "https://ankitjavaarena.github.io/Ankit-portfolio/");


        // template name corresponds to /templates/email-template.html
        return templateEngine.process("email-template", context);
    }


    public byte[] generateEnrollPdf() throws DocumentException {
        List<Enroll> enrollList = enrollRepository.findAll();

        // Inject data into Thymeleaf context
        Context context = new Context();
        context.setVariable("enrollList", enrollList);
        context.setVariable("generatedOn", LocalDateTime.now());


        // Process the Thymeleaf template into HTML
        String htmlContent = templateEngine.process("email-pdf-template", context);

        // Convert HTML to PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }

    public byte[] generateEnrollExcel() throws Exception {
        List<Enroll> enrollList = enrollRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Email Report");

            // ===== Header Style =====
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // ===== Body Style (Centered) =====
            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // ===== Create Header Row =====
            String[] headers = {"ID", "Recipient", "Salutation", "Status", "Count", "Subscribed"};
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25); // makes header taller for aesthetics

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===== Body Rows =====
            int rowIdx = 1;
            for (Enroll e : enrollList) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);

                Cell c0 = row.createCell(0);
                c0.setCellValue(e.getId() != null ? e.getId() : 0);
                c0.setCellStyle(bodyStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(e.getTo() != null ? e.getTo() : "");
                c1.setCellStyle(bodyStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(e.getSalutation() != null ? e.getSalutation() : "");
                c2.setCellStyle(bodyStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(e.getStatus() != null ? e.getStatus() : "");
                c3.setCellStyle(bodyStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(e.getCount() != null ? e.getCount() : 0);
                c4.setCellStyle(bodyStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(Boolean.TRUE.equals(e.getSubscribe()) ? "Yes" : "No");
                c5.setCellStyle(bodyStyle);
            }

            // ===== Auto-size Columns with Padding =====
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, currentWidth + 1000); // add extra width for padding
            }

            // ===== Write to Byte Array =====
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public void bulkUpdateEnrollData(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // first sheet (Email Report)
            int rows = sheet.getPhysicalNumberOfRows();

            // Skip header row (start from i = 1)
            for (int i = 1; i < rows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Long id = (long) row.getCell(0).getNumericCellValue();

                // Fetch existing record from DB
                Enroll enroll = enrollRepository.findById(id).orElse(null);
                if (enroll == null) continue; // skip if not found

                // Read updated values from Excel
                String to = getStringCellValue(row.getCell(1));
                String salutation = getStringCellValue(row.getCell(2));
                String status = getStringCellValue(row.getCell(3));
                Integer count = (int) row.getCell(4).getNumericCellValue();
                String subscribed = getStringCellValue(row.getCell(5));

                // Update fields
                enroll.setTo(to);
                enroll.setSalutation(salutation);
                enroll.setStatus(status);
                enroll.setCount(count);
                enroll.setSubscribe("Yes".equalsIgnoreCase(subscribed));

                enrollRepository.save(enroll);
            }
        }
    }

    // Helper method for safe string extraction
    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }



}
