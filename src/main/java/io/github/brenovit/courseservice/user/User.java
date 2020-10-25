package io.github.brenovit.courseservice.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends DefaultResponse{			
	private String id;
	private Long code;
	private String name;
}
