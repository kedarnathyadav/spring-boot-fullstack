package com.kedarnath.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceTest {
    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // Given
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());  // Creating a mock list of customers
        when(page.getContent()).thenReturn(customers);         // Mocking the getContent method of the page
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);  // Mocking the repository call

        // When
        List<Customer> expected = underTest.selectAllCustomers();  // Calling the method under test

        // Then
        assertThat(expected).isEqualTo(customers);  // Verifying the result is equal to the mocked list of customers

        // Capturing the Pageable argument passed to findAll method
        ArgumentCaptor<Pageable> pageArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageArgumentCaptor.capture());  // Verifying the call to findAll

        // Asserting the Pageable argument passed is of the correct type and has the expected page size
        assertThat(pageArgumentCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void selectCustomerById() {
        //Given
        int id = 1;
        //When
        underTest.selectCustomerById(id);
        //Then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer(
                1L, "king", "123@gmail.com", "password", Gender.MALE, 20
        );
        //When
        underTest.insertCustomer(customer);
        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = "123@gmail.com";
        //When
        underTest.existsPersonWithEmail(email);
        //Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsPersonWithId() {
        //Given
        Integer id = 1;
        //When
        underTest.existsPersonWithId(id);
        //Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        //Given
        Integer id = 1;
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = new Customer(
                1L, "king", "123@gmail.com", "password", Gender.MALE, 20
        );
        //When
        underTest.updateCustomer(customer);
        //Then
        verify(customerRepository).save(customer);
    }
}