### step 401:

Security intro

### step 402:

if we inspect any one can access the api

### step 403;

spring security 6

### step 404:

refer https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
documentation

### step 405:

refer https://jwt.io/

### step 406:

Refer https://jwt.io/introduction

### step 407:

we are work with tokens to secure

### step 408:

install all the dependencies we need

Refer https://github.com/jwtk/jjwt

add dependencies

```text
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jsonwebtoken.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jsonwebtoken.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jsonwebtoken.version}</version>
            <scope>runtime</scope>
        </dependency>
```

### step 409:

Run the application normally without docker
it might not work as you stopped the postgres last time

```text
docker run --name postgres -e POSTGRES_DB=customer -e POSTGRES_USER=kedarnath -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:16
```

In case you enter something wrong

```text
docker stop postgres-container
docker rm postgres-container
```

if you try to access the api it asks to sign on

### step 410:

create a package jwt under ```com.kedarnath```
create a file named ```JWTUtil.java```

### step 411:

JWTUtil.java

```java
package com.kedarnath.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY =
            "king_123456898_king_123456898king_123456898king_123456898king_123456898king_123456898";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .issuer("https://kedarnath.com")
                .expiration(Date.from(now.plus(15, ChronoUnit.DAYS)))
                .claims(claims) // Keep claims map
                .signWith(SignatureAlgorithm.HS256, getSignedKey())
                .compact();
    }

    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
```

### step 412:

update CustomerController.java

```java
package com.kedarnath.customer;

import jwt.JWTUtil;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomers(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomer(customerId);

    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer customerId,
                               @RequestBody CustomerUpdateRequest updatedRequest) {
        customerService.updateCustomer(customerId, updatedRequest);
    }


}

```

### step 414:

create a package jwt under ```com.kedarnath```
create a file named ```SecurityFilterChain.java```

SecurityFilterChain.java:

```
package com.kedarnath.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers")
                        .permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

```

test the application using postman, and it gives when you post for registration
and when you use get request it gets forbidden

### step 415:

paste your jwt in jwt.io debugger website to check it
you can see the date created and date expires and roles

### step 416:

Refer:
https://docs.spring.io/spring-security/reference/servlet/architecture.html

### step 417:

![](backend/src/main/resources/static/images/img.png)
we have achieved the first part second part we do later lets start third part .
the plan for 3rd part is below
![](backend/src/main/resources/static/images/img_1.png)

### step 418:

lets understand from backwards

![](backend/src/main/resources/static/images/img_1.png)

ctrl + n -> User class which has relation to the user details service

### step 419:

we are going implement that User class with our Customer class

Customer.java:

```java
package com.kedarnath.customer;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_email_unique",
                        columnNames = "email"
                )
        }
)
public class Customer implements UserDetails {
    @Id
    @SequenceGenerator(
            name = "customer_id_seq",
            sequenceName = "customer_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id_seq"
    )
    private Long id;
    @Column(
            nullable = false
    )
    private String name;
    @Column(
            nullable = false
    )
    private String email;
    @Column(
            nullable = false
    )
    private Integer age;

    @Column(
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(
            nullable = false
    )
    private String password;

    public Customer(Long id,
                    String name,
                    String email,
                    String password, Gender gender, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }

    public Customer(String name,
                    String email,
                    String password,
                    Integer age,
                    Gender gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(age, customer.age) && gender == customer.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, gender);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

```

add a column password and add it to the constructor using refractor so that all the classes gets updated

right click on constructor -> refractor -> change signature-> click + -> Type : String , name : password, value: "
password"
-> using triangle arrow move this below the email -> click refractor.

### step 420:

to make sure test cases gets passed

delete v2 and v3
modify v1 db migration

```text
CREATE TABLE CUSTOMER
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    email    TEXT NOT NULL,
    password TEXT NOT NULL,
    age      INT  NOT NULL,
    gender   TEXT NOT NULL
);
```

update row mapper and some service classes:
CustomerListDataAccessService.java

````java
package com.kedarnath.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {
    private final static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(
                1L,
                "Vikram",
                "vikram@gmail.com",
                "password",
                Gender.MALE, 27
        );
        customers.add(alex);
        Customer shruthi = new Customer(
                2L,
                "Shruthi",
                "Shruthi@gmail.com",
                "password",
                Gender.MALE, 26
        );
        customers.add(shruthi);
        Customer priya = new Customer(
                3L,
                "Priya",
                "Priya@gmail.com",
                "password", Gender.MALE, 29
        );
        customers.add(priya);
        Customer disha = new Customer(
                4L,
                "Disha",
                "Disha@gmail.com",
                "password", Gender.MALE, 26
        );
        customers.add(disha);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();
//                .orElseThrow(() -> new IllegalArgumentException("customer with id [%s] not found".formatted(customerId)));
    }

    @Override
    public void insertCustomer(Customer customer) {

    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer update) {
        customers.add(update);
    }


}

````

update:
CustomerJDBCDataAccessService.java

```
package com.kedarnath.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {

        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                """;
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                  INSERT INTO customer(name, email, password, age, gender)
                  VALUES(?, ?, ?, ?, ?)
                """;
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAge(),
                customer.getGender().name()
        );

        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, customerId);
        System.out.println("deleteCustomerById Result = " + result);
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getName(),
                    update.getId()
            );
            System.out.println(" Update Customer Name Result = " + result);
        }
        if (update.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getAge(),
                    update.getId()
            );
            System.out.println(" Update Customer Age Result = " + result);
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getEmail(),
                    update.getId()
            );
            System.out.println(" Update Customer Email Result = " + result);
        }

    }
}

```

delete the flyway and customer tables from database and run Integration test
it fails saying forbidden. we will fix it soon.

### step 421:

![](backend/src/main/resources/static/images/img_1.png)
before saving the username and password on to the database lets do the UserDetailService and
PasswordEncoder associated with DaoAuthentication Provider.

First focus is UserDetailsService

Open Intellij

create a new class CustomerUserDetailsService.java under customer

```text
package com.kedarnath.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerDao customerDao;

    public CustomerUserDetailsService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerDao.selectUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
    }
}

```

we are going to add a method to Customer Dao, so we also need to update the implementations
CustomerDao.java

```java
package com.kedarnath.customer;


import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer id);

    void insertCustomer(Customer customer);

    boolean existsPersonWithEmail(String email);

    boolean existsPersonWithId(Integer id);

    void deleteCustomerById(Integer id);

    void updateCustomer(Customer update);

    Optional<Customer> selectUserByEmail(String email);
}

```

CustomerJPADataAccessService.java

````java
package com.kedarnath.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao {

    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {

        return customerRepository.findById(id);
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);

    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customerRepository.existsCustomerById(id);
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customerRepository.deleteById(id);
    }

    @Override
    public void updateCustomer(Customer update) {
        customerRepository.save(update);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customerRepository.findCustomersByEmail(email);
    }
}

````

CustomerRepository.java

```java
package com.kedarnath.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {


    boolean existsCustomerByEmail(String email);

    boolean existsCustomerById(Integer id);

    Optional<Customer> findCustomersByEmail(String email);

}

```

CustomerListDataAccessService.java

```java
package com.kedarnath.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {
    private final static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(
                1L,
                "Vikram",
                "vikram@gmail.com",
                "password",
                Gender.MALE, 27
        );
        customers.add(alex);
        Customer shruthi = new Customer(
                2L,
                "Shruthi",
                "Shruthi@gmail.com",
                "password",
                Gender.MALE, 26
        );
        customers.add(shruthi);
        Customer priya = new Customer(
                3L,
                "Priya",
                "Priya@gmail.com",
                "password", Gender.MALE, 29
        );
        customers.add(priya);
        Customer disha = new Customer(
                4L,
                "Disha",
                "Disha@gmail.com",
                "password", Gender.MALE, 26
        );
        customers.add(disha);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();
//                .orElseThrow(() -> new IllegalArgumentException("customer with id [%s] not found".formatted(customerId)));
    }

    @Override
    public void insertCustomer(Customer customer) {

    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer update) {
        customers.add(update);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getUsername().equals(email))
                .findFirst();
    }


}

```

CustomerJDBCDataAccessService.java

```
package com.kedarnath.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {

        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                """;
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                  INSERT INTO customer(name, email, password, age, gender)
                  VALUES(?, ?, ?, ?, ?)
                """;
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAge(),
                customer.getGender().name()
        );

        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, customerId);
        System.out.println("deleteCustomerById Result = " + result);
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getName(),
                    update.getId()
            );
            System.out.println(" Update Customer Name Result = " + result);
        }
        if (update.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getAge(),
                    update.getId()
            );
            System.out.println(" Update Customer Age Result = " + result);
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getEmail(),
                    update.getId()
            );
            System.out.println(" Update Customer Email Result = " + result);
        }

    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        var sql = """
                SELECT id, name, email, password, age, gender
                FROM customer
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, email)
                .stream()
                .findFirst();

    }
}

```

### step 422:

![](backend/src/main/resources/static/images/img_1.png)

Now focus is on Passwordencoder:

refer: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html

we are going to use BCrypt
refer: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-bcrypt

if you see in controller registartion request we got no password in it lets add password

make sure you add using refactor option by ide
CustomerRegistrationRequest.java

```java
package com.kedarnath.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        String password,
        Integer age,
        Gender gender
) {

}

```

CustomerService.java

```java
package com.kedarnath.customer;

import com.kedarnath.exception.DuplicateResourceException;
import com.kedarnath.exception.RequestValidationException;
import com.kedarnath.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao
                .selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found"
                        .formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException("customer with email [%s] already exists"
                    .formatted(email));

        }
        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {

        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found"
                    .formatted(id));

        }

        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updatedRequest) {
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if (updatedRequest.name() != null && !updatedRequest.name().equals(customer.getName())) {
            customer.setName(updatedRequest.name());
            changes = true;
        }

        if (updatedRequest.age() != null && !updatedRequest.age().equals(customer.getAge())) {
            customer.setAge(updatedRequest.age());
            changes = true;
        }

        if (updatedRequest.email() != null && !updatedRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsPersonWithEmail(updatedRequest.email())) {
                throw new DuplicateResourceException("customer with email [%s] already exists"
                        .formatted(updatedRequest.email()));

            }

            customer.setEmail(updatedRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);


    }
}

```

Tests will fail
we will fix it next step

### step 423:

fix the CustomerServiceTest.java:

```java
package com.kedarnath.customer;

import com.kedarnath.exception.DuplicateResourceException;
import com.kedarnath.exception.RequestValidationException;
import com.kedarnath.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, passwordEncoder);
    }


    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with id [%s] not found".formatted(id));

    }

    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, "password", 19, Gender.MALE
        );
        String passwordHash = "$97097987575&*^";
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);
        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void addCustomerThrowsExceptionWhenEmailExists() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, "password", 19, Gender.MALE
        );

        // When & Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("customer with email [alex@gmail.com] already exists");

        // Verify that insertCustomer is never called
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void deleteCustomerByIdThrowsExceptionWhenIdNotExists() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found"
                        .formatted(id));
        //Then
        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerproperties() {
        //Given
        int id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                (long) id, "alex", email, "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "alexandra", newEmail, 19
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "alexandra", null, null
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 26
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void willThrowWhenTryingToUpdateEmailWhenAlreadyTaken() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);
        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("customer with email [%s] already exists"
                        .formatted(newEmail));

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenNoChnageInUpdate() {
        //Given
        int id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                (long) id, "alex", email, "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

}
```

Make sure all the customer tests are running fine

Update Main.java:- to store the password in hash

```java
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


```

made a mistakein SecurityConfig:

fixed SecurityConfig:

```java
package com.kedarnath.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```

Run the application, and you see that password is saved in the form of hash.

### step 424: date 21st oct 2024

![](backend/src/main/resources/static/images/img_1.png)
finished the passwordEncoder

Update SecurityConfig:

```
package com.kedarnath.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

}

```

Go to AuthenticationManger class and its implementation ProviderManager.java

in this you got a method with list of providers next we create one

### step 425:

let configure AUthentication Manager with DaoAuthentication Provider

SecurityConfig.java:

```text
package com.kedarnath.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
}
```

### step 426:

![](backend/src/main/resources/static/images/img.png)

now we have to filter intercepts request
valid token
set authentication

we want to attach a filter to the filter chain

next what we are going to do is
![](backend/src/main/resources/static/images/img_2.png)

Implementation in next step

### step 427:

Create new file under JWT:
JWTAuthenticationFilter.java

```text
package com.kedarnath.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }

        String jwt = authHeader.substring(7);

    }
}

```

### step 428:

![](backend/src/main/resources/static/images/img_2.png)
Extract the subject

update JWtUtil.java and JWTAuthenticationFilter.java

JWtUtil.java:

```
package com.kedarnath.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY =
            "king_123456898_king_123456898king_123456898king_123456898king_123456898king_123456898";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .issuer("https://kedarnath.com")
                .expiration(Date.from(now.plus(15, ChronoUnit.DAYS)))
                .claims(claims) // Keep claims map
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        // Use parserBuilder instead of the deprecated parser()
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Signing key is used to verify the JWT signature
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the JWT and retrieve claims
                .getBody();  // Return the Claims object
    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


}
```

JWTAuthenticationFilter.java:

```text
package com.kedarnath.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTAuthenticationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }

        String jwt = authHeader.substring(7);
        String subject = jwtUtil.getSubject(jwt);

    }
}

```

### step 429:

Let's start by loading the details

update JwtAuthenticationFilter.java

```text
package com.kedarnath.jwt;

import com.kedarnath.customer.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }

        String jwt = authHeader.substring(7);
        String subject = jwtUtil.getSubject(jwt);


        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

        }
    }
}

```

next validate the token and all the other steps like UsernamePasswordAuthToken, Set Authentication, Next Filter
JWTUtil.java:

```
package com.kedarnath.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY =
            "king_123456898_king_123456898king_123456898king_123456898king_123456898king_123456898";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .issuer("https://kedarnath.com")
                .expiration(Date.from(now.plus(15, ChronoUnit.DAYS)))
                .claims(claims) // Keep claims map
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        // Use parserBuilder instead of the deprecated parser()
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Signing key is used to verify the JWT signature
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the JWT and retrieve claims
                .getBody();  // Return the Claims object
    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    public boolean isTokenVald(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}
```

JWTAuthenticationFilter.java

```
package com.kedarnath.jwt;

import com.kedarnath.customer.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }

        String jwt = authHeader.substring(7);
        String subject = jwtUtil.getSubject(jwt);


        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            if (jwtUtil.isTokenVald(jwt
                    , userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

```

Next step is to register the filter

### step 430:

Update filter chain config:
SecurityFilterChainConfig.java:

```text
package com.kedarnath.security;

import com.kedarnath.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers").permitAll()  // Allow unauthenticated access to this endpoint
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless session management
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }
}

```

### step 431:

made many mistakes

successfully fixed this time in few minutes thank GOD.
comments are the mistake
register a customer you get a token using postman test it back to get method.

```
package com.kedarnath.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//        }
//
//        String jwt = authHeader.substring(7);
//        String subject = jwtUtil.getSubject(jwt);
//
//
//        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
//            if (jwtUtil.isTokenVald(jwt
//                    , userDetails.getUsername())) {
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//                authenticationToken.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Check if the Authorization header is null or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue the filter chain
            return; // Exit early
        }

        String jwt = authHeader.substring(7).trim(); // Extract the JWT token (after "Bearer ")

        if (jwt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is missing");
            return; // Exit early
        }

        // Get the subject from the JWT token
        String subject = jwtUtil.getSubject(jwt);

        // Proceed only if the subject is not null and the user is not already authenticated
        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            // Validate the JWT token
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                // Create the authentication token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is invalid");
                return; // Exit early
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

}

```

### step 432:

Let's fix the tests
Run all the test CustomerRespositoryTest will fail and Integration test will fail
create a new test
TestConfig.java:

```java
package com.kedarnath;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```

CustomerRepositoryTest.java

```java
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
```

Integration test still fails we will fix it soon.

### step 433:

In Integration tests we are getting failed output as we need to pass the token with it.
we are going look into security architecture and there are some mistakes in customer controller
we fix them and come back to fix the failed tests

### step 434:

![](backend/src/main/resources/static/images/img_2.png)
Diagram is explained clearly go through all the classes in the images you will
see the interconnections and methods used to perform action.
Last filterchain is the main place

### step 435:

we have not concentrated much on testing you can test if you want to do more.
next we are going to learn DTO's which simply the integration Test.

### step 436:

DTO Pattern:

we should never expose the password even though its in hash

### step 437:

![](backend/src/main/resources/static/images/img_3.png)

Refer : https://mapstruct.org/

DTO- DATA TRANSFER OBJECT

create a java class:
CustomerDTO.java

```java
package com.kedarnath.customer;

import java.util.List;

public record CustomerDTO(
        Long id,
        String name,
        String email,
        Gender gender,
        Integer age,
        List<String> roles,
        String username
) {

}

```

Create another java class:
CustomerDTOMapper.java

```java
package com.kedarnath.customer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerDTOMapper implements Function<Customer, CustomerDTO> {
    @Override
    public CustomerDTO apply(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getGender(),
                customer.getAge(),
                customer.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                customer.getUsername()
        );
    }
}

```

Update CustomerController.java

```java
package com.kedarnath.customer;

import com.kedarnath.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public CustomerDTO getCustomers(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomer(customerId);

    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer customerId,
                               @RequestBody CustomerUpdateRequest updatedRequest) {
        customerService.updateCustomer(customerId, updatedRequest);
    }


}


```

Update CustomerService.java

```java
package com.kedarnath.customer;

import com.kedarnath.exception.DuplicateResourceException;
import com.kedarnath.exception.RequestValidationException;
import com.kedarnath.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomers() {

        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao
                .selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found"
                        .formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException("customer with email [%s] already exists"
                    .formatted(email));

        }
        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {

        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found"
                    .formatted(id));

        }

        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updatedRequest) {
        Customer customer = customerDao
                .selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found"
                        .formatted(customerId)
                ));

        boolean changes = false;

        if (updatedRequest.name() != null && !updatedRequest.name().equals(customer.getName())) {
            customer.setName(updatedRequest.name());
            changes = true;
        }

        if (updatedRequest.age() != null && !updatedRequest.age().equals(customer.getAge())) {
            customer.setAge(updatedRequest.age());
            changes = true;
        }

        if (updatedRequest.email() != null && !updatedRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsPersonWithEmail(updatedRequest.email())) {
                throw new DuplicateResourceException("customer with email [%s] already exists"
                        .formatted(updatedRequest.email()));

            }

            customer.setEmail(updatedRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);


    }
}

```

Update CustomerServiceTest.java

```java
package com.kedarnath.customer;

import com.kedarnath.exception.DuplicateResourceException;
import com.kedarnath.exception.RequestValidationException;
import com.kedarnath.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;


    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, customerDTOMapper, passwordEncoder);
    }


    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerDTO expected = customerDTOMapper.apply(customer);
        //When
        CustomerDTO actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with id [%s] not found".formatted(id));

    }

    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, "password", 19, Gender.MALE
        );
        String passwordHash = "$97097987575&*^";
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);
        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void addCustomerThrowsExceptionWhenEmailExists() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, "password", 19, Gender.MALE
        );

        // When & Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("customer with email [alex@gmail.com] already exists");

        // Verify that insertCustomer is never called
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void deleteCustomerByIdThrowsExceptionWhenIdNotExists() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found"
                        .formatted(id));
        //Then
        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerproperties() {
        //Given
        int id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                (long) id, "alex", email, "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "alexandra", newEmail, 19
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "alexandra", null, null
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 26
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void willThrowWhenTryingToUpdateEmailWhenAlreadyTaken() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);
        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("customer with email [%s] already exists"
                        .formatted(newEmail));

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenNoChnageInUpdate() {
        //Given
        int id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                (long) id, "alex", email, "password", Gender.MALE, 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

}
```

### step 438:

fixed first part in integration test:
canRegisterACustomer

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    public static final String CUSTOMER_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();


        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();


        //make sure that customer is present
        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers).contains(expectedCustomer);


        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        faker = new Faker();
        fakerName = faker.name();
        String newName = fakerName.fullName();

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, "password", gender, age
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }


}

```

next two are exercise

### step 439:

I tried but I couldn't.

explained the error that we are expecting the 404 but received the 403. we will fix it in the next video.

### step 440:

add a new file
DelegatedAuthEntryPoint.java under exception

```text
package com.kedarnath.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class DelegatedAuthEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public DelegatedAuthEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException, ServletException {
        handlerExceptionResolver.resolveException(
                request, response, null, authException
        );
    }
}

```

add this to the filter chain SecurityFilterChainConfig.java

```
package com.kedarnath.security;

import com.kedarnath.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers").permitAll()  // Allow unauthenticated access to this endpoint
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless session management
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Add JWT filter
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}

```

if you run this now and try to access the api get request
http://localhost:8080/api/v1/customers/999

you get 404 not found which is right but the message is missing.

### step 441:

create a new file ApiError.java

```
package com.kedarnath.exception;

import java.time.LocalDateTime;

public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime
) {
}

```

create a new file DefaultExceptionHandler.java

```java
package com.kedarnath.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);

    }
}

```

now when we try to access that link we get the message and code properly

### step 442:

when we try to access the api with no key it says 200 which is wrong lets fix it

we fixed it, and we gave a default exception as well

```java
package com.kedarnath.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handleException(InsufficientAuthenticationException e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}

```

### step 443:

Now we will fix the Integration test.
Think if you delete the customer from where jwtToken is from you will be in trouble. we will solve it in next step

### step 444:

fixed by creating two customers and deleting and trying to check by using one customer jwttoken

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    public static final String CUSTOMER_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();


        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();


        //make sure that customer is present
        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers).contains(expectedCustomer);


        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, email + ".uk", "password", age, gender
        );
        //send a post request to create customer 1
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //send a post request to create customer 2
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //customer 2 deletes the customer 1
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        //customer 2 gets the customer 1 by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        faker = new Faker();
        fakerName = faker.name();
        String newName = fakerName.fullName();

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, "password", gender, age
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }


}

```

### step 445:

Let's fix the canUpdate:

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    public static final String CUSTOMER_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();


        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();


        //make sure that customer is present
        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers).contains(expectedCustomer);


        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, email + ".uk", "password", age, gender
        );
        //send a post request to create customer 1
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //send a post request to create customer 2
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        //customer 2 deletes the customer 1
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        //customer 2 gets the customer 1 by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        faker = new Faker();
        fakerName = faker.name();
        String newName = fakerName.fullName();

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO expected = new CustomerDTO(
                id, newName, email, gender, age, List.of("ROLE_USER"), email
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }


}

```

Run Integration test to check

### step 446:

Run the frontend and check whether it works fine, but it won't, I know.
go to network and check fetch in inspection you will see the CORS Error

we will be working on it in the coming steps

### step 447:

Refer https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html

Update the Webmvcconfig.java to CorsConfig.java

move the CorsConfig.java to security package and delete the config folder and update the code

```text
package com.kedarnath.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;
    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;
    @Value("#{'${cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    @Value("#{'${cors.exposed-headers}'.split(',')}")
    private List<String> expectedHeaders;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        CorsRegistration corsRegistration = registry.addMapping("/api/**");
//        allowedOrigins.forEach(corsRegistration::allowedOrigins);
//        allowedMethods.forEach(corsRegistration::allowedMethods);
//    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(expectedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}

```

add some values in the application.yml

```ymal
server:
  port: 8080
  error:
    include-message: always

cors:
  allowed-origins: "*"
  allowed-methods: "*"
  allowed-headers: "*"
  exposed-headers: "*"

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customer
    username: kedarnath
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show_sql: true
  main:
    web-application-type: servlet
```

add this cors policy to the filter

```
package com.kedarnath.security;

import com.kedarnath.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers").permitAll()  // Allow unauthenticated access to this endpoint
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless session management
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Add JWT filter
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}

```

### step 448:

![](backend/src/main/resources/static/images/img_4.png)
execute the above as exercise

### step 449:

create new package com/keda

create some files:
AuthenticationController.java:

```java
package com.kedarnath.auth;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, response.token())
                .body(response);
    }
}

```

AuthenticationRequest.java:

```java
package com.kedarnath.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}

```

AuthenticationResponse.java:

```java
package com.kedarnath.auth;

import com.kedarnath.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO

) {
}

```

AuthenticationService.java:

```java
package com.kedarnath.auth;

import com.kedarnath.customer.Customer;
import com.kedarnath.customer.CustomerDTO;
import com.kedarnath.customer.CustomerDTOMapper;
import com.kedarnath.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager, CustomerDTOMapper customerDTOMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        Customer principal = (Customer) authentication.getPrincipal();
        CustomerDTO customerDTO = customerDTOMapper.apply(principal);
        String token = jwtUtil.issueToken(customerDTO.username(), customerDTO.roles());

        return new AuthenticationResponse(token, customerDTO);
    }
}

```

now register a customer you get a token in headers

now post request to http://localhost:8080/api/v1/auth/login using the registered username and password

now you get token and customerDto

now try with wrong password you get 500 error which is wrong as it has been unauthorized

update exception handler

```java
package com.kedarnath.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handleException(InsufficientAuthenticationException e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleException(BadCredentialsException e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e,
                                                    HttpServletRequest request
    ) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}

```

That's it now get the right response

### step 450:

lets do the test

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.auth.AuthenticationRequest;
import com.kedarnath.auth.AuthenticationResponse;
import com.kedarnath.customer.CustomerDTO;
import com.kedarnath.customer.CustomerRegistrationRequest;
import com.kedarnath.customer.Gender;
import com.kedarnath.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private JWTUtil jwtUtil;
    private static final Random RANDOM = new Random();
    public static final String AUTHENTICATION_PATH = "/api/v1/auth";

    public static final String CUSTOMER_PATH = "/api/v1/customers";

    @Test
    void canLogin() {
        //create registration customerRegistrationRequest
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        String password = "password";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );
        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        //send a post customerRegistrationRequest
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(
                                customerRegistrationRequest),
                        CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();
        String jwtToken = result.getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        AuthenticationResponse authenticationResponse = result.getResponseBody();

        CustomerDTO customerDTO = authenticationResponse.customerDTO();
        assertThat(jwtUtil.isTokenValid(
                jwtToken,
                customerDTO.username()
        ));
        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }

}

```

Run all the tests and check

### step 451 :

Register a new customer

Post :--- http://localhost:8080/api/v1/customers
body-> raw -> json-> {
"name": "Alex",
"email":"Alex@gmail.com",
"gender":"MALE",
"age":65,
"password":"password"

}

Try to login with the registered detailsL

post:---http://localhost:8080/api/v1/auth/login
body-> raw -> json-> {
{
"username" : "Alex@gmail.com",
"password":"password"
}

paste the token in the jwt.io website and check

### step 452 :

mistake as i though that why are we passing the customer details on request

in AuthenticationController.java

```java
package com.kedarnath.auth;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, response.token())
                .body(response);// should be removed
    }
}

```

not removing now as we need to fix tests and implementions of this method.

### step 453:

commit and push the files to github

as I am not doing the cloud stuff because i am on free tier and it takes alot time so i skip it.
any way the deployment work action will fail as in the ec2 instance got rds which has 3 versions of flyway while
we deleted the two and made it in one

if you are creating a new one no issues

ifnot ssh into rds and delete the data

go to enrironment
select this app environment
click on configuration

copy the end point of the database

```
docker run --rm -it postgres bash
```

```
-U kedarnath -h <paste the endpoint link here> -d postgres
```

enter the password

```
drop database customer;
```

```
create database customer;
```

ctrl+d twice

```
watch docker ps
```

now deployment will be successsul

### step 454:

click on environment-> select the app -> copy the url

go to postman and try to access the api

it will say full authentication is required

create a user

try to send the request by using the token -> uisng the option bearer token

### step 455

you remember there is api named ping

but you cant access it because you allowed only few uri's
if you know.

exercise is to give access to ping with no security

### step 456:

Go to securityfilterchain

```text
package com.kedarnath.security;

import com.kedarnath.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/customers",
                                "api/v1/auth/login"
                        )
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/ping"
                        )
                        .permitAll()  // Allow unauthenticated access to this endpoint
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless session management
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Add JWT filter
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}

```

push it and deploy and try to access it using postman.

remember you should try to access it from url of elk environmnet

### step 457:

we have secured our apis now.
next we are going work on react login and registartion pages

### step 458:

we are going to work on login first from next step.

### step 459:

![](backend/src/main/resources/static/images/img_5.png)

we need to attach token to get the access in the headers.

### step 460:

We are going to use React router

refer https://reactrouter.com/en/main

if you are using nextjs you dont need the routing it comes inbuild but as we are using the
react we need that.

Go to https://reactrouter.com/en/main/start/tutorial

copy

```
npm install react-router-dom
```

we dont need the rest

type

```
q
```

in the terminal

and now run the command

after finishing the installation

run

```
npm run dev
```

still the application wont work

### step 461:

go to frontend

Update main.jsx:

```text
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";

const {ToastContainer} = createStandaloneToast()

const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <h1>Login Page</h1>
        },
        {
            path: "dashboard",
            element: <App/>
        }
    ]
)
createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <RouterProvider router={router}/>
            <ToastContainer/>
        </ChakraProvider>
    </StrictMode>,
)

```

### step 462:

remember do it using refractor
made many changes in the components directory create a customer directory and move the customer named
files into it
rename the card.jsx to ```CustomerCard.jsx```
create a directory with name login and create a javascript file named login.jsx

```text
import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image,
} from '@chakra-ui/react'

const Login = () => {
    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'}>Sign in to your account</Heading>
                    <FormControl id="email">
                        <FormLabel>Email address</FormLabel>
                        <Input type="email"/>
                    </FormControl>
                    <FormControl id="password">
                        <FormLabel>Password</FormLabel>
                        <Input type="password"/>
                    </FormControl>
                    <Stack spacing={6}>
                        <Stack
                            direction={{base: 'column', sm: 'row'}}
                            align={'start'}
                            justify={'space-between'}>
                            <Checkbox>Remember me</Checkbox>
                            <Text color={'blue.500'}>Forgot password?</Text>
                        </Stack>
                        <Button colorScheme={'blue'} variant={'solid'}>
                            Sign in
                        </Button>
                    </Stack>
                </Stack>
            </Flex>
            <Flex flex={1}>
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;
```

open main.jsx

```text
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";

const {ToastContainer} = createStandaloneToast()

const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <Login/>
        },
        {
            path: "dashboard",
            element: <App/>
        }
    ]
)
createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <RouterProvider router={router}/>
            <ToastContainer/>
        </ChakraProvider>
    </StrictMode>,
)

```

### step 463:

update the login.jsx

```
import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link,
} from '@chakra-ui/react'

const Login = () => {
    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'}>Sign in to your account</Heading>
                    <FormControl id="email">
                        <FormLabel>Email address</FormLabel>
                        <Input type="email"/>
                    </FormControl>
                    <FormControl id="password">
                        <FormLabel>Password</FormLabel>
                        <Input type="password"/>
                    </FormControl>
                    <Stack spacing={6}>
                        <Stack
                            direction={{base: 'column', sm: 'row'}}
                            align={'start'}
                            justify={'space-between'}>
                            <Checkbox>Remember me</Checkbox>
                            <Text color={'blue.500'}>Forgot password?</Text>
                        </Stack>
                        <Button colorScheme={'blue'} variant={'solid'}>
                            Sign in
                        </Button>
                    </Stack>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyItems={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enrol Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;
```

### step 464:

update login by adding formik

```text
import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link,
} from '@chakra-ui/react';
import {Formik} from "formik";
import * as Yup from 'yup';

const LoginForm = () => {
    return (
        <Formik
            validateOnMount={true}
            validationSchema={
            Yup.object({
                username: Yup.string().email("Must be Valid email").required("Email is required"),
                password:Yup.string()
                    .max(20,"Password cannot be more than 20 characters")
                    .required("Password is required")
            })}
            initialValues={{username:'', password:''}}
            onSubmit={(values) => {
                alert(JSON.stringify(values, null, 0));
            }}>
        </Formik>
    );
};




const Login = () => {
    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'}>Sign in to your account</Heading>
                    <LoginForm/>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyItems={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;
```

### step 465:

update login form by adding form and linking the elements.
added brand icon on top of the form.
I left empty add something when you get a chance.

```text
import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link, Box, Alert, AlertIcon,
} from '@chakra-ui/react';
import {Formik, Form, useField} from "formik";
import * as Yup from 'yup';

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const LoginForm = () => {
    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string().email("Must be Valid email").required("Email is required"),
                    password: Yup.string()
                        .max(20, "Password cannot be more than 20 characters")
                        .required("Password is required")
                })}
            initialValues={{username: '', password: ''}}
            onSubmit={(values) => {
                alert(JSON.stringify(values, null, 0));
            }}>
            {({isValid, isSubmitting}) => (
                <Form>
                    <Stack spacing={15}>
                        <MyTextInput
                            label={"Email"}
                            name={"username"}
                            type={"email"}
                            placeholder={"Enter your username"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"Enter your Password"}
                        />
                        <Button
                            type={"submit"}
                            disabled={!isValid || isSubmitting}>
                            Login
                        </Button>
                    </Stack>
                </Form>)
            }
        </Formik>
    );
};


const Login = () => {
    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={""}
                        boxSize={"200px"}
                        alt={"Kedarnath Logo"}

                    />
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm/>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;
```

### step 466:

when we enter id and password we get a pop up of the details entered

now lets create a service that will to send these details to server

add the method login to client.js

```text
import axios from 'axios';

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`);
    } catch (e) {
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer // <-- move this inside the axios.post() function call
        );
    } catch (e) {
        throw e;
    }
}
export const deleteCustomer = async (id) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`
        );
    } catch (e) {
        throw e;
    }
}
export const updateCustomer = async (id, update) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            update
        );
    } catch (e) {
        throw e;
    }
}
//new method
export const login = async (usernameAndPassword) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`,
            usernameAndPassword
        );
    } catch (e) {
        throw e;
    }
}


