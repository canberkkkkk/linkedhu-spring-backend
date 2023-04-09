package linkedhu;

import java.util.ArrayList;

import linkedhu.constants.RoleConstants;
import linkedhu.model.Role;
import linkedhu.model.User;
import linkedhu.service.AnnouncementService;
import linkedhu.service.UserService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LinkedhuApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedhuApplication.class, args);
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
