package org.scoula.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class HomeController {

    @GetMapping("/api/home")
    public String home() {
        log.info("===================> HomeController /");
        return "home";
    }
}