```

### step 467:

![](backend/src/main/resources/static/images/img_6.png)
context will be responsible for having user details and function to login and to check
whether the user is authenticated or not

refer https://react.dev/learn/passing-data-deeply-with-context

### step 468:

create a directory and file frontend/react/src/components/context/AuthContext.jsx

```jsx
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    //TODO: save the token
                    setCustomer({
                        ...res.data.customerDTO
                    })
                    resolve();
                }
            ).catch(err => {
                reject(err);
            })
        })
    }
    return (
        <AuthContext.Provider value={{
            customer,
            login
        }}>

        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

### step 469:

we are going to add the Authprovider on top of the routing routes

let go to main and update the code

```
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";

const {ToastContainer} = createStandaloneToast()

const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <Login/>
        },
        {
            path: "dashboard",
            element: <App/>
        }
    ]
)
createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <AuthProvider>
                <RouterProvider router={router}/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </StrictMode>,
)

```

use the auth method we created in the authcontext in login.jsx and also add
the error notification.
Login.jsx

```
import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link, Box, Alert, AlertIcon,
} from '@chakra-ui/react';
import {Formik, Form, useField} from "formik";
import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const LoginForm = () => {
    const {login} = useAuth();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string().email("Must be Valid email").required("Email is required"),
                    password: Yup.string()
                        .max(20, "Password cannot be more than 20 characters")
                        .required("Password is required")
                })}
            initialValues={{username: '', password: ''}}
            onSubmit={(values, {setSubmitting}) => {
                // alert(JSON.stringify(values, null, 0));
                setSubmitting(true);
                login(values).then(res => {
                    // TODO: navigate to dahsboard
                    console.log("Success login", res);
                }).catch(err => {
                    errorNotification(
                        err.code,
                        err.response.data.message
                    )
                }).finally(() => {
                    setSubmitting(false);
                })
            }}>
            {({isValid, isSubmitting}) => (
                <Form>
                    <Stack spacing={15}>
                        <MyTextInput
                            label={"Email"}
                            name={"username"}
                            type={"email"}
                            placeholder={"Enter your username"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"Enter your Password"}
                        />
                        <Button
                            type={"submit"}
                            disabled={!isValid || isSubmitting}>
                            Login
                        </Button>
                    </Stack>
                </Form>)
            }
        </Formik>
    );
};


const Login = () => {


    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={""}
                        boxSize={"200px"}
                        alt={"Kedarnath Logo"}

                    />
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm/>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;
```

### step 470 made a mistake in AuthContext.jsx as its not rendering the children

update the AuthContext.jsx:

```jsx
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    console.log(jwtToken);
                    setCustomer({
                        ...res.data.customerDTO
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }

    return (
        <AuthContext.Provider value={{customer, login}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

try to login with right password and wrong password.

### step 471:

we have to store the token in local storage with less expiration time .
how to check the local storage

got to developer tools under applications-> storage -> you will find key and a value

now you only the chakra ui color mode value

lets do save the token in local storage in next step

### step 472:

save the token to local storage

```jsx
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    //TODO: save token
                    localStorage.setItem("access_token", jwtToken);
                    console.log(jwtToken);
                    setCustomer({
                        ...res.data.customerDTO
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }

    return (
        <AuthContext.Provider value={{customer, login}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

### step 473:

refr https://reactrouter.com/en/main/hooks/use-navigate

```jsx
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    localStorage.setItem("access_token", jwtToken);
                    console.log(jwtToken);
                    setCustomer({
                        ...res.data.customerDTO
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }

    return (
        <AuthContext.Provider value={{customer, login}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

### step 474:

update the client js attaching the token on each request

```js
import axios from 'axios';

const getAuthConfig = () => (
    {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`
        }
    }
)
export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            getAuthConfig()
        );
    } catch (e) {
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer // <-- move this inside the axios.post() function call
        );
    } catch (e) {
        throw e;
    }
}
export const deleteCustomer = async (id) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            getAuthConfig()
        );
    } catch (e) {
        throw e;
    }
}
export const updateCustomer = async (id, update) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            update,
            getAuthConfig()
        );
    } catch (e) {
        throw e;
    }
}

export const login = async (usernameAndPassword) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`,
            usernameAndPassword
        );
    } catch (e) {
        throw e;
    }
}


```

### step 475:

in registration form we got no option for password lets do it
CreateCustomerForm.jsx

```textimport {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({fetchCustomers, onClose}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                    password: ''
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    password: Yup.string()
                        .min(7, 'Password should be 7 characters or more')
                        .max(20, 'Password should be 20 characters or less')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(['MALE', 'FEMALE'], 'Invalid gender')
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            successNotification(
                                "Customer Saved",
                                `${customer.name} was successfully saved`
                            );
                            fetchCustomers(); // Refresh customer list
                            onClose(); // Close the drawer upon success
                        })
                        .catch(err => {
                            errorNotification(
                                err.code,
                                err.response.data.message
                            );
                        })
                        .finally(() => {
                            setSubmitting(false);
                        });
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing="24px">
                            <MyTextInput label="Name" name="name" type="text" placeholder="Enter your name"/>
                            <MyTextInput label="Email" name="email" type="text" placeholder="Enter your email"/>
                            <MyTextInput label="Age" name="age" type="number" placeholder="16"/>
                            <MyTextInput label="Password" name="password" type="password"
                                         placeholder={"pick a secured password"}/>
                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>
                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;

```

### step 476

No ability of logout within context we logout component.

add a logout component:

```
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    localStorage.setItem("access_token", jwtToken);
                    console.log(jwtToken);
                    setCustomer({
                        ...res.data.customerDTO
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }
    const logout = async () => {
        localStorage.removeItem("access_token")
        setCustomer(null)
    }


    return (
        <AuthContext.Provider value={{customer, login, logout}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

update sidebar by ading onclick event on sigout button

```jsx
'use client'

import {
    IconButton,
    Avatar,
    Box,
    CloseButton,
    Flex,
    HStack,
    VStack,
    Icon,
    useColorModeValue,
    Text,
    Drawer,
    DrawerContent,
    useDisclosure,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList, Image,
} from '@chakra-ui/react'
import {
    FiHome,
    FiTrendingUp,
    FiCompass,
    FiStar,
    FiSettings,
    FiMenu,
    FiBell,
    FiChevronDown,
} from 'react-icons/fi'
import {useAuth} from "../context/AuthContext.jsx";

const LinkItems = [
    {name: 'Home', icon: FiHome},
    {name: 'Trending', icon: FiTrendingUp},
    {name: 'Explore', icon: FiCompass},
    {name: 'Favourites', icon: FiStar},
    {name: 'Settings', icon: FiSettings},
]

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex h="20" flexDirection="column" alignItems="center" mx="8" mb={70} mt={2}
                  justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold" mb={5}>
                    Dashboard
                </Text>
                <Image
                    borderRadius='full'
                    boxSize='75px'
                    src='https://bit.ly/dan-abramov'
                    alt='Dan Abramov'
                />
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, children, ...rest}) => {
    return (
        <Box
            as="a"
            href="#"
            style={{textDecoration: 'none'}}
            _focus={{boxShadow: 'none'}}>
            <Flex
                align="center"
                p="4"
                mx="4"
                borderRadius="lg"
                role="group"
                cursor="pointer"
                _hover={{
                    bg: 'red.400',
                    color: 'white',
                }}
                {...rest}>
                {icon && (
                    <Icon
                        mr="4"
                        fontSize="16"
                        _groupHover={{
                            color: 'white',
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        </Box>
    )
}

const MobileNav = ({onOpen, ...rest}) => {
    const {logout} = useAuth();
    return (
        <Flex
            ml={{base: 0, md: 60}}
            px={{base: 4, md: 4}}
            height="20"
            alignItems="center"
            bg={useColorModeValue('white', 'gray.900')}
            borderBottomWidth="1px"
            borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            {...rest}>
            <IconButton
                display={{base: 'flex', md: 'none'}}
                onClick={onOpen}
                variant="outline"
                aria-label="open menu"
                icon={<FiMenu/>}
            />

            <Text
                display={{base: 'flex', md: 'none'}}
                fontSize="2xl"
                fontFamily="monospace"
                fontWeight="bold">
                Logo
            </Text>

            <HStack spacing={{base: '0', md: '6'}}>
                <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                            <HStack>
                                <Avatar
                                    size={'sm'}
                                    src={
                                        'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                    }
                                />
                                <VStack
                                    display={{base: 'none', md: 'flex'}}
                                    alignItems="flex-start"
                                    spacing="1px"
                                    ml="2">
                                    <Text fontSize="sm">Justina Clark</Text>
                                    <Text fontSize="xs" color="gray.600">
                                        Admin
                                    </Text>
                                </VStack>
                                <Box display={{base: 'none', md: 'flex'}}>
                                    <FiChevronDown/>
                                </Box>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'gray.900')}
                            borderColor={useColorModeValue('gray.200', 'gray.700')}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem>Billing</MenuItem>
                            <MenuDivider/>
                            <MenuItem onClick={logout}>Sign out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}

const SidebarWithHeader = ({children}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen}
                placement="left"
                onClose={onClose}
                returnFocusOnClose={false}
                onOverlayClick={onClose}
                size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            {/* mobilenav */}
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">
                {children} {/* Content */}
            </Box>
        </Box>
    )
}

export default SidebarWithHeader

```

page not getting refreshed will fix it soon. next ste is to fix the sign out button.

### step 477:

in the side bar we have added the user name and role

but it breaks sometime due to null values so fixed it by using ? like customer?.name

```jsx
'use client'

import {
    IconButton,
    Avatar,
    Box,
    CloseButton,
    Flex,
    HStack,
    VStack,
    Icon,
    useColorModeValue,
    Text,
    Drawer,
    DrawerContent,
    useDisclosure,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList, Image,
} from '@chakra-ui/react'
import {
    FiHome,
    FiTrendingUp,
    FiCompass,
    FiStar,
    FiSettings,
    FiMenu,
    FiBell,
    FiChevronDown,
} from 'react-icons/fi'
import {useAuth} from "../context/AuthContext.jsx";

const LinkItems = [
    {name: 'Home', icon: FiHome},
    {name: 'Trending', icon: FiTrendingUp},
    {name: 'Explore', icon: FiCompass},
    {name: 'Favourites', icon: FiStar},
    {name: 'Settings', icon: FiSettings},
]

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex h="20" flexDirection="column" alignItems="center" mx="8" mb={70} mt={2}
                  justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold" mb={5}>
                    Dashboard
                </Text>
                <Image
                    borderRadius='full'
                    boxSize='75px'
                    src='https://bit.ly/dan-abramov'
                    alt='Dan Abramov'
                />
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, children, ...rest}) => {
    return (
        <Box
            as="a"
            href="#"
            style={{textDecoration: 'none'}}
            _focus={{boxShadow: 'none'}}>
            <Flex
                align="center"
                p="4"
                mx="4"
                borderRadius="lg"
                role="group"
                cursor="pointer"
                _hover={{
                    bg: 'red.400',
                    color: 'white',
                }}
                {...rest}>
                {icon && (
                    <Icon
                        mr="4"
                        fontSize="16"
                        _groupHover={{
                            color: 'white',
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        </Box>
    )
}

const MobileNav = ({onOpen, ...rest}) => {
    const {logout, customer} = useAuth();
    return (
        <Flex
            ml={{base: 0, md: 60}}
            px={{base: 4, md: 4}}
            height="20"
            alignItems="center"
            bg={useColorModeValue('white', 'gray.900')}
            borderBottomWidth="1px"
            borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            {...rest}>
            <IconButton
                display={{base: 'flex', md: 'none'}}
                onClick={onOpen}
                variant="outline"
                aria-label="open menu"
                icon={<FiMenu/>}
            />

            <Text
                display={{base: 'flex', md: 'none'}}
                fontSize="2xl"
                fontFamily="monospace"
                fontWeight="bold">
                Logo
            </Text>

            <HStack spacing={{base: '0', md: '6'}}>
                <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                            <HStack>
                                <Avatar
                                    size={'sm'}
                                    src={
                                        'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                    }
                                />
                                <VStack
                                    display={{base: 'none', md: 'flex'}}
                                    alignItems="flex-start"
                                    spacing="1px"
                                    ml="2">
                                    <Text fontSize="sm">{customer?.name}-{customer?.email}</Text>
                                    {customer?.roles.map((role, id) => (
                                        <Text id={id} fontSize="xs" color="gray.600">
                                            {role}
                                        </Text>
                                    ))}

                                </VStack>
                                <Box display={{base: 'none', md: 'flex'}}>
                                    <FiChevronDown/>
                                </Box>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'gray.900')}
                            borderColor={useColorModeValue('gray.200', 'gray.700')}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem>Billing</MenuItem>
                            <MenuDivider/>
                            <MenuItem onClick={logout}>Sign out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}

const SidebarWithHeader = ({children}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen}
                placement="left"
                onClose={onClose}
                returnFocusOnClose={false}
                onOverlayClick={onClose}
                size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            {/* mobilenav */}
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">
                {children} {/* Content */}
            </Box>
        </Box>
    )
}

export default SidebarWithHeader

```

### step 478:

![](backend/src/main/resources/static/images/img_6.png)

we are going add protected route

create a file under shared
ProtectedRoute.jsx

```text
import {useEffect} from "react";
import {useNavigate} from "react-router-dom";

const ProtectedRoute = ({children}) => {
    const isCustomerAuthenticated = false; // based on this condition it will go to login we need to add function here
    const navigate = useNavigate();

    useEffect(() => {
        if (!isCustomerAuthenticated) {
            navigate("/");
        }
    })
    return isCustomerAuthenticated ? children : "";
}

export default ProtectedRoute;
```

add this in main.jsx:

```text
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./services/ProtectedRoute.jsx";

const {ToastContainer} = createStandaloneToast()

const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <Login/>
        },
        {
            path: "dashboard",
            element: <ProtectedRoute><App/></ProtectedRoute>
        }
    ]
)
createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <AuthProvider>
                <RouterProvider router={router}/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </StrictMode>,
)

```

### step 479:

refer
https://github.com/auth0/jwt-decode
https://www.npmjs.com/package/jwt-decode

we also need to check whether the token is expired for that we need thid decoder

```
npm install jwt-decode
```

add the method in AuthCOntext

```text
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";
import {jwtDecode} from "jwt-decode";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    localStorage.setItem("access_token", jwtToken);
                    console.log(jwtToken);
                    setCustomer({
                        ...res.data.customerDTO
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }
    const logout = () => {
        localStorage.removeItem("access_token")
        setCustomer(null)
    }

    const isCustomerAuthenticated = () => {
        const token = localStorage.getItem("access_token");
        if (!token) {
            return false;
        }
        const {exp: expiration} = jwtDecode(token);
        if (Date.now() > expiration * 1000) {
            logout()
            return false;
        }

        return true;

    }


    return (
        <AuthContext.Provider value={{customer, login, logout, isCustomerAuthenticated}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

Add this new method to protectedRoute:

```text
import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../components/context/AuthContext.jsx";

const ProtectedRoute = ({children}) => {
    const {isCustomerAuthenticated} = useAuth();
    const navigate = useNavigate();


    useEffect(() => {
        if (!isCustomerAuthenticated()) {
            navigate("/");
        }
    })
    return isCustomerAuthenticated() ? children : "";
}

export default ProtectedRoute;
```

### step 480:

Lets test the functionality

some issues in the console

fixed it:

```text
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";
import {jwtDecode} from "jwt-decode";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    useEffect(() => {
        let token = localStorage.getItem("access_token");
        if (token) {
            token = jwtDecode(token);
            setCustomer({
                username: token.sub,
                roles: token.scopes
            })
        }
    }, [])

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    localStorage.setItem("access_token", jwtToken);
                    console.log(jwtToken);
                    const decodeToken = jwtDecode(jwtToken);
                    setCustomer({
                        username: decodeToken.sub,
                        roles: decodeToken.scopes
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }
    const logout = () => {
        localStorage.removeItem("access_token")
        setCustomer(null)
    }

    const isCustomerAuthenticated = () => {
        const token = localStorage.getItem("access_token");
        if (!token) {
            return false;
        }
        const {exp: expiration} = jwtDecode(token);
        if (Date.now() > expiration * 1000) {
            logout()
            return false;
        }

        return true;

    }


    return (
        <AuthContext.Provider value={{customer, login, logout, isCustomerAuthenticated}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

Sidebar.jsx

```
'use client'

import {
    IconButton,
    Avatar,
    Box,
    CloseButton,
    Flex,
    HStack,
    VStack,
    Icon,
    useColorModeValue,
    Text,
    Drawer,
    DrawerContent,
    useDisclosure,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList, Image,
} from '@chakra-ui/react'
import {
    FiHome,
    FiTrendingUp,
    FiCompass,
    FiStar,
    FiSettings,
    FiMenu,
    FiBell,
    FiChevronDown,
} from 'react-icons/fi'
import {useAuth} from "../context/AuthContext.jsx";

const LinkItems = [
    {name: 'Home', icon: FiHome},
    {name: 'Trending', icon: FiTrendingUp},
    {name: 'Explore', icon: FiCompass},
    {name: 'Favourites', icon: FiStar},
    {name: 'Settings', icon: FiSettings},
]

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex h="20" flexDirection="column" alignItems="center" mx="8" mb={70} mt={2}
                  justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold" mb={5}>
                    Dashboard
                </Text>
                <Image
                    borderRadius='full'
                    boxSize='75px'
                    src='https://bit.ly/dan-abramov'
                    alt='Dan Abramov'
                />
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, children, ...rest}) => {
    return (
        <Box
            as="a"
            href="#"
            style={{textDecoration: 'none'}}
            _focus={{boxShadow: 'none'}}>
            <Flex
                align="center"
                p="4"
                mx="4"
                borderRadius="lg"
                role="group"
                cursor="pointer"
                _hover={{
                    bg: 'red.400',
                    color: 'white',
                }}
                {...rest}>
                {icon && (
                    <Icon
                        mr="4"
                        fontSize="16"
                        _groupHover={{
                            color: 'white',
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        </Box>
    )
}

const MobileNav = ({onOpen, ...rest}) => {
    const {logout, customer} = useAuth();
    return (
        <Flex
            ml={{base: 0, md: 60}}
            px={{base: 4, md: 4}}
            height="20"
            alignItems="center"
            bg={useColorModeValue('white', 'gray.900')}
            borderBottomWidth="1px"
            borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            {...rest}>
            <IconButton
                display={{base: 'flex', md: 'none'}}
                onClick={onOpen}
                variant="outline"
                aria-label="open menu"
                icon={<FiMenu/>}
            />

            <Text
                display={{base: 'flex', md: 'none'}}
                fontSize="2xl"
                fontFamily="monospace"
                fontWeight="bold">
                Logo
            </Text>

            <HStack spacing={{base: '0', md: '6'}}>
                <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                            <HStack>
                                <Avatar
                                    size={'sm'}
                                    src={
                                        'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                    }
                                />
                                <VStack
                                    display={{base: 'none', md: 'flex'}}
                                    alignItems="flex-start"
                                    spacing="1px"
                                    ml="2">
                                    <Text fontSize="sm">{customer?.username}</Text>
                                    {customer?.roles.map((role, id) => (
                                        <Text key={id} fontSize="xs" color="gray.600">
                                            {role}
                                        </Text>
                                    ))}

                                </VStack>
                                <Box display={{base: 'none', md: 'flex'}}>
                                    <FiChevronDown/>
                                </Box>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'gray.900')}
                            borderColor={useColorModeValue('gray.200', 'gray.700')}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem>Billing</MenuItem>
                            <MenuDivider/>
                            <MenuItem onClick={logout}>Sign out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}

const SidebarWithHeader = ({children}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen}
                placement="left"
                onClose={onClose}
                returnFocusOnClose={false}
                onOverlayClick={onClose}
                size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            {/* mobilenav */}
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">
                {children} {/* Content */}
            </Box>
        </Box>
    )
}

export default SidebarWithHeader

```

```text
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/customer/CustomerCard.jsx";
import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

const App = () => {

    const [customers, setCustomers] = useState([]); // Initialize as an array
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");

    const fetchCustomers = () => {
        setLoading(true);
        getCustomers().then(res => {
            // console.log('API Response:', res.data);  // Debugging the response

            // Ensure the response is an array, or default to an empty array
            if (Array.isArray(res.data)) {
                setCustomers(res.data);  // Set customers only if it's an array
                setError("");  // Reset the error state if the fetch is successful
            } else {
                console.error("Unexpected response format:", res.data);
                setCustomers([]);  // Fallback to an empty array if response is invalid
                setError("Unexpected data format from API");
            }
        }).catch((err) => {
            console.error("Error fetching customers:", err);  // Log error for debugging
            setError(err?.response?.data?.message || "An error occurred while fetching customers");
            errorNotification(
                err.code,
                err?.response?.data?.message || "An error occurred"
            );
        })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchCustomers();
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (err) {
        console.log('Current error:', err);  // Log the error to understand its value
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Text mt={5}>Oops, there was an error: {err}</Text>
            </SidebarWithHeader>
        );
    }

    if (!Array.isArray(customers) || customers.length === 0) {
        // console.log('Customers array is empty or not an array:', customers);  // Log for debugging
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Text mt={5}>No customers available.</Text>
            </SidebarWithHeader>
        );
    }

    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
            <Wrap justify="center" spacing="30px">
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <CardWithImage
                            {...customer}
                            imageNumber={index}
                            fetchCustomers={fetchCustomers}
                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
};

export default App;

```

### step 481

if logged in should not be accessing the login page again so lets fix it

```
import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link, Box, Alert, AlertIcon,
} from '@chakra-ui/react';
import {Formik, Form, useField} from "formik";
import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/notification.js";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const LoginForm = () => {
    const {login} = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string().email("Must be Valid email").required("Email is required"),
                    password: Yup.string()
                        .max(20, "Password cannot be more than 20 characters")
                        .required("Password is required")
                })}
            initialValues={{username: '', password: ''}}
            onSubmit={(values, {setSubmitting}) => {
                // alert(JSON.stringify(values, null, 0));
                setSubmitting(true);
                login(values).then(res => {
                    // TODO: navigate to dahsboard
                    navigate("/dashboard");
                    console.log("Successfully  logged in");
                }).catch(err => {
                    errorNotification(
                        err.code,
                        err.response.data.message
                    )
                }).finally(() => {
                    setSubmitting(false);
                })
            }}>
            {({isValid, isSubmitting}) => (
                <Form>
                    <Stack spacing={15}>
                        <MyTextInput
                            label={"Email"}
                            name={"username"}
                            type={"email"}
                            placeholder={"Enter your username"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"Enter your Password"}
                        />
                        <Button
                            type={"submit"}
                            disabled={!isValid || isSubmitting}>
                            Login
                        </Button>
                    </Stack>
                </Form>)
            }
        </Formik>
    );
};


const Login = () => {
    const {customer} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={""}
                        boxSize={"200px"}
                        alt={"Kedarnath Logo"}

                    />
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm/>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;
```

### step 482:

when the token gets expired it should automatically redirect to login

to test this go to backend in the below change the expiration time to 30 seconds

JWTUtil.java

```text
package com.kedarnath.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY =
            "king_123456898_king_123456898king_123456898king_123456898king_123456898king_123456898";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        String token = Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .issuer("https://kedarnath.com")
                .expiration(Date.from(now.plus(30, ChronoUnit.SECONDS)))
                .claims(claims) // Keep claims map
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
        return token;
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        // Use parserBuilder instead of the deprecated parser()
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Signing key is used to verify the JWT signature
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the JWT and retrieve claims
                .getBody();  // Return the Claims object
    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}
```

update the main.java abit

```text
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
            String email = firstName + "." + lastName + "@kedarnath.com";
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    email,
                    passwordEncoder.encode("password"),
                    age,
                    gender);
            customerRepository.save(customer);
            System.out.println(email);
        };
    }


}


```

now restart the server and test it. it should work but you need refresh manually.

now change the JWTUtil.java back to normal:

```text
package com.kedarnath.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY =
            "king_123456898_king_123456898king_123456898king_123456898king_123456898king_123456898";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        String token = Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .issuer("https://kedarnath.com")
                .expiration(Date.from(now.plus(15, ChronoUnit.DAYS)))
                .claims(claims) // Keep claims map
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
        return token;
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        // Use parserBuilder instead of the deprecated parser()
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Signing key is used to verify the JWT signature
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the JWT and retrieve claims
                .getBody();  // Return the Claims object
    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}
```

### step 483:

now let's deploy it on aws
![](backend/src/main/resources/static/images/img_7.png)
Everything should work fine even in aws

### step 484:

exercise is to build a registration form for customer

### step 485

Create a folder named signup under components

create a file named Signup.jsx
Signup.jsx

```text
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {Flex, Heading, Image, Link, Stack, Text} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const Signup = () => {
    const {customer} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={""}
                        boxSize={"200px"}
                        alt={"Kedarnath Logo"}

                    />
                    <Heading fontSize={'2xl'} mb={15}>Register for an account</Heading>
                    <CreateCustomerForm/>
                    <Link color={"blue.500"} href={"/"}>
                        Have an account? Login now.
                    </Link>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )

}
export default Signup;
```

Update CreateCustomerForm.jsx

```text
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({fetchCustomers, onClose}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                    password: ''
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    password: Yup.string()
                        .min(7, 'Password should be 7 characters or more')
                        .max(20, 'Password should be 20 characters or less')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(['MALE', 'FEMALE'], 'Invalid gender')
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            successNotification(
                                "Customer Saved",
                                `${customer.name} was successfully saved`
                            );
                            fetchCustomers && fetchCustomers(); // Refresh customer list
                            onClose(); // Close the drawer upon success
                        })
                        .catch(err => {
                            errorNotification(
                                err.code,
                                err.response.data.message
                            );
                        })
                        .finally(() => {
                            setSubmitting(false);
                        });
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing="24px">
                            <MyTextInput label="Name" name="name" type="text" placeholder="Enter your name"/>
                            <MyTextInput label="Email" name="email" type="text" placeholder="Enter your email"/>
                            <MyTextInput label="Age" name="age" type="number" placeholder="16"/>
                            <MyTextInput label="Password" name="password" type="password"
                                         placeholder={"pick a secured password"}/>
                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>
                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

Update main.jsx for the route

```text
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'
import {createStandaloneToast} from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./services/ProtectedRoute.jsx";
import Signup from "./components/signup/Signup";

const {ToastContainer} = createStandaloneToast()

const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <Login/>
        },
        {
            path: "/signup",
            element: <Signup/>
        },
        {
            path: "dashboard",
            element: <ProtectedRoute><App/></ProtectedRoute>
        }
    ]
)
createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <AuthProvider>
                <RouterProvider router={router}/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </StrictMode>,
)

```

in the dashboard page on the right corner the details of the customer will be mising lets fix it

### step 486:

in AuthContext.jsx extract the set customer to method and export it

```text
import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";
import {jwtDecode} from "jwt-decode";


const AuthContext = createContext({});

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null);

    const setCustomerFromToken = () => {
        let token = localStorage.getItem("access_token");
        if (token) {
            token = jwtDecode(token);
            setCustomer({
                username: token.sub,
                roles: token.scopes
            })
        }
    }

    useEffect(() => {
        setCustomerFromToken();
    }, [])

    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(
                res => {
                    const jwtToken = res.headers["authorization"];
                    localStorage.setItem("access_token", jwtToken);
                    // console.log(jwtToken);
                    const decodeToken = jwtDecode(jwtToken);
                    setCustomer({
                        username: decodeToken.sub,
                        roles: decodeToken.scopes
                    })
                    resolve(res);
                }
            ).catch(err => {
                reject(err);
            })
        })
    }
    const logout = () => {
        localStorage.removeItem("access_token")
        setCustomer(null)
    }

    const isCustomerAuthenticated = () => {
        const token = localStorage.getItem("access_token");
        if (!token) {
            return false;
        }
        const {exp: expiration} = jwtDecode(token);
        if (Date.now() > expiration * 1000) {
            logout()
            return false;
        }

        return true;

    }


    return (
        <AuthContext.Provider value={{customer, login, logout, isCustomerAuthenticated, setCustomerFromToken}}>
            {children} {/* Ensure children are rendered */}
        </AuthContext.Provider>
    );
}


