package com.api.service;

import com.api.model.LostItemsDetails;
import com.api.model.User;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ILostItemsRegistryService {

     ResponseEntity<String> processLostItemsDetails(MultipartFile file) throws IOException;

     ResponseEntity<List<LostItemsDetails>> claimLostItems(String itemName, String place, String userId, Integer claimQuantity) throws BadRequestException;

     ResponseEntity<List<LostItemsDetails>> retrieveLostItemsDetails(Pageable pageable);

     ResponseEntity<List<LostItemsDetails>> retrieveClaimedItemsDetails(int page, int size);

     String createRoles(User user);
}
