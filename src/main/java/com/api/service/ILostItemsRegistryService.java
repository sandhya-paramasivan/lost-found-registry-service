package com.api.service;

import com.api.model.LostItemsDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ILostItemsRegistryService {

     String processLostItemsDetails(MultipartFile file) throws IOException;

     List<LostItemsDetails> claimLostItems(String itemName, String place, String userId, Integer claimQuantity);

     List<LostItemsDetails> retrieveLostItemsDetails(int page, int size);

     List<LostItemsDetails> retrieveClaimedItemsDetails(int page, int size);
}
