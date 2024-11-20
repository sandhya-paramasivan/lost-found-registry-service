package com.api.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Users")
@Data
@Builder(toBuilder = true)
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private List<String> roles;
}
