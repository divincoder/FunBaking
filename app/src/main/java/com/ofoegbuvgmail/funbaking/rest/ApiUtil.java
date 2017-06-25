package com.ofoegbuvgmail.funbaking.rest;

public class ApiUtil {

    private ApiUtil() {
    }

    private static final String baseUrl = "https://d17h27t6h515a5.cloudfront.net/";

    public static BakingApiInterface getApiInterface() {

        return RetrofitClient.getClient(baseUrl).create(BakingApiInterface.class);
    }
}
