package com.example.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
public class Address {

    private String houseNumber;
    private String street;
    private String city;
    private String zipCode;
}
