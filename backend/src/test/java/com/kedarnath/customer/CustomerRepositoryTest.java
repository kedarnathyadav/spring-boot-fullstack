package com.kedarnath.customer;

import com.kedarnath.AbstractTestcontainersUnitTest;
import com.kedarnath.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class CustomerRepositoryTest extends AbstractTestcontainersUnitTest {
    @Autowired
    private CustomerRepository underTest;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                "password", 20,

                Gender.MALE);
        underTest.save(customer);

        int id = Math.toIntExact(underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        //When
        boolean actual = underTest.existsCustomerByEmail(email);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();


        //When
        boolean actual = underTest.existsCustomerByEmail(email);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                "password", 20,

                Gender.MALE);
        underTest.save(customer);

        int id = Math.toIntExact(underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        //When
        boolean actual = underTest.existsCustomerById(id);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        //Given

        int id = -1;
        //When
        boolean actual = underTest.existsCustomerById(id);
        //Then
        assertThat(actual).isFalse();
    }
}