export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
```

update CreateCustomerForm with onSuccess()

```text
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({onSuccess}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                    password: ''
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    password: Yup.string()
                        .min(7, 'Password should be 7 characters or more')
                        .max(20, 'Password should be 20 characters or less')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(['MALE', 'FEMALE'], 'Invalid gender')
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            successNotification(
                                "Customer Saved",
                                `${customer.name} was successfully saved`
                            );
                            onSuccess(res.headers["authorization"]);
                        })
                        .catch(err => {
                            errorNotification(
                                err.code,
                                err.response.data.message
                            );
                        })
                        .finally(() => {
                            setSubmitting(false);
                        });
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing="24px">
                            <MyTextInput label="Name" name="name" type="text" placeholder="Enter your name"/>
                            <MyTextInput label="Email" name="email" type="text" placeholder="Enter your email"/>
                            <MyTextInput label="Age" name="age" type="number" placeholder="16"/>
                            <MyTextInput label="Password" name="password" type="password"
                                         placeholder={"pick a secured password"}/>
                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>
                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

update CreateCustomerDrawer with onSuccess()

```
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const AddIcon = () => "+";
const CloseIcon = () => "X";

const CreateCustomerDrawer = ({fetchCustomers}) => {
    const {isOpen, onOpen, onClose} = useDisclosure();

    return (
        <>
            <Button leftIcon={<AddIcon/>} colorScheme="teal" onClick={onOpen}>
                Create Customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size="xl">
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create New Customer</DrawerHeader>
                    <DrawerBody>
                        <CreateCustomerForm onSuccess={fetchCustomers}
                        />
                    </DrawerBody>
                    <DrawerFooter>
                        <Button leftIcon={<CloseIcon/>} colorScheme="teal" onClick={onClose}>
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    );
};


export default CreateCustomerDrawer;
```

last Signup.jsx

```text
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {Flex, Heading, Image, Link, Stack, Text} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const Signup = () => {
    const {customer, setCustomerFromToken} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={""}
                        boxSize={"200px"}
                        alt={"Kedarnath Logo"}

                    />
                    <Heading fontSize={'2xl'} mb={15}>Register for an account</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
                        localStorage.setItem("access_token", token)
                        setCustomerFromToken()
                        navigate("/dashboard");
                    }}/>
                    <Link color={"blue.500"} href={"/"}>
                        Have an account? Login now.
                    </Link>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link href={"#"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )

}
export default Signup;
```

Next step is to deploy on elastic bean stalk

### step 487:

commit all the changes with the commit message

```text
Registration Solution - React
```

### step 488:

only one instance cant handle all the requests you might need more ec2 to handle the request.

### step 489:

![](backend/src/main/resources/static/images/img_8.png)

above represents the things we have done so far
![](backend/src/main/resources/static/images/img_9.png)
we are going to use amazon amplify to deploy angular or react
the request will be sent to load balancer

we can configure in a way that if there is 50% usage in one ec2 then the load balancer could
add one more ec2 and so on. if one fails it stops sending the traffic to that instance.

### step 490:

elastic load balancing
Refer : https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html

### step 491:

we are going to create a load balancer in a public subnet

if we go to the current environment and try to add a load balancer the problem is it

will create a elb - elastic load balancer which is version 1 which is going to get stopped soon

we have to create using alb - application load balancer thats why we are
going to create a new environment with alb and delete the existing one

### step 492:

click create new environment

select web server environment

application name: kedarnath-api

Environment name: kedarnath-api

domain name: kedarnath-api

platform: Docker
branch : ecs running 0n 64bit amazon linux 2
version: recommended

Application code:--upload your code--
version label: v1
upload your code->local file->

you need to upload Dockerrun.aws.json

update the Dockerrun.aws.json from

```text
{
  "AWSEBDockerrunVersion": 2,
  "containerDefinitions": [
    {
      "name": "kedarnath-react",
      "image": "dkedarnath/kedarnath-react:latest",
      "essential": true,
      "memory": 256,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 5173
        }
      ],
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    },
    {
      "name": "kedarnath-api",
      "image": "dkedarnath/kedarnath-api:15.10.2024.12.33.00",
      "essential": true,
      "memory": 512,
      "portMappings": [
        {
          "hostPort": 8080,
          "containerPort": 8080
        }
      ],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://awseb-e-nskvm2faru-stack-awsebrdsdatabase-xn3ngsekufx0.c1qawm2oqlx3.us-east-1.rds.amazonaws.com:5432/customer"
        }
      ],
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

Update to below by removing the front end we will change the rds url after deploying this

```text
{
  "AWSEBDockerrunVersion": 2,
  "containerDefinitions": [
       {
      "name": "kedarnath-api",
      "image": "dkedarnath/kedarnath-api:15.10.2024.12.33.00",
      "essential": true,
      "memory": 512,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 8080
        }
      ],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://awseb-e-nskvm2faru-stack-awsebrdsdatabase-xn3ngsekufx0.c1qawm2oqlx3.us-east-1.rds.amazonaws.com:5432/customer"
        }
      ],
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

RDS url needs to be updated we will do it after creating the environment
upload this file now

click next

Presets: custom configuration

In Instances:
```Security group```:-
select default

In capacity:

```Max instances```: 2

```Processor```: arm64

```Instance types```: t4g.micro

```Placement```:select all three zones

In Modify rolling updates and deployments:
```Deployment policy```: Rolling

In security select the key pair created earlier
leave the IAM instance profile as it is.

In network:

```Load Balancer subnets```:

1. eu-west-1a
2. eu-west-1b

Instance settings:

1. enable Public ip address
2. Instance subnets :
    1. eu-west-1a
    2. eu-west-1b

Database Settings:

1. Database subnets
    1. eu-west-1a
    2. eu-west-1b

In Database:

1. snapshot: none
2. Database Setttings:
    1. Engine: postgres
    2. Engine version: 13.7 or any latest version
    3. Instance Class: select free tier t3 micro/small
    4. Storage: 40
    5. Username: kedarnath
    6. Password: password
3. Database deletion policy
    1. Select ```Delete```

finished just save it.
wait it gets finished building.

### step 493:

1. The health conditions will be severe.
   ELB health is failing or not available for all instances
2. Go to configuration
    1. copy RDS Endpoint without port
    2. Go to Dockerrun.aws.json--> update the rds url.
3. refer https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.managing.db.html

    ```text
              "value": "jdbc:postgresql://awseb-e-nskvm2faru-stack-awsebrdsdatabase-xn3ngsekufx0.c1qawm2oqlx3.us-east-1.rds.amazonaws.com:5432/ebdb"
    
    ```

4. The default db given by ELB is ebdb. let use it for now.

5. deploy the file on ELB.

6. Eventually we will automate this with git hub actions.

7. wait for some time.

8. we haven't configured the configured the load balancer end point and how to behave.
9. The environment will fail again due to spring security
10. We will fix it in next step

### step 494:

1. Load balancer asking the ec2 instance is it ready to accept the traffic but the ec2 rejecting it with 403 error.
2. Go to Environment-> go to configuration -> navigate to load balancer-> edit
3. you will listener port 80 and processor. Don't make any changes here.
4. Go to ec2 instance-> navigate to load balancer -> click on it
5. Copy the Dns url and paste it in new tab -> you get a message 403.
6. Navigate to listeners-> click on default route column -> Forwarded to starting with aws...
7. You will go to target groups - you will see the target group is unhealthy
8. click on Health checks -> edit -> update the success code to 403.
9. After a while the group becomes healthy
10. After a while environment turns healthy as well
11. We know that is not right just revert the changes back to status code 200.
12. We will fix this in next step

### step 495:

1. Refer https://docs.spring.io/spring-boot/docs/2.2.x/reference/html/production-ready-features.html
2. Actuator helps to monitor your application.
3. Ther are many end points be careful which one are you exposing. Our main concentration is on health.
4. add the dependency to pom.xml
    ```text
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    ```
5. update the SecurityFilterChainConfig.java
   ```text
        package com.kedarnath.security;

    import com.kedarnath.jwt.JWTAuthenticationFilter;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.web.AuthenticationEntryPoint;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    
    @Configuration
    @EnableWebSecurity
    public class SecurityFilterChainConfig {
    
        private final AuthenticationProvider authenticationProvider;
        private final JWTAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationEntryPoint authenticationEntryPoint;
    
        public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
            this.authenticationProvider = authenticationProvider;
            this.jwtAuthenticationFilter = jwtAuthenticationFilter;
            this.authenticationEntryPoint = authenticationEntryPoint;
        }
    
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
                    .authorizeHttpRequests(authorization -> authorization
                            .requestMatchers(HttpMethod.POST,
                                    "/api/v1/customers",
                                    "api/v1/auth/login"
                            )
                            .permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    "/ping"
                            )
                            .permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    "/actuator/**"
                            )
                            .permitAll()  // Allow unauthenticated access to this endpoint
                            .anyRequest().authenticated()  // All other requests require authentication
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless session management
                    )
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Add JWT filter
                    .exceptionHandling(exceptionHandling ->
                            exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
                    );
    
               return http.build();
            }
        }

    ```

6. Test ```http://localhost:8080/actuator```. You will see only few.
7. update the application.yml

    ```yaml
    server:
      port: 8080
      error:
        include-message: always
    
    cors:
      allowed-origins: "*"
      allowed-methods: "*"
      allowed-headers: "*"
      exposed-headers: "*"
    
    management:
      endpoints:
        web:
          exposure:
            include: "health,info"
    
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/customer
        username: kedarnath
        password: password
      jpa:
        hibernate:
          ddl-auto: validate
        properties:
          hibernate:
            dialect: org.hibernate.dialect.PostgreSQLDialect
            format_sql: true
        show_sql: true
      main:
        web-application-type: servlet
    ```

   ```include: "health,info"``` in here instead of health, info you can use * to get all the endpoints.which is not good
   as
   someone can even shutdown as there is a endpoint shutdown.

### step 496: 29th oct 2024 11:02 AM

1. Go to github
2. Go to current project settings
3. Navigate to Secrets and variables.
4. Update the EB-APPLICATION_NAME,EB_ENVIRONMENT_NAME,EB_ENVIRONMENT_URL

FINISHED AT 11:14 aM

### STEP 497: 29TH OCT 11:14 AM

1. you can remove the below code fromDockerrun.aws.json and add it to application.yml.
    ```text  
        "environment": [
            {
              "name": "SPRING_DATASOURCE_URL",
              "value": "jdbc:postgresql://awseb-e-nskvm2faru-stack-awsebrdsdatabase-xn3ngsekufx0.c1qawm2oqlx3.us-east-1.rds.amazonaws.com:5432/ebdb"
            }
          ],
    ```

2. for now we dont want any changes in application.yml so we left the above code as it is but created a new filw
   application-dev.yml and updated in it for the future reference.
    ```yaml
    server:
      port: 8080
      error:
        include-message: always
    
    cors:
      allowed-origins: "*"
      allowed-methods: "*"
      allowed-headers: "*"
      exposed-headers: "*"
    
    management:
      endpoints:
        web:
          exposure:
            include: "health,info"
    
    spring:
      datasource:
    #    below line is the code updated
        url: jdbc:postgresql://awseb-e-nskvm2faru-stack-awsebrdsdatabase-xn3ngsekufx0.c1qawm2oqlx3.us-east-1.rds.amazonaws.com:5432/ebdb
        username: kedarnath
        password: password
      jpa:
        hibernate:
          ddl-auto: validate
        properties:
          hibernate:
            dialect: org.hibernate.dialect.PostgreSQLDialect
            format_sql: true
        show_sql: true
      main:
        web-application-type: servlet
    ```
3. Now push the code to GitHub with the commit message ```Expose health and info metrics and add application-dev.yml```
4. Wait for couple of minutes first it will be then it goes sever as we haven't configured the load balancer yet.

FINISHED AT 11:28 AM

### STEP 498 29TH OCT 11:29 AM:

1. Go to the configuration
2. Edit load balancer
    1. Under Processes -> select the default
    2. Click->Actions-> edit
    3. Path:/acuator/health->save
    4. Apply
3. Go to Ec2 instance -> load balancers -> click on load balancer we are using
4. Navigate to Forward to starting with "aws...." under listeners - you can see its still failing the health checks.
5. Come back and select Health checks-> you can see the Path is updated.
6. After a couple of minutes this turns healthy.
7. Copy the DNS name and go to postman -> try to request with get by adding "/actuator/health" at the end-> you get
   status : up
8. Now if you check even the environment will be healthy.
9. Now delete the previous environment.

FINISHED AT 11:49 AM

### STEP 499 29TH OCT 11:50 AM

1. Amazon Route 53 -> link: https://aws.amazon.com/route53/
2. Click on get started
3. you will see options like Register domain , Transfer domain and few more.
4. Our first thing is to obtain a domain.
5. So select the Register domain -> Get started
6. give a name(kedarnath.dev) and try there are many option you can choose any and purchase a domain. look for the
   cheaper one...:-))

FINISHED AT 11:56 AM

### STEP 500 29TH OCT 11:56 AM

1. After purchasing if you go to Route 53 dashboard you will see 1 Hosted zone and 1 Domain registration.
2. click on Hosted zone
3. Select the hosted zone you have got.
4. click on create a new record.
    1. Record-name: customer-api
    2. Record-type: A-/routes traffic to an IPV4 address and some AWS resources
    3. Enable Alias
    4. choose endpoint: Alias to Application and load balancer
    5. choose Region: select same region as load balancer.
    6. Choose loadbalance: select the load balance you hav got
    7. Routing policy: Simple routing
    8. click on Create Records
5. copy the name of the record created and go to post man
    1. paste "customer-api.kedarnath.dev/actuator/health"  in post man
    2. Do get request
    3. you get the response "status : up"
6. As you this is http:// .... but we want to use https we will do it in next step.

FINISHED AT 12:16 PM

### STEP 501 29TH OCT 12:17 PM

1. Refer https://en.wikipedia.org/wiki/Transport_Layer_Security
2. ![](backend/src/main/resources/static/images/img_10.png)

FINISHED AT 12:25 PM

### STEP 502 29TH OCT 12:25 PM

1. Go to certificate manager on aws
2. click on Request a certificate
3. select request a public certicate
    1. Fully qualified domain name: customer-api.kedarnath.dev
    2. Leave the rest default
    3. click on request
4. Now we have certificate -> click on it
    1. status will pending validation-> we will validate in next steps
    2. click on create records in Route 53
    3. select your domain and click create record
    4. after couple of minutes the pending will change to success.
5. As we got the certificate now we can attach it to our load balancer.

FINISHED AT 12:34 PM

### step 503 29TH OCT 12:34 PM

1. Open ELB environment
2. select the configuration
3. edit load balancer
4. Navigate to listeners and click on add listener
    1. Port: 443
    2. Protocol: HTTPS
    3. select the ssl certificate we just created
    4. SSl_policy: ELBSecurityPolicy-2016-08
    5. click on Add
5. Navigate to Processess and click on add Process
    1. Name: https
    2. Port: 443
    3. Protocol: HTTPS
    4. HTTP code: 200
    5. Path: "/acutuator/health"
    6. Leave the rest default
    7. click on Add
6. click Apply
7. Wait for the Environment to load.
8. after successfully loading -> go to ec2 instance -> load balancers -> select the load balancer -> you will see two
   listeners.
    1. click on the new https:443 forwarded to
    2. you will see the healthy: 1
9. Lets test it go to chrome ```https://customer-api.kedarnath.dev``` -> you get the 403 error
10. you will also see the lock symbol on left next to url. That means connection is secure.
11. next we want to forward the http requests to https
12. open terminal type
    ```text
            curl http://customer-api.kedarnath.dev
    ```
    you will get the 403 error
13. which shouldn't happen. we are going configure it to https in next step.

FINISHED AT 1:51 PM

### STEP 504 29TH OCT 1:51 PM

1. Go to load balancer -> select your load balancer
2. select the HTTP:80 -> click on actions and delete it
3. Now add a listener
    1. Port: 80
    2. Protocol: HTTP
    3. Action: Redirect
        1. Protocol:HTTPS
        2. Port: 443
        3. leave the rest default
    4. click on add
4. Now it is done and test it by going to terminal
   ```text
            curl http://customer-api.kedarnath.dev
    ```
   you will get the 301 moved permanently
5. go to terminal
   ```text
            curl https://customer-api.kedarnath.dev
    ```
   you will get the 403 error

FINISHED AT 2:11 PM

### STEP 505 29TH OCT 2:12 PM

1. Have a knowledge of it properly and take responsibility and try to fix the issues

FINISHED AT 2:13 PM

### STEP 506 29TH OCT 2:14PM

1. Now we are going to deploy our frontend
2. Its not good practice to deploy front ends on docker.
3. Best way to deploy frontend is to use the manage solution aws amplify is one them.
4. If you are using nextjs then you use Vercel
5. Search for aws amplify in aws and click on it
6. Refer: https://docs.aws.amazon.com/amplify/latest/userguide/welcome.html

FINISHED at 2:21 PM

### STEP 507 29TH OCT 3:12 PM

1. SELECT Github -> click continue
2. Grant permission of repository to aws
3. select all for now // if you are doing it for your company select Only select repositories
4. In this page we select the repository before that let me show you the apps that got access of your github.
5. Go to Github
6. Go to settings
7. Navigate to Integration-> application
8. there you can see the apps that got access you can also revoke the permission
9. rest we do it next step

FINISHED AT 3:21 pm

### STEP 508 29TH OCT 3:22 PM

1. SELECT the repository
2. Branch: main
3. Enable Connecting a monorepo
4. path: frontend/react

FINISHED AT 3:24 pm

### STEP 509 29TH OCT 3:25 PM

1. Click next
2. Build settings:
    1. App name: Full stack Course - react
    2. before we go further lets do some things
3. Go to intellij-> go to terminal ->
    1. ```cd frontend/react```
    2. ```node -v```
    3. ```npm help``` -- you will see list of all commands
    4. ```npm ci```
    5. ```npm run build```
    6. we get a folder called dist
    7. remember that we need to configure the .env file >> VITE_API_BASE_URL=http://loalhoset:8080
4. Now lets go back to aws amplify
   FINISHED AT 3:45 PM
5.

### STEP 510 29TH OCT 4:00 PM

1. Go to aws Amplify
2. click on edit build and test settings

    ```yaml
    version: 1
    applications:
      -frontend:
        phases:
          preBuild:
            commands:
              - npm use ${VERSION_NODE_17}
              - npm ci
            build:
              commands:
                - npm use ${VERSION_NODE_17}
                - echo 'VITE_API_BASE_URL=$VITE_API_BASE_UR' > .env.production
                - npm run build
          artifacts:
            baseDirectory: /dist
            files:
              - '**/*'
          cache:
            paths:
              - node_modules/**/**
        appRoot: frontend/react
    
    ```

3. click on save
4. click on advanced setting
5. add and environment variable key: VITE_API_BASE_URL, value: https://customer-api.amigoscode.dev

FINISHED AT 4:33 PM 6 MIN 33 SEC VIDEO

### STEP 511 29TH OCT 4:34 PM

1. Click next
2. click save and deploy
3. wait for it
4. click on url -> tada your front end is ready
5. we have successfully deployed our application
6. Test it

FINISHED AT 4:41 PM

### STEP 512 29TH OCT 4:41 PM

1. Go to aws amplify
2. click on full stack course - react under the all apps-> you will see few steps to do
3. now we cover only few let do first domain management -> on the left navigate to domain management
4. click on add domain
5. full-stack-react.kedarnath.dev
6. leave the rest default and click on save
7. you see a text saying The required CNAME records are not yet configured for the below subdomains
8. click Actions -> View DND Records
9. there will be two copy first one
10. go to route 53 -> select the hosted zone -> select your hosted zone -> click create record
    1. record name: www.full-stack-react
    2. Record type: CNAME
    3. value: paste the value you have copied
    4. leave the rest default
    5. click on add another record
    6. record name: full-stack-react
    7. value: same value as above record
    8. click create record by leaving the rest as default
    9. if it says it already exists check by going back ifnot thats fine
11. if you go back to domain management-> you will see we verified domain ownership we are propagating takes up to 30
    mins

FINISHED AT 5:36 PM

### STEP 513 29TH OCT 5:37 PM

1. if you want to access control with username and password you can do it as well.
2. we dont need it right for admin control you can create something like that.
3.

FINISHED AT 5:50 PM

### STEP 514 29TH OCT 5:52 PM

1. go to aws amplify
2. click on previews
3. click enable previews
4. Install github app
5. select the branch -> click on manage
6. enable Pull request Previews -> confirm
   FINISHED AT 6:02 PM

### STEP 515 29TH OCT 6:02 PM

1. now lets make some changes in our app to test the preview of aws amplify for ci /cd
2. let add links to home for dashboard by creating new component.
3. Create new component Home.jsx
    ```text
    import {
        Wrap,
        WrapItem,
        Spinner,
        Text,
    } from '@chakra-ui/react';
    import SidebarWithHeader from "./components/shared/SideBar";
    import {useEffect, useState} from "react";
    import {getCustomers} from "./services/client.js";
    import CardWithImage from "./components/customer/CustomerCard.jsx";
    import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
    import {errorNotification} from "./services/notification.js";
    
    const Home = () => {
    
        return (
            <SidebarWithHeader>
                <Text fontSize={"6xl"}>DashBoard</Text>
            </SidebarWithHeader>
        );
    };
    
    export default Home;
    
    ```
4. Update Customer.jsx
    ```text
    import {
        Wrap,
        WrapItem,
        Spinner,
        Text,
    } from '@chakra-ui/react';
    import SidebarWithHeader from "./components/shared/SideBar";
    import {useEffect, useState} from "react";
    import {getCustomers} from "./services/client.js";
    import CardWithImage from "./components/customer/CustomerCard.jsx";
    import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
    import {errorNotification} from "./services/notification.js";
    
    const Customer = () => {
    
        const [customers, setCustomers] = useState([]); // Initialize as an array
        const [loading, setLoading] = useState(false);
        const [err, setError] = useState("");
    
        const fetchCustomers = () => {
            setLoading(true);
            getCustomers().then(res => {
                // console.log('API Response:', res.data);  // Debugging the response
    
                // Ensure the response is an array, or default to an empty array
                if (Array.isArray(res.data)) {
                    setCustomers(res.data);  // Set customers only if it's an array
                    setError("");  // Reset the error state if the fetch is successful
                } else {
                    console.error("Unexpected response format:", res.data);
                    setCustomers([]);  // Fallback to an empty array if response is invalid
                    setError("Unexpected data format from API");
                }
            }).catch((err) => {
                console.error("Error fetching customers:", err);  // Log error for debugging
                setError(err?.response?.data?.message || "An error occurred while fetching customers");
                errorNotification(
                    err.code,
                    err?.response?.data?.message || "An error occurred"
                );
            })
                .finally(() => {
                    setLoading(false);
                });
        };
    
        useEffect(() => {
            fetchCustomers();
        }, []);
    
        if (loading) {
            return (
                <SidebarWithHeader>
                    <Spinner
                        thickness='4px'
                        speed='0.65s'
                        emptyColor='gray.200'
                        color='blue.500'
                        size='xl'
                    />
                </SidebarWithHeader>
            );
        }
    
        if (err) {
            console.log('Current error:', err);  // Log the error to understand its value
            return (
                <SidebarWithHeader>
                    <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                    <Text mt={5}>Oops, there was an error: {err}</Text>
                </SidebarWithHeader>
            );
        }
    
        if (!Array.isArray(customers) || customers.length === 0) {
            // console.log('Customers array is empty or not an array:', customers);  // Log for debugging
            return (
                <SidebarWithHeader>
                    <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                    <Text mt={5}>No customers available.</Text>
                </SidebarWithHeader>
            );
        }
    
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Wrap justify="center" spacing="30px">
                    {customers.map((customer, index) => (
                        <WrapItem key={index}>
                            <CardWithImage
                                {...customer}
                                imageNumber={index}
                                fetchCustomers={fetchCustomers}
                            />
                        </WrapItem>
                    ))}
                </Wrap>
            </SidebarWithHeader>
        );
    };
    
    export default Customer;
    
    ```
5. Update Login.jsx
    ```text
    import {
        Button,
        Flex,
        Text,
        FormLabel,
        Heading,
        Input,
        Stack,
        Image, Link, Box, Alert, AlertIcon,
    } from '@chakra-ui/react';
    import {Formik, Form, useField} from "formik";
    import * as Yup from 'yup';
    import {useAuth} from "../context/AuthContext.jsx";
    import {errorNotification} from "../../services/notification.js";
    import {useNavigate} from "react-router-dom";
    import {useEffect} from "react";
    
    const MyTextInput = ({label, ...props}) => {
        // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
        // which we can spread on <input>. We can use field meta to show an error
        // message if the field is invalid, and it has been touched (i.e. visited)
        const [field, meta] = useField(props);
        return (
            <Box>
                <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
                <Input className="text-input" {...field} {...props} />
                {meta.touched && meta.error ? (
                    <Alert className="error" status={"error"} mt={"2"}>
                        <AlertIcon/>
                        {meta.error}
                    </Alert>
                ) : null}
            </Box>
        );
    };
    
    const LoginForm = () => {
        const {login} = useAuth();
        const navigate = useNavigate();
    
        return (
            <Formik
                validateOnMount={true}
                validationSchema={
                    Yup.object({
                        username: Yup.string().email("Must be Valid email").required("Email is required"),
                        password: Yup.string()
                            .max(20, "Password cannot be more than 20 characters")
                            .required("Password is required")
                    })}
                initialValues={{username: '', password: ''}}
                onSubmit={(values, {setSubmitting}) => {
                    // alert(JSON.stringify(values, null, 0));
                    setSubmitting(true);
                    login(values).then(res => {
                        // TODO: navigate to dashboard
                        navigate("/dashboard");
                        console.log("Successfully  logged in");
                    }).catch(err => {
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false);
                    })
                }}>
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={15}>
                            <MyTextInput
                                label={"Email"}
                                name={"username"}
                                type={"email"}
                                placeholder={"Enter your username"}
                            />
                            <MyTextInput
                                label={"Password"}
                                name={"password"}
                                type={"password"}
                                placeholder={"Enter your Password"}
                            />
                            <Button
                                type={"submit"}
                                disabled={!isValid || isSubmitting}>
                                Login
                            </Button>
                        </Stack>
                    </Form>)
                }
            </Formik>
        );
    };
    
    
    const Login = () => {
        const {customer} = useAuth();
        const navigate = useNavigate();
    
        useEffect(() => {
            if (customer) {
                navigate("/dashboard/customers");
            }
        })
    
        return (
            <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
                <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                    <Stack spacing={4} w={'full'} maxW={'md'}>
                        <Image
                            src={""}
                            boxSize={"200px"}
                            alt={"Kedarnath Logo"}
    
                        />
                        <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                        <LoginForm/>
                        <Link color={"blue.500"} href={"/signup"}>
                            Don't have an account? Signup now.
                        </Link>
                    </Stack>
                </Flex>
                <Flex flex={1} p={10} flexDirection={"column"}
                      alignItems={"center"}
                      justifyContent={"center"}
                      bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                    <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                        <Link href={"#"}>
                            Enroll Now
                        </Link>
                    </Text>
                    <Image
                        alt={'Login Image'}
                        objectFit={'scale-down'}
                        src={
                            'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                        }
                    />
                </Flex>
            </Stack>
        )
    }
    
    export default Login;
    ```
6. Update signup.jsx
    ```text
    import {useAuth} from "../context/AuthContext.jsx";
    import {useNavigate} from "react-router-dom";
    import {useEffect} from "react";
    import {Flex, Heading, Image, Link, Stack, Text} from "@chakra-ui/react";
    import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";
    
    const Signup = () => {
        const {customer, setCustomerFromToken} = useAuth();
        const navigate = useNavigate();
    
        useEffect(() => {
            if (customer) {
                navigate("/dashboard/customers");
            }
        })
    
        return (
            <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
                <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                    <Stack spacing={4} w={'full'} maxW={'md'}>
                        <Image
                            src={""}
                            boxSize={"200px"}
                            alt={"Kedarnath Logo"}
    
                        />
                        <Heading fontSize={'2xl'} mb={15}>Register for an account</Heading>
                        <CreateCustomerForm onSuccess={(token) => {
                            localStorage.setItem("access_token", token)
                            setCustomerFromToken()
                            navigate("/dashboard");
                        }}/>
                        <Link color={"blue.500"} href={"/"}>
                            Have an account? Login now.
                        </Link>
                    </Stack>
                </Flex>
                <Flex flex={1} p={10} flexDirection={"column"}
                      alignItems={"center"}
                      justifyContent={"center"}
                      bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                    <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                        <Link href={"#"}>
                            Enroll Now
                        </Link>
                    </Text>
                    <Image
                        alt={'Login Image'}
                        objectFit={'scale-down'}
                        src={
                            'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                        }
                    />
                </Flex>
            </Stack>
        )
    
    }
    export default Signup;
    ```
