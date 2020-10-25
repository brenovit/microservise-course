package io.github.brenovit.courseservice.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultResponse {
	private String message;
	private Integer status;
	private String error;
}
