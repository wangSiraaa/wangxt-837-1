package com.maritime.bunkering.enums;

public enum ApplyStatusEnum {

    DRAFT(0, "草稿"),
    SUBMITTED(1, "已提交"),
    CERT_PASSED(2, "证书复核通过"),
    OIL_CHECK_PASSED(3, "油品检测通过"),
    PENDING_CONFIRM(4, "待确认"),
    CONFIRMED(5, "已确认"),
    REJECTED(6, "已拒绝"),
    COMPLETED(7, "已完成"),
    CANCELLED(8, "已取消"),
    PENDING_SIGN(9, "待签收"),
    SIGN_RECEIVED(10, "已签收"),
    SIGN_REJECTED(11, "签收拒绝");

    private final Integer code;
    private final String desc;

    ApplyStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ApplyStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
