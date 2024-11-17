package com.example.controller;

import com.example.model.LostItemsDetails;
import com.example.service.LostItemsRegistryService;
import com.example.validation.ValidateInputParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}")
@Slf4j
public class LostItemsRegistryServiceController {

    private final LostItemsRegistryService lostItemsRegistryService;

    public LostItemsRegistryServiceController(LostItemsRegistryService lostItemsRegistryService) {
        this.lostItemsRegistryService = lostItemsRegistryService;
    }

    @PostMapping("/uploadData")
    public String updateLostItemsRegistry(@ValidateInputParams @RequestParam("lostItemsFile") MultipartFile lostItemsFile) {
        return lostItemsRegistryService.processLostItemsDetails(lostItemsFile);
    }

    @GetMapping("/retrieveLostItemsDetails")
    public List<LostItemsDetails> retrieveLostItemsDetails(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return lostItemsRegistryService.retrieveLostItemsDetails(page,size);
    }

    @PostMapping("/claimItems")
    public List<LostItemsDetails> claimLostItems(@RequestParam("itemName") String itemName,@RequestParam("place") String place,
                                 @RequestParam("userId") String userId, @RequestParam("claimQuantity") Integer claimQuantity) {
        return lostItemsRegistryService.claimLostItems(itemName,place,userId,claimQuantity);
    }

}
