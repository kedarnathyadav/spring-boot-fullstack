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
                27,
                Gender.MALE);
        customers.add(alex);
        Customer shruthi = new Customer(
                2L,
                "Shruthi",
                "Shruthi@gmail.com",
                26,
                Gender.MALE);
        customers.add(shruthi);
        Customer priya = new Customer(
                3L,
                "Priya",
                "Priya@gmail.com",
                29,
                Gender.MALE);
        customers.add(priya);
        Customer disha = new Customer(
                4L,
                "Disha",
                "Disha@gmail.com",
                26,
                Gender.MALE);
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
