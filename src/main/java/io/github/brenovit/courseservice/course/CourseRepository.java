package io.github.brenovit.courseservice.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

	List<Course> findAllByUserCode(Long userCode);

}
