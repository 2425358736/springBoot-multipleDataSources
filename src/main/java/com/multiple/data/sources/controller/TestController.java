package com.multiple.data.sources.controller;

import com.multiple.data.sources.service.TestService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 */
@RestController
@RequestMapping("/test")
@CrossOrigin
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/multiSourceOperation")
    private String multiSourceOperation() {
        return testService.multiSourceOperation();
    }
}
