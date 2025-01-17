package org.prgrms.voucherapplication.exception;

/**
 * 에러 종류를 정의한 enum class
 * INVALID_MENU: 잘못된 메뉴 입력 시 발생
 * INVALID_VOUCHER_TYPE: 잘못된 바우처 타입 입력 시 발생
 * NO_SUCH_VOUCHER: 잘못된 바우처 id로 바우처를 찾는 경우 발생
 */
public enum ErrorType {
    INVALID_MENU("Invalid menu"),
    INVALID_VOUCHER_TYPE("Invalid voucher type"),
    NO_SUCH_VOUCHER("No such voucher.");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
