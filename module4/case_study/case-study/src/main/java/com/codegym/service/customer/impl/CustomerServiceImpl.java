package com.codegym.service.customer.impl;

import com.codegym.entity.customer.Customer;
import com.codegym.entity.customer.CustomerType;
import com.codegym.repository.CustomerRepository;
import com.codegym.repository.CustomerTypeRepository;
import com.codegym.service.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Override
    public List<Customer> findAll() {
        return this.customerRepository.findAll();
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return this.customerRepository.findAll(pageable);
    }

    @Override
    public Page<Customer> findByNameContaining(Pageable pageable, String name) {
        return this.customerRepository.findByNameContaining(pageable, name);
    }

    @Override
    public void deleteCustomer(String id) {
        this.customerRepository.deleteById(id);
    }

    @Override
    public void save(Customer customer) {
        this.customerRepository.save(customer);
    }

    @Override
    public List<CustomerType> allCustomerType() {
        return this.customerTypeRepository.findAll();
    }
}
