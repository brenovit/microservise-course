package io.github.brenovit.courseservice.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.brenovit.courseservice.infraestructure.HeaderHelper;
import io.github.brenovit.courseservice.user.User;
import io.github.brenovit.courseservice.user.UserApi;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Service
@AllArgsConstructor
public class CourseService {
	
	private CourseRepository repository;
	private UserApi userApi;
	
	@Autowired
	private HeaderHelper headerHelper;
	
	public Course save(Course entity) {		
		entity.setUserCode(getUser().getCode());
		entity.setCreatedByName(getUser().getName());
		return repository.save(entity);
	}
	
	public Course findById(Long id) {
		Course course = repository
				.findById(id)
				.orElseThrow(() -> new RuntimeException("Course not found"));
		
		if(course.getUserCode() != getUser().getCode()) {
			throw new RuntimeException("Course doesn't belong to this User");
		}
		return course; 
	}
	
	public List<Course> findAll(){
		return repository.findAllByUserCode(getUser().getCode());
	}
	
	public Course update(Long id, Course entity) {
		Course course = findById(id);		
		course.setDescription(entity.getDescription());
		course.setTitle(entity.getTitle());
		return repository.save(course);
	}
	
	public void delete(Long id) {
		findById(id);
		repository.deleteById(id);
	}
	
	@SneakyThrows
	private User getUser() {
		User user = userApi.findUser(headerHelper.getUserId());		
		if(user.getMessage() != null) {
			throw new RuntimeException(user.getMessage());
		}		
		return user; 
	}
}
