package org.scoula.account.domain;

import java.util.HashMap;
import java.util.Map;

public enum BankCode {
    IBK("003", "기업은행"),
    KB("004", "국민은행"),
    NH("011", "농협은행"),
    WR("020", "우리은행"),
    SC("023", "SC제일은행"),
    CT("027", "한국씨티은행"),
    KEB("081", "하나은행"),
    SH("088", "신한은행"),
    KK("090", "카카오뱅크"),
    TS("092", "토스뱅크");


    private final String code;
    private final String name;

    private static final Map<String, BankCode> NAME_MAP;
    static {
        NAME_MAP = new HashMap<>();
        for (BankCode bankCode : values()) {
            NAME_MAP.put(bankCode.name, bankCode);
        }
    }

    BankCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
//    public String getName() { return name; }

    public static BankCode fromName(String name) {
        BankCode bankCode = NAME_MAP.get(name);
        if (bankCode == null) {
            throw new IllegalArgumentException("잘못된 은행 이름입니다.: " + name);
        }
        return bankCode;
    }

//    public static BankCode fromName(String name) {
//        for(BankCode bankCode : values()) {
//            if(bankCode.name.equals(name)) {
//                return bankCode;
//            }
//        }
//        throw new IllegalArgumentException("잘못된 은행 이름입니다.: " + name);
//    }
}
