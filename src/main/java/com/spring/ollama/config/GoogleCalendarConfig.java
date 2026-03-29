/*
package com.spring.ollama.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    @Bean
    public Calendar calendarService() throws Exception {

        InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream("credentials.json");

        if (stream == null) {
            throw new RuntimeException("❌ credentials.json NOT FOUND in resources");
        }

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(stream)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        return new Calendar.Builder(
                transport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        )
                .setApplicationName("Spring AI Scheduler")
                .build();
    }
}*/
