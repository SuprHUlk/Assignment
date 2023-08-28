package com.example.demo.repository;

import com.example.demo.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface SessionRepository extends JpaRepository<Session, Long> {
    boolean existsByMentorIdAndDateAndTime(String mentorId, LocalDate date, LocalTime time);
}
