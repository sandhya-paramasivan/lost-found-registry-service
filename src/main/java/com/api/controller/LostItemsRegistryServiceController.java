package com.api.controller;

import com.api.model.LostItemsDetails;
import com.api.service.LostItemsRegistryService;
import com.api.validation.ValidateInputFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/admin/uploadData")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    private ResponseEntity<String> updateLostItemsRegistry(@ValidateInputFile @RequestParam("lostItemsFile") MultipartFile lostItemsFile) throws IOException {
        return lostItemsRegistryService.processLostItemsDetails(lostItemsFile);
    }

    @GetMapping("/user/retrieveLostItemsDetails")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LostItemsDetails>> retrieveLostItemsDetails(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lostItemsRegistryService.retrieveLostItemsDetails(pageable);
    }

    @PostMapping("/user/claimItems")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LostItemsDetails>> claimLostItems(@RequestParam("itemName") String itemName,@RequestParam("place") String place,
                                 @RequestParam("userId") String userId, @RequestParam("claimQuantity") Integer claimQuantity) throws BadRequestException {
        return lostItemsRegistryService.claimLostItems(itemName,place,userId,claimQuantity);
    }

    @GetMapping("/admin/retrieveClaimedItemsDetails")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LostItemsDetails>> retrieveClaimedItemsDetails(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return lostItemsRegistryService.retrieveClaimedItemsDetails(page,size);
    }


}
