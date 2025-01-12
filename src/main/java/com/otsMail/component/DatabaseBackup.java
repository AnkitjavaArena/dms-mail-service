package com.otsMail.component;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otsMail.model.Enroll;

import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class DatabaseBackup {
	// TODO change to jpa rather than plain jdbc
	@PreDestroy
	public void backupDatabase() {
		this.exportEnrollDataToFile();
	}

	public void exportEnrollDataToFile() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:h2:mem:test", "test", "test");
			String sqlQuery = "SELECT * FROM \"Enroll\"";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlQuery);
			List<Enroll> enrollList = new ArrayList<>();
			while (resultSet.next()) {
				Enroll enroll = new Enroll();
				enroll.setId(resultSet.getLong("Id"));
				enroll.setTo(resultSet.getString("Recipient"));
				enroll.setSalutation(resultSet.getString("Salutation"));
				enroll.setTime(resultSet.getObject("Time", LocalDateTime.class));
				enroll.setStatus(resultSet.getString("Status"));
				enroll.setCount(resultSet.getInt("Count"));
				enroll.setSubscribe(resultSet.getBoolean("Subscribe"));

				enrollList.add(enroll);
			}
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.writeValue(new File("enroll_backup.json"), enrollList);
			log.info("Enroll data exported successfully to enroll_backup.json");

		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error exporting data: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println("Error closing database resources: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
