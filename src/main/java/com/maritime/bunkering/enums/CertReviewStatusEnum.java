package com.maritime.bunkering.enums;

public enum CertReviewStatusEnum {

    PENDING(0, "待复核"),
    PASSED(1, "已复核通过"),
    REJECTED(2, "复核不通过");

    private final Integer code;
    private final String desc;

    CertReviewStatusEnum(Integer code, String desc) {
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
        for (CertReviewStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
