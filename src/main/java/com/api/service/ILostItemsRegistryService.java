package com.api.service;

import com.api.model.LostItemsDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ILostItemsRegistryService {

     String processLostItemsDetails(MultipartFile file) throws IOException;

     ResponseEntity<List<LostItemsDetails>> claimLostItems(String itemName, String place, String userId, Integer claimQuantity);

     ResponseEntity<List<LostItemsDetails>> retrieveLostItemsDetails(int page, int size);

     ResponseEntity<List<LostItemsDetails>> retrieveClaimedItemsDetails(int page, int size);
}
