package com.github.gpor0.jaffas.context;

public enum EnvironmentEnum {
    DEV(String.valueOf("DEV")), TEST(String.valueOf("TEST")), STAGE(String.valueOf("STAGE")), PROD(String.valueOf("PROD"));

    private String value;

    EnvironmentEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

}