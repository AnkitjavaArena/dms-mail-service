package com.otsMail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.otsMail.dao.EmailTrackRepository;
import com.otsMail.model.EmailTrack;
import com.otsMail.model.Receipient;
import com.otsMail.model.RecipientDetail;
import com.otsMail.service.MailService;
import com.otsMail.util.AppConstants;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MailController {

	@Autowired
	private MailService mailService;
	private final @NonNull EmailTrackRepository emailTrackRepository;

	@PostMapping(AppConstants.API + "/sendMail")
	public String sendOtpEmail(@RequestBody RecipientDetail recipientDetail) {
		for (Receipient recipient : recipientDetail.getRecipients()) {
			mailService.sendEmailToRecipient(recipient);
		}
		return "email sending initiated!";
	}

	@PostMapping(AppConstants.API + "/insert")
	public ResponseEntity<EmailTrack> createEmailTrackRecord(@RequestBody EmailTrack emailTrack) {
		EmailTrack savedEmailTrack = emailTrackRepository.save(emailTrack);
		return new ResponseEntity<>(savedEmailTrack, HttpStatus.CREATED);
	}
}