7. Update SideBar.jsx
    ```text
    'use client'
    
    import {
        IconButton,
        Avatar,
        Box,
        CloseButton,
        Flex,
        HStack,
        VStack,
        Icon,
        useColorModeValue,
        Text,
        Drawer,
        DrawerContent,
        useDisclosure,
        Menu,
        MenuButton,
        MenuDivider,
        MenuItem,
        MenuList, Image,
    } from '@chakra-ui/react'
    import {
        FiHome,
        FiSettings,
        FiMenu,
        FiBell,
        FiChevronDown, FiUsers,
    } from 'react-icons/fi'
    import {useAuth} from "../context/AuthContext.jsx";
    
    const LinkItems = [
        {name: 'Home', route: '/dashboard', icon: FiHome},
        {name: 'Customers', route: '/dashboard/customers', icon: FiUsers},
        {name: 'Settings', route: '/dashboard/sttings', icon: FiSettings}
    ]
    
    const SidebarContent = ({onClose, ...rest}) => {
        return (
            <Box
                transition="3s ease"
                bg={useColorModeValue('white', 'gray.900')}
                borderRight="1px"
                borderRightColor={useColorModeValue('gray.200', 'gray.700')}
                w={{base: 'full', md: 60}}
                pos="fixed"
                h="full"
                {...rest}>
                <Flex h="20" flexDirection="column" alignItems="center" mx="8" mb={70} mt={2}
                      justifyContent="space-between">
                    <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold" mb={5}>
                        Dashboard
                    </Text>
                    <Image
                        borderRadius='full'
                        boxSize='75px'
                        src='https://bit.ly/dan-abramov'
                        alt='Dan Abramov'
                    />
                    <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
                </Flex>
                {LinkItems.map((link) => (
                    <NavItem key={link.name} route={link.route} icon={link.icon}>
                        {link.name}
                    </NavItem>
                ))}
            </Box>
        )
    }
    
    const NavItem = ({icon, route, children, ...rest}) => {
        return (
            <Box
                as="a"
                href={`${route}`}
                style={{textDecoration: 'none'}}
                _focus={{boxShadow: 'none'}}>
                <Flex
                    align="center"
                    p="4"
                    mx="4"
                    borderRadius="lg"
                    role="group"
                    cursor="pointer"
                    _hover={{
                        bg: 'red.400',
                        color: 'white',
                    }}
                    {...rest}>
                    {icon && (
                        <Icon
                            mr="4"
                            fontSize="16"
                            _groupHover={{
                                color: 'white',
                            }}
                            as={icon}
                        />
                    )}
                    {children}
                </Flex>
            </Box>
        )
    }
    
    const MobileNav = ({onOpen, ...rest}) => {
        const {logout, customer} = useAuth();
        return (
            <Flex
                ml={{base: 0, md: 60}}
                px={{base: 4, md: 4}}
                height="20"
                alignItems="center"
                bg={useColorModeValue('white', 'gray.900')}
                borderBottomWidth="1px"
                borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
                justifyContent={{base: 'space-between', md: 'flex-end'}}
                {...rest}>
                <IconButton
                    display={{base: 'flex', md: 'none'}}
                    onClick={onOpen}
                    variant="outline"
                    aria-label="open menu"
                    icon={<FiMenu/>}
                />
    
                <Text
                    display={{base: 'flex', md: 'none'}}
                    fontSize="2xl"
                    fontFamily="monospace"
                    fontWeight="bold">
                    Logo
                </Text>
    
                <HStack spacing={{base: '0', md: '6'}}>
                    <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                    <Flex alignItems={'center'}>
                        <Menu>
                            <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                                <HStack>
                                    <Avatar
                                        size={'sm'}
                                        src={
                                            'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                        }
                                    />
                                    <VStack
                                        display={{base: 'none', md: 'flex'}}
                                        alignItems="flex-start"
                                        spacing="1px"
                                        ml="2">
                                        <Text fontSize="sm">{customer?.username}</Text>
                                        {customer?.roles.map((role, id) => (
                                            <Text key={id} fontSize="xs" color="gray.600">
                                                {role}
                                            </Text>
                                        ))}
    
                                    </VStack>
                                    <Box display={{base: 'none', md: 'flex'}}>
                                        <FiChevronDown/>
                                    </Box>
                                </HStack>
                            </MenuButton>
                            <MenuList
                                bg={useColorModeValue('white', 'gray.900')}
                                borderColor={useColorModeValue('gray.200', 'gray.700')}>
                                <MenuItem>Profile</MenuItem>
                                <MenuItem>Settings</MenuItem>
                                <MenuItem>Billing</MenuItem>
                                <MenuDivider/>
                                <MenuItem onClick={logout}>Sign out</MenuItem>
                            </MenuList>
                        </Menu>
                    </Flex>
                </HStack>
            </Flex>
        )
    }
    
    const SidebarWithHeader = ({children}) => {
        const {isOpen, onOpen, onClose} = useDisclosure()
    
        return (
            <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
                <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
                <Drawer
                    isOpen={isOpen}
                    placement="left"
                    onClose={onClose}
                    returnFocusOnClose={false}
                    onOverlayClick={onClose}
                    size="full">
                    <DrawerContent>
                        <SidebarContent onClose={onClose}/>
                    </DrawerContent>
                </Drawer>
                {/* mobilenav */}
                <MobileNav onOpen={onOpen}/>
                <Box ml={{base: 0, md: 60}} p="4">
                    {children} {/* Content */}
                </Box>
            </Box>
        )
    }
    
    export default SidebarWithHeader
    
    ```
8. Update Home.jsx
    ```text
    import {
        Wrap,
        WrapItem,
        Spinner,
        Text,
    } from '@chakra-ui/react';
    import SidebarWithHeader from "./components/shared/SideBar";
    import {useEffect, useState} from "react";
    import {getCustomers} from "./services/client.js";
    import CardWithImage from "./components/customer/CustomerCard.jsx";
    import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
    import {errorNotification} from "./services/notification.js";
    
    const Home = () => {
    
        return (
            <SidebarWithHeader>
                <Text fontSize={"6xl"}>DashBoard</Text>
            </SidebarWithHeader>
        );
    };
    
    export default Home;
    
    ```
9. Last update the main.jsx
    ```text
    import {StrictMode} from 'react'
    import {createRoot} from 'react-dom/client'
    import {ChakraProvider, Text} from '@chakra-ui/react'
    import Customer from './Customer.jsx'
    import {createStandaloneToast} from '@chakra-ui/react'
    import {createBrowserRouter, RouterProvider} from "react-router-dom";
    import Login from "./components/login/Login.jsx";
    import AuthProvider from "./components/context/AuthContext.jsx";
    import ProtectedRoute from "./services/ProtectedRoute.jsx";
    import Signup from "./components/signup/Signup";
    import Home from "./Home.jsx";
    
    const {ToastContainer} = createStandaloneToast()
    
    const router = createBrowserRouter(
        [
            {
                path: "/",
                element: <Login/>
            },
            {
                path: "/signup",
                element: <Signup/>
            },
            {
                path: "dashboard",
                element: <ProtectedRoute><Home/></ProtectedRoute>
            },
            {
                path: "dashboard/customers",
                element: <ProtectedRoute><Customer/></ProtectedRoute>
            }
        ]
    )
    createRoot(document.getElementById('root')).render(
        <StrictMode>
            <ChakraProvider>
                <AuthProvider>
                    <RouterProvider router={router}/>
                </AuthProvider>
                <ToastContainer/>
            </ChakraProvider>
        </StrictMode>,
    )
    
    ```
10. We push this to git now

FINISHED AT 30TH OCT 9:23 AM

### STEP 516 30TH OCT : AM

1. under github work actions update the frontend-react-cd.yml not to work because its taken care by aws amplify.

    ```text
    name: CD - Deploy Frontend React
    
    on:
      workflow_dispatch:
      push:
        branches:
          - main
        paths:
          - frontend/react/**
    
    jobs:
      deploy:
        if: false
        runs-on: ubuntu-latest
        defaults:
          run:
            working-directory: ./frontend/react
    
        steps:
          - uses: actions/checkout@v4
    
          - name: Slack commit message and sha
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":"https://github.com/kedarnathyadav/spring-boot-fullstack/commit/${{ github.sha }} - ${{ github.event.head_commit.message }}"}' \
              ${{secrets.SLACK_WEBHOOK_URL}}
    
          - name: List Files in Working Directory
            run: ls -la
    
          - name: Send Slack Message
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":"Deployment started :progress_bar: :spring:"}' \
              ${{ secrets.SLACK_WEBHOOK_URL }}
    
          - name: Set Build Number
            id: build-number
            run: echo "BUILD_NUMBER=$(date '+%d.%m.%Y.%H.%M.%S')" >> $GITHUB_OUTPUT
    
          - name: Login to Docker Hub
            uses: docker/login-action@v2
            with:
              username: ${{ secrets.DOCKERHUB_USERNAME }}
              password: ${{ secrets.DOCKERHUB_ACCESS_TOKEN }}
    
          - name: Docker Build and Push
            run: |
              chmod +x ../../.ci/build-publish.sh
              USERNAME=dkedarnath \
              REPO=kedarnath-react \
              TAG=${{ steps.build-number.outputs.BUILD_NUMBER }} \
              ../../.ci/build-publish.sh . \
               --build-arg api_base_url=${{ secrets.API_BASE_URL }}:8080
    
          - name: Send Slack Message
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":":docker: Image tag:${{ steps.build-number.outputs.BUILD_NUMBER }} pushed to https://hub.docker.com/repository/docker/dkedarnath/kedarnath-react"}' \
              ${{ secrets.SLACK_WEBHOOK_URL }}
    
          - name: Update Dockerrun.aws.json api image tag with new build number
            run: |
              echo "Dockerrun.aws.json before updating the tag"
              cat ../../Dockerrun.aws.json
              sed -i -E 's|(dkedarnath/kedarnath-react:)[^"]*|\1'${{ steps.build-number.outputs.BUILD_NUMBER }}'|' ../../Dockerrun.aws.json
              echo "Dockerrun.aws.json after updating the tag"
              cat ../../Dockerrun.aws.json
    
          - name: Send Slack Message
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":":aws: Starting deployment to Elastic Beanstalk "}' \
              ${{ secrets.SLACK_WEBHOOK_URL }}
    
          - name: Deploy to EB
            uses: einaregilsson/beanstalk-deploy@v22
            with:
              aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
              aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
              application_name: ${{ secrets.EB_APPLICATION_NAME }}
              environment_name: ${{ secrets.EB_ENVIRONMENT_NAME }}
              version_label: ${{ steps.build-number.outputs.BUILD_NUMBER }}
              version_description: ${{ github.SHA }}
              region: ${{ secrets.EB_REGION }}
              deployment_package: Dockerrun.aws.json
              wait_for_environmnt_recovery: 60
    
          - name: Send Slack Message
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":":githubloading: Committing to repo  "}' \
              ${{ secrets.SLACK_WEBHOOK_URL }}
    
          - name: Commit and Push Dockerrun.aws.json
            run: |
              git config user.name github-actions
              git config user.email github-actions@github.com
              git add ../../Dockerrun.aws.json
              git commit -m "Update Dockerrun.aws.json docker image with new tag ${{ steps.build-number.outputs.BUILD_NUMBER }}"
              git push
    
          - name: Send Slack Message
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":"Deployment and commit completed :github_check_mark: :party_blob: - https://kedarnath-api-env.eba-9pwqzaur.us-east-1.elasticbeanstalk.com/"}' \
              ${{ secrets.SLACK_WEBHOOK_URL }}
    
          - name: Send Slack Message
            if: always()
            run: |
              curl -X POST -H 'Content-type: application/json' \
              --data '{"text":"Job status ${{ job.status }} "} ' \
              ${{ secrets.SLACK_WEBHOOK_URL }}
    
    ```
2. open terminal in frontend

    ```text
    git checkout -b improve-routes
    git status
    pwd
    cd ../../
    ls
    git add .
    git commit -m "New React routes"
    ```
3. push using intellij
4. go to git hub and create pull request you will see aws amplify preview running
5. Go to aws amplify-> previews -> wait till the status completes
6. it takes around 9 min
7. if you go back to github you can see a link for preview link -> test it but normally you should separate test api to
   test

FINISHED AT 10:10 AM

### STEP 517 30TH OCT 10:11 AM

1. Rebase and merge
2. Delete the branch
3. Now if you go to the aws amplify you will automaticalky deploy to environment. Awesome right?
4. Successfully deployed the new version. Hooo!

FINISHED AT 30TH OCT 10:29 AM

### STEP 518- 524 30TH OCT 10:29 AM

1. Before you learn angular you should know typescript
2. open visual studio code and create a folder TsTutorial
3. Install the below softwares
    1. nodejs
    2. npm install npm@latest
    3. npm install -g ts-code
    4. npm install -g typescript
4. worst explanation so far its not going to work and is clear at step 525
5. create file name hello.ts and add text in it "console.log('Hello Typescript');"
6. run now you get some error to fix it you need to add tsconfig file

FINISHED AT 30TH OCT 1:52 PM

### STEP 525 30TH OCT 1:52 PM

1. open terminal type
   ```
   tsc -init
   ```
2. no run the code you get output.
3. create file comments.ts

    ```text
    //This is sinle line comment
    
    /**
     * This is a multi line comment
     * 
     */
    
    console.log('Hello World');
    ```
4. create a file declare-variables.ts

```text
let company = 'Google';
let isMale = true;
let lotteryNumber = 777;

let person = {
    name: 'junior',
    location: 'Philadelphia'
};

console.log(company);
console.log(isMale);
console.log(lotteryNumber);
console.log(person);
console.log("Person Name: "+person.name);

//once const s assigned you cant resign like let 
const PI = 3.14;

console.log(PI);

var homeTown = 'Nagaram';

console.log(homeTown);
//var is not recommended as it has a global scope
```

Function Scoping: var is function-scoped, not block-scoped. This means that if you declare a var inside a block (e.g.,
an if statement), it’s still accessible outside that block. This can lead to unintended behavior, whereas let and const
are block-scoped, limiting variables to the enclosing block:

typescript
Copy code
if (true) {
var x = 5;
}
console.log(x); // 5 (accessible outside the block)

if (true) {
let y = 10;
}
console.log(y); // Error: y is not defined
Hoisting: Variables declared with var are hoisted to the top of their function or global scope and initialized with
undefined, leading to potential confusion:

typescript
Copy code
console.log(z); // undefined (due to hoisting)
var z = 10;

// Using let or const
console.log(a); // Error: Cannot access 'a' before initialization
let a = 10;
No Temporal Dead Zone (TDZ): With var, there’s no temporal dead zone, so you can reference a var variable before its
declaration, which can lead to unexpected bugs. let and const prevent this by throwing an error if accessed before
declaration.

Inconsistent Behavior: var can lead to odd and inconsistent behaviors, especially when dealing with loops and closures.
For instance, variables declared with var in a for loop share the same function scope, which can lead to incorrect
values when used within closures:

typescript
Copy code
for (var i = 0; i < 3; i++) {
setTimeout(() => console.log(i), 1000); // Outputs 3, 3, 3
}

for (let j = 0; j < 3; j++) {
setTimeout(() => console.log(j), 1000); // Outputs 0, 1, 2
}
const for Constants: In TypeScript, const is available and better suited for declaring constants since it ensures that
the variable’s value cannot be reassigned. var does not offer this immutability.

FINISHED AT 30TH OCT 2:07 PM

### STEP 526 30TH OCT 2:08 PM

1. we can infer type on variables
2. create a file String-type.ts
    ```text
    let myName: string = 'Junior';
    
    myName  = "Junior";
    myName  = `Junior`;
    
    //function
    function sayHello(name: string): void {
        console.log(`Hello ${name}`);
    }
    
    sayHello("king");
    
    function sayHelloToany(name: any): void {
        console.log(`Hello ${name}`);
    }
    
    sayHelloToany("king");
    sayHelloToany(99);
    ```

FINISHED AT 30TH OCT 2:18 PM

### STEP 527 30TH OCT 2:18 PM

1. create a file number-type.ts

```text
let age: number = -4;

let decimalnumber = 90.85;

let change: number = 0.0043;

age = 31;

console.log(age);
console.log(decimalnumber);
console.log(change);

```

FINISHED AT 30TH OCT 2:23 PM

### STEP 528 30TH OCT : PM

1. create a file boolean-type.ts

```text
let isLoggedIn: boolean;

isLoggedIn =true;

let isAdmin = false;

console.log(isLoggedIn, isAdmin);

//string, number, boolean
```

FINISHED AT 30TH OCT 2:25 PM

### STEP 529 30TH OCT : PM

1. create a file array-type.ts
    ```text
    //asigning arrays
    let names: string[] = [];
    //or you can write them like below
    let names1: Array<string> = [];
    //or
    let names2: string[] = ['king','queen'];
    //for numbers
    let numbers: number[] = [1,2];
    
    //pushing values
    names.push('Apple');
    names1.push('Mango');
    names2.push('slave');
    numbers.push(7);
    
    //printing the arrays
    console.log("Names:",names);
    console.log("Names1:", names1);
    console.log("Names2:", names2);
    console.log("Numbers:", numbers);
    ```

FINISHED AT 30TH OCT 2:37 PM

### STEP 530 30TH OCT 2:37 PM

1. create a file object-type.ts

```text
let dog: { name: string, age: number, favplace: string[]};

dog  = {name:'blacky',age:3, favplace:['bridge','beach']};

console.log(dog);

```

FINISHED AT 30TH OCT 2:41 PM

### STEP 531 31TH OCT 10:10 AM

1. create a file any-type.ts

```text
let stuff : any;

stuff = 'junior';

stuff = 888;

stuff = true;

stuff = { name: 'junior', hobbies: ['junior1','junior2']}

stuff = [2,3,4,5];

console.log(stuff);

// This is not recommended
```

FINISHED AT 31TH OCT 10:24 PM

### STEP 532 31TH OCT 10:25 AM

1. create a file union-type.ts

```
let userId: number | string | boolean | {name: string};

userId = 90;

userId = 'king';

console.log(userId);

let customers: (number | string)[] = [];

customers.push(10);
customers.push('king');

console.log(customers);




```

FINISHED AT 30TH OCT 10:39 AM

### STEP 533 31TH OCT 10:40 AM

1. create a file type-allias.ts

```text
type ID = number | string;

let customerId: ID;

customerId = 99;

type Rectangle = { length: number, name: string};

let myRoom: Rectangle;

myRoom = { length: 88, name: 'room name'};

console.log(myRoom);

```

FINISHED AT 31TH OCT 10:50 AM

### STEP 534 31TH OCT 10:50 AM

1. create a file tuples.ts

```text
let country: [name: string, area: number];

country =['uk',600];

let modernCity: [Population: number, area: number, name: string];

modernCity = [8,12,'New York'];

console.log(modernCity);
```

FINISHED AT 31TH OCT 10:55 AM

### STEP 535 31TH OCT 10:55 AM

1. create a file enum.ts

```text
enum Role {
ROLE_USER,
ROLE_ADMIN,
ROLE_SYSADMIN,
}
enum Role1 {
ROLE_USER = 5,
ROLE_ADMIN= 6,
ROLE_SYSADMIN,   // by default will be 7 as continution
}

enum Role2 {
ROLE_USER = 'user',
ROLE_ADMIN= 'admin',
ROLE_SYSADMIN='sys admin',   
}
enum Role3 {
ROLE_USER = 'user',
ROLE_ADMIN= 1,
ROLE_SYSADMIN='sys admin',   
}


let user: { name: string, role: Role};

user = {name: 'junior', role:Role.ROLE_ADMIN};

console.log(user);

let user1: { name: string, role: Role1};
user1 = {name: 'junior', role:Role1.ROLE_ADMIN};

console.log(user1);

let user2: { name: string, role: Role2};
user2 = {name: 'junior', role:Role2.ROLE_ADMIN};

console.log(user2);
```

FINISHED AT 31TH OCT 11:09 AM

### STEP 535 31TH OCT 11:10 AM

1. create a file never-type.ts
   What is the never Type?
   The never type in TypeScript is a special type that represents values that should never occur. It signifies the
   absence of a value and is used when a function or code block is expected to never complete normally. Essentially,
   it’s a type for cases where a function either throws an error or doesn’t return a result.

When to Use never
Functions That Always Throw Errors

If a function throws an error and doesn’t return a value, TypeScript infers its return type as never. This way,
TypeScript knows that the function execution stops there.

typescript
Copy code
function throwError(message: string): never {
throw new Error(message);
}
Functions with Infinite Loops

A function that runs infinitely without completing also has a never return type since it technically never returns.

typescript
Copy code
function infiniteLoop(): never {
while (true) {
// Infinite loop
}
}
Exhaustiveness Checking in Type Guards or Switch Statements

The never type is also used in type-checking scenarios where all possible cases of a union type are handled. If a value
of never type is reached, it indicates a mistake (like an unhandled case).

typescript
Copy code
type Shape = "circle" | "square";

function handleShape(shape: Shape) {
switch (shape) {
case "circle":
console.log("Circle");
break;
case "square":
console.log("Square");
break;
default:
const _exhaustiveCheck: never = shape; // Error if a new shape type is added but not handled
return _exhaustiveCheck;
}
}
Here, if a new shape is added to Shape, TypeScript will throw an error in the default case, making sure all cases are
explicitly handled.

Summary
The never type is useful in TypeScript for catching unexpected or impossible situations, enforcing exhaustive type
checks, and for functions that are expected to not return any value.
FINISHED AT 31TH OCT 11:18 AM

### STEP 537 31TH OCT : AM

1. create a file name unknown-type.ts

```text
let customerInput: unknown;

customerInput = 10;

customerInput='age value';

console.log(customerInput);

let customerAge: string;

// assertion
customerAge = customerInput as string;

//control flow
if(typeof customerInput === 'string'){
    customerAge = customerInput;
}
```

FINISHED AT 31TH OCT 11:23 AM

### STEP 538 31TH OCT 11:24 AM

1. create a file name literal-type.ts

```
let userType: "USER" | "ADMIN";

userType = "ADMIN";

function saveUser(userId: number, type: "USER" | "ADMIN"): void {
  if (type === "USER") {
    console.log(`Saving new User: \n ${userId}. ${type}`)
  } else {
    console.log(`Saving new Admin: \n ${userId}. ${type}`)
  }
}


saveUser(10,'USER');
saveUser(20,'ADMIN');

type BINARY =  0 | 1;

let clientId: BINARY;

clientId=0;
```

FINISHED AT 31TH OCT 11:45 AM

### STEP 539 31TH OCT 11:45 AM

1. create a file name function.ts

```text
function add(a: number, b: number): number{
    return a+ b;
}

let multiply = (a: number, b: number) => a*b;
//or
let multiply1 = (a: number, b: number) => {
    return a*b;
}

console.log(add(5,9));
console.log(multiply(5,9));

//function with optional parameter "lastName?: string" you can send this or not do this by adding ?
function formatName(firstName: string, lastName?: string):string{
    return `${firstName} ${lastName}`;
};

console.log("\nFunction with optional parameters: \n"+formatName('junior','smith'));
console.log(formatName('junior'));

//function with default parameters
//default parameters should always come at the end

function formatNameWithDefaultParams(firstName: string, lastName =  'king'):string{
    return `${firstName} ${lastName}`;
};

console.log("\nFunction with default parameters: \n"+formatNameWithDefaultParams('junior'));

//rest parameters is always going to be some array type
function printNames(firstName: string, ...allTherest: string[]){
    return firstName +" "+ allTherest.join(' ');
};

console.log(printNames("\nFunction with rest parameters: \n"+'junior','John','Idiot'));


function addValues(val1: number, val2: number): number;
function addValues(val1: string, val2: string): string;
function addValues(a: any, b: any) {
    return a + b;
};


console.log(addValues(5,8));
console.log(addValues('18','26'));

```

FINISHED AT 31TH OCT 12:15 PM

### STEP 540 31TH OCT 12:15 PM

1. create a file name class.ts

```text
class Person{
    firstName: string;
    age: number;

    constructor(name: string, something: number){
        this.firstName = name;
        this.age =something;
    }

    printName() {
        console.log(`Your name is ${this.firstName} and  Your age is ${this.age}` );
    }
}

const junior = new Person('Junior',30);
junior.printName();
```

FINISHED AT 31TH OCT 12:21 PM

### STEP 541 31TH OCT 12:21 PM

1. we got a major problem as veariables are accessed directly outside the class
2. we can fix them by making it private

```text
class Person{
    private firstName: string;
    private age: number;

    constructor(name: string, something: number){
        this.firstName = name;
        this.age =something;
    }

    printName() {
        console.log(`Your name is ${this.firstName} and  Your age is ${this.age}` );
    }
}

const junior = new Person('Junior',30);
junior.printName();
```

3. we might have many variable we cant add them individually to contructor. so we can do like below

    ```text
    class Person{
        // private firstName: string;
        // private age: number;
    
        constructor(private firstName: string,
            private age: number
        ){
          
        }
    
        printName() {
            // console.log(`Your name is ${this.firstName} and  Your age is ${this.age}` );
        }
    }
    
    const junior = new Person('Junior',30);
    // junior.printName();
    
    console.log(junior);
    ```

FINISHED AT 31TH OCT 12:34 pM

### STEP 542 31TH OCT 1234 pM

1. you can make variables read only
2. update the class as below

```text
class Person{
    // private firstName: string;
    // private age: number;

    constructor(private readonly firstName: string,
        private age: number
    ){
      
    }

    printName() {
        console.log(`Your name is ${this.firstName} and  Your age is ${this.age}` );
    }
}

const junior = new Person('Junior',30);
// junior.printName();

console.log(junior);
```

FINISHED AT 31TH OCT 12:37 AM

### STEP 543 31TH OCT 12:37 AM

1. create another class Uscitizen extends person. so Uscitizen inherits all person details.
2. you have to use super keyword to send the data to person

```text
class Person{
    constructor(private readonly name: string,
        private id: number
    ){
      
    }

    
}

class Uscitizen extends Person{
    constructor(name: string, id: number, private ssn: string){
        super(name,id);
        this.ssn=ssn;
    }
};

const john = new Uscitizen("king",7,'dvghj');

console.log(john);
```

FINISHED AT 31TH OCT 12: 55 PM

### STEP 544  1st NOV 09:03 AM

1. Topic getters and setters
2. Update the class.ts

```text
class Car{
    constructor(private make: string, private model: string){

    }

    set carMake(value: string){
        this.make = value;
    }
    get carMakeg(){
        return this.make;
    }
};

const acura  = new Car('Acura','TL');

console.log(acura.carMakeg);

acura.carMake='Honda';

console.log(acura.carMakeg);

```

FINISHED AT 1st NOV 09:59 AM

### STEP 545  1st NOV 10:00 AM

1. defining static properties or static functions
2. static vaVariables can be accessed only using class name . This can't be addressed using this keyword unless it's a
   static block.

```text
class Car{

    static MAX_NUM_OF_WHEELS = 4;

    constructor(private make: string, private model: string){

    }

    set carMake(value: string){
        this.make = value;
    }
    get carMakeg(){
        return this.make;
    }

    static carStats(mile: number){
        console.log(this.MAX_NUM_OF_WHEELS);
        return {mileage: mile, type: 'Hybrid'}
    };
};

const acura  = new Car('Acura','TL');

console.log(acura.carMakeg);

acura.carMake='Honda';

console.log(acura.carMakeg);
console.log(Car.MAX_NUM_OF_WHEELS);
console.log(Car.carStats(10));



```

FINISHED AT 1st NOV 10:14 AM

### STEP 544  1st NOV 10:14 AM

1. abstract class
2. you cant create a instance of abstract class

```text
abstract class Animal{

    constructor(private name: string){}

    abstract printSound(sound:string): void;
}

class Dog extends Animal {

    printSound(sound: string): void {
       console.log(`${sound}`);
    }

}

const blaki = new Dog(`blaki`);
blaki.printSound("Bark...")
console.log(blaki);
```

FINISHED AT 1st NOV 10:22 AM

### STEP 547  1st NOV 10:22 AM

1. Interface - used to give structure and shape
2. create a file interface.ts

```text
interface Computer{
    name: string;
    ram: string;
    size: number;

    connect(adapter: string): void;

}

let lattitude: Computer;

lattitude = {
    name: 'Latitude',
    ram: '32gb',
    size:15,

    connect(adapter: string): void{
        console.log(`Power on. Connected to adapter ${adapter}`);
    }
}

lattitude.connect('Adapter 1');

console.log(lattitude);

```

FINISHED AT 1st NOV 10:30 AM

### STEP 548  1st NOV  10:30 AM

1. Interface are alot used with class. we will see the example in this step.

```text

interface HttpConnection {
    createConnection(url: string): void;

}

class MakeConnection implements HttpConnection{
    private headers: string[];
    private body: string;
    constructor(headers: string[], body: string){
        this.headers=headers;
        this.body=body;
    }
    createConnection(url: string): void {
        console.log(`Connection created to ${url}`);
    }
    
}
```

FINISHED AT 1st NOV 10:42 AM

### STEP 549  1st NOV 10:42 AM

1. we can one interface extends another interface
2. interface can extend any number of interfaces
3. class can also extend any number of interfaces

```text
interface patient {
    name: string;
}
interface Client extends patient{
    rating: number;
}

class Tenant implements Client {
    rating: number;
    name: string;

    constructor(rating: number,name: string){
        this.rating = rating;
        this.name =name;
    }
    
}
```

FINISHED AT 1st NOV 10:49 AM

### STEP 550  1st NOV 10:50 AM

1. Really cool thing that we can do interfaces is we can define them as type of function.

```text

interface Calculate {
    (number1: number, number2: number): number;
}

let cal: Calculate;

cal =(a: number,b: number) => {
    return a+b;
}

console.log(cal(10,5));
```

FINISHED AT 1st NOV 10:58 AM

### STEP 551  1st NOV 11:00 AM

1. you can have optional properties in interface using ?
2. you cant have private or public variables

```text
interface Calculate {
    (number1?: number, readonly number2: number): number;
}

let cal: Calculate;

cal =(a: number,b: number) => {
    return a+b;
}

console.log(cal(10,5));
```

FINISHED AT 1st NOV 11:02 AM

### STEP 552  1st NOV 11:02 AM

1. genrics- one class withdifferent type of data

