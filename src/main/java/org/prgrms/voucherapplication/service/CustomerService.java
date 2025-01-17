package org.prgrms.voucherapplication.service;

import org.prgrms.voucherapplication.entity.Customer;
import org.prgrms.voucherapplication.repository.FileCustomerRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Customer관련 service
 */
@Service
public class CustomerService {
    private FileCustomerRepository repository;

    public CustomerService(FileCustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * 저장된 모든 customer 객체를 List로 반환
     *
     * @return
     * @throws IOException
     */
    public List<Customer> getAllCustomer() throws IOException {
        return repository.findAll();
    }
}
