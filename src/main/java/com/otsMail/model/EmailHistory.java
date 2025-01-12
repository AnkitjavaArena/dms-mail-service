package com.otsMail.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private Long id;
	@ManyToOne
	@JoinColumn(name = "EnrollId", referencedColumnName = "Id")
	private Enroll enroll; // Foreign Key
	@Column(name = "Sender", nullable = false)
	private String sender;
	@Column(name = "Recipient", nullable = false)
	private String recipient;
	@Column(name = "Timestamp", nullable = false)
	private LocalDateTime timestamp;
	@Column(name = "Salutation")
	private String salutation;
	@Column(name = "Status", nullable = false)
	private String status;
	@Column(name = "Subscribe", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean subscribe = Boolean.TRUE;
}
