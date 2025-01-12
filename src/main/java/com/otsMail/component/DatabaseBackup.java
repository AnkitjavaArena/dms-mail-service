package com.otsMail.component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otsMail.dao.EnrollRepository;
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

	// TODO we need to take backup of other files-create a dir and store those
	// files, currently only Enroll data
	@PreDestroy
	public void exportDataFromDbtoFile() {
		try {
			List<Enroll> enrollList = enrollRepository.findAll();
			objectMapper.writeValue(new File("enroll_backup.json"), enrollList);
			log.info("Enroll data exported successfully to enroll_backup.json");
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

		} catch (IOException e) {
			log.error("Error loading enroll data from backup file: {}", e.getMessage());
		}
	}

}
