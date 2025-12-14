package com.learnnow.course.controller;

import com.learnnow.course.model.Course;
import com.learnnow.course.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;

    CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    @PostMapping()
    public ResponseEntity<Course> addCourse(/*@RequestBody TokenRequest tokenRequest, */@RequestBody Course course) {
        return ResponseEntity.ok(courseService.addCourse(/*tokenRequest, */course));
    }
}