```text
// function updateUser(oldUser: any, newUser: any):any{
//     return {...oldUser, ...newUser};
// }

// any is not sugessted as you know so we use the generics

function updateUser<T,V>(oldUser: T, newUser: V): T & V{
    return {...oldUser, ...newUser};
}
function makeAdmin<T>(user: T): T {
    return {...user, admin: true};
}

let user11 = {name: 'junior'};
let user22 = { age: 25, gender:'M'};

console.log(updateUser(user11,user22));

console.log(makeAdmin(user11));



```

FINISHED AT 1st NOV 11:17 AM

### STEP 553  1st NOV 11:17 AM

1. we can use generics with class as well

```text

class Planet<A>{
    private closestStar: A;

    constructor(closestStar: A){
        this.closestStar = closestStar;
    }
}

const earth = new Planet<string>('Sun');

const planetX = new Planet<{name: string, distance: number}>({name:'Xorox', distance:10});

interface UserData {
    size: number;
    data:string[]
}

const planetZ = new Planet<UserData>({data:['Xorox'], size:10});

interface CustomerData<K>{
    size: number;
    data: K[];
}

let newUser1 : CustomerData<number>;

newUser1 = {size: 77, data: [6,8,9]};

console.log(newUser1);
```

FINISHED AT 1st NOV 11:33 AM

### STEP 554  1st NOV 11:33 AM

1. we are going to start angular from next section
   FINISHED AT 1st NOV 11:34 AM

### STEP 555  1st NOV 11:34 AM

1. we are going build the same application we build using react earlier
   FINISHED AT 1st NOV 11:35 AM

### STEP 556  1st NOV 11:35 AM

1. what is angular
2. it is framework build by google and using typescript
3. two way binding
4. small components
   FINISHED AT 1st NOV 11:37 AM

### STEP 557  1st NOV 11:37 AM

1. what is module in angular
2. ![](backend/src/main/resources/static/images/img_11.png)
3. it is a container for above
4. easier to manage components
5. it has atleast one root module

FINISHED AT 1st NOV 11:4 AM

### STEP 558  1st NOV 11:4 AM

1. ![](backend/src/main/resources/static/images/img_12.png)
2. directives are used to manipulate the html css pages and also data binding.
3. these html css are called dom- data object models
4. we can achieve it using the ngIf, ngFor and custom dirctives

FINISHED AT 1st NOV 11:52 AM

### STEP 559  1st NOV 11:52 AM

1. install angular cli

```text
npm install @angular/cli
```

FINISHED AT 1st NOV 11:57 AM

### STEP 560  1st NOV 11:4 AM

1. navigate to frontend in terminal

    ```text
    ng new angular --routing --strict --no-standalone
    ```
2. In the older versions it used to ask for do you want routing but it wont in 18. so we add --routing.
3. In 18 it will ask you do you want the server side rendering you can select no for now. In the future we might need
   performance then we go for it.
4. Recnt version standalone components but now as we are learning stage we stick to old version and upgrade to new soon.

FINISHED AT 2nd NOV 11:00 AM

### STEP 561  2nd NOV 11:00 AM.

1. Intro to structure
   FINISHED AT 1st NOV 11:06 AM

### STEP 562  2nd NOV 11:06 AM

1. package.json
2. you can change version and add dependencies with scope like devlopment then they wont be included during package.
3. you can do adding dependencies manually and also using commands we will seee in next steps
   FINISHED AT 2nd NOV 11:17 AM

### STEP 563  2nd NOV 11:20 AM

1. angular.json is similar to application.properties or application.yml in spring boot

FINISHED AT 2nd NOV 11:30 AM

### STEP 564  2nd NOV 11:30 AM

1. clear the content in app.component.html except the <router-outlet/>
2. add <h1>Hello Angular</h1>
3. you can see it reflects immediately on your browser
4. i for got to tell to run your angular project. navigate to project folder on terminal.
5. enter
   ```text
        ng serve 
    ```

FINISHED AT 2nd NOV 11:45 AM

### STEP 565  2nd NOV 11:45 AM

1. how to create a new component in angular
2. you can use angular cli
    1. make sure you are in project folder path:
       ```text
             cd frontend/angular/src/app
       ```
    2. ```ng g c my-first```
    3. four files will be created.
    4. component will be added automatically

   FINISHED AT 2nd NOV 12:10 AM

### step 566 2nd Nov 12.10 Pm

1. how to include our new component in another component.
2. update app.component.html
    ```text
    <h1>Hello Angular</h1>
    <div>
      <app-my-first></app-my-first>
    </div>
    <router-outlet></router-outlet>
    
    ```
3. <app-my-first></app-my-first> - this is how you can do it.

Finishied at 12:20 pm 2nd nov

### step 567 2nd nov 12:20 pm

1. In my-first.component.html

```text
<input type = "text">
<button> click me </button>
<p> Your name is : </p>
```

Finished at 12:34 pm

### step 568 2nd nov 12:34 pm

1. getting values from ts file to the html file
2. update my-first.component.ts

```text
import {Component} from '@angular/core';

@Component({
  selector: 'app-my-first',
  templateUrl: './my-first.component.html',
  styleUrl: './my-first.component.scss'
})
export class MyFirstComponent {
  inputValue: string = 'Hello from kedarnath';

}

```

3. update my-first.component.html

    ```text
    <input type="text" [value]='inputValue'>
    <button> click me</button>
    <p> Your name is : {{inputValue}}</p>
    
    ```

4. {{}} this is called interpolation. we use [value] instead of value for anglar to get the value
5. in order to perform two way binding.

    ```text
    <input type="text" [(ngModel)]='inputValue'>
    <button> click me</button>
    <p> Your name is : {{inputValue}}</p>
    ```

6. make sure to import formsmodule in app.module.ts

    ```
    import {NgModule} from '@angular/core';
    import {BrowserModule} from '@angular/platform-browser';
    
    import {AppRoutingModule} from './app-routing.module';
    import {AppComponent} from './app.component';
    import {MyFirstComponent} from './my-first-component/my-first.component';
    import {FormsModule} from "@angular/forms";
    
    @NgModule({
      declarations: [
        AppComponent,
        MyFirstComponent
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule
      ],
      providers: [],
      bootstrap: [AppComponent]
    })
    export class AppModule {
    }
    
    ```

finishied at 12:57 pm

### step 569 2nd nov 1:00 pm

1. Event binding like clicking on button for example
2. update my-first.component.ts

    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrl: './my-first.component.scss'
    })
    export class MyFirstComponent {
      inputValue: string = 'Hello from kedarnath';
    
      clickMe(): void {
        alert(this.inputValue);
      }
    }
    
    ```

3. update my-first.component.html

    ```text
    <input type="text" [(ngModel)]='inputValue'>
    <button (click)="clickMe()"> click me</button>
    <p> Your name is : {{inputValue}}</p>
    
    ```

Finished at 1:07 pm

### step 570 2nd nov  1:07 pm

1. what about adding some behaviour like condition.
2. update my-first.component.ts

    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrl: './my-first.component.scss'
    })
    export class MyFirstComponent {
      inputValue: string = 'Hello from kedarnath';
      displayMsg = false;
    
      clickMe(): void {
        this.displayMsg = !this.displayMsg;
      }
    }
    
    ```

3. update my-first.component.html

    ```text
    <input type="text" [(ngModel)]='inputValue'>
    <button (click)="clickMe()"> click me</button>
    <p *ngIf="displayMsg"> Your name is : {{inputValue}}</p>
    
    ```

4. one more like if your input is greater than 8 then only display message we can do that.

    ```text
    <input type="text" [(ngModel)]='inputValue'>
    <button (click)="clickMe()"> click me</button>
    <p *ngIf="inputValue.length>=8"> Your name is : {{inputValue}}</p>
    ```

Finished at 7:26 pm

### step 571 2nd nov 7:26 pm

1. let see some more actions using cick
2. update my-first.component.ts

    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrl: './my-first.component.scss'
    })
    export class MyFirstComponent {
      inputValue: string = 'Hello from kedarnath';
      displayMsg = false;
      msgList: string[] = [];
    
      clickMe(): void {
        this.msgList.push(this.inputValue);
        this.inputValue = ''; // Clear the input field
      }
    }
    
    ```

3. what happens is when ever you clikc the input will be added to the array.
4. update my-first.component.html

    ```text
    <input type="text" [(ngModel)]='inputValue'>
    <button (click)="clickMe()"> click me</button>
    <p *ngIf="inputValue.length>=8"> Your name is : {{inputValue}}</p>
    
    
    <p *ngFor="let elem of msgList">{{elem}}</p>
    
    ```

5. when ever you click the input data will be displayed below by clearing the given input for next input.
6. lets see more functionalities like handlig json object
7. update my-first.component.ts

    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrl: './my-first.component.scss'
    })
    export class MyFirstComponent {
      inputValue: string = 'Hello from kedarnath';
      displayMsg = false;
      // msgList: string[] = [];
      //or
    
      msgList: Array<string> = [];
    
      msgListComposed: any[] = [];
    
    
      clickMe(): void {
        this.msgList.push(this.inputValue);
        this.msgListComposed.push({
          name: this.inputValue,
          visible: true
        });
        this.inputValue = '';// Clear the input field
        console.log(this.msgList);
      }
    }
    
    ```

8. update my-first.component.html

    ```text
    <input type="text" [(ngModel)]='inputValue'>
    <button (click)="clickMe()"> click me</button>
    <p *ngIf="inputValue.length>=8"> Your name is : {{inputValue}}</p>
    
    
    <p *ngFor="let elem of msgList">{{elem}}</p>
    <p *ngFor="let elem of msgListComposed">{{elem.name + '-' + elem.visible}}</p>
    
    ``` 

9. we are accessing the json object in here.
   Finished at 7:46 pm

### step 572 3rd nov 11:37 AM

1. how to pass input from parent to child
2. make inputValue as Input

    ```text
    import {Component, Input} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrls: ['./my-first.component.scss']
    })
    export class MyFirstComponent {
    
      @Input()
      inputValue: string = 'Hello from kedarnath';
    
      displayMsg = false;
      // msgList: string[] = [];
      //or
    
      msgList: Array<string> = [];
    
      msgListComposed: any[] = [];
    
    
      clickMe(): void {
        this.msgList.push(this.inputValue);
        this.msgListComposed.push({
          name: this.inputValue,
          visible: true
        });
        this.inputValue = '';// Clear the input field
        console.log(this.msgList);
      }
    }
    
    ```

3. you can send it to child as below

    ```text
    <h1>Hello Angular</h1>
    <app-my-first
      [inputValue]="'angular-hello'">
    </app-my-first>
    <app-my-first
      [inputValue]="'king-hello'">
    </app-my-first>
    <router-outlet></router-outlet>
    
    ```
   Finished at 1:38 pm

### step 573 3rd nov 1:38 pm

1. what if we want input from child to parent.
2. we are going to create a counter next hello angular text
3. update app.component.html
    ```text
    <h1>Hello Angular {{ clickCount }}</h1>
    <app-my-first
      [inputValue]="'angular-hello'"
      (childClicked)="handleChildClick()"
    >
    </app-my-first>
    <app-my-first
      [inputValue]="'king-hello'"
      (childClicked)="handleChildClick()"
    >
    </app-my-first>
    <router-outlet></router-outlet>
    
    ```
4. add a output value in my-first.component.ts
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrls: ['./my-first.component.scss']
    })
    export class MyFirstComponent {
    
      @Input()
      inputValue: string = 'Hello from kedarnath';
    
      @Output()
      childClicked: EventEmitter<void> = new EventEmitter<void>();
    
      displayMsg = false;
    
      // msgList: string[] = [];
      //or
      msgList: Array<string> = [];
    
      clickMe(): void {
        this.msgList.push(this.inputValue);
        this.inputValue = '';// Clear the input field
        this.childClicked.emit();
        console.log(this.msgList);
      }
    }
    
    ```
5. app.component.ts - handle the click event
    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-root',
      templateUrl: './app.component.html',
      styleUrl: './app.component.scss'
    })
    export class AppComponent {
      title = 'angular';
      clickCount = 0;
    
      handleChildClick() {
        this.clickCount++;
      }
    }
    
    ```

Finished at 2:17 pm

### step 574 3rd nov 2:17 pm

1. add h3 with last created element in app.component.html
2. to achieve this update app.component.html
    ```text
    <h1>Hello Angular {{ clickCount }}</h1>
    <h3>Last created element: {{ lastCreatedElement }}</h3>
    
    <app-my-first
      [inputValue]="'angular-hello'"
      (childClicked)="handleChildClick()"
      (elementCreated)="displayLastCreatedElement($event)"
    >
    </app-my-first>
    <app-my-first
      [inputValue]="'king-hello'"
      (childClicked)="handleChildClick()"
      (elementCreated)="displayLastCreatedElement($event)"
    
    >
    </app-my-first>
    <router-outlet></router-outlet>
    
    ```
3. update app.component.ts
    ```
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-root',
      templateUrl: './app.component.html',
      styleUrl: './app.component.scss'
    })
    export class AppComponent {
      title = 'angular';
      clickCount = 0;
      lastCreatedElement: any;
    
      handleChildClick() {
        this.clickCount++;
      }
    
      displayLastCreatedElement(element: string) {
        this.lastCreatedElement = element;
    
      }
    }
    
    ```
4. update my-first.component.ts
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrls: ['./my-first.component.scss']
    })
    export class MyFirstComponent {
    
      @Input()
      inputValue: string = 'Hello from kedarnath';
    
      @Output()
      childClicked: EventEmitter<void> = new EventEmitter<void>();
    
      @Output()
      elementCreated: EventEmitter<string> = new EventEmitter<string>();
    
    
      displayMsg = false;
    
      // msgList: string[] = [];
      //or
      msgList: Array<string> = [];
    
      clickMe(): void {
        this.msgList.push(this.inputValue);
        this.childClicked.emit();
        this.elementCreated.emit(this.inputValue);
        this.inputValue = '';// Clear the input field
        console.log(this.msgList);
      }
    }
    
    ```

Finished at 2:35 pm

### step 575 3rd nov 2:35 pm

1. let clear the old code and start fresh to perform calculations using button and display output
2. update app.component.html
    ```text
    <h1>Hello Angular</h1>
    <app-my-first></app-my-first>
    <router-outlet></router-outlet>
    
    ```
3. my-first.component.html
    ```text
    <input type="text" [(ngModel)]='value1'>
    <input type="text" [(ngModel)]='value2'>
    <br>
    <p>Result : {{ result }}</p>
    <button (click)="sum()">+</button>
    <button (click)="sub()">-</button>
    <button (click)="multiply()">*</button>
    <button (click)="divide()">/</button>
    
    
    
    ```
4. my-first.component.ts
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrls: ['./my-first.component.scss']
    })
    export class MyFirstComponent {
      value1 = 0;
      value2 = 0;
      result = 0;
    
      sum() {
        this.result = +this.value1 + +this.value2
      }
    
      sub() {
        this.result = this.value1 - this.value2
      }
    
      multiply() {
        this.result = this.value1 * this.value2
      }
    
      divide() {
        this.result = this.value1 / this.value2
      }
    }
    
    ```
5. here we are writing the business logic in the component which is not the write way.
6. component has to be used to display data or information.
7. we need service class to write business logic
8. let's create one
9. navigate src/app using terminal ```cd src/app```
10. create a directory ```mkdir services```
11. create a service ```ng g c MyCalculator```

#### FINISHED at 4 NOVEMBER 2024 10:14 AM

### **STEP 576 4 NOVEMBER 2024 10:14 AM**

1. What makes a service different from other classes
2. We got Injectible element to inject this service and it got provideIn root. This means
   the service will be initialized by angular in the starting of the project.
3. Update my-calculator.service.ts
    ```text
    import {Injectable} from '@angular/core';
    
    @Injectable({
      providedIn: 'root'
    })
    export class MyCalculatorService {
    
      constructor() {
      }
    
      sum(value1: number, value2: number) {
        return +value1 + +value2
      }
    
      sub(value1: number, value2: number) {
        return +value1 - +value2
      }
    
      multiply(value1: number, value2: number) {
        return +value1 * +value2
      }
    
      divide(value1: number, value2: number) {
        return +value1 / +value2
      }
    }
    
    ```
