package com.kedarnath;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.Customer;
import com.kedarnath.customer.CustomerRepository;
import com.kedarnath.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {


    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }

    @Bean
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName().toLowerCase();
            String lastName = name.lastName().toLowerCase();
            int age = random.nextInt(25, 99);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName + "." + lastName + "@kedarnath.com",
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    age,
                    gender);
            customerRepository.save(customer);
        };
    }


}

