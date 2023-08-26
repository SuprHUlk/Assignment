package com.example.demo.services;

import com.example.demo.entity.Session;
import com.example.demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    //Initializes the table
    public void initTable() {
        LocalDate date=LocalDate.of(2023, 8, 26);
        LocalTime time=LocalTime.of(18, 0);
        LocalDateTime bookedAt=LocalDateTime.now();

        Session session=new Session();

        session.setUserId("user1");
        session.setMentorId("mentor1");
        session.setDate(date);
        session.setTime(time);
        session.setBookedAt(bookedAt);

        sessionRepository.save(session);
    }


    //A user can only cancel the session if the difference btw current time and the session time is more than minDur
    public String cancelSession(Session session) {

        //minDur is the time difference after which after a user cannot cancel the session
        //it is in minutes, so 12hours*60minutes
        long minDur=12*60;

        LocalDate date=session.getDate();
        LocalTime time=session.getTime();

        if(durCheck(date, time, minDur)) {
            sessionRepository.delete(session);
            return "Session Cancelled";
        }

        return "Session cannot be cancelled";

    }

    //A user can only reschedule the session if the difference btw current time and the session time is more than minDur
    public String rescheduleSession(Session session) {

        String mentorId=session.getMentorId();
        LocalDate rescheduledDate=session.getDate();
        LocalTime rescheduledTime=session.getTime();

        //Checks if the mentor is free at the rescheduled date and time or not
        //If he is not it return an appropriate message
        if(check(mentorId, rescheduledDate, rescheduledTime)) {
            return "Session cannot be rescheduled";
        }

        //minDur is the time difference after which after a user cannot reschedule the session
        //it is in minutes, so 4hours*60minutes
        long minDur=4*60;

        //checks if the difference btw reschedule time and current time is greater than minDur or not
        if(durCheck(rescheduledDate, rescheduledTime, minDur)) {

            session.setDate(rescheduledDate);
            session.setTime(rescheduledTime);
            session.setBookedAt(LocalDateTime.now());
            sessionRepository.save(session);
            return "Session rescheduled";

        }

        return "Session cannot be rescheduled";

    }

    //Checks if the mentor is free at the given time and date or not
    private boolean check(String mentorId, LocalDate sessionDate, LocalTime sessionTime) {

        return sessionRepository.existsByMentorIdAndDateAndTime(mentorId, sessionDate, sessionTime);

    }

    //checks if the difference btw given time and current time is greater than minDur or not
    private boolean durCheck(LocalDate sessionDate, LocalTime sessionTime, long minDur) {

        LocalDateTime sessionDateTime=LocalDateTime.of(sessionDate, sessionTime);
        LocalDateTime curDateTime=LocalDateTime.of(LocalDate.now(), LocalTime.now());
        long dur=Duration.between(curDateTime, sessionDateTime).toMinutes();

        return dur>minDur;
    }

    //Logic for recurring session at regular intervals
    //Here frequency is number of session in a week and duration is number of months
    public String rebookSession(Session session, int frequency, int duration) {

        //Store all the new sessions created
        List<Session> sessions=new ArrayList<>();

        //converts months to week by multiplying duration with 4 because there are 4 weeks in a month
        //So we get total number of weeks
        for(int week=1; week<=duration*4; week++) {

            //Increase the date by weeks
            LocalDate newWeek=session.getDate().plusWeeks(week);

            //Creates required number of session per week.
            for(int day=0; day<frequency; day++) {
                Session newSession=new Session();
                newSession.setUserId(session.getUserId());
                newSession.setMentorId(session.getMentorId());
                newSession.setDate(newWeek.plusDays((long)day));
                newSession.setTime(session.getTime());
                newSession.setBookedAt(LocalDateTime.now());
                sessions.add(newSession);
            }
        }

        //Saves all the new session created in the table
        sessionRepository.saveAll(sessions);

        return "Recurring on";

    }


}
