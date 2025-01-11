package com.otsMail.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.otsMail.config.EmailConfig;
import com.otsMail.dao.EmailTrackRepository;
import com.otsMail.model.Enroll;
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
        List<Enroll> enroll = recipients.stream()
                .map(recipient -> Enroll.builder()
                        .from(emailConfig.getFrom()) 
                        .salutation(this.getSalutationValue(recipient))
                        .to(recipient.getEmail()) 
                        .time(LocalDateTime.now())
                        .status("PENDING")
                        .count(AppConstants.DEFAULT_COUNT)
                        .subscribe(Boolean.TRUE) 
                        .build())
                .toList();

        // Save all records to the database
       emailTrackRepository.saveAll(enroll);	
	}
	
	public String getSalutationValue(Receipient recipient) {
		String salutation="Team";
		if (recipient.getSalutation() != null && !(recipient.getSalutation().isBlank())) {
			salutation = recipient.getSalutation();
		}
		return salutation;
	}
	
	public String getEmailContent(String salutation) throws IOException {
		String template = new String(
				Files.readAllBytes(Paths.get(new ClassPathResource("email-template.html").getURI())));
		return template.replace("{{salutation}}", salutation);
	}
	
}
