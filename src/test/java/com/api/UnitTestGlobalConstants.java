package com.api;


import org.springframework.http.HttpHeaders;

import java.util.Map;

public class UnitTestGlobalConstants {

    public static final Map<String,String> VALID_HEADERS = Map.of(
            "Content-Type" ,"multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"
    );

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        VALID_HEADERS.forEach(headers::add);
        return headers;
    }
    public static final HttpHeaders VALID_HEADER_FILE = getHttpHeaders();


}
