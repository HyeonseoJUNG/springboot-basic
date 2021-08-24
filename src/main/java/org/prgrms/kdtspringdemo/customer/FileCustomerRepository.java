package org.prgrms.kdtspringdemo.customer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
@Primary
public class FileCustomerRepository implements CustomerRepository, InitializingBean {
    private final Map<UUID, Customer> storage = new ConcurrentHashMap<>();
    private final Map<UUID, Customer> blacklist_storage = new ConcurrentHashMap<>();
    private final String FILE_NAME = "customer_blacklist.csv";

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return Optional.ofNullable(storage.get(customerId));
    }

    @Override
    public Customer insert(Customer customer) {
        return null;
    }

    @Override
    public Stream<Customer> findAll() {
        return storage.values().stream();
    }

    @Override
    public Stream<Customer> findBlacklist() {
        return blacklist_storage.values().stream();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[FileCustomerRepository]afterPropertiesSet called!");
        try (BufferedReader reader = Files.newBufferedReader(Path.of(FILE_NAME))) {
            reader.lines().forEach(line -> {
                String[] dataArray = line.split(",");
                String customerType = dataArray[0];
                String uuid = dataArray[1];
                String name = dataArray[2];

                if (customerType.equals("normal")) {
                    var customer = new NormalCustomer(UUID.fromString(uuid), name);
                    storage.put(customer.getId(), customer);
                } else if (customerType.equals("black")) {
                    var customer = new BlackCustomer(UUID.fromString(uuid), name);
                    storage.put(customer.getId(), customer);
                    blacklist_storage.put(customer.getId(), customer);
                } else {
                    System.out.println("None CustomerType!!! : " + customerType);
                }
            });
        } catch (IOException e) {
            System.out.println("Doesn't exist file.");
        }
    }
}