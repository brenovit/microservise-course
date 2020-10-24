package io.github.brenovit.courseservice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {
	
	private CourseService service;
	
	@PostMapping("")
	public ResponseEntity<Course> save(@RequestBody Course entity){
		return ResponseEntity.ok(service.save(entity));
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<Course> upate(@PathVariable("id") Long id, @RequestBody Course entity){
		return ResponseEntity.ok(service.update(id, entity));
	}
	
	@GetMapping("")
	public ResponseEntity<List<Course>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Course> findById(@PathVariable("id") Long id){
		return ResponseEntity.ok(service.findById(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id){
		service.delete(id);
		return ResponseEntity.ok("Course deleted");
	}
}
