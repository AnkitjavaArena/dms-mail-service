package com.otsMail.component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otsMail.dao.EmailHistoryRepository;
import com.otsMail.dao.EnrollRepository;
import com.otsMail.model.EmailHistory;
import com.otsMail.model.Enroll;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class DatabaseBackup {
	@Autowired
	private EnrollRepository enrollRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private EmailHistoryRepository emailHistoryRepository;

	@PreDestroy
	public void exportDataFromDbtoFile() {
		try {
			List<Enroll> enrollList = enrollRepository.findAll();
			objectMapper.writeValue(new File("enroll_backup.json"), enrollList);
			log.info("Enroll data exported successfully to enroll_backup.json");
			List<EmailHistory> emailHistoryList = emailHistoryRepository.findAll();
			objectMapper.writeValue(new File("emailHistory_backup.json"), emailHistoryList);
			log.info("Email History exported successfully to emailHistory_backup.json");
		} catch (IOException e) {
			log.error("Error exporting data: {}", e.getMessage(), e);
		}
	}

	@PostConstruct
	public void importDataToDb() {
		try {
			List<Enroll> enrollList = objectMapper.readValue(new File("enroll_backup.json"),
					objectMapper.getTypeFactory().constructCollectionType(List.class, Enroll.class));
			for (Enroll enroll : enrollList) {
				enrollRepository.save(enroll);
			}
			log.info("Enroll data successfully loaded into the database.");

			List<EmailHistory> emailHistoryList = objectMapper.readValue(new File("emailHistory_backup.json"),
					objectMapper.getTypeFactory().constructCollectionType(List.class, EmailHistory.class));
			for (EmailHistory emailHistory : emailHistoryList) {
				emailHistoryRepository.save(emailHistory);
			}
			log.info("Email History successfully loaded into the database.");

		} catch (IOException e) {
			log.error("Error loading enroll data from backup file: {}", e.getMessage());
		}
	}

}
