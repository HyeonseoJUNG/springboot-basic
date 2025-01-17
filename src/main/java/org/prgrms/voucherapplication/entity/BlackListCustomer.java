package org.prgrms.voucherapplication.entity;

/**
 * 고객 블랙리스트 Entity
 * field: 고객 ID, 고객 이름
 */
public record BlackListCustomer(long id, String name) implements Customer {
    @Override
    public long getCustomerId() {
        return id;
    }

    @Override
    public String getCustomerName() {
        return name;
    }

}
