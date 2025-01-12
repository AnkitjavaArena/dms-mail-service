package com.otsMail.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EmailHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	@Column(name = "EnrollId", nullable = false)
	private Long enrollId;
	@Column(name = "Sender", nullable = false)
	private String sender;
	@Column(name = "Recipient", nullable = false)
	private String recipient;
	@Column(name = "Timestamp", nullable = false)
	private LocalDateTime timestamp;
	@Column(name = "Status", nullable = false)
	private String status;
	@Column(name = "Subscribe", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean subscribe = Boolean.TRUE;
}
