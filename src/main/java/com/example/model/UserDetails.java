package com.example.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@Document(collection = "UserDetails")
public class UserDetails {

    @Id
    private String userId;
    private String username;
    private int claimedQuantity;
    private Address address;

}
