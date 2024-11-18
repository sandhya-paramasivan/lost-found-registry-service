package com.api.controller;

import com.api.model.LostItemsDetails;
import com.api.service.LostItemsRegistryService;
import com.api.validation.ValidateInputFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String updateLostItemsRegistry(@ValidateInputFile @RequestParam("lostItemsFile") MultipartFile lostItemsFile) throws IOException {
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

    @GetMapping("/retrieveClaimedItemsDetails")
    public List<LostItemsDetails> retrieveClaimedItemsDetails(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return lostItemsRegistryService.retrieveClaimedItemsDetails(page,size);
    }

}
