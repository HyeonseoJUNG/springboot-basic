package org.prgrms.kdt.customer.repository;

import org.prgrms.kdt.customer.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CustomerNamedJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepository.class);

    private final DataSource datasource;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final PlatformTransactionManager transactionManager;

    private final TransactionTemplate transactionTemplate;

    private static final RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        UUID customerId = toUUID(resultSet.getBytes("customer_id"));
        String customerName = resultSet.getString("name");
        String customerEmail = resultSet.getString("email");
        LocalDateTime lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customerId, customerName, customerEmail, lastLoginAt, createdAt);
    };

    public CustomerNamedJdbcRepository(DataSource datasource, TransactionTemplate transactionTemplate, NamedParameterJdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
        this.datasource = datasource;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.transactionManager = transactionManager;
    }

    private Map<String, Object> toParamMap(Customer customer) {
        return new HashMap<String, Object>() {{
            put("customerId", customer.getCustomerId().toString().getBytes());
            put("name", customer.getName());
            put("email", customer.getEmail());
            put("createdAt", Timestamp.valueOf(customer.getCreatedAt()));
            put("lastLoginAt", customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
        }};
    }

    @Override
    public Customer insert(Customer customer) {
        int update = jdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(:customerId), :name, :email, :createdAt)",
                toParamMap(customer));
        if (update != 1) {
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        int update = jdbcTemplate.update("UPDATE customers SET name = :name, email = :email, last_login_at = :lastLoginAt WHERE customer_id = UUID_TO_BIN(:customerId)",
                toParamMap(customer));
        if (update != 1) {
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "select * from customers where customer_id = UUID_TO_BIN(:customerId)",
                    Collections.singletonMap("customerId", customerId.toString().getBytes()),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where name = :name",
                    Collections.singletonMap("name", name),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where email = :email",
                    Collections.singletonMap("email", email),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    public void testTransaction(Customer customer) {
        var transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            jdbcTemplate.update("UPDATE customers SET name = :name WHERE customer_id = UUID_TO_BIN(:customerId)", toParamMap(customer));
            jdbcTemplate.update("UPDATE customers SET email = :email WHERE customer_id = UUID_TO_BIN(:customerId)", toParamMap(customer));
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            logger.error("Got error", e);
            transactionManager.rollback(transaction);
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM customers", Collections.emptyMap());
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Collections.emptyMap(), Integer.class);
    }

    private void mapToCustomer(List<Customer> allCustomers, ResultSet resultSet) throws SQLException {
        UUID customerId = toUUID(resultSet.getBytes("customer_id"));
        String customerName = resultSet.getString("name");
        String customerEmail = resultSet.getString("email");
        LocalDateTime lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        allCustomers.add(new Customer(customerId, customerName, customerEmail, lastLoginAt, createdAt));
    }

    static UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
