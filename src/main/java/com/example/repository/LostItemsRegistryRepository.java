package com.example.repository;

import com.example.model.LostItemsDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LostItemsRegistryRepository extends MongoRepository<LostItemsDetails,Integer> {

    List<LostItemsDetails> findByItemNameAndPlace(String itemName, String place);
}
