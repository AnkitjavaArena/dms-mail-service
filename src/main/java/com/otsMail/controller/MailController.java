package com.otsMail.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	public String sendEmail(@RequestBody RecipientDetail recipientDetail) {
		emailHelper.registerRecipients(recipientDetail);

		for (Recipient recipient : emailHelper.filterUniqueReceipient(recipientDetail.getRecipients())) {
			mailService.sendEmailToRecipient(recipient);
		}
		return "email sending initiated!";
	}

	@PostMapping(AppConstants.API + "/enroll")
	public String Enrollrecipient(@RequestBody RecipientDetail recipientDetail) {
		emailHelper.registerRecipients(recipientDetail);
		return "Recipients Registered";
	}

}
