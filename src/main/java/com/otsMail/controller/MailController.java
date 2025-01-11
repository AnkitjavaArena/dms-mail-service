package com.otsMail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.otsMail.dao.EmailTrackRepository;
import com.otsMail.model.Enroll;
import com.otsMail.model.Receipient;
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
	private final @NonNull EmailTrackRepository emailTrackRepository;
	private final @NonNull EmailHelper emailHelper;

	@PostMapping(AppConstants.API + "/sendMail")
	public String sendEmail(@RequestBody RecipientDetail recipientDetail) {
		emailHelper.insertRecord(recipientDetail);
		for (Receipient recipient : recipientDetail.getRecipients()) {
			mailService.sendEmailToRecipient(recipient);
		}
		return "email sending initiated!";
	}

	@PostMapping(AppConstants.API + "/insert")
	public ResponseEntity<Enroll> createEmailTrackRecord(@RequestBody Enroll enroll) {
		Enroll savedEmailTrack = emailTrackRepository.save(enroll);
		return new ResponseEntity<>(savedEmailTrack, HttpStatus.CREATED);
	}
}
