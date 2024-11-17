package com.example.model;

import com.example.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Document(collection = "LostItemDetails")
public class LostItemsDetails {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String itemName;
    private int quantity;
    private String place;
    private StatusEnum status;

    @DBRef
    private List<UserDetails> userDetailsList;

}
