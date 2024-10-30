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

```java
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

now try with wrong password you get 500 error which is wrong as it has be unauthorized

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

as i am not doing the cloud stuff because i am on free tier and it takes alot time so i skip it.
any way the deploy workaction will fail as in the ec2 instance got rds which has 3 versions of flyway while
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
                .issuer("http://kedarnath.com")
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
                .issuer("http://kedarnath.com")
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

```include: "health,info"``` in here instead of health, info you can use * to get all the endpoints.which is not good as
someone can even shutdown as there is a endpoint shutdown.

### step 496: 29th oct 2024 11:02 AM

1. Go to github
2. Go to current project settings
3. Navigate to Secrets and variables.
4. Update the EB-APPLICATION_NAME,EB_ENVIRONMENT_NAME,EB_ENVIRONMENT_URL

FINISHED AT 11:14 aM

### STEP 497: 29TH OCT 11:14 AM

1. you can remove the below code fromDockerrun.aws.json and add it to application.yml.
    ```json  
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
3.

FINISHED AT 30TH OCT : AM

### STEP 518 30TH OCT : AM

FINISHED AT 30TH OCT : AM

### STEP 519 30TH OCT : AM

FINISHED AT 30TH OCT : AM

### STEP 520 30TH OCT : AM

FINISHED AT 30TH OCT : AM

### STEP 520 30TH OCT : AM

FINISHED AT 30TH OCT : AM

### STEP 520 30TH OCT : AM

FINISHED AT 30TH OCT : AM

### STEP 520 30TH OCT : AM

FINISHED AT 30TH OCT : AM
