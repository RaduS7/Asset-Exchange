package org.example.spring1;

import org.example.spring1.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class Spring1Application {

  public static void main(String[] args) {
    SpringApplication.run(Spring1Application.class, args);
  }
}
