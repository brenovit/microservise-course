package io.github.brenovit.courseservice;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CourseService {
	
	private CourseRepository repository;
	
	public Course save(Course entity) {
		return repository.save(entity);
	}
	
	public Course findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
	}
	
	public List<Course> findAll(){
		return repository.findAll();
	}
	
	public Course update(Long id, Course entity) {
		Course findById = findById(id);
		findById.setDescription(entity.getDescription());
		findById.setTitle(entity.getTitle());		
		return repository.save(entity);
	}
	
	public void delete(Long id) {
		repository.deleteById(id);
	}
}
