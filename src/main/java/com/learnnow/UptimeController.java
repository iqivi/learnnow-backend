package com.learnnow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UptimeController {

    @GetMapping("/uptime")
    public String uptime() {
        long seconds = (System.currentTimeMillis() - BackendApplication.startTime) / 1000;
        return "{ \"uptime\": " + seconds + " }";
    }
}

