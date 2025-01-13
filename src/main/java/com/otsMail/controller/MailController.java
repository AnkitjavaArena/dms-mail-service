package com.otsMail.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.otsMail.dao.EnrollRepository;
import com.otsMail.model.Recipient;
import com.otsMail.model.RecipientDetail;
import com.otsMail.service.MailService;
import com.otsMail.util.AppConstants;
import com.otsMail.util.EmailHelper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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

}
