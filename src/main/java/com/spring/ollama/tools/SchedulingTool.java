/*
package com.spring.ollama.tools;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;

@Slf4j
@Component
public class SchedulingTool {

    private final Calendar service;

    public SchedulingTool(Calendar service) {
        this.service = service;
    }

    @Tool(description = "Schedule an interview on Google Calendar. Provide summary, description, attendee email, start time and end time.")
    public String scheduleInterview(
            String summary,
            String description,
            String attendeeEmail,
            ZonedDateTime startTime,
            ZonedDateTime endTime
    ) {
        try {
            log.info("Scheduling interview for {}", attendeeEmail);

            Event event = new Event()
                    .setSummary(summary)
                    .setDescription(description)
                    .setAttendees(Collections.singletonList(
                            new EventAttendee().setEmail(attendeeEmail)
                    ));

            event.setStart(toEventDateTime(startTime));
            event.setEnd(toEventDateTime(endTime));

            Event createdEvent = service.events()
                    .insert("primary", event)
                    .setSendUpdates("all")
                    .execute();

            return "✅ Interview scheduled: " + createdEvent.getHtmlLink();

        } catch (Exception e) {
            log.error("Error scheduling interview", e);
            return "❌ Failed to schedule interview: " + e.getMessage();
        }
    }

    // helper method
    private EventDateTime toEventDateTime(ZonedDateTime time) {
        return new EventDateTime()
                .setDateTime(new DateTime(time.toInstant().toEpochMilli()))
                .setTimeZone(time.getZone().toString());
    }
}*/
