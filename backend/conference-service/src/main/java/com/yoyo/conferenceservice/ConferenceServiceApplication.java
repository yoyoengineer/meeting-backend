package com.yoyo.conferenceservice;

import com.kanz.conferenceservice.event.messagedriven.MyChannels;
import com.kanz.conferenceservice.service.characters.AttendanceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBinding(MyChannels.class)
public class ConferenceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConferenceServiceApplication.class, args);
	}

}
