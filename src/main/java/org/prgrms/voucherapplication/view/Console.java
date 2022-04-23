package org.prgrms.voucherapplication.view;

import org.prgrms.voucherapplication.entity.BlackListCustomer;
import org.prgrms.voucherapplication.entity.Customer;
import org.prgrms.voucherapplication.entity.SqlVoucher;
import org.prgrms.voucherapplication.entity.Voucher;
import org.prgrms.voucherapplication.exception.InvalidCustomerInformationTypeException;
import org.prgrms.voucherapplication.exception.InvalidMenuException;
import org.prgrms.voucherapplication.exception.InvalidVoucherTypeException;
import org.prgrms.voucherapplication.view.io.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 콘솔로 입출력하는 class
 */
@Component
public class Console implements Input, Output {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * 화면에 출력할 문자열
     */
    private static final String MENU_INPUT_TEXT = "=== Voucher Program ===\n"
            + "Type \'exit\' to exit the program.\n"
            + "--------------------------------------------------------------------\n"
            + "Type \'1\' to create a new voucher in file.\n"
            + "Type \'2\' to list all vouchers in file.\n"
            + "Type \'3\' to list blacklist.\n"
            + "--------------------------------------------------------------------\n"
            + "Type \'4\' to create a new customer in DB.\n"
            + "Type \'5\' to create a voucher in DB.\n"
            + "Type \'6\' to show a voucher list owned by a customer.\n"
            + "Type \'7\' to delete all vouchers owned by a specific customer.\n"
            + "Type \'8\' to issue a voucher to a customer.\n"
            + "Type \'9\' to show a customer who have a specific voucher\n";

    private static final String VOUCHER_TYPE_INPUT_TEXT = "Type 1 to select fixed amount voucher.\n"
            + "Type 2 to select percent discount voucher.";
    private static final String VOUCHER_DISCOUNT_PERCENT_INPUT_TEXT = "Type discount rate."
            + "e.g., If you type \'10\', it means \'10%\'";
    private static final String VOUCHER_DISCOUNT_AMOUNT_INPUT_TEXT = "Type discount amount."
            + "e.g., If you type \'10\', it means \'$10\'";

    private static final String CUSTOMER_ID_INPUT_TEXT = "Type a customer ID: ";
    private static final String CUSTOMER_NAME_INPUT_TEXT = "Type a customer name: ";
    private static final String CUSTOMER_EMAIL_INPUT_TEXT = "Type a customer email: ";

    private static final String CUSTOMER_INFORMATION_TYPE_INPUT_TEXT = "Type the ID, name, or email "
            + "of the customer whose voucher information you want to check.\n"
            + "Type \'ID\' to enter the ID.\n"
            + "Type \'name\' to enter the name.\n"
            + "Type \'email\' to enter the email.\n";

    private static final String VOUCHER_ID_INPUT_TEXT = "Type a voucher ID: ";


    /**
     * 메뉴 설명 문자열을 출력하고
     * 메뉴 입력을 받아서 반환
     *
     * @return
     * @throws InvalidMenuException
     */
    @Override
    public Menu inputMenu() throws InvalidMenuException {
        System.out.println(MENU_INPUT_TEXT);
        String input = scanner.nextLine();
        return Menu.getMenu(input);
    }

    /**
     * 바우처 설명 문자열을 출력하고
     * 바우처 type을 입력받아서 반환
     * 환 * @return
     *
     * @throws InvalidVoucherTypeException
     */
    @Override
    public VoucherType inputVoucherType() throws InvalidVoucherTypeException {
        System.out.println(VOUCHER_TYPE_INPUT_TEXT);
        String input = scanner.nextLine();
        return VoucherType.getVoucherType(input);
    }

    /**
     * 바우처 타입에 따라
     * 할인액 또는 할인가 입력에 대한 문자열을 출력하고
     * 입력 받은 값을 반환
     *
     * @param type
     * @return
     * @throws InputMismatchException
     */
    @Override
    public Long inputDiscount(VoucherType type) throws InputMismatchException {
        if (type.equals(VoucherType.FIXED_AMOUNT)) {
            System.out.println(VOUCHER_DISCOUNT_AMOUNT_INPUT_TEXT);
        } else {
            System.out.println(VOUCHER_DISCOUNT_PERCENT_INPUT_TEXT);
        }

        String input = scanner.nextLine();
        return Long.valueOf(input);
    }

    /**
     * 고객 이름, 이메일 요청에 대한 문자열을 출력하고
     * 입력받은 고객 이름, 이메일에 따라서 Customer 객체 생성
     * @return 입력받은 name, email로 만든 Customer 객체
     */
    @Override
    public Customer inputCustomerCreation() {
        System.out.println(CUSTOMER_NAME_INPUT_TEXT);
        String name = scanner.nextLine();
        System.out.println(CUSTOMER_EMAIL_INPUT_TEXT);
        String email = scanner.nextLine();
        return new Customer(UUID.randomUUID(), name, email, LocalDateTime.now());
    }

    /**
     * 고객 id, 이름, 이메일 중 하나를 입력하여
     * 해당하는 CustomerInformationType 객체 반환
     * @return 입력된 고객 정보 타입과 일치하는 CustomerInformationType 객체
     */
    @Override
    public CustomerInformationType inputCustomerInformationForSearching() throws InvalidCustomerInformationTypeException {
        System.out.println(CUSTOMER_INFORMATION_TYPE_INPUT_TEXT);
        return CustomerInformationType.valueOf(scanner.nextLine().toUpperCase());
    }

    @Override
    public UUID inputCustomerID() {
        System.out.println(CUSTOMER_ID_INPUT_TEXT);
        return UUID.fromString(scanner.nextLine());
    }

    @Override
    public String inputCustomerName() {
        System.out.println(CUSTOMER_NAME_INPUT_TEXT);
        return scanner.nextLine();
    }

    @Override
    public String inputCustomerEmail() {
        System.out.println(CUSTOMER_EMAIL_INPUT_TEXT);
        return scanner.nextLine();
    }

    @Override
    public UUID inputVoucherID() {
        System.out.println(VOUCHER_ID_INPUT_TEXT);
        return UUID.fromString(scanner.nextLine());
    }

    /**
     * 모든 바우처 정보를 출력
     *
     * @param vouchers: Voucher 타입의 List
     */
    @Override
    public void printVoucherList(List<Voucher> vouchers) {
        vouchers.stream().forEach(voucher -> System.out.println(voucher.toString()));
    }

    /**
     * 모든 블랙리스트의 고객 정보를 출력
     *
     * @param customers
     */
    @Override
    public void printBlackList(List<BlackListCustomer> customers) {
        customers.stream().forEach(customer -> System.out.println(customer.toString()));
    }

    /**
     * DB에 있는 바우처 리스트 출력
     *
     * @param vouchers: SqlVoucher 타입의 List
     */
    @Override
    public void printSqlVoucherList(List<SqlVoucher> vouchers) {
        vouchers.stream().forEach(voucher -> System.out.println(voucher.toString()));
    }

}