4. update my-first.component.ts
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {MyCalculatorService} from "../services/my-calculator.service";
    
    @Component({
      selector: 'app-my-first',
      templateUrl: './my-first.component.html',
      styleUrls: ['./my-first.component.scss']
    })
    export class MyFirstComponent {
      value1 = 0;
      value2 = 0;
      result = 0;
    
    
      constructor(private readonly calculator: MyCalculatorService) {
    
      }
    
      sum() {
        this.result = this.calculator.sum(+this.value1, +this.value2);
      }
    
      sub() {
        this.result = this.calculator.sub(+this.value1, +this.value2);
      }
    
      multiply() {
        this.result = this.calculator.multiply(+this.value1, +this.value2);
      }
    
      divide() {
        this.result = this.calculator.divide(+this.value1, +this.value2);
      }
    }
    
    ```
5. you can remove the provideIN root from the service and add in the app.module.ts
6. Update my-calculator.service.ts
    ```text
    import {Injectable} from '@angular/core';
    
    @Injectable()
    export class MyCalculatorService {
    
      constructor() {
      }
    
      sum(value1: number, value2: number) {
        return +value1 + +value2
      }
    
      sub(value1: number, value2: number) {
        return +value1 - +value2
      }
    
      multiply(value1: number, value2: number) {
        return +value1 * +value2
      }
    
      divide(value1: number, value2: number) {
        return +value1 / +value2
      }
    }
    
    ```
7. update the app.module.ts
    ```text
    import {NgModule} from '@angular/core';
    import {BrowserModule} from '@angular/platform-browser';
    
    import {AppRoutingModule} from './app-routing.module';
    import {AppComponent} from './app.component';
    import {MyFirstComponent} from './my-first-component/my-first.component';
    import {FormsModule} from "@angular/forms";
    import {CommonModule} from "@angular/common";
    import {MyCalculatorService} from "./services/my-calculator.service";
    
    @NgModule({
      declarations: [
        AppComponent,
        MyFirstComponent
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        CommonModule
      ],
      providers: [
        MyCalculatorService
      ],
      bootstrap: [AppComponent]
    })
    export class AppModule {
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 10:45 AM

### **STEP 577 4 NOVEMBER 2024 10:45 AM**

1. we will add a menu to go to different pages
2. update app.component.html
    ```
    <div>
      <a href="#">Page 1</a>&nbsp;|&nbsp;
      <a href="#">Page 2</a>&nbsp;|&nbsp;
      <a href="#">Page 3</a>
    </div>
    <h1>Hello Angular</h1>
    <app-my-first></app-my-first>
    <router-outlet></router-outlet>
    
    ```
3. lets generate those page
4. create a directory under src/app -> ```mkdir pages```
5. create three page components after ```cd pages```
    ```text
    ng g c page1
    ng g c page2
    ng g c page3
    ```

#### FINISHED at 4 NOVEMBER 2024 10:54 AM

### **STEP 578 4 NOVEMBER 2024 10:54 AM**

1. for implementing the route on the page links we learn some things then we come back to it.
2. We use angular routing to perform these
3. <router-outlet></router-outlet> - this plays very important role in routing.
4. we will use it next step

#### FINISHED at 4 NOVEMBER 2024 11:04 AM

### **STEP 579 4 NOVEMBER 2024 11:10AM**

1. we will see how to do routes
2. Go to app-routing.module.ts
    ```
    import {NgModule} from '@angular/core';
    import {RouterModule, Routes} from '@angular/router';
    import {Page1Component} from "./pages/page1/page1.component";
    import {Page2Component} from "./pages/page2/page2.component";
    import {Page3Component} from "./pages/page3/page3.component";
    
    
    const routes: Routes = [
      {
        path: 'page-1',
        component: Page1Component
      },
      {
        path: 'page-2',
        component: Page2Component
      },
      {
        path: 'page-3',
        component: Page3Component
      },
    ]
    
    @NgModule({
      imports: [RouterModule.forRoot(routes)],
      exports: [RouterModule]
    })
    export class AppRoutingModule {
    }
    
    ```
3. update app.component.html
    ```text
    <div>
      <a href="/page-1">Page 1</a>&nbsp;|&nbsp;
      <a href="/page-2">Page 2</a>&nbsp;|&nbsp;
      <a href="/page-3">Page 3</a>
    </div>
    
    <router-outlet></router-outlet>
    
    ```

#### FINISHED at 4 NOVEMBER 2024 11:50 AM

### **STEP 580 4 NOVEMBER 2024 11:50AM**

1. if you remember the page was reloading but using the routerLink it wont reload
2. if you want to load some data then want to redirected to page we use click event
   with Router.navigate
3. update app.component.html
    ```text
    <div>
      <a routerLink="/page-1">Page 1</a>&nbsp;|&nbsp;
      <a routerLink="/page-2">Page 2</a>&nbsp;|&nbsp;
      <a (click)="navigateToPage3()" href="javascript:void(0);">Page 3</a>
    </div>
    
    <router-outlet></router-outlet>
    
    ```
4. update app.component.ts
    ```text
    import {Component} from '@angular/core';
    import {Router} from "@angular/router";
    
    @Component({
      selector: 'app-root',
      templateUrl: './app.component.html',
      styleUrl: './app.component.scss'
    })
    export class AppComponent {
      title = 'angular';
      clickCount = 0;
      lastCreatedElement: any;
    
      constructor(private router: Router) {
      }
    
      handleChildClick() {
        this.clickCount++;
      }
    
      displayLastCreatedElement(element: string) {
        this.lastCreatedElement = element;
    
      }
    
      navigateToPage3() {
        this.router.navigate(['page-3']);
      }
    
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 12:12 PM

### **STEP 581 4 NOVEMBER 2024 12:12 PM**

1. we are going to use prime ng here https://primeng.org/
2. Refer the website

#### FINISHED at 4 NOVEMBER 2024 12:19 PM

### **STEP 582 4 NOVEMBER 2024 12:36 PM**

1. clean all the code let get some thing real
2. delete my-first component, services complete folder, Complete Pages fodler
3. Clean the import and usage of these components in app.module.ts and routing.ts, app.component.html,app.component.ts
4. make sure the page is fine without errors

#### FINISHED at 4 NOVEMBER 2024 12:43 PM

### **STEP 583 4 NOVEMBER 2024 12:43 PM**

1. install primeng, prime icons, prime flex
2. ```npm install primeng```
3. ```npm install primeng```
4. ```npm i primeflex```

#### FINISHED at 4 NOVEMBER 2024 12:50 PM

### **STEP 584 4 NOVEMBER 2024 12:50 PM**

1. update the angular.json by adding all the css files url to it of the ones installed above
    ```text
    {
      "$schema": "./node_modules/@angular/cli/lib/config/schema.json",
      "version": 1,
      "newProjectRoot": "projects",
      "projects": {
        "angular": {
          "projectType": "application",
          "schematics": {
            "@schematics/angular:component": {
              "style": "scss",
              "standalone": false
            },
            "@schematics/angular:directive": {
              "standalone": false
            },
            "@schematics/angular:pipe": {
              "standalone": false
            }
          },
          "root": "",
          "sourceRoot": "src",
          "prefix": "app",
          "architect": {
            "build": {
              "builder": "@angular-devkit/build-angular:application",
              "options": {
                "outputPath": "dist/angular",
                "index": "src/index.html",
                "browser": "src/main.ts",
                "polyfills": [
                  "zone.js"
                ],
                "tsConfig": "tsconfig.app.json",
                "inlineStyleLanguage": "scss",
                "assets": [
                  {
                    "glob": "**/*",
                    "input": "public"
                  }
                ],
                "styles": [
                  "src/styles.scss",
                  "node_modules/primeng/resources/primeng.min.css",
                  "node_modules/primeng/resources/themes/lara-light-blue/theme.css",
                  "node_modules/primeicons/primeicons.css",
                  "node_modules/primeflex/primeflex.min.css"
                ],
                "scripts": []
              },
              "configurations": {
                "production": {
                  "budgets": [
                    {
                      "type": "initial",
                      "maximumWarning": "500kB",
                      "maximumError": "1MB"
                    },
                    {
                      "type": "anyComponentStyle",
                      "maximumWarning": "2kB",
                      "maximumError": "4kB"
                    }
                  ],
                  "outputHashing": "all"
                },
                "development": {
                  "optimization": false,
                  "extractLicenses": false,
                  "sourceMap": true
                }
              },
              "defaultConfiguration": "production"
            },
            "serve": {
              "builder": "@angular-devkit/build-angular:dev-server",
              "configurations": {
                "production": {
                  "buildTarget": "angular:build:production"
                },
                "development": {
                  "buildTarget": "angular:build:development"
                }
              },
              "defaultConfiguration": "development"
            },
            "extract-i18n": {
              "builder": "@angular-devkit/build-angular:extract-i18n"
            },
            "test": {
              "builder": "@angular-devkit/build-angular:karma",
              "options": {
                "polyfills": [
                  "zone.js",
                  "zone.js/testing"
                ],
                "tsConfig": "tsconfig.spec.json",
                "inlineStyleLanguage": "scss",
                "assets": [
                  {
                    "glob": "**/*",
                    "input": "public"
                  }
                ],
                "styles": [
                  "src/styles.scss"
                ],
                "scripts": []
              }
            }
          }
        }
      }
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 01:01 PM

### **STEP 585 4 NOVEMBER 2024 02:35 PM**

1. lets test it whether they are working
2. we got issue here use primeng@17.8.9 instead of 11 as its giving issues
3. ```npm instal primeng@17.8.9```
4. just for reference you can use the following codes as well to clean and install again

    ```text
    rm -rf dist node_modules
    npm install
    ```
5. let continue update app.component.html
    ```
    <input type="text" pInputText/>
    <router-outlet></router-outlet>
    
    ```
6. Add InputTextModule to app.module.ts

```text
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {InputTextModule} from 'primeng/inputtext';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";


@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CommonModule,
    InputTextModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}

```

#### FINISHED at 4 NOVEMBER 2024 02:40 PM PM

### **STEP 586 4 NOVEMBER 2024 02:45 PM**

1. plan for login page, register page
2. just showed how it looks like

#### FINISHED at 4 NOVEMBER 2024 02:40 PM PM

### **STEP 587 4 NOVEMBER 2024 02:48 PM**

1. how the final application looks like
2. just showing how it looks like

#### FINISHED at 4 NOVEMBER 2024 02:52 PM

### **STEP 588 4 NOVEMBER 2024 02:52 PM**

1. angular is a component based framework
2. we split into parts like side bar with menu card, customer card, header bar.

#### FINISHED at 4 NOVEMBER 2024 02:54 PM

### **STEP 589 4 NOVEMBER 2024 02:54 PM**

1. lets create one page which holds all these parts
2. go to terminal navigate to src/app ```cd src/app```
3. ```mkdir components```
4. ```cd components```
5. ```ng g c Customer```
6. we should divide the page into multiple parts as we planned earlier. we got base now lets build skeleton.
7. update customer.component.html
    ```text
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        app-Menu
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          Header
        </div>
        <div class="main-container p-3">
          Button here
          <div>
            customers will be displayed here
          </div>
        </div>
    
      </div>
    </div>
    
    ```
8. update customer.component.scss
    ```text
    .main-container {
      background-color: #edf2f7;
      height: 100%;
    }
    
    ```
9. make sure you have added the route for this component
10. Update app-routing.module.ts
    ```
    import {NgModule} from '@angular/core';
    import {RouterModule, Routes} from '@angular/router';
    import {CustomerComponent} from "./components/customer/customer.component";
    
    
    const routes: Routes = [
      {
        path: 'customers',
        component: CustomerComponent
      }
    ]
    
    @NgModule({
      imports: [RouterModule.forRoot(routes)],
      exports: [RouterModule]
    })
    export class AppRoutingModule {
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 04:06 PM

### **STEP 590 4 NOVEMBER 2024 04:06 PM**

1. Let create app-menu now
2. open terminal navigate to src/app/components
3. ```ng g c menu-bar```
4. update menu-bar.component.html
    ```text
    <h4 class="text-center mt-0">Dashboard</h4>
    <div class="text-center">
      <p-avatar
        image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
        styleClass="mr-2"
        size="xlarge"
        shape="circle"/>
    <div>
      <p *ngFor="let item of menu">
        {{ item.label }}
      </p>
    </div>
    ```
5. update menu-bar.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {MenuItem} from "primeng/api";
    
    @Component({
      selector: 'app-menu-bar',
      templateUrl: './menu-bar.component.html',
      styleUrl: './menu-bar.component.scss',
    })
    export class MenuBarComponent {
      menu: Array<MenuItem> = [
        {label: 'Home', icon: 'pi pi-home'},
        {label: 'Customers', icon: 'pi pi-user'},
        {label: 'Settings', icon: 'pi pi-cog'}
      ];
    }
    
    ```

6. skip the below one those are updated i just tried and wasted a lot time
7. update menu-bar.component.html
    ```text
    <h4 class="text-center mt-0">Dashboard</h4>
    <div class="text-center">
      <p-avatar
        image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
        styleClass="mr-2"
        size="xlarge"
        shape="circle"/>
    </div>
    <div class="card flex justify-content-center">
      <p-menu [model]="items" class="mr-2"/>
    </div>
    
    ```
8. update menu-bar.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {MenuItem} from "primeng/api";
    
    @Component({
      selector: 'app-menu-bar',
      templateUrl: './menu-bar.component.html',
      styleUrl: './menu-bar.component.scss',
    })
    export class MenuBarComponent implements OnInit {
    
    
      items: MenuItem[] | undefined;
    
      ngOnInit() {
        this.items = [
          {label: 'Home', icon: 'pi pi-home'},
          {label: 'Customers', icon: 'pi pi-user'},
          {label: 'Settings', icon: 'pi pi-cog'}
        ];
      }
    
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 05:50 PM

### **STEP 591 4 NOVEMBER 2024 05:50 PM**

1. ```ng g c MenuItem```
2. update menu-bar.component.html
    ```text
    <h4 class="text-center mt-0">Dashboard</h4>
    <div class="text-center">
      <p-avatar
        image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
        styleClass="mr-2"
        size="xlarge"
        shape="circle"/>
    </div>
    <div>
      <app-menu-item *ngFor="let item of menu" class="mr-2"
                     [menuItem]="item"
      >
      </app-menu-item>
    
    </div>
    
    ```
3. update menu-item.component.ts
    ```text
    import {Component, Input} from '@angular/core';
    import {MenuItem} from "primeng/api";
    
    @Component({
      selector: 'app-menu-item',
      templateUrl: './menu-item.component.html',
      styleUrl: './menu-item.component.scss'
    })
    export class MenuItemComponent {
    
      @Input()
      menuItem: MenuItem = {}
    }
    
    ```
4. update menu-item.component.html
    ```text
    <div>
      <i [class]="menuItem.icon">
        {{ menuItem.label }}
      </i>
    </div>
    
    ```
5. add some styling like hoover color blue in menu-item.component.scss
    ```text
    div {
      padding: 1rem;
      border-radius: 0.5rem;
      width: 100%;
    
      &:hover {
        background-color: #4299e1;
        color: #fff;
        cursor: pointer;
      }
    
      i {
        padding-right: 10px;
      }
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 06:34 PM

### **STEP 592 4 NOVEMBER 2024 06:34 PM**

1. let's implement header now
2. ```ng g c HeaderBar```
3. update header-bar.component.html
    ```text
    <div class="flex flex-column align-items-end border-bottom-1 border-gray-200 pt-3 pb-3">
      <div class="flex flex-row align-items-center">
        <button
          pButton
          pRipple
          type="button"
          icon="pi pi-bell"
          class="p-button-rounded p-button-secondary p-button-text mr-3"
        >
        </button>
        <p-avatar
          image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
          size="normal"
          shape="circle"
          class="mr-2"
    
        ></p-avatar>
        <div>
          <p class="text-base mb-0 mt-1 text-gray-700">King&#64;kingdoms.com</p>
          <p class="text-sm mb-1 mt-0 text-gray-500">ROLE_ADMIN</p>
        </div>
        <div>
          <button
            (click)="menu.toggle($event)"
            class="p-button-secondary p-button-text"
            icon="pi pi-angle-down"
            pButton
            pRipple
            type="button"
          ></button>
          <p-menu #menu [model]="items" [popup]="true"/>
        </div>
      </div>
    </div>
    
    ```
4. update header-bar.components.ts
    ```text
    import {Component} from '@angular/core';
    import {MenuItem} from "primeng/api";
    
    @Component({
      selector: 'app-header-bar',
      templateUrl: './header-bar.component.html',
      styleUrl: './header-bar.component.scss'
    })
    export class HeaderBarComponent {
      items: MenuItem[] = [
        {label: 'Profile', icon: 'pi pi-user'},
        {label: 'Settings', icon: 'pi pi-cog'},
        {separator: true},
        {label: 'Sign out', icon: 'pi pi-sign-out'}
      ];
    
    }
    
    ```

#### FINISHED at 4 NOVEMBER 2024 08:35 PM

### **STEP 593 5 NOVEMBER 2024 11:56 AM**

1. just add a button
2. update customer.component.html
    ```text
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
          >
          </button>
          <div>
            customers will be displayed here
          </div>
        </div>
    
      </div>
    </div>
    
    ```

#### FINISHED at 5 NOVEMBER 2024 12:00 PM

### **STEP 594 5 NOVEMBER 2024 12:00 PM**

1. creating the sidebar on right we clikc create customer
2. update customer.components.ts
    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent {
      display: boolean = false;
    
    }
    
    ```
3. update customer.components.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >Content
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            customers will be displayed here
          </div>
        </div>
    
      </div>
    </div>
    
    ```
4. make sure you add required imports in app.module.ts

#### FINISHED at 5 NOVEMBER 2024 12:38 PM

### **STEP 595 5 NOVEMBER 2024 12:38 AM**

1. create a new component ``` ng g c MangeCustomer```
2. update the customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            customers will be displayed here
          </div>
        </div>
    
      </div>
    </div>
    
    ```
3. update the mange-customer.component.html
    ```text
    <div>
      <h2>Title here</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
    </div>
    
    ```

#### FINISHED at 5 NOVEMBER 2024 12:51 PM

### **STEP 596 5 NOVEMBER 2024 12:51 AM**

1. let's add gender and some other options like submit and cancel button
2. update the mange-customer.component.html
    ```text
    <div>
      <h2>Title here</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="gender">Gender</label>
        <select
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option>Select Option</option>
          <option>Male</option>
          <option>Female</option>
        </select>
      </div>
      <button
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```
3. Update the mange-customer.component.scss
    ```text
    .custom-select {
      padding: 0.75rem 0.75rem;
      border: 1px solid #ced4da;
      
    }
    
    ```

#### FINISHED at 5 NOVEMBER 2024 1:05 PM

### **STEP 597 5 NOVEMBER 2024 1:05 PM**

1. create a login page
2. ```ng g c Login```
3. exercise /login - only login page nothing else
4. update routing

```text
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CustomerComponent} from "./components/customer/customer.component";
import {LoginComponent} from "./components/login/login.component";


const routes: Routes = [
  {
    path: 'customers',
    component: CustomerComponent
  },
  {
    path: 'login',
    component: LoginComponent
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

```

#### FINISHED at 5 NOVEMBER 2024 1:09 PM

### **STEP 598 5 NOVEMBER 2024 1:09 PM**

1. exercise solution

#### FINISHED at 5 NOVEMBER 2024 1:09 PM

### **STEP 599 5 NOVEMBER 2024 1:09 PM**

1. divide page into two
2. create a login form
3. update login.component.html
    ```text
    <div class="flex" style="height: 100vh">
      <div class="col-6">
        <div class="text-center">
          <p-avatar
            image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
            styleClass="mr-2"
            size="xlarge"
            shape="circle"/>
          <h1>Sign in into your Account.</h1>
        </div>
        <div class="field">
          <label for="email">Email</label>
          <input
            id="email"
            pInputText
            placeholder="MaryJane@gmail.com"
            type="email"
            class="w-full"
          >
        </div>
        <div class="field">
          <label for="password">Password</label>
          <input
            id="password"
            pInputText
            placeholder=""
            type="password"
            class="w-full"
          >
        </div>
        <button
          pButton
          label="submit"
          class="w-full"
        >
        </button>
        <button
          pButton
          label="Don't have an account? Signup now!"
          class="w-full p-button-link"
        >
        </button>
      </div>
      <div class="col-6"></div>
    </div>
    
    ```

#### FINISHED at 5 NOVEMBER 2024 1:24 PM

### **STEP 600 and 601 5 NOVEMBER 2024 1:24 PM**

1. let's add a image on the right and add some styling to fit the image
2. update login.component.html
    ```text
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Sign in into your Account.</h1>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <button
            pButton
            label="submit"
            class="w-full"
          >
          </button>
          <button
            pButton
            label="Don't have an account? Signup now!"
            class="w-full p-button-link pl-0"
          >
          </button>
    
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```
3. update login.component.scss
    ```text
    .right-container {
      background-image: linear-gradient(to right, #2b6cb0, #6b46c1);
    
      .background {
        object-fit: scale-down;
        height: auto;
        max-width: 100%;
      }
    
      a {
        text-decoration: none;
        color: #ffffff;
      }
    }
    
    ```

#### FINISHED at 5 NOVEMBER 2024 1:45 PM

### **STEP 602 5 NOVEMBER 2024 1:45 PM**

1. we will connect the frontend to backend
2. we need service to do that
3. under src/app '''mkdir services'''
4. ```cd services ```
5. ```mkdir authentication ```
6. ```cd authentication ```
7. ```ng g s Authentication```

#### FINISHED at 5 NOVEMBER 2024 1:48 PM

### **STEP 603 5 NOVEMBER 2024 1:53 PM**

1. create a new directory under src/app
2. ```mkdir models```
3. create a new file name authentication-request.ts

````
export interface AuthenticationRequest {
  username?: string;
  password?: string;
}
````

#### FINISHED at 5 NOVEMBER 2024 2:00 PM

### **STEP 604 5 NOVEMBER 2024 2:00 PM**

1. Task bind the username and password to login form

#### FINISHED at 5 NOVEMBER 2024 2:01 PM

### **STEP 605 5 NOVEMBER 2024 2:01 PM**

1. open login.component.ts
    ```text
    import {Component} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    
    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrl: './login.component.scss'
    })
    export class LoginComponent {
      authenticationRequest: AuthenticationRequest = {};
    
    
    }
    
    ```
2. update login.component.html
    ```text
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Sign in into your Account.</h1>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              [(ngModel)]="authenticationRequest.username"
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              [(ngModel)]="authenticationRequest.password"
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <button
            pButton
            label="submit"
            class="w-full"
          >
          </button>
          <button
            pButton
            label="Don't have an account? Signup now!"
            class="w-full p-button-link pl-0"
          >
          </button>
    
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```

#### FINISHED at 5 NOVEMBER 2024 2:05 PM

### **STEP 606 5 NOVEMBER 2024 2:05 PM**

1. open authentication.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {HttpClient} from "@angular/common/http";
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    
    @Injectable({
      providedIn: 'root'
    })
    export class AuthenticationService {
    
      constructor(
        private http: HttpClient
      ) {  }
      
      login(authRequest: AuthenticationRequest): Observable<any> {
        return this.http.post('http://localhost:8080/api/v1/auth/login',authRequest);
      }
    }
    
    ```

#### FINISHED at 5 NOVEMBER 2024 2:17 PM

### **STEP 607 5 NOVEMBER 2024 2:17 PM**

1. let use the application service now in login component
2. update the login.component.html
    ```text
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Sign in into your Account.</h1>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              [(ngModel)]="authenticationRequest.username"
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              [(ngModel)]="authenticationRequest.password"
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <button
            pButton
            label="Login"
            class="w-full"
            (click)="login()"
          >
          </button>
          <button
            pButton
            label="Don't have an account? Signup now!"
            class="w-full p-button-link pl-0"
          >
          </button>
    
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```
3. update the login.component.ts
    ```text
    import {Component} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    
    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrl: './login.component.scss'
    })
    export class LoginComponent {
      authenticationRequest: AuthenticationRequest = {};
    
      constructor(private authenticationService: AuthenticationService) {
      }
    
    
      login() {
        this.authenticationService.login(this.authenticationRequest)
          .subscribe({
            next: (authenticationResponse) => {
              console.log(authenticationResponse);
            }
          });
      }
    }
    
    ```

#### FINISHED at 5 NOVEMBER 2024 6:15 PM

### **STEP 608 5 NOVEMBER 2024 6:15 PM**

1. your login wont load giving some issue in console
2. you have add HttpClient module but there is one more problem its deprecated from angular 16

#### FINISHED at 5 NOVEMBER 2024 6:15 PM

### **STEP 609 5 NOVEMBER 2024 6:16 PM**

1. but i have fixed it
    ```
    import {NgModule} from '@angular/core';
    import {BrowserModule} from '@angular/platform-browser';
    
    import {InputTextModule} from 'primeng/inputtext';
    import {AppRoutingModule} from './app-routing.module';
    import {AppComponent} from './app.component';
    import {FormsModule} from "@angular/forms";
    import {CommonModule} from "@angular/common";
    import {CustomerComponent} from './components/customer/customer.component';
    import {MenuBarComponent} from './components/menu-bar/menu-bar.component';
    import {AvatarModule} from 'primeng/avatar';
    import {AvatarGroupModule} from 'primeng/avatargroup';
    import {MenuModule} from "primeng/menu";
    import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
    import {MenuItemComponent} from './components/menu-item/menu-item.component';
    import {HeaderBarComponent} from './components/header-bar/header-bar.component';
    import {Button, ButtonDirective} from "primeng/button";
    import {Ripple} from "primeng/ripple";
    import {SidebarModule} from "primeng/sidebar";
    import {ManageCustomerComponent} from './components/mange-customer/mange-customer.component';
    import {LoginComponent} from './components/login/login.component';
    import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
    import {AuthenticationService} from "./services/authentication/authentication.service";
    
    @NgModule({
      declarations: [
        AppComponent,
        CustomerComponent,
        MenuBarComponent,
        MenuItemComponent,
        HeaderBarComponent,
        ManageCustomerComponent,
        LoginComponent,
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        CommonModule,
        InputTextModule,
        AvatarModule,
        AvatarGroupModule,
        MenuModule,
        BrowserAnimationsModule,
        Button,
        ButtonDirective,
        Ripple,
        SidebarModule
      ],
      providers: [
        AuthenticationService,
        provideHttpClient(withInterceptorsFromDi())
      ],
      bootstrap: [AppComponent]
    })
    export class AppModule {
    }
    
    ```
2. update the app.module.ts like above
3. now it works i mean page loads
4. make sure your backend is running
5. try to login with new credentials inserted
6. you get token back in console

#### FINISHED at 5 NOVEMBER 2024 08:00 PM

### **STEP 610 6 NOVEMBER 2024 11:44 AM**

1. in order to match any data of backend here we create model similar to backend and usethem to match the data and
   process
2. under models create two more files
3. create file authentication-response.ts
    ```text
    import {CustomerDTO} from "./customer-dto";
    
    export interface AuthenticationResponse {
      token?: string;
      customerDTO?: CustomerDTO;
    }
    
    ```
4. create file customer-dto.ts
    ```text
    export interface CustomerDTO {
      id?: number,
    
      name?: string,
    
      email?: string,
    
      gender?: 'MALE' | 'FEMALE',
    
      age?: number,
    
      roles: string[],
    
      username?: string
    }
    
    ```

#### FINISHED at 6 NOVEMBER 2024 11:48 PM

### **STEP 611 6 NOVEMBER 2024 11:48 AM**

1. In authentication service lets use this response
2. update authentication-service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {HttpClient} from "@angular/common/http";
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    
    @Injectable({
      providedIn: 'root'
    })
    export class AuthenticationService {
    
      constructor(private http: HttpClient) {
      }
    
      login(authRequest: AuthenticationRequest): Observable<AuthenticationResponse> {
        return this.http.post<AuthenticationResponse>('http://localhost:8080/api/v1/auth/login', authRequest);
      }
    }
    
    
    ```

#### FINISHED at 6 NOVEMBER 2024 11:51 PM

### **STEP 612 6 NOVEMBER 2024 11:51 AM**

1. update the login component to check credentials and show wrong credentials with nice looking
   message
2. update login.component.ts
    ```text
    import {Component} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    
    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrl: './login.component.scss'
    })
    export class LoginComponent {
      authenticationRequest: AuthenticationRequest = {};
      errorMsg: string = '';
    
      constructor(private authenticationService: AuthenticationService) {
      }
    
    
      login() {
        this.errorMsg = '';
        this.authenticationService.login(this.authenticationRequest)
          .subscribe({
            next: (authenticationResponse) => {
              console.log(authenticationResponse);
            },
            error: (error) => {
              if (error.error.statusCode === 401) {
                this.errorMsg = 'Login and / or pass is incorrect';
    
              }
            }
          });
      }
    }
    
    ```
3. update login.component.html
    ```text
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Sign in into your Account.</h1>
          </div>
          <div>
            <p-message *ngIf="errorMsg" severity="error" [text]="errorMsg" styleClass="mb-3"></p-message>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              [(ngModel)]="authenticationRequest.username"
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              [(ngModel)]="authenticationRequest.password"
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <button
            pButton
            label="Login"
            class="w-full"
            (click)="login()"
          >
          </button>
          <button
            pButton
            label="Don't have an account? Signup now!"
            class="w-full p-button-link pl-0"
          >
          </button>
    
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```

#### FINISHED at 6 NOVEMBER 2024 12:29 PM

### **STEP 613 6 NOVEMBER 2024 12:29 PM**

1. lets store the response in local storage of browser
2. update login.component.ts
    ```text
    import {Component} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    import {Router} from "@angular/router";
    
    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrl: './login.component.scss'
    })
    export class LoginComponent {
      authenticationRequest: AuthenticationRequest = {};
      errorMsg: string = '';
    
      constructor(
        private authenticationService: AuthenticationService,
        private router: Router
      ) {
      }
    
    
      login() {
        this.errorMsg = '';
        this.authenticationService.login(this.authenticationRequest)
          .subscribe({
            next: (authenticationResponse) => {
              // console.log(authenticationResponse);
              localStorage.setItem('authenticationRequest', JSON.stringify(authenticationResponse));
              this.router.navigate(['customers']);
            },
            error: (error) => {
              if (error.error.statusCode === 401) {
                this.errorMsg = 'Login and / or pass is incorrect';
    
              }
            }
          });
      }
    }
    
    ```

#### FINISHED at 6 NOVEMBER 2024 12:39 PM

### **STEP 614 6 NOVEMBER 2024 12:39 PM**

1. we need to secure our routes

#### FINISHED at 6 NOVEMBER 2024 12:40 PM

### **STEP 615 6 NOVEMBER 2024 12:40 PM**

1. route guard will be provided by angular for this purpose
2. it is a interface
3. like canactivate, can deactivate and some others

#### FINISHED at 6 NOVEMBER 2024 12:43 PM

### **STEP 616 6 NOVEMBER 2024 12:43 PM**

1. lets create our route guard
2. navigate to services on terminal
3. ```mkdir guard```
4. ```cd guard```
5. ```ng g s AccessGuard```
6. update access-guard.service.ts

#### FINISHED at 6 NOVEMBER 2024 12:49 PM

### **STEP 617 6 NOVEMBER 2024 12:49 PM**

1. update app-routing.module.ts
    ```text
    import {NgModule} from '@angular/core';
    import {RouterModule, Routes} from '@angular/router';
    import {CustomerComponent} from "./components/customer/customer.component";
    import {LoginComponent} from "./components/login/login.component";
    import {AccessGuardService} from "./services/guard/access-guard.service";
    
    
    const routes: Routes = [
      {
        path: 'customers',
        component: CustomerComponent,
        canActivate: [AccessGuardService]
      },
      {
        path: 'login',
        component: LoginComponent
      }
    ]
    
    @NgModule({
      imports: [RouterModule.forRoot(routes)],
      exports: [RouterModule]
    })
    export class AppRoutingModule {
    }
    
    ```
2. now if you try to access the /customers you cant as we set it to false

#### FINISHED at 6 NOVEMBER 2024 12:55 PM

### **STEP 618 6 NOVEMBER 2024 12:55 PM**

1. we need to implement logic in the guard now
2. we need to check whether a token is stored in the local storage and validate and checl not expired.
3. we need some services to do it.
4. ```npm i @auth0/angular-jwt``` in the main frontend/angular

#### FINISHED at 6 NOVEMBER 2024 1:01 PM

### **STEP 619 6 NOVEMBER 2024 1:02 PM**

1. set the token in storage using login.component.ts
    ```text
    import {Component} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    import {Router} from "@angular/router";
    
    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrl: './login.component.scss'
    })
    export class LoginComponent {
      authenticationRequest: AuthenticationRequest = {};
      errorMsg: string = '';
    
      constructor(
        private authenticationService: AuthenticationService,
        private router: Router
      ) {
      }
    
    
      login() {
        this.errorMsg = '';
        this.authenticationService.login(this.authenticationRequest)
          .subscribe({
            next: (authenticationResponse) => {
              // console.log(authenticationResponse);
              localStorage.setItem('user', JSON.stringify(authenticationResponse));
              this.router.navigate(['customers']);
            },
            error: (error) => {
              if (error.error.statusCode === 401) {
                this.errorMsg = 'Login and / or pass is incorrect';
    
              }
            }
          });
      }
    }
    
    ```
2. update the access-guard.service.ts. we are going take token and verify
    ```text
    import {Injectable} from '@angular/core';
    import {
      ActivatedRouteSnapshot,
      CanActivate,
      Router,
      RouterStateSnapshot,
      UrlTree
    } from '@angular/router';
    import {AuthenticationResponse} from '../../models/authentication-response';
    import {JwtHelperService} from '@auth0/angular-jwt';
    
    @Injectable({
      providedIn: 'root'
    })
    export class AccessGuardService implements CanActivate {
    
      constructor(
        private router: Router,
        private jwtHelper: JwtHelperService
      ) {
      }
    
      canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
      ): boolean | UrlTree {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(storedUser);
          const token = authResponse.token;
          if (token) {
            const isTokenNonExpired = !this.jwtHelper.isTokenExpired(token);
            if (isTokenNonExpired) {
              return true;
            }
          }
        }
        return this.router.createUrlTree(['login']);
      }
    }
    
    ```
3. now it should working great

#### FINISHED at 6 NOVEMBER 2024 2:28 PM

### **STEP 620 6 NOVEMBER 2024 2:28 PM**

1. lets get customers now
2. go to src/app/services on terminal
3. ```mkdir customer```
4. ```cd customer```
5. ```ng g s CustomerService```

#### FINISHED at 6 NOVEMBER 2024 2:32 PM

### **STEP 621 6 NOVEMBER 2024 2:32 PM**

1. Exercise: implement a method that implements to receive all customers

#### FINISHED at 6 NOVEMBER 2024 2:35 PM

### **STEP 622 6 NOVEMBER 2024 2:35 PM**

1. update customer-service.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        return this.http.get<Array<CustomerDTO>>('http://localhost:8080/api/v1/customers');
      }
    }
    
    ```

#### FINISHED at 6 NOVEMBER 2024 2:44 PM

### **STEP 623 6 NOVEMBER 2024 2:44 PM**

1. let use it in customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
    
      constructor(
        private customerService: CustomerServiceService
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    }
    
    ```
2. if you try now you get 403 error as we have not passed any token

#### FINISHED at 6 NOVEMBER 2024 2:53 PM

### **STEP 624 6 NOVEMBER 2024 2:53 PM**

1. send the headers with token but this is manual
2. After Bearer replace it with your token
    ```text
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient, HttpHeaders} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        let headers: HttpHeaders = new HttpHeaders();
        headers = headers.set('Authorization', 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGVsc2llLnp1bGF1ZkBrZWRhcm5hdGguY29tIiwiaWF0IjoxNzMwOTA0OTY3LCJpc3MiOiJodHRwOi8va2VkYXJuYXRoLmNvbSIsImV4cCI6MTczMjIwMDk2Nywic2NvcGVzIjpbIlJPTEVfVVNFUiJdfQ.Mqqkt4bvBsKZYvoTbH2fP1Yfc3DimeQ9DKeEekeDQRg')
        return this.http.get<Array<CustomerDTO>>('http://localhost:8080/api/v1/customers',
          {
            headers: headers,
          }
        );
      }
    }
    
    ```

#### FINISHED at 6 NOVEMBER 2024 3:02 PM

### **STEP 625 6 NOVEMBER 2024 3:03 PM**

1. let display it just to test
2. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div *ngFor="let customer of customers">
              {{ customer.name + ' ' + customer.age }}
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    ```

#### FINISHED at 6 NOVEMBER 2024 3:06 PM

### **STEP 626 6 NOVEMBER 2024 3:06 PM**

1. if you both the services got some same url
2. if you wan to change port then we need to go to all files and do it
3. To fix this we use properties file
4. open terminal
5. navigate src/app
6. ```ng g environments```
7. two files will be generated environ.ts is where we write these links. we see .development but for production it will
   be like .prod.

#### FINISHED at 6 NOVEMBER 2024 3:13 PM

### **STEP 627 6 NOVEMBER 2024 3:13 PM**

1. add those url in this environment files
2. two file one is environment.ts which is used for production
3. second file is environment.developement.ts which is used for development that is now.
4. you might think how this is configured in angular.json. you will see when developement use this file.
5. for now add the same code on both files

```text
export const environment = {
  api: {
    baseUrl: 'http://localhost:8080',
    authUrl: '/api/v1/auth/login',
    customerUrl: '/api/v1/customers',
  }
};


```

#### FINISHED at 6 NOVEMBER 2024 4:10 PM

### **STEP 628 6 NOVEMBER 2024 4:11 PM**

1. update authentication.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {HttpClient} from "@angular/common/http";
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {environment} from '../../../environments/environment.development';
    
    @Injectable({
      providedIn: 'root'
    })
    export class AuthenticationService {
      private readonly authUrl = `${environment.api.baseUrl}/${environment.api.authUrl}`;
    
      constructor(private http: HttpClient) {
      }
    
      login(authRequest: AuthenticationRequest): Observable<AuthenticationResponse> {
        return this.http.post<AuthenticationResponse>(this.authUrl, authRequest);
      }
    }
    
    
    ```
2. update customer-service.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient, HttpHeaders} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    import {environment} from "../../../environments/environment.development";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
    
      private readonly customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`;
    
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        let headers: HttpHeaders = new HttpHeaders();
        headers = headers.set('Authorization', 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGVsc2llLnp1bGF1ZkBrZWRhcm5hdGguY29tIiwiaWF0IjoxNzMwOTA0OTY3LCJpc3MiOiJodHRwOi8va2VkYXJuYXRoLmNvbSIsImV4cCI6MTczMjIwMDk2Nywic2NvcGVzIjpbIlJPTEVfVVNFUiJdfQ.Mqqkt4bvBsKZYvoTbH2fP1Yfc3DimeQ9DKeEekeDQRg')
        return this.http.get<Array<CustomerDTO>>(this.customerUrl,
          {
            headers: headers,
          }
        );
      }
    }
    
    ```

#### FINISHED at 6 NOVEMBER 2024 4:32 PM

### **STEP 629 6 NOVEMBER 2024 4:11 PM**

1. you remember manual token. we need to make it automatically
2. you can check the below code of customer-service.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient, HttpHeaders} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    import {environment} from "../../../environments/environment.development";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
      
      private readonly customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`;
    
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        const jwtToken = ' extract token from storage';
        let headers: HttpHeaders = new HttpHeaders();
        headers = headers.set('Authorization', 'Bearer '+jwtToken)
        return this.http.get<Array<CustomerDTO>>(this.customerUrl,
          {
            headers: headers,
          }
        );
      }
    }
    
    ```
3. The problem in here is its redundant. some code of lines are repeating.
4. look for better olution in next step

#### FINISHED at 6 NOVEMBER 2024 4:42 PM

### **STEP 630 6 NOVEMBER 2024 4:11 PM**

1. we need to intercept the request and attach the token automatically
2. go to src/app/services on terminal
3. ```mkdir interceptor```
4. ```cd interceptor```
5. ```ng g s  interceptor```
6. add the below code to http-interceptor.service.ts
    ```text
    import { Injectable } from '@angular/core';
    import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
    import {Observable} from "rxjs";
    
    @Injectable({
      providedIn: 'root'
    })
    export class HttpInterceptorService implements HttpInterceptor {
    
      constructor() { }
    
      intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req);
      }
    }
    
    ```

#### FINISHED at 6 NOVEMBER 2024 4:50 PM

### **STEP 631 6 NOVEMBER 2024 4:50 PM**

1. angular is not using the Interceptor we should tell to you use it.
2. go to app.module.ts
3. update the code
    ```
    import {NgModule} from '@angular/core';
    import {BrowserModule} from '@angular/platform-browser';
    
    import {InputTextModule} from 'primeng/inputtext';
    import {AppRoutingModule} from './app-routing.module';
    import {AppComponent} from './app.component';
    import {FormsModule} from "@angular/forms";
    import {CommonModule} from "@angular/common";
    import {CustomerComponent} from './components/customer/customer.component';
    import {MenuBarComponent} from './components/menu-bar/menu-bar.component';
    import {AvatarModule} from 'primeng/avatar';
    import {AvatarGroupModule} from 'primeng/avatargroup';
    import {MenuModule} from "primeng/menu";
    import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
    import {MenuItemComponent} from './components/menu-item/menu-item.component';
    import {HeaderBarComponent} from './components/header-bar/header-bar.component';
    import {Button, ButtonDirective} from "primeng/button";
    import {Ripple} from "primeng/ripple";
    import {SidebarModule} from "primeng/sidebar";
    import {ManageCustomerComponent} from './components/mange-customer/mange-customer.component';
    import {LoginComponent} from './components/login/login.component';
    import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
    import {AuthenticationService} from "./services/authentication/authentication.service";
    import {MessageModule} from "primeng/message";
    import {JwtHelperService, JWT_OPTIONS} from '@auth0/angular-jwt';
    import {HttpInterceptorService} from "./services/interceptor/http-interceptor.service";
    
    @NgModule({
      declarations: [
        AppComponent,
        CustomerComponent,
        MenuBarComponent,
        MenuItemComponent,
        HeaderBarComponent,
        ManageCustomerComponent,
        LoginComponent,
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        CommonModule,
        InputTextModule,
        AvatarModule,
        AvatarGroupModule,
        MenuModule,
        BrowserAnimationsModule,
        Button,
        ButtonDirective,
        Ripple,
        SidebarModule,
        MessageModule
      ],
      providers: [
        AuthenticationService,
        provideHttpClient(withInterceptorsFromDi()),
        {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
        JwtHelperService,
        {
          provide: HTTP_INTERCEPTORS,
          useClass: HttpInterceptorService,
          multi: true,
        }
      ],
      bootstrap: [AppComponent]
    })
    export class AppModule {
    }
    
    ```
4. The code added was
    ```
     {
          provide: HTTP_INTERCEPTORS,
          useClass: HttpInterceptorService,
          multi: true,
        }
    ```

#### FINISHED at 7 NOVEMBER 2024 11:25 PM

### **STEP 632 7 NOVEMBER 2024 11:26 PM**

1. lets implement our interceptor
    ```text
    import {Injectable} from '@angular/core';
    import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from "@angular/common/http";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    
    @Injectable({
      providedIn: 'root'
    })
    export class HttpInterceptorService implements HttpInterceptor {
    
      constructor() {
      }
    
      intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(storedUser);
          const token = authResponse.token;
          if (token) {
            const authReq = req.clone({
              headers: new HttpHeaders({
                Authorization: `Bearer ${token}`
              })
            });
            return next.handle(authReq);
          }
        }
        return next.handle(req);
      }
    }
    
    ```
2. Now the customers will be loaded on to page

#### FINISHED at 7 NOVEMBER 2024 1:21 PM

### **STEP 633 7 NOVEMBER 2024 1:21 PM**

