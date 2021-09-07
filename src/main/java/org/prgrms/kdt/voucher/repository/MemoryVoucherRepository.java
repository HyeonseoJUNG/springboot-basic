package org.prgrms.kdt.voucher.repository;

import org.prgrms.kdt.voucher.Voucher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MemoryVoucherRepository implements VoucherRepository {
    List<Optional<Voucher>> myVoucherRepository = new ArrayList<>();

    @Override
    public Optional<Voucher> findById(final UUID voucherId) {
        return Optional.empty();
    }

    @Override
    public List<Optional<Voucher>> findAll() {
        return myVoucherRepository;
    }

    @Override
    public void addVoucher(final Optional<Voucher> voucher) {
        myVoucherRepository.add(voucher);
    }
}