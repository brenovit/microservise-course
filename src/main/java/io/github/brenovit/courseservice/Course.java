package io.github.brenovit.courseservice;

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

}
