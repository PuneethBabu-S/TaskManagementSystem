package pbs.tms.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
public class TestController {
    @GetMapping
    public String test() {
        return "Test Controller";
    }
}