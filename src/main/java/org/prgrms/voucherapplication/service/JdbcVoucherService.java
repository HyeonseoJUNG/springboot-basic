package org.prgrms.voucherapplication.service;

import org.prgrms.voucherapplication.entity.Customer;
import org.prgrms.voucherapplication.entity.SqlVoucher;
import org.prgrms.voucherapplication.repository.customer.jdbc.JdbcCustomerRepository;
import org.prgrms.voucherapplication.repository.voucher.jdbc.JdbcVoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JdbcVoucherService {

    private JdbcCustomerRepository customerRepository;
    private JdbcVoucherRepository voucherRepository;

    public JdbcVoucherService(JdbcCustomerRepository customerRepository, JdbcVoucherRepository voucherRepository) {
        this.customerRepository = customerRepository;
        this.voucherRepository = voucherRepository;
    }

    /**
     * DB에 바우처 저장
     *
     * @param voucher
     */
    public void saveVoucher(SqlVoucher voucher) {
        voucherRepository.insert(voucher);
    }

    public void saveVoucher(String voucherType, long discountAmount) {
        SqlVoucher voucher = new SqlVoucher(UUID.randomUUID(), voucherType, discountAmount, LocalDateTime.now());
        voucherRepository.insert(voucher);
    }

    /**
     * 입력한 고객이 보유한 바우처 리스트 반환
     *
     * @param customer
     * @return
     */
    public Optional<List<SqlVoucher>> getVouchersByOwnedCustomer(Customer customer) {
        return voucherRepository.findByVoucherOwner(customer.getCustomerId());
    }

    public Optional<List<SqlVoucher>> getVouchersByOwnedCustomer(UUID customerId) {
        return voucherRepository.findByVoucherOwner(customerId);
    }

    /**
     * 특정 고객이 보유한 바우처 모두 삭제
     *
     * @param customer
     */
    public void deleteVouchersByOwnedCustomer(Customer customer) {
        Optional<List<SqlVoucher>> byVoucherOwner = voucherRepository.findByVoucherOwner(customer.getCustomerId());
        byVoucherOwner.ifPresent(vouchers ->
                vouchers.forEach(voucher -> voucherRepository.deleteById(voucher.getVoucherId())));
    }

    public void deleteVouchersByOwnedCustomer(UUID customerId) {
        Optional<List<SqlVoucher>> byVoucherOwner = voucherRepository.findByVoucherOwner(customerId);
        byVoucherOwner.ifPresent(vouchers ->
                vouchers.forEach(voucher -> voucherRepository.deleteById(voucher.getVoucherId())));
    }

    public void deleteVoucherById(UUID voucherId) {
        voucherRepository.deleteById(voucherId);
    }

    /**
     * 모든 바우처 리스트 반환
     *
     * @return
     */
    public List<SqlVoucher> getAllVoucher() {
        return voucherRepository.findAll();
    }

    public List<SqlVoucher> getVouchersByCondition(Optional<LocalDateTime> startDateTime,
                                                   Optional<LocalDateTime> endDateTime,
                                                   Optional<String> voucherType) {
        return voucherRepository.findByCondition(startDateTime, endDateTime, voucherType);
    }

    /**
     * 특정 바우처를 특정 고객에게 발행
     *
     * @param voucherId
     * @param customer
     */
    public void issueVoucherToCustomer(UUID voucherId, Customer customer) {
        Optional<SqlVoucher> voucher = voucherRepository.findById(voucherId);
        if (voucher.isPresent()) {
            SqlVoucher sqlVoucher = voucher.get();
            sqlVoucher.issueVoucher(customer.getCustomerId());
            voucherRepository.update(sqlVoucher);
        }
    }

    public void issueVoucherToCustomer(UUID voucherId, UUID customerId) {
        Optional<SqlVoucher> voucher = voucherRepository.findById(voucherId);
        if (voucher.isPresent()) {
            SqlVoucher sqlVoucher = voucher.get();
            sqlVoucher.issueVoucher(customerId);
            voucherRepository.update(sqlVoucher);
        }
    }

    /**
     * 특정 바우처를 가진 고객 정보 조회
     *
     * @param voucherId
     * @return
     */
    public Optional<Customer> getCustomerByVoucherId(UUID voucherId) {
        Optional<SqlVoucher> voucher = voucherRepository.findById(voucherId);
        if (voucher.isPresent() && voucher.get().getVoucherOwner() != null) {
            return customerRepository.findById(voucher.get().getVoucherOwner());
        }
        return Optional.empty();
    }

    public Optional<SqlVoucher> getVoucherById(UUID voucherId) {
        return voucherRepository.findById(voucherId);
    }

    /**
     * 발급되지 않은 바우처 조회
     *
     * @return
     */
    public List<SqlVoucher> getUnissuedVouchers() {
        return voucherRepository.findByIsIssued(false).get();
    }
}