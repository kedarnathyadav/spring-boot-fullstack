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
