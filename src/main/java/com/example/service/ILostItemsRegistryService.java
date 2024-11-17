package com.example.service;

import com.example.model.LostItemsDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ILostItemsRegistryService {

     String processLostItemsDetails(MultipartFile file);

     List<LostItemsDetails> retrieveLostItemsDetails(int page, int size);

     List<LostItemsDetails> claimLostItems(String itemName, String place, String userId, Integer claimQuantity);
}
