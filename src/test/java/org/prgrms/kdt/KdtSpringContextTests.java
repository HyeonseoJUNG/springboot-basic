package org.prgrms.kdt;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgrms.kdt.domain.order.OrderItem;
import org.prgrms.kdt.domain.order.OrderStatus;
import org.prgrms.kdt.domain.voucher.FixedAmountVoucher;
import org.prgrms.kdt.domain.voucher.VoucherRepository;
import org.prgrms.kdt.domain.voucher.VoucherType;
import org.prgrms.kdt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringJUnitConfig
public class KdtSpringContextTests {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    OrderService orderService;

    @Autowired
    VoucherRepository voucherRepository;

    @Test
    @DisplayName("applicationContext가 생성되어야 한다.")
    public void testApplicationContext() {
        assertThat(applicationContext, notNullValue());
    }

    @Test
    @DisplayName("VoucherRepositoryCreation가 빈으로 등록되어 있어야 한다.")
    public void testVoucherRepositoryCreation() {
        var bean = applicationContext.getBean(VoucherRepository.class);
        assertThat(bean, notNullValue());
    }

    @Test
    @DisplayName("orderService를 사용해서 주문을 생성할 수 있다.")
    public void testOrderService() {
        //given
        var fixedAmountVoucher = new FixedAmountVoucher(new RandomDataGenerator().nextLong(0, 10000), VoucherType.FIXED_AMOUNT, 100);
        voucherRepository.insert(fixedAmountVoucher);

        //when
        var order = orderService.createOrder(
                new RandomDataGenerator().nextLong(0, 10000),
                List.of(new OrderItem(new RandomDataGenerator().nextLong(0, 10000), 200L, 1L)),
                fixedAmountVoucher.getVoucherId());

        //then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));
    }

    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.domain.voucher", "org.prgrms.kdt.domain.order", "org.prgrms.kdt.service"}
    )
    static class Config {
    }
}