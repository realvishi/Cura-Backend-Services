package com.in.talkey;

import com.in.talkey.entity.Users;
import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TalkeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalkeyApplication.class, args);
		System.out.println("Done!");
	}

}
