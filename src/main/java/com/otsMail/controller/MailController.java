package com.otsMail.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.otsMail.dao.EnrollRepository;
import com.otsMail.model.Recipient;
import com.otsMail.model.RecipientDetail;
import com.otsMail.service.MailService;
import com.otsMail.util.AppConstants;
import com.otsMail.util.EmailHelper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MailController {

	@Autowired
	private MailService mailService;
	private final @NonNull EnrollRepository enrollRepository;
	private final @NonNull EmailHelper emailHelper;

	@PostMapping(AppConstants.API + "/sendMail")
	public ResponseEntity<?> sendEmail(@RequestBody RecipientDetail recipientDetail) {
		emailHelper.registerRecipients(recipientDetail);

		for (Recipient recipient : emailHelper.filterUniqueReceipient(recipientDetail.getRecipients())) {
			mailService.sendEmailToRecipient(recipient);
		}
		return ResponseEntity.ok("email sending initiated!");
	}

	@PostMapping(AppConstants.API + "/enroll")
	public ResponseEntity<?> Enrollrecipient(@RequestBody RecipientDetail recipientDetail) {
		emailHelper.registerRecipients(recipientDetail);
		return ResponseEntity.ok("Recipients Registered");
	}

	@GetMapping(AppConstants.API + "/mailSubscribedRecipient")
	public ResponseEntity<?> mailtoAllsubscribedRecipients() {
		mailService.sendEmailsToAllActiveandSubscribedRecipients();
		return ResponseEntity.ok("Mail Sent to all active and subscribed Recipients.");

	}

	// status
	// active- means email working
	// inactive means emailId removed permanently
	// subscribe true- means send message
	// subscribe false- means stop sending note but can be modified later because
	// they may be hiring.

	@PostMapping(AppConstants.API + "/setInActive")
	public ResponseEntity<?> modifystatusToInactive(@RequestBody List<String> emailIds) {
		return ResponseEntity.ok(mailService.setStatusToInactive(emailIds));
	}

    /**
     * To Download email data as PDF
     * @return
     */
    @GetMapping(AppConstants.API + "/enroll/generatePdf")
    public ResponseEntity<byte[]> generatePdf() {
        try {
            byte[] pdfBytes = mailService.generateEnrollPdf();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=email-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * To downlad email as excel
     * @return
     */
    @GetMapping(AppConstants.API + "/generateExcel")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] excelData = mailService.generateEnrollExcel();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=email-report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * To bulk update data
     * @param file
     * @return
     */
    @PostMapping(AppConstants.API + "/bulkUpdate")
    public ResponseEntity<String> uploadEnrollExcel(@RequestParam("file") MultipartFile file) {
        try {
            mailService.bulkUpdateEnrollData(file);
            return ResponseEntity.ok("Bulk update successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing file: " + e.getMessage());
        }
    }


}
