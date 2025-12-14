package com.learnnow.certificate.controller;

import com.learnnow.course.service.CourseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificate")
public class CertificateController {
    private final CourseService courseService;

    public CertificateController(CourseService courseService) {
        this.courseService = courseService;
    }
}
