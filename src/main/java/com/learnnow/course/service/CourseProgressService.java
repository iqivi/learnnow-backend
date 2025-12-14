package com.learnnow.course.service;

import com.learnnow.course.repository.CourseProgressRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseProgressService {
    private CourseProgressRepository courseProgressRepository;

    public CourseProgressService(CourseProgressRepository courseProgressRepository) {
        this.courseProgressRepository = courseProgressRepository;
    }
}
