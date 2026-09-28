package com.sdet.api.testdata;

public final class ApiTestConstants {

    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int PET_NOT_FOUND_CODE = 1;

    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String ERROR_TYPE = "error";
    public static final String PET_NOT_FOUND_MESSAGE = "Pet not found";
    public static final String BAD_INPUT_MESSAGE = "bad input";
    public static final String NUMBER_FORMAT_EXCEPTION = "NumberFormatException";

    private ApiTestConstants() {
    }
}