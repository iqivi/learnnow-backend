package com.learnnow.course.service;

import com.learnnow.course.exception.CourseNotFoundException;
import com.learnnow.course.model.Course;
import com.learnnow.course.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course getCourse(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isEmpty())
            throw new CourseNotFoundException();
        return course.get();
    }

    public Course addCourse(/*TokenRequest tokenRequest, */Course course) {
        //if (tokenRequest.getUser().getRole() != AUTHOR)
        //    throw new InsufficientPrivilegesException();
        return courseRepository.save(course);
    }
}
