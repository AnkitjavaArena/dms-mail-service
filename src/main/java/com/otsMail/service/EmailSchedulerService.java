package com.otsMail.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.otsMail.model.Recipient;
import com.otsMail.util.EmailHelper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailSchedulerService {
	private final @NonNull MailService mailService;
	private final @NonNull EmailHelper emailHelper;

	@Value("${scheduled.enabled:false}")  // Default to false if not set
    private boolean isScheduledEnabled;
	/**
	 * Sends Email to recipient with status as active and  subscription as true every Monday at 10AM.
	 */
	@Scheduled(cron = "0 0 10 * * MON")
	public void sendEmailsEveryMondayAt10AM() {
	       if(isScheduledEnabled) {
			log.info("Sending emails every Monday at 10 Am to all recipient with status as active and subscription as true: {} ", LocalDateTime.now());
			List<Recipient> recipients = emailHelper.getActiveEnrollments()
									.stream()
									.map(enroll -> Recipient.builder()
												.email(enroll.getTo())
												.salutation(enroll.getSalutation())
												.build())
									.collect(Collectors.toList());
			for (Recipient recipient : recipients) {
				mailService.sendEmailToRecipient(recipient);
			}
		}else {
			log.info("Schedular set to false");
		}
	}
	
}
