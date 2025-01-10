package com.otsMail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.otsMail.model.Receipient;
import com.otsMail.model.RecipientDetail;
import com.otsMail.service.MailService;

@RestController
public class MailController {

	@Autowired
	private MailService mailService;

	@PostMapping("/sendMail")
	public String sendOtpEmail(@RequestBody RecipientDetail recipientDetail) {
		for (Receipient recipient : recipientDetail.getRecipients()) {
			mailService.sendEmailToRecipient(recipient);
		}
		return "email sending initiated!";

	}
}
