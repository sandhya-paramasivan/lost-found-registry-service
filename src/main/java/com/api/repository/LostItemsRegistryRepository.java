package com.api.repository;

import com.api.enums.StatusEnum;
import com.api.model.LostItemsDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LostItemsRegistryRepository extends MongoRepository<LostItemsDetails,String> {

    List<LostItemsDetails> findByItemNameAndPlace(String itemName, String place);

    List<LostItemsDetails> findByStatus(StatusEnum statusEnum , Pageable pageable);

}
