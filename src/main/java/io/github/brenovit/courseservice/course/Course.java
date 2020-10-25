package io.github.brenovit.courseservice.course;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Course {
	
	@Id
	@GeneratedValue
	private Long id;
	private String title;
	private String description;
	private Long userCode;
	private String createdByName;
	
}
