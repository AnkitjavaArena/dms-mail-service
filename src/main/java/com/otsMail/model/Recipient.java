package com.otsMail.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Recipient {
	private String email;
	private String salutation;

}
