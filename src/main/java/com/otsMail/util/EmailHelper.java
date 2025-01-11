package com.otsMail.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.otsMail.config.EmailConfig;
import com.otsMail.dao.EmailTrackRepository;
import com.otsMail.model.EmailTrack;
import com.otsMail.model.Receipient;
import com.otsMail.model.RecipientDetail;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class EmailHelper {
	private  final @NonNull EmailConfig emailConfig;
	private final @NonNull EmailTrackRepository emailTrackRepository;
	
	public void insertRecord(RecipientDetail recipientDetail) {
        List<Receipient> recipients = recipientDetail.getRecipients();
        //TODO need to verify whether a email is already present or not, if present no insertion just update some fields
        //TODO salutation is missing in entity and table
        List<EmailTrack> emailTracks = recipients.stream()
                .map(recipient -> EmailTrack.builder()
                        .from(emailConfig.getFrom()) 
                        .to(recipient.getEmail()) 
                        .time(LocalDateTime.now())
                        .status("PENDING")
                        .count(AppConstants.DEFAULT_COUNT)
                        .subscribe(Boolean.TRUE) 
                        .build())
                .toList();

        // Save all records to the database
       emailTrackRepository.saveAll(emailTracks);

		
	}

}
