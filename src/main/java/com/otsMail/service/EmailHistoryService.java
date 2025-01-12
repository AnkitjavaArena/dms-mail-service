package com.otsMail.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.otsMail.config.EmailConfig;
import com.otsMail.dao.EmailHistoryRepository;
import com.otsMail.model.EmailHistory;
import com.otsMail.model.Enroll;
import com.otsMail.model.Recipient;
import com.otsMail.util.EmailHelper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {
	private final @NonNull EmailHelper emailHelper;
	private final @NonNull EmailConfig emailConfig;
	private final @NonNull EmailHistoryRepository emailHistoryRepository;
	
	public EmailHistory createEmailHistory(Recipient recipient, LocalDateTime localDateTime) {
		Enroll enroll=emailHelper.getEnrollByEmail(recipient.getEmail());
		  EmailHistory emailHistory = EmailHistory.builder()
			        .enrollId(enroll.getId())
	                .sender(emailConfig.getFrom())
	                .recipient(enroll.getTo())
	                .timestamp(localDateTime)
	                .status(enroll.getStatus())
	                .subscribe(enroll.getSubscribe())
	                .build();
	        return emailHistoryRepository.save(emailHistory);
	}

}
