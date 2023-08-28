package com.example.demo.controller;

import com.example.demo.entity.Session;
import com.example.demo.services.SessionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostConstruct
    void initTable() {sessionService.initTable();}

    @DeleteMapping({"session/cancelSession"})
    public String cancelSession(@RequestBody Session session) {
        return sessionService.cancelSession(session);
    }

    @PutMapping({"session/rescheduleSession"})
    public String rescheduleSession(@RequestBody Session session) {
        return sessionService.rescheduleSession(session);
    }

    //Here frequency is number of session in a week and duration is number of months
    @PostMapping({"session/rebookSession"})
    public String rebookSession(@RequestBody Session session, @RequestParam int frequency, @RequestParam int duration) {
        return sessionService.rebookSession(session, frequency, duration);
    }
}
