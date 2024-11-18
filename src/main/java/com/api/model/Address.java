package com.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Address {

    private String houseNumber;
    private String street;
    private String city;
    private String zipCode;
}