1. lets create customer card component
2. go to components on terminal
3. ```ng g c CustomerCard```
4. add this to customer.component.html

    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div>
              <app-customer-card *ngFor="let customer of customers"></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    ```

#### FINISHED at 7 NOVEMBER 2024 1:28 PM

### **STEP 634 7 NOVEMBER 2024 1:28 PM**

1. update customer-card.component.html
    ```text
    <p-card [style]="{width: '316px' }" styleClass="p-card-shadow text-center">
      <ng-template pTemplate="header">
        <div>
          <img class="border-round-top cover"
               height="120"
               alt="card"
               src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png">
        </div>
        <div class="flex justify-content-center" style="margin-top: -50px">
          <img
            class="customer-avatar"
            src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
          >
        </div>
      </ng-template>
      <ng-template pTemplate="content">
        <p-badge [value]="'1'"></p-badge>
        <h2>Customer Name</h2>
        <p class="text-gray-600">email</p>
        <p class="text-gray-600">22 | MALE</p>
      </ng-template>
      <ng-template
        pTemplate="footer"
      >
        <button
          pButton
          label="Update"
          class="p-button-secondary p-button-outlined p-button-rounded"
        ></button>
        <button
          pButton
          label="Delete"
          class="p-button-danger p-button-outlined p-button-rounded ml-3"
        ></button>
    
      </ng-template>
    </p-card>
    
    ```
2. update customer-card.component.scss
    ```text
    .customer-avatar {
      width: 92px !important;
      height: 92px !important;
      border-radius: 50% !important;
      border: solid 2px white;
    }
    
    img.cover {
      object-fit: cover;
    }
    
    ```

#### FINISHED at 7 NOVEMBER 2024 1:56 PM

### **STEP 635 7 NOVEMBER 2024 1:56 PM**

1. exercise to display the user data in place of dummy data.

#### FINISHED at 7 NOVEMBER 2024 1:58 PM

### **STEP 636 7 NOVEMBER 2024 1:58 PM**

1. update customer-dto.ts by making role optional
    ```text
    export interface CustomerDTO {
      id?: number,
    
      name?: string,
    
      email?: string,
    
      gender?: 'MALE' | 'FEMALE',
    
      age?: number,
    
      roles?: string[],
    
      username?: string
    }
    
    ```
2. update customer-card.component.ts
    ```text
    import {Component, Input} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    
    @Component({
      selector: 'app-customer-card',
      templateUrl: './customer-card.component.html',
      styleUrl: './customer-card.component.scss'
    })
    export class CustomerCardComponent {
    
      @Input()
      customer: CustomerDTO = {};
    
      @Input()
      customerIndex: number = 0;
    
    
    }
    
    ```
3. update customer-card.component.html
    ```text
    <p-card [style]="{width: '316px' }" styleClass="p-card-shadow text-center">
      <ng-template pTemplate="header">
        <div>
          <img class="border-round-top cover"
               height="120"
               alt="card"
               src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png">
        </div>
        <div class="flex justify-content-center" style="margin-top: -50px">
          <img
            class="customer-avatar"
            src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
          >
        </div>
      </ng-template>
      <ng-template pTemplate="content">
        <p-badge [value]="''+(customerIndex+1)"></p-badge>
        <h2>{{ customer.name }}</h2>
        <p class="text-gray-600">{{ customer.email }}</p>
        <p class="text-gray-600">{{ customer.age }} | {{ customer.gender }}</p>
      </ng-template>
      <ng-template
        pTemplate="footer"
      >
        <button
          pButton
          label="Update"
          class="p-button-secondary p-button-outlined p-button-rounded"
        ></button>
        <button
          pButton
          label="Delete"
          class="p-button-danger p-button-outlined p-button-rounded ml-3"
        ></button>
    
      </ng-template>
    </p-card>
    
    ```
4. update customer.component.html to pass customer and index as input
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div>
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    ```

#### FINISHED at 7 NOVEMBER 2024 2:19 PM

### **STEP 637 7 NOVEMBER 2024 2:19 PM**

1. we are going to register the customer using frontend and the point in here is
   this side bar has to be generic so that we could use this for update as well.
2. In manage-customer.component.ts add a variable of type CustomerRegistrationRequest
    ```text
    import {Component, Input} from '@angular/core';
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 9:49 AM

### **STEP 638 8 NOVEMBER 2024 9:49 AM**

1. create a file named customer-registration-request.ts under models
    ```text
    export interface CustomerRegistrationRequest {
      name?: string;
      email?: string;
      password?:string;
      age?: number;
      gender?: 'MALE' | 'FEMALE';
    }
    ```
2. add the import to manage-customer.component.ts
    ```text
    import {Component, Input} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 9:55 AM

### **STEP 639 8 NOVEMBER 2024 9:56 AM**

1. Bind the customer to manage-customer.component.html
    ```text
    <div>
      <h2>Title here</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          [(ngModel)]="customer.name"
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          [(ngModel)]="customer.email"
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="password">Password</label>
        <input
          [(ngModel)]="customer.password"
          id="password"
          pInputText
          placeholder=""
          type="password"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          [(ngModel)]="customer.age"
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="gender">Gender</label>
        <select
          [(ngModel)]="customer.gender"
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option value="">Select Option</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```

#### FINISHED at 8 NOVEMBER 2024 10:01 AM

### **STEP 640 8 NOVEMBER 2024 10:01 AM**

1. exercise to disable submit button until the customer data is valid

#### FINISHED at 8 NOVEMBER 2024 10:02 AM

### **STEP 641 8 NOVEMBER 2024 10:02 AM**

1. update manage-customer.component.ts
    ```text
    import {Component, Input} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
    
    
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.hasLength(this.customer.email) &&
          this.hasLength(this.customer.password) &&
          this.hasLength(this.customer.gender) &&
          this.customer.age !== undefined &&
          this.customer.age > 0;
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    }
    
    ```
2. update manage-customer.component.html
   ```text
   <div>
     <h2>Title here</h2>
     <div class="field">
       <label for="firstname">Name</label>
       <input
         [(ngModel)]="customer.name"
         id="firstname"
         pInputText
         placeholder="Mary Jane"
         type="text"
         class="w-full"
       >
     </div>
     <div class="field">
       <label for="email">Email</label>
       <input
         [(ngModel)]="customer.email"
         id="email"
         pInputText
         placeholder="MaryJane@gmail.com"
         type="email"
         class="w-full"
       >
     </div>
     <div class="field">
       <label for="password">Password</label>
       <input
         [(ngModel)]="customer.password"
         id="password"
         pInputText
         placeholder=""
         type="password"
         class="w-full"
       >
     </div>
     <div class="field">
       <label for="age">Age</label>
       <input
         [(ngModel)]="customer.age"
         id="age"
         pInputText
         placeholder="16"
         type="number"
         class="w-full"
       >
     </div>
     <div class="field">
       <label for="gender">Gender</label>
       <select
         [(ngModel)]="customer.gender"
         id="gender"
         class="p-dropdown w-full custom-select"
       >
         <option value="">Select Option</option>
         <option value="MALE">Male</option>
         <option value="FEMALE">Female</option>
       </select>
     </div>
     <button
       [disabled]="!isCustomerValid"
       pButton
       label="submit"
       class="w-full"
     >
     </button>
     <button
       pButton
       label="cancel"
       class="w-full p-button-outlined p-button-danger mt-2"
     >
     </button>
   </div>
   
   ```

#### FINISHED at 8 NOVEMBER 2024 10:16 AM

### **STEP 642 8 NOVEMBER 2024 10:16 AM**

1. exercise delegate the operation to parent compoenent

#### FINISHED at 8 NOVEMBER 2024 10:17 AM

### **STEP 643 8 NOVEMBER 2024 10:17 AM**

1. delegate the operation between parent and child
2. update manage-customer.component.ts
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
      
      @Output()
      submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;
      
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.hasLength(this.customer.email) &&
          this.hasLength(this.customer.password) &&
          this.hasLength(this.customer.gender) &&
          this.customer.age !== undefined &&
          this.customer.age > 0;
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    
      onSubmit() {
        this.submit.emit(this.customer);
      }
    }
    
    ```
3. update manage-customer.component.html
    ```
    <div>
      <h2>Title here</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          [(ngModel)]="customer.name"
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          [(ngModel)]="customer.email"
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="password">Password</label>
        <input
          [(ngModel)]="customer.password"
          id="password"
          pInputText
          placeholder=""
          type="password"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          [(ngModel)]="customer.age"
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="gender">Gender</label>
        <select
          [(ngModel)]="customer.gender"
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option value="">Select Option</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button
        [disabled]="!isCustomerValid"
        (click)="onSubmit()"
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```

#### FINISHED at 8 NOVEMBER 2024 10:24 AM

### **STEP 644 8 NOVEMBER 2024 10:24 AM**

1. we are emiting the customer let's use in the customer component
2. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        (submit)="save($event)"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div>
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    ```
3. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
    
      constructor(
        private customerService: CustomerServiceService
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        console.log(customer);
      }
    }
    
    ```
4. if you test it the customer data will be printed on to console

#### FINISHED at 8 NOVEMBER 2024 10:37 AM

### **STEP 645 8 NOVEMBER 2024 10:37 AM**

1. update customer-service.service.ts
    ```
    
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient, HttpHeaders} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    import {environment} from "../../../environments/environment.development";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
    
      private readonly customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`;
    
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        return this.http.get<Array<CustomerDTO>>(this.customerUrl);
      }
      
      registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
        return this.http.post<void>(this.customerUrl, customer);
        
      }
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 10:59 AM

### **STEP 646 8 NOVEMBER 2024 10:59 AM**

1. exercise after clicking on submit clear the input and close the side bar and refresh the customers

#### FINISHED at 8 NOVEMBER 2024 11:00 AM

### **STEP 647 8 NOVEMBER 2024 11:00 AM**

1. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                }
              }
            )
        }
      }
    }
    
    ```
2. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div>
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    ```

#### FINISHED at 8 NOVEMBER 2024 11:16 AM

### **STEP 648 8 NOVEMBER 2024 11:16 AM**

1. styling the customer card
2. update the customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    ```

#### FINISHED at 8 NOVEMBER 2024 11:19 AM

### **STEP 649 8 NOVEMBER 2024 11:19 AM**

1. give notification that user creation success
2. go to primeng search for toast
3. <p-toast position="bottom-center"></p-toast>
4. this is what we need but we also need to inject some services we will do in next step

#### FINISHED at 8 NOVEMBER 2024 11:30 AM

### **STEP 650 8 NOVEMBER 2024 11:30 AM**

1. update app.module.ts
    ```text
    import {NgModule} from '@angular/core';
    import {BrowserModule} from '@angular/platform-browser';
    
    import {InputTextModule} from 'primeng/inputtext';
    import {AppRoutingModule} from './app-routing.module';
    import {AppComponent} from './app.component';
    import {FormsModule} from "@angular/forms";
    import {CommonModule} from "@angular/common";
    import {CustomerComponent} from './components/customer/customer.component';
    import {MenuBarComponent} from './components/menu-bar/menu-bar.component';
    import {AvatarModule} from 'primeng/avatar';
    import {AvatarGroupModule} from 'primeng/avatargroup';
    import {MenuModule} from "primeng/menu";
    import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
    import {MenuItemComponent} from './components/menu-item/menu-item.component';
    import {HeaderBarComponent} from './components/header-bar/header-bar.component';
    import {Button, ButtonDirective} from "primeng/button";
    import {Ripple} from "primeng/ripple";
    import {SidebarModule} from "primeng/sidebar";
    import {ManageCustomerComponent} from './components/mange-customer/mange-customer.component';
    import {LoginComponent} from './components/login/login.component';
    import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
    import {AuthenticationService} from "./services/authentication/authentication.service";
    import {MessageModule} from "primeng/message";
    import {JwtHelperService, JWT_OPTIONS} from '@auth0/angular-jwt';
    import {HttpInterceptorService} from "./services/interceptor/http-interceptor.service";
    import {CustomerCardComponent} from './components/customer-card/customer-card.component';
    import {CardModule} from "primeng/card";
    import {BadgeModule} from "primeng/badge";
    import {ToastModule} from "primeng/toast";
    import {MessageService} from "primeng/api";
    
    @NgModule({
      declarations: [
        AppComponent,
        CustomerComponent,
        MenuBarComponent,
        MenuItemComponent,
        HeaderBarComponent,
        ManageCustomerComponent,
        LoginComponent,
        CustomerCardComponent,
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        CommonModule,
        InputTextModule,
        AvatarModule,
        AvatarGroupModule,
        MenuModule,
        BrowserAnimationsModule,
        Button,
        ButtonDirective,
        Ripple,
        SidebarModule,
        MessageModule,
        CardModule,
        BadgeModule,
        ToastModule
      ],
      providers: [
        AuthenticationService,
        provideHttpClient(withInterceptorsFromDi()),
        {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
        JwtHelperService,
        {
          provide: HTTP_INTERCEPTORS,
          useClass: HttpInterceptorService,
          multi: true,
        },
        MessageService,
      ],
      bootstrap: [AppComponent]
    })
    export class AppModule {
    }
    
    ```
2. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-toast position="bottom-center"></p-toast>
    
    ```
3. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    }
    
    ```
4. now when customer gets registered you get a beautiful toast.

#### FINISHED at 8 NOVEMBER 2024 11:51 AM

### **STEP 651 8 NOVEMBER 2024 11:51 AM**

1. let give some more styling
2. different profile image for each
3. update customer-card.component.html
    ```text
    <p-card [style]="{width: '316px' }" styleClass="p-card-shadow text-center">
      <ng-template pTemplate="header">
        <div>
          <img class="border-round-top cover"
               height="120"
               alt="card"
               src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png">
        </div>
        <div class="flex justify-content-center" style="margin-top: -50px">
          <img
            class="customer-avatar"
            [src]="customerImage"
          >
        </div>
      </ng-template>
      <ng-template pTemplate="content">
        <p-badge [value]="''+(customerIndex+1)"></p-badge>
        <h2>{{ customer.name }}</h2>
        <p class="text-gray-600">{{ customer.email }}</p>
        <p class="text-gray-600">{{ customer.age }} | {{ customer.gender }}</p>
      </ng-template>
      <ng-template
        pTemplate="footer"
      >
        <button
          pButton
          label="Update"
          class="p-button-secondary p-button-outlined p-button-rounded"
        ></button>
        <button
          pButton
          label="Delete"
          class="p-button-danger p-button-outlined p-button-rounded ml-3"
        ></button>
    
      </ng-template>
    </p-card>
    
    ```
4. update customer-card.component.ts
    ```text
    import {Component, Input} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    
    @Component({
      selector: 'app-customer-card',
      templateUrl: './customer-card.component.html',
      styleUrl: './customer-card.component.scss'
    })
    export class CustomerCardComponent {
    
      @Input()
      customer: CustomerDTO = {};
    
      @Input()
      customerIndex: number = 0;
    
      get customerImage(): string {
        const gender = this.customer.gender === 'MALE' ? 'men' : 'women';
    
        return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`;
      }
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 12:16 AM

### **STEP 652 8 NOVEMBER 2024 12:16 AM**

1. exercise implement delete functionality with popup to delete confirmation

#### FINISHED at 8 NOVEMBER 2024 12:18 AM

### **STEP 653 8 NOVEMBER 2024 12:18 AM**

1. add click function to delete button in card
2. update customer-card.component.html
    ```text
    <p-card [style]="{width: '316px' }" styleClass="p-card-shadow text-center">
      <ng-template pTemplate="header">
        <div>
          <img class="border-round-top cover"
               height="120"
               alt="card"
               src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png">
        </div>
        <div class="flex justify-content-center" style="margin-top: -50px">
          <img
            class="customer-avatar"
            [src]="customerImage"
          >
        </div>
      </ng-template>
      <ng-template pTemplate="content">
        <p-badge [value]="''+(customerIndex+1)"></p-badge>
        <h2>{{ customer.name }}</h2>
        <p class="text-gray-600">{{ customer.email }}</p>
        <p class="text-gray-600">{{ customer.age }} | {{ customer.gender }}</p>
      </ng-template>
      <ng-template
        pTemplate="footer"
      >
        <button
          pButton
          label="Update"
          class="p-button-secondary p-button-outlined p-button-rounded"
        ></button>
        <button
          pButton
          label="Delete"
          class="p-button-danger p-button-outlined p-button-rounded ml-3"
          (click)="onDelete()"
        ></button>
    
      </ng-template>
    </p-card>
    
    ```
3. add the delete emitter as output and emit through function
4. update customer-card.component.ts
    ```
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    
    @Component({
      selector: 'app-customer-card',
      templateUrl: './customer-card.component.html',
      styleUrl: './customer-card.component.scss'
    })
    export class CustomerCardComponent {
    
      @Input()
      customer: CustomerDTO = {};
    
      @Input()
      customerIndex: number = 0;
    
      @Output()
      delete: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();
    
      get customerImage(): string {
        const gender = this.customer.gender === 'MALE' ? 'men' : 'women';
    
        return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`;
      }
    
      onDelete() {
        this.delete.emit(this.customer);
      }
    }
    
    ```
5. let use this delete function in parent customer component
6. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
                (delete)="deleteCustomer($event)"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-toast position="bottom-center"></p-toast>
    
    ```
7. lets just print the output data we got passed from customer-card
8. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        console.log(customer);
      }
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 1:01 AM

### **STEP 654 8 NOVEMBER 2024 1:01 AM**

1. lets add the notification pop
2. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
                (delete)="deleteCustomer($event)"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-confirmDialog
      header="Confirmation"
      icon="pi pi-exclamation-triangle"
      acceptLabel="Delete"
      acceptButtonStyleClass="p-button-danger"
      acceptIcon="pi pi-trash"
      rejectLabel="Cancel"
    />
    <p-toast position="bottom-center"></p-toast>
    
    ```
3. let's use this in our ui in next step

#### FINISHED at 8 NOVEMBER 2024 1:11 AM

### **STEP 655 8 NOVEMBER 2024 1:11 AM**

1. add confirmation service to app.module.ts
    ```text
    import {NgModule} from '@angular/core';
    import {BrowserModule} from '@angular/platform-browser';
    
    import {InputTextModule} from 'primeng/inputtext';
    import {AppRoutingModule} from './app-routing.module';
    import {AppComponent} from './app.component';
    import {FormsModule} from "@angular/forms";
    import {CommonModule} from "@angular/common";
    import {CustomerComponent} from './components/customer/customer.component';
    import {MenuBarComponent} from './components/menu-bar/menu-bar.component';
    import {AvatarModule} from 'primeng/avatar';
    import {AvatarGroupModule} from 'primeng/avatargroup';
    import {MenuModule} from "primeng/menu";
    import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
    import {MenuItemComponent} from './components/menu-item/menu-item.component';
    import {HeaderBarComponent} from './components/header-bar/header-bar.component';
    import {Button, ButtonDirective} from "primeng/button";
    import {Ripple} from "primeng/ripple";
    import {SidebarModule} from "primeng/sidebar";
    import {ManageCustomerComponent} from './components/mange-customer/mange-customer.component';
    import {LoginComponent} from './components/login/login.component';
    import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
    import {AuthenticationService} from "./services/authentication/authentication.service";
    import {MessageModule} from "primeng/message";
    import {JwtHelperService, JWT_OPTIONS} from '@auth0/angular-jwt';
    import {HttpInterceptorService} from "./services/interceptor/http-interceptor.service";
    import {CustomerCardComponent} from './components/customer-card/customer-card.component';
    import {CardModule} from "primeng/card";
    import {BadgeModule} from "primeng/badge";
    import {ToastModule} from "primeng/toast";
    import {ConfirmationService, MessageService} from "primeng/api";
    import {ConfirmDialogModule} from "primeng/confirmdialog";
    
    @NgModule({
      declarations: [
        AppComponent,
        CustomerComponent,
        MenuBarComponent,
        MenuItemComponent,
        HeaderBarComponent,
        ManageCustomerComponent,
        LoginComponent,
        CustomerCardComponent,
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        CommonModule,
        InputTextModule,
        AvatarModule,
        AvatarGroupModule,
        MenuModule,
        BrowserAnimationsModule,
        Button,
        ButtonDirective,
        Ripple,
        SidebarModule,
        MessageModule,
        CardModule,
        BadgeModule,
        ToastModule,
        ConfirmDialogModule
      ],
      providers: [
        AuthenticationService,
        provideHttpClient(withInterceptorsFromDi()),
        {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
        JwtHelperService,
        {
          provide: HTTP_INTERCEPTORS,
          useClass: HttpInterceptorService,
          multi: true,
        },
        MessageService,
        ConfirmationService
      ],
      bootstrap: [AppComponent]
    })
    export class AppModule {
    }
    
    ```
2. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              console.log('Delete pressed');
            }
          }
        )
      }
    }
    
    ```
3. now you dialogbox will popup and if you press delete you get a console output for now.

#### FINISHED at 8 NOVEMBER 2024 1:23 AM

### **STEP 656 8 NOVEMBER 2024 1:23 AM**

1. access the delete end point in customer service
2. update customer-service.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient, HttpHeaders} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    import {environment} from "../../../environments/environment.development";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
    
      private readonly customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`;
    
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        return this.http.get<Array<CustomerDTO>>(this.customerUrl);
      }
    
      registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
        return this.http.post<void>(this.customerUrl, customer);
    
      }  
      
      deleteCustomer(id: number | undefined): Observable<void> {
        return this.http.delete<void>(`${this.customerUrl}/${id}` );
      }
      
      
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 1:27 AM

### **STEP 657 8 NOVEMBER 2024 1:27 AM**

1. update customer.component.ts
    ```
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    }
    
    ```
2. test now it should be working fine

#### FINISHED at 8 NOVEMBER 2024 1:46 AM

### **STEP 658 8 NOVEMBER 2024 1:46 AM**

1. we start implementing update customer
2. we need a model of customer update request as we can update only 3 things

#### FINISHED at 8 NOVEMBER 2024 1:48 AM

### **STEP 659 8 NOVEMBER 2024 1:48 AM**

1. create customer-update-request.ts file under the models folder

```text
export interface CustomerUpdateRequest {
  name?: string;
  age?: number;
  email?: string;
}

```

#### FINISHED at 8 NOVEMBER 2024 1:51 AM

### **STEP 660 8 NOVEMBER 2024 1:51 AM**

1. update customer-service.service.ts
    ```text
    import {Injectable} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {Observable} from "rxjs";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {HttpClient, HttpHeaders} from "@angular/common/http";
    import {CustomerDTO} from "../../models/customer-dto";
    import {environment} from "../../../environments/environment.development";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {CustomerUpdateRequest} from "../../models/customer-update-request";
    
    @Injectable({
      providedIn: 'root'
    })
    export class CustomerServiceService {
    
      private readonly customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`;
    
    
      constructor(private http: HttpClient) {
      }
    
      findAll(): Observable<Array<CustomerDTO>> {
        return this.http.get<Array<CustomerDTO>>(this.customerUrl);
      }
    
      registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
        return this.http.post<void>(this.customerUrl, customer);
    
      }
    
      deleteCustomer(id: number | undefined): Observable<void> {
        return this.http.delete<void>(`${this.customerUrl}/${id}`);
      }
      
      updateCustomer(id: number | undefined, customer: CustomerUpdateRequest): Observable<void> {
        return this.http.put<void>(`${this.customerUrl}/${id}`,customer);
      }
    
    
    }
    
    ```

#### FINISHED at 8 NOVEMBER 2024 1:53 AM

### **STEP 661 8 NOVEMBER 2024 1:53 AM**

1. update manage-customer.component.ts by creating a input of operation
    ```
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
      @Input()
      operation: 'create' | 'update' ='create';
    
      @Output()
      submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;
    
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.hasLength(this.customer.email) &&
          this.hasLength(this.customer.password) &&
          this.hasLength(this.customer.gender) &&
          this.customer.age !== undefined &&
          this.customer.age > 0;
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    
      onSubmit() {
        this.submit.emit(this.customer);
      }
    }
    
    ```
2. bind it conditional in html
    ```text
    <div>
      <h2>Title here</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          [(ngModel)]="customer.name"
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          [(ngModel)]="customer.email"
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="password">Password</label>
        <input
          [(ngModel)]="customer.password"
          id="password"
          pInputText
          placeholder=""
          type="password"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          [(ngModel)]="customer.age"
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="gender">Gender</label>
        <select
          [(ngModel)]="customer.gender"
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option value="">Select Option</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button
        [disabled]="!isCustomerValid"
        (click)="onSubmit()"
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```
3. update the customer.component.ts by adding operation variable in it
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
      
      operation: 'create' | 'update' ='create';
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    }
    
    ```
4. update customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
        [operation]="operation"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
                (delete)="deleteCustomer($event)"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-confirmDialog
      header="Confirmation"
      icon="pi pi-exclamation-triangle"
      acceptLabel="Delete"
      acceptButtonStyleClass="p-button-danger"
      acceptIcon="pi pi-trash"
      rejectLabel="Cancel"
    />
    <p-toast position="bottom-center"></p-toast>
    
    ```

#### FINISHED at 8 NOVEMBER 2024 2:06 AM

### **STEP 662 8 NOVEMBER 2024 2:06 AM**

1. when we click on create customer button we see title we will change according to operation we do
2. update manage-customer.component.ts
    ```text
    import {Component, EventEmitter, input, Input, OnInit, Output} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent implements OnInit {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
      @Input()
      operation: 'create' | 'update' = 'create';
    
      title = 'New Customer';
    
      @Output()
      submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;
    
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.hasLength(this.customer.email) &&
          this.hasLength(this.customer.password) &&
          this.hasLength(this.customer.gender) &&
          this.customer.age !== undefined &&
          this.customer.age > 0;
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    
      onSubmit() {
        this.submit.emit(this.customer);
      }
    
      ngOnInit(): void {
        if (this.operation === 'update') {
          this.title = 'Update Customer';
        }
      }
    }
    
    ```
3. update mange-customer.component.html
    ```
    <div>
      <h2>{{ title }}</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          [(ngModel)]="customer.name"
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          [(ngModel)]="customer.email"
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="password">Password</label>
        <input
          [(ngModel)]="customer.password"
          id="password"
          pInputText
          placeholder=""
          type="password"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          [(ngModel)]="customer.age"
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="gender">Gender</label>
        <select
          [(ngModel)]="customer.gender"
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option value="">Select Option</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button
        [disabled]="!isCustomerValid"
        (click)="onSubmit()"
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```

#### FINISHED at 8 NOVEMBER 2024 2:22 AM

### **STEP 663 8 NOVEMBER 2024 2:22 AM**

1. implementing the update button
2. update customer-card.component.ts
    ```
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerUpdateRequest} from "../../models/customer-update-request";
    
    @Component({
      selector: 'app-customer-card',
      templateUrl: './customer-card.component.html',
      styleUrl: './customer-card.component.scss'
    })
    export class CustomerCardComponent {
    
      @Input()
      customer: CustomerDTO = {};
    
      @Input()
      customerIndex: number = 0;
    
      @Output()
      delete: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();
    
      @Output()
      update: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();
    
      get customerImage(): string {
        const gender = this.customer.gender === 'MALE' ? 'men' : 'women';
    
        return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`;
      }
    
      onDelete() {
        this.delete.emit(this.customer);
      }
    
      onUpdate() {
        this.update.emit(this.customer);
      }
    }
    
    ```
3. update customer-card.component.html
    ```text
    <p-card [style]="{width: '316px' }" styleClass="p-card-shadow text-center">
      <ng-template pTemplate="header">
        <div>
          <img class="border-round-top cover"
               height="120"
               alt="card"
               src="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png">
        </div>
        <div class="flex justify-content-center" style="margin-top: -50px">
          <img
            class="customer-avatar"
            [src]="customerImage"
          >
        </div>
      </ng-template>
      <ng-template pTemplate="content">
        <p-badge [value]="''+(customerIndex+1)"></p-badge>
        <h2>{{ customer.name }}</h2>
        <p class="text-gray-600">{{ customer.email }}</p>
        <p class="text-gray-600">{{ customer.age }} | {{ customer.gender }}</p>
      </ng-template>
      <ng-template
        pTemplate="footer"
      >
        <button
          pButton
          label="Update"
          class="p-button-secondary p-button-outlined p-button-rounded"
          (click)="onUpdate()"
        ></button>
        <button
          pButton
          label="Delete"
          class="p-button-danger p-button-outlined p-button-rounded ml-3"
          (click)="onDelete()"
        ></button>
    
      </ng-template>
    </p-card>
    
    ```
4. update customer.component.html
    ```
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
        [operation]="operation"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="display = true"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
                (delete)="deleteCustomer($event)"
                (update)="updateCustomer($event)"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-confirmDialog
      header="Confirmation"
      icon="pi pi-exclamation-triangle"
      acceptLabel="Delete"
      acceptButtonStyleClass="p-button-danger"
      acceptIcon="pi pi-trash"
      rejectLabel="Cancel"
    />
    <p-toast position="bottom-center"></p-toast>
    
    ```
5. update customer.component.ts
    ```
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
      operation: 'create' | 'update' = 'create';
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    
      updateCustomer(customerDTO: CustomerDTO) {
        this.display = true;
        this.customer = customerDTO;
        this.operation = 'update';
      }
    }
    
    ```

#### FINISHED at 9 NOVEMBER 2024 7:08 PM

### **STEP 664 9 NOVEMBER 2024 7:09 PM**

1. we got two bugs
2. one - submit button in update is disabled
3. two - once you click submit side bar stuck with update details even on clicking create
4. lets fix bug two first
5. update customer.component.html by adding a create customer function to create button
    ```
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-mange-customer
        [customer]="customer"
        (submit)="save($event)"
        [operation]="operation"
      ></app-mange-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="createCustomer()"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
                (delete)="deleteCustomer($event)"
                (update)="updateCustomer($event)"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-confirmDialog
      header="Confirmation"
      icon="pi pi-exclamation-triangle"
      acceptLabel="Delete"
      acceptButtonStyleClass="p-button-danger"
      acceptIcon="pi pi-trash"
      rejectLabel="Cancel"
    />
    <p-toast position="bottom-center"></p-toast>
    
    ```
6. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
      operation: 'create' | 'update' = 'create';
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
    
        if (customer) {
          this.customerService.registerCustomer(customer)
            .subscribe(
              {
                next: () => {
                  this.display = false;
                  this.findAllCustomers();
                  this.customer = {};
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Success',
                      detail: `Customer ${customer.name} has been successfully saved.`,
                    });
                }
              }
            )
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    
      updateCustomer(customerDTO: CustomerDTO) {
        this.display = true;
        this.customer = customerDTO;
        this.operation = 'update';
      }
    
      createCustomer() {
        this.display = true;
        this.customer = {};
        this.operation = 'create';
      }
    }
    
    ```

#### FINISHED at 9 NOVEMBER 2024 7:21 PM

### **STEP 665 9 NOVEMBER 2024 7:21 PM**

