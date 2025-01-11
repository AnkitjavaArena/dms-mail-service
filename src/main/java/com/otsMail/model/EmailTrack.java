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
@Table(name = "EmailTrack")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTrack {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "Sender")
	private String from;
	@Column(name = "Receipient")
	private String to;
	@Column(name = "Time")
	private LocalDateTime time;
	@Column(name = "Status")
	private String status;
	@Column(name = "Count")
	private Integer count;
	@Column(name = "Subscribe")
	private Boolean subscribe;

}
