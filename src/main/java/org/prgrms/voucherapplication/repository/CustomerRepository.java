package org.prgrms.voucherapplication.repository;

import org.prgrms.voucherapplication.entity.Customer;
import org.prgrms.voucherapplication.entity.Voucher;

import java.io.IOException;
import java.util.List;

public interface CustomerRepository {

    List<Customer> findAll() throws IOException;
}