1. Fixing the submit button
2. update manage-customer.component.ts
    ```text
    import {Component, EventEmitter, input, Input, OnInit, Output} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-mange-customer',
      templateUrl: './mange-customer.component.html',
      styleUrl: './mange-customer.component.scss'
    })
    export class ManageCustomerComponent implements OnInit {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
      @Input()
      operation: 'create' | 'update' = 'create';
    
      title = 'New Customer';
    
      @Output()
      submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;
    
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.customer.age !== undefined && this.customer.age > 0 &&
          this.hasLength(this.customer.email) &&
    
          (this.operation === 'update' ||
            this.hasLength(this.customer.password) &&
            this.hasLength(this.customer.gender));
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    
      onSubmit() {
        this.submit.emit(this.customer);
      }
    
      ngOnInit(): void {
        if (this.operation === 'update') {
          this.title = 'Update Customer';
        }
      }
    }
    
    ```

#### FINISHED at 9 NOVEMBER 2024 7:30 PM

### **STEP 666 10 NOVEMBER 2024 10:06 AM**

1. task:- click on submit to update the customer in backend
2. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
      operation: 'create' | 'update' = 'create';
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
    
        if (customer) {
          if(this.operation==="create"){
            this.customerService.registerCustomer(customer)
              .subscribe(
                {
                  next: () => {
                    this.display = false;
                    this.findAllCustomers();
                    this.customer = {};
                    this.messageService.add(
                      {
                        severity: 'success',
                        summary: 'Success',
                        detail: `Customer ${customer.name} has been successfully saved.`,
                      });
                  }
                });
          }else if(this.operation==="update"){
            this.customerService.updateCustomer(1,customer)
              .subscribe(
                {
                  next: () => {
                    this.display = false;
                    this.findAllCustomers();
                    this.customer = {};
                    this.messageService.add(
                      {
                        severity: 'success',
                        summary: 'Customer Updated',
                        detail: `Customer ${customer.name} has been successfully updated.`,
                      });
                  }
                });
          }
          
        } 
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    
      updateCustomer(customerDTO: CustomerDTO) {
        this.display = true;
        this.customer = customerDTO;
        this.operation = 'update';
      }
    
      createCustomer() {
        this.display = true;
        this.customer = {};
        this.operation = 'create';
      }
    }
    
    ```
3. if you check in here we have hardcoded the id
4. we will fix this in next step

#### FINISHED at 10 NOVEMBER 2024 10:21 AM

### **STEP 667 10 NOVEMBER 2024 10:21 AM**

1. update customer-registration-request.ts
    ```text
    export interface CustomerRegistrationRequest {
      id?: number;
      name?: string;
      email?: string;
      password?: string;
      age?: number;
      gender?: 'MALE' | 'FEMALE';
    }
    
    ```
2. update customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
      operation: 'create' | 'update' = 'create';
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
    
        if (customer) {
          if (this.operation === "create") {
            this.customerService.registerCustomer(customer)
              .subscribe(
                {
                  next: () => {
                    this.display = false;
                    this.findAllCustomers();
                    this.customer = {};
                    this.messageService.add(
                      {
                        severity: 'success',
                        summary: 'Success',
                        detail: `Customer ${customer.name} has been successfully saved.`,
                      });
                  }
                });
          } else if (this.operation === "update") {
            this.customerService.updateCustomer(customer.id, customer)
              .subscribe(
                {
                  next: () => {
                    this.display = false;
                    this.findAllCustomers();
                    this.customer = {};
                    this.messageService.add(
                      {
                        severity: 'success',
                        summary: 'Customer Updated',
                        detail: `Customer ${customer.name} has been successfully updated.`,
                      });
                  }
                });
          }
    
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    
      updateCustomer(customerDTO: CustomerDTO) {
        this.display = true;
        this.customer = customerDTO;
        this.operation = 'update';
      }
    
      createCustomer() {
        this.display = true;
        this.customer = {};
        this.operation = 'create';
      }
    }
    
    ```

#### FINISHED at 10 NOVEMBER 2024 10:49 AM

### **STEP 668 10 NOVEMBER 2024 10:49 AM**

1. bug with the title fixed it in here
2. update manage-custome.component.html
    ```text
    <div>
      <h2>{{ operation === 'create' ? 'New Customer' : 'Update Customer' }}</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          [(ngModel)]="customer.name"
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          [(ngModel)]="customer.email"
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="password">Password</label>
        <input
          [(ngModel)]="customer.password"
          id="password"
          pInputText
          placeholder=""
          type="password"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          [(ngModel)]="customer.age"
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="gender">Gender</label>
        <select
          [(ngModel)]="customer.gender"
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option value="">Select Option</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button
        [disabled]="!isCustomerValid"
        (click)="onSubmit()"
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```
3. let clean it up in manage-customer.component.ts as we dont need onInit anymore
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-manage-customer',
      templateUrl: './manage-customer.component.html',
      styleUrl: './manage-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
      @Input()
      operation: 'create' | 'update' = 'create';
    
      @Output()
      submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;
    
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.customer.age !== undefined && this.customer.age > 0 &&
          this.hasLength(this.customer.email) &&
    
          (this.operation === 'update' ||
            this.hasLength(this.customer.password) &&
            this.hasLength(this.customer.gender));
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    
      onSubmit() {
        this.submit.emit(this.customer);
      }
    
    
    }
    
    ```

#### FINISHED at 10 NOVEMBER 2024 11:19 AM

### **STEP 669 10 NOVEMBER 2024 11:19 AM**

1. task making cancel button work
2. add click event on cancel button in manage-customer.component.html
    ```text
    <div>
      <h2>{{ operation === 'create' ? 'New Customer' : 'Update Customer' }}</h2>
      <div class="field">
        <label for="firstname">Name</label>
        <input
          [(ngModel)]="customer.name"
          id="firstname"
          pInputText
          placeholder="Mary Jane"
          type="text"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input
          [(ngModel)]="customer.email"
          id="email"
          pInputText
          placeholder="MaryJane@gmail.com"
          type="email"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="password">Password</label>
        <input
          [(ngModel)]="customer.password"
          id="password"
          pInputText
          placeholder=""
          type="password"
          class="w-full"
        >
      </div>
      <div class="field">
        <label for="age">Age</label>
        <input
          [(ngModel)]="customer.age"
          id="age"
          pInputText
          placeholder="16"
          type="number"
          class="w-full"
        >
      </div>
      <div class="field" *ngIf="operation === 'create'">
        <label for="gender">Gender</label>
        <select
          [(ngModel)]="customer.gender"
          id="gender"
          class="p-dropdown w-full custom-select"
        >
          <option value="">Select Option</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button
        [disabled]="!isCustomerValid"
        (click)="onSubmit()"
        pButton
        label="submit"
        class="w-full"
      >
      </button>
      <button
        (click)="onCancel()"
        pButton
        label="cancel"
        class="w-full p-button-outlined p-button-danger mt-2"
      >
      </button>
    </div>
    
    ```
3. create this cancel method in manage-customer.component.ts
    ```text
    import {Component, EventEmitter, Input, Output} from '@angular/core';
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    
    @Component({
      selector: 'app-manage-customer',
      templateUrl: './manage-customer.component.html',
      styleUrl: './manage-customer.component.scss'
    })
    export class ManageCustomerComponent {
    
      @Input()
      customer: CustomerRegistrationRequest = {};
      @Input()
      operation: 'create' | 'update' = 'create';
    
      @Output()
      submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;
    
      @Output()
      cancel: EventEmitter<void> = new EventEmitter<void>();
    
      get isCustomerValid(): boolean {
        return this.hasLength(this.customer.name) &&
          this.customer.age !== undefined && this.customer.age > 0 &&
          this.hasLength(this.customer.email) &&
    
          (this.operation === 'update' ||
            this.hasLength(this.customer.password) &&
            this.hasLength(this.customer.gender));
      }
    
      private hasLength(input: string | undefined): boolean {
        return input !== null && input !== undefined && input.length > 3;
    
      }
    
      onSubmit() {
        this.submit.emit(this.customer);
      }
    
    
      protected readonly oncancel = oncancel;
    
      onCancel() {
        this.cancel.emit();
      }
    }
    
    ```
4. add this to customer.component.html
    ```text
    <p-sidebar
      [(visible)]="display"
      [position]="'right'"
      styleClass="p-sidebar-lg"
      [transitionOptions]="'300ms cubic-bezier(0,0, 0.2,1)'"
    >
      <app-manage-customer
        [customer]="customer"
        (submit)="save($event)"
        [operation]="operation"
        (cancel)="cancel()"
      ></app-manage-customer>
    </p-sidebar>
    
    <div class="flex align-items-stretch" style="height: 99vh;">
      <div class="flex flex-column align-items-stretch border-right-1 col-2 border-gray-200">
        <app-menu-bar/>
      </div>
      <div class="flex flex-column flex-grow-1">
        <div>
          <app-header-bar/>
        </div>
        <div class="main-container p-3">
          <button
            pButton
            icon="pi pi-plus"
            label="create customer"
            class="p-button-success"
            (click)="createCustomer()"
          >
          </button>
          <div>
            <div class="flex justify-content-center align-items-center flex-wrap gap-5 mt-4">
              <app-customer-card
                *ngFor="let customer of customers; let index=index"
                [customer]="customer"
                [customerIndex]="index"
                (delete)="deleteCustomer($event)"
                (update)="updateCustomer($event)"
              ></app-customer-card>
            </div>
          </div>
        </div>
    
      </div>
    </div>
    
    <p-confirmDialog
      header="Confirmation"
      icon="pi pi-exclamation-triangle"
      acceptLabel="Delete"
      acceptButtonStyleClass="p-button-danger"
      acceptIcon="pi pi-trash"
      rejectLabel="Cancel"
    />
    <p-toast position="bottom-center"></p-toast>
    
    ```
5. add cancel method in customer.component.ts
    ```text
    import {Component, OnInit} from '@angular/core';
    import {CustomerDTO} from "../../models/customer-dto";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {ConfirmationService, MessageService} from "primeng/api";
    
    @Component({
      selector: 'app-customer',
      templateUrl: './customer.component.html',
      styleUrl: './customer.component.scss'
    })
    export class CustomerComponent implements OnInit {
      display: boolean = false;
    
      operation: 'create' | 'update' = 'create';
    
    
      customers: CustomerDTO[] = [];
      customer: CustomerRegistrationRequest = {};
    
      constructor(
        private customerService: CustomerServiceService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
      ) {
      }
    
      ngOnInit(): void {
        this.findAllCustomers();
      }
    
      private findAllCustomers() {
        this.customerService.findAll()
          .subscribe(
            {
              next: (data: CustomerDTO[]) => {
                this.customers = data;
                console.log(this.customers);
              }
            }
          )
      }
    
      save(customer: CustomerRegistrationRequest) {
    
        if (customer) {
          if (this.operation === "create") {
            this.customerService.registerCustomer(customer)
              .subscribe(
                {
                  next: () => {
                    this.display = false;
                    this.findAllCustomers();
                    this.customer = {};
                    this.messageService.add(
                      {
                        severity: 'success',
                        summary: 'Success',
                        detail: `Customer ${customer.name} has been successfully saved.`,
                      });
                  }
                });
          } else if (this.operation === "update") {
            this.customerService.updateCustomer(customer.id, customer)
              .subscribe(
                {
                  next: () => {
                    this.display = false;
                    this.findAllCustomers();
                    this.customer = {};
                    this.messageService.add(
                      {
                        severity: 'success',
                        summary: 'Customer Updated',
                        detail: `Customer ${customer.name} has been successfully updated.`,
                      });
                  }
                });
          }
    
        }
      }
    
      deleteCustomer(customer: CustomerDTO) {
        this.confirmationService.confirm(
          {
            header: 'Delete Customer',
            message: `Are you sure to delete ${customer.name}?`,
            accept: () => {
              this.customerService.deleteCustomer(customer.id)
                .subscribe(
                  {
                    next: () => {
                      this.findAllCustomers();
                      this.messageService.add(
                        {
                          severity: 'success',
                          summary: 'Customer deleted',
                          detail: `Customer ${customer.name} has been successfully deleted.`,
                        }
                      )
                    }
                  }
                )
            }
          }
        )
      }
    
      updateCustomer(customerDTO: CustomerDTO) {
        this.display = true;
        this.customer = customerDTO;
        this.operation = 'update';
      }
    
      createCustomer() {
        this.display = true;
        this.customer = {};
        this.operation = 'create';
      }
    
      cancel() {
        this.display = false;
        this.customer = {};
        this.operation = 'create';
      }
    }
    
    ```

#### FINISHED at 10 NOVEMBER 2024 11:27 AM

### **STEP 670 10 NOVEMBER 2024 11:27 AM**

1. tasks: display the customer login inform on the right top and implement signout
2. update header-bar.component.ts
    ```
    import {Component} from '@angular/core';
    import {MenuItem} from "primeng/api";
    
    @Component({
      selector: 'app-header-bar',
      templateUrl: './header-bar.component.html',
      styleUrl: './header-bar.component.scss'
    })
    export class HeaderBarComponent {
      items: MenuItem[] = [
        {label: 'Profile', icon: 'pi pi-user'},
        {label: 'Settings', icon: 'pi pi-cog'},
        {separator: true},
        {label: 'Sign out', icon: 'pi pi-sign-out'}
      ];
    
      get userName(): string {
        return '--';
      }
    
      get userRole(): string {
        return '--';
      }
    
    }
    
    ```
3. bind these two username and role in html
    ```text
    <div class="flex flex-column align-items-end border-bottom-1 border-gray-200 pt-3 pb-3">
      <div class="flex flex-row align-items-center">
        <button
          pButton
          pRipple
          type="button"
          icon="pi pi-bell"
          class="p-button-rounded p-button-secondary p-button-text mr-3"
        >
        </button>
        <p-avatar
          image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
          size="normal"
          shape="circle"
          class="mr-2"
    
        ></p-avatar>
        <div>
          <p class="text-base mb-0 mt-1 text-gray-700">{{ userName }}</p>
          <p class="text-sm mb-1 mt-0 text-gray-500">{{ userRole }}</p>
        </div>
        <div>
          <button
            (click)="menu.toggle($event)"
            class="p-button-secondary p-button-text"
            icon="pi pi-angle-down"
            pButton
            pRipple
            type="button"
          ></button>
          <p-menu #menu [model]="items" [popup]="true"/>
        </div>
      </div>
    </div>
    
    ```
4. Exercise is to replace those test values with real values

#### FINISHED at 10 NOVEMBER 2024 12:00 PM

### **STEP 671 10 NOVEMBER 2024 12:00 PM**

1. update header-bar.component.ts
    ```text
    import {Component} from '@angular/core';
    import {MenuItem} from "primeng/api";
    import {AuthenticationResponse} from "../../models/authentication-response";
    
    @Component({
      selector: 'app-header-bar',
      templateUrl: './header-bar.component.html',
      styleUrl: './header-bar.component.scss'
    })
    export class HeaderBarComponent {
      items: MenuItem[] = [
        {label: 'Profile', icon: 'pi pi-user'},
        {label: 'Settings', icon: 'pi pi-cog'},
        {separator: true},
        {label: 'Sign out', icon: 'pi pi-sign-out'}
      ];
    
      storedUser = localStorage.getItem('user');
    
      get userName(): string {
    
        if (this.storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(this.storedUser);
          if (authResponse && authResponse.customerDTO && authResponse.customerDTO.username) {
            return authResponse.customerDTO.username;
          }
        }
        return '--';
      }
    
      get userRole(): string {
        if (this.storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(this.storedUser);
          if (authResponse && authResponse.customerDTO && authResponse.customerDTO.roles) {
            return authResponse.customerDTO.roles[0];
          }
        }
        return '--';
      }
    
    }
    
    ```
2. update header-bar.component.html
    ```text
    <div class="flex flex-column align-items-end border-bottom-1 border-gray-200 pt-3 pb-3">
      <div class="flex flex-row align-items-center">
        <button
          pButton
          pRipple
          type="button"
          icon="pi pi-bell"
          class="p-button-rounded p-button-secondary p-button-text mr-3"
        >
        </button>
        <p-avatar
          image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
          size="normal"
          shape="circle"
          class="mr-2"
    
        ></p-avatar>
        <div>
          <p class="text-base mb-0 mt-1 text-gray-700">{{ userName }}</p>
          <p class="text-sm mb-1 mt-0 text-gray-500">{{ userRole }}</p>
        </div>
        <div>
          <button
            (click)="menu.toggle($event)"
            class="p-button-secondary p-button-text"
            icon="pi pi-angle-down"
            pButton
            pRipple
            type="button"
          ></button>
          <p-menu #menu [model]="items" [popup]="true"/>
        </div>
      </div>
    </div>
    
    ```

#### FINISHED at 10 NOVEMBER 2024 12:07 PM

### **STEP 672 10 NOVEMBER 2024 12:07 PM**

1. update header-bar.component.ts
    ```
    import {Component} from '@angular/core';
    import {MenuItem, MenuItemCommandEvent} from "primeng/api";
    import {AuthenticationResponse} from "../../models/authentication-response";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    import {Router} from "@angular/router";
    
    @Component({
      selector: 'app-header-bar',
      templateUrl: './header-bar.component.html',
      styleUrl: './header-bar.component.scss'
    })
    export class HeaderBarComponent {
    
      constructor(
        private router: Router,
      ) {
      }
    
      items: MenuItem[] = [
        {label: 'Profile', icon: 'pi pi-user'},
        {label: 'Settings', icon: 'pi pi-cog'},
        {separator: true},
        {
          label: 'Sign out',
          icon: 'pi pi-sign-out',
          command: () => {
            localStorage.clear();
            this.router.navigate(['login']);
          }
        }
      ];
    
      storedUser = localStorage.getItem('user');
    
      get userName(): string {
    
        if (this.storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(this.storedUser);
          if (authResponse && authResponse.customerDTO && authResponse.customerDTO.username) {
            return authResponse.customerDTO.username;
          }
        }
        return '--';
      }
    
      get userRole(): string {
        if (this.storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(this.storedUser);
          if (authResponse && authResponse.customerDTO && authResponse.customerDTO.roles) {
            return authResponse.customerDTO.roles[0];
          }
        }
        return '--';
      }
    
    }
    
    ```

#### FINISHED at 10 NOVEMBER 2024 12:18 PM

### **STEP 673 10 NOVEMBER 2024 12:18 PM**

1. exercise - implement signuppage

#### FINISHED at 10 NOVEMBER 2024 12:20 PM

### **STEP 674 10 NOVEMBER 2024 12:20 PM**

1. ```ng g c Register```

#### FINISHED at 10 NOVEMBER 2024 12:21 PM

### **STEP 675 10 NOVEMBER 2024 12:21 PM**

1. update app.routing.module.ts to add routing to register page
    ```
    import {NgModule} from '@angular/core';
    import {RouterModule, Routes} from '@angular/router';
    import {CustomerComponent} from "./components/customer/customer.component";
    import {LoginComponent} from "./components/login/login.component";
    import {AccessGuardService} from "./services/guard/access-guard.service";
    import {RegisterComponent} from "./components/register/register.component";
    
    
    const routes: Routes = [
      {
        path: 'customers',
        component: CustomerComponent,
        canActivate: [AccessGuardService]
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'register',
        component: RegisterComponent
      },
    
    ]
    
    @NgModule({
      imports: [RouterModule.forRoot(routes)],
      exports: [RouterModule]
    })
    export class AppRoutingModule {
    }
    
    ```
2. update login.component.html to redirect to register when click on signup
    ```
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Sign in into your Account.</h1>
          </div>
          <div>
            <p-message *ngIf="errorMsg" severity="error" [text]="errorMsg" styleClass="mb-3"></p-message>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              [(ngModel)]="authenticationRequest.username"
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              [(ngModel)]="authenticationRequest.password"
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <button
            pButton
            label="Login"
            class="w-full"
            (click)="login()"
          >
          </button>
          <button
            pButton
            label="Don't have an account? Signup now!"
            class="w-full p-button-link pl-0"
            (click)="register()"
          >
          </button>
    
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```
3. update login.component.html to redirect to register when click on signup
    ```text
    import {Component} from '@angular/core';
    import {AuthenticationRequest} from "../../models/authentication-request";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    import {Router} from "@angular/router";
    
    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrl: './login.component.scss'
    })
    export class LoginComponent {
      authenticationRequest: AuthenticationRequest = {};
      errorMsg: string = '';
    
      constructor(
        private authenticationService: AuthenticationService,
        private router: Router
      ) {
      }
    
    
      login() {
        this.errorMsg = '';
        this.authenticationService.login(this.authenticationRequest)
          .subscribe({
            next: (authenticationResponse) => {
              // console.log(authenticationResponse);
              localStorage.setItem('user', JSON.stringify(authenticationResponse));
              this.router.navigate(['customers']);
            },
            error: (error) => {
              if (error.error.statusCode === 401) {
                this.errorMsg = 'Login and / or pass is incorrect';
    
              }
            }
          });
      }
    
      register() {
        this.router.navigate(['register']);
      }
    }
    
    
    ```

#### FINISHED at 10 NOVEMBER 2024 12:37 PM

### **STEP 676 10 NOVEMBER 2024 12:37 PM**

1. copy some content from login component
2. update register.component.html
    ```
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Signup for a account.</h1>
          </div>
          <div>
            <p-message *ngIf="errorMsg" severity="error" [text]="errorMsg" styleClass="mb-3"></p-message>
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <button
            pButton
            label="Login"
            class="w-full"
          >
          </button>
    
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```
3. update register.component.ts
    ```text
    import {Component} from '@angular/core';
    
    @Component({
      selector: 'app-register',
      templateUrl: './register.component.html',
      styleUrl: './register.component.scss'
    })
    export class RegisterComponent {
      errorMsg = '';
    
    }
    
    ```
4. update register.component.scss
    ```
    .right-container {
      background-image: linear-gradient(to right, #2b6cb0, #6b46c1);
    
      .background {
        object-fit: scale-down;
        height: auto;
        max-width: 100%;
      }
    
      a {
        text-decoration: none;
        color: #ffffff;
      }
    }
    
    ```

#### FINISHED at 10 NOVEMBER 2024 1:45 PM

### **STEP 677 10 NOVEMBER 2024 1:52 PM**

1. update register.component.html
    ```
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Signup for a account.</h1>
          </div>
          <div>
            <p-message *ngIf="errorMsg" severity="error" [text]="errorMsg" styleClass="mb-3"></p-message>
          </div>
          <div class="field">
            <label for="name">Name</label>
            <input
              id="name"
              pInputText
              placeholder="Mary Jane"
              type="text"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="age">Age</label>
            <input
              id="age"
              pInputText
              placeholder="16"
              type="number"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="gender">Gender</label>
            <select
              id="gender"
              class="p-dropdown w-full custom-select"
            >
              <option value="">Select Option</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <button
            pButton
            label="Create an Account"
            class="w-full"
          >
          </button>
          <button
            (click)="login()"
            pButton
            label="Have an account? Login now!"
            class="w-full p-button-link pl-0"
    
          >
          </button>
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```
2. update register.component.ts
    ```
    import {Component} from '@angular/core';
    import {Router} from "@angular/router";
    
    @Component({
      selector: 'app-register',
      templateUrl: './register.component.html',
      styleUrl: './register.component.scss'
    })
    export class RegisterComponent {
      constructor(private router: Router,) {
      }
    
      errorMsg: any;
    
    
      login() {
        this.router.navigate(['login']);
      }
    }
    
    ```
3. update register.component.scss
    ```text
    .right-container {
      background-image: linear-gradient(to right, #2b6cb0, #6b46c1);
    
      .background {
        object-fit: scale-down;
        height: auto;
        max-width: 100%;
      }
    
      a {
        text-decoration: none;
        color: #ffffff;
      }
    }
    
    .custom-select {
      padding: 0.75rem 0.75rem;
      border: 1px solid #ced4da;
    
    }
    
    
    
    ```

#### FINISHED at 10 NOVEMBER 2024 2:22 PM

### **STEP 678 10 NOVEMBER 2024 2:22 PM**

1. update register.component.html
    ```
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Signup for a account.</h1>
          </div>
          <div>
            <p-message *ngIf="errorMsg" severity="error" [text]="errorMsg" styleClass="mb-3"></p-message>
          </div>
          <div class="field">
            <label for="name">Name</label>
            <input
              [(ngModel)]="customer.name" ]
              id="name"
              pInputText
              placeholder="Mary Jane"
              type="text"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              [(ngModel)]="customer.email" ]
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              [(ngModel)]="customer.password"
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="age">Age</label>
            <input
              [(ngModel)]="customer.age"
              id="age"
              pInputText
              placeholder="16"
              type="number"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="gender">Gender</label>
            <select
              [(ngModel)]="customer.gender"
              id="gender"
              class="p-dropdown w-full custom-select"
            >
              <option value="">Select Option</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <button
            pButton
            label="Create an Account"
            class="w-full"
          >
          </button>
          <button
            (click)="login()"
            pButton
            label="Have an account? Login now!"
            class="w-full p-button-link pl-0"
    
          >
          </button>
          <app-manage-customer></app-manage-customer>
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```
2. update register.component.ts
    ```text
    import {Component} from '@angular/core';
    import {Router} from "@angular/router";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    
    @Component({
      selector: 'app-register',
      templateUrl: './register.component.html',
      styleUrl: './register.component.scss'
    })
    export class RegisterComponent {
    
      errorMsg: any;
    
      customer: CustomerRegistrationRequest = {};
    
      constructor(private router: Router,) {
      }
    
      login() {
        this.router.navigate(['login']);
      }
    }
    
    ```

#### FINISHED at 10 NOVEMBER 2024 2:22 PM

### **STEP 679 10 NOVEMBER 2024 2:22 PM**

1. after registration login the customer and redirect to customers page
2. update register.component.ts
    ```text
    import {Component} from '@angular/core';
    import {Router} from "@angular/router";
    import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
    import {CustomerServiceService} from "../../services/customer/customer-service.service";
    import {AuthenticationService} from "../../services/authentication/authentication.service";
    import {AuthenticationRequest} from "../../models/authentication-request";
    
    @Component({
      selector: 'app-register',
      templateUrl: './register.component.html',
      styleUrl: './register.component.scss'
    })
    export class RegisterComponent {
    
      errorMsg: any;
    
      customer: CustomerRegistrationRequest = {};
    
      constructor(private router: Router,
                  private customerService: CustomerServiceService,
                  private authenticationService: AuthenticationService
      ) {
      }
    
      login() {
        this.router.navigate(['login']);
      }
    
      createAccount() {
        this.customerService.registerCustomer(this.customer)
          .subscribe({
            next: () => {
              const authReq: AuthenticationRequest = {
                username: this.customer.email,
                password: this.customer.password,
              }
              this.authenticationService.login(authReq)
                .subscribe({
                  next: (authenticationResponse) => {
                    // console.log(authenticationResponse);
                    localStorage.setItem('user', JSON.stringify(authenticationResponse));
                    this.router.navigate(['customers']);
                  },
                  error: (error) => {
                    if (error.error.statusCode === 401) {
                      this.errorMsg = 'Login and / or pass is incorrect';
    
                    }
                  }
                });
            }
          });
      }
    }
    
    ```
3. there are some mistakes in file so update register.component.html
    ```text
    <div class="flex" style="height: 100vh">
      <div class="col-6 m-3 flex flex-column justify-content-center">
        <div class="m-auto">
    
          <div class="text-center">
            <p-avatar
              image="https://totalpng.com//public/uploads/preview/kedarnath-hindi-text-png-11657791984ueqb8jmssb.png"
              styleClass="mr-2"
              size="xlarge"
              shape="circle"/>
            <h1>Signup for a account.</h1>
          </div>
          <div>
            <p-message *ngIf="errorMsg" severity="error" [text]="errorMsg" styleClass="mb-3"></p-message>
          </div>
          <div class="field">
            <label for="name">Name</label>
            <input
              [(ngModel)]="customer.name"
              id="name"
              pInputText
              placeholder="Mary Jane"
              type="text"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="email">Email</label>
            <input
              [(ngModel)]="customer.email"
              id="email"
              pInputText
              placeholder="MaryJane@gmail.com"
              type="email"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              [(ngModel)]="customer.password"
              id="password"
              pInputText
              placeholder=""
              type="password"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="age">Age</label>
            <input
              [(ngModel)]="customer.age"
              id="age"
              pInputText
              placeholder="16"
              type="number"
              class="w-full"
            >
          </div>
          <div class="field">
            <label for="gender">Gender</label>
            <select
              [(ngModel)]="customer.gender"
              id="gender"
              class="p-dropdown w-full custom-select"
            >
              <option value="">Select Option</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <button
            (click)="createAccount()"
            pButton
            label="Create an Account"
            class="w-full"
          >
          </button>
          <button
            (click)="login()"
            pButton
            label="Have an account? Login now!"
            class="w-full p-button-link pl-0"
    
          >
          </button>
        </div>
      </div>
      <div class="col-6 flex flex-column justify-content-center align-items-center right-container">
        <h1>
          <a href="#" target="_blank">Enroll now</a>
        </h1>
        <img
          class="background p-6 pt-0"
          src="https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          alt="Nothing here :-))">
    
      </div>
    </div>
    
    ```

#### FINISHED at 11 NOVEMBER 2024 11:14 AM

### **STEP 680 11 NOVEMBER 2024 11:14 AM**

1. when someone http://localhost:4200/ it should redirect to login
2. update app-routing.module.ts
    ```text
    import {NgModule} from '@angular/core';
    import {RouterModule, Routes} from '@angular/router';
    import {CustomerComponent} from "./components/customer/customer.component";
    import {LoginComponent} from "./components/login/login.component";
    import {AccessGuardService} from "./services/guard/access-guard.service";
    import {RegisterComponent} from "./components/register/register.component";
    
    
    const routes: Routes = [
      {
        path: '',
        redirectTo: '/login',
        pathMatch: 'full',
      },
      {
        path: 'customers',
        component: CustomerComponent,
        canActivate: [AccessGuardService]
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'register',
        component: RegisterComponent
      },
    
    ]
    
    @NgModule({
      imports: [RouterModule.forRoot(routes)],
      exports: [RouterModule]
    })
    export class AppRoutingModule {
    }
    
    ```

#### FINISHED at 11 NOVEMBER 2024 11:19 AM

### **STEP 681 11 NOVEMBER 2024 11:19 AM**

1. update the main git ignore file
2. delete the angular git ignore file
3. 