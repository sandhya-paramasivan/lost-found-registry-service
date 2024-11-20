package com.api.service;

import com.api.enums.StatusEnum;
import com.api.exception.InternalServerException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.LostItemsDetails;
import com.api.model.User;
import com.api.model.UserDetails;
import com.api.repository.UserDetailsRepository;
import com.api.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.api.repository.LostItemsRegistryRepository;

@Service
@Slf4j
public class LostItemsRegistryService implements ILostItemsRegistryService {


    private final LostItemsRegistryRepository lostItemsRegistryRepository;
    private final UserDetailsServiceIntegration userDetailsServiceIntegration;
    private final UserRepository userRepository;

    public LostItemsRegistryService(LostItemsRegistryRepository lostItemsRegistryRepository, UserDetailsServiceIntegration userDetailsServiceIntegration, UserRepository userRepository, UserDetailsRepository userDetailsRepository) {
        this.lostItemsRegistryRepository = lostItemsRegistryRepository;
        this.userDetailsServiceIntegration = userDetailsServiceIntegration;
        this.userRepository = userRepository;
    }


   /* Following method takes file as input and creates record in the database*/
    @Override
    public ResponseEntity<String> processLostItemsDetails(MultipartFile lostItemsFile) {
        try {
            Reader reader = new InputStreamReader(lostItemsFile.getInputStream());
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            List<LostItemsDetails> lostItems = constructLostItemEntity(csvParser);
            lostItemsRegistryRepository.saveAll(lostItems);
            return ResponseEntity.ok("Data processed successfully");
        }
        catch (Exception e) {
            throw new InternalServerException("Internal Server Error");
        }
    }

    List<LostItemsDetails> constructLostItemEntity(CSVParser csvParser) {
        List<LostItemsDetails> lostItems = new ArrayList<>();
        csvParser.stream().forEach(record -> {
            String[] value = record.get(0).split(";");
            LostItemsDetails lostItemsDetails= LostItemsDetails.builder().itemName(value[0])
                    .quantity(Integer.parseInt(value[1]))
                    .place(value[2]).status(StatusEnum.LOST).build();
            lostItems.add(lostItemsDetails);
            });
        return lostItems;
    }

/*
    Following method retrievelostitems with status LOST
*/
    @Override
    public ResponseEntity<List<LostItemsDetails>> retrieveLostItemsDetails(Pageable pageable) {
        List<LostItemsDetails> lostitems = lostItemsRegistryRepository.findByStatus(StatusEnum.LOST,pageable);
        if (lostitems.isEmpty()) {
            throw new ResourceNotFoundException("Resource Not found Exception");
        }
        return ResponseEntity.ok(lostitems);
    }

    /*
    Following method is invoked for claiming the lost items and update with status CLAIMED
*/
    @Override
    public ResponseEntity<List<LostItemsDetails>>claimLostItems(String itemName, String place, String userId, Integer claimQuantity) throws BadRequestException {
        var lostItems =  lostItemsRegistryRepository.findByItemNameAndPlace(itemName,place);
        var toClaimLostItems = lostItems.stream().filter(lostItemsDetails-> !lostItemsDetails.getStatus().equals(StatusEnum.CLAIMED)).collect(Collectors.toList());
        if(toClaimLostItems.isEmpty()){
            throw new BadRequestException("There are no items to be claimed");
        }

        updateUserDetails(toClaimLostItems,userId,claimQuantity);
        return ResponseEntity.ok(toClaimLostItems);
    }

    @Override
    public ResponseEntity<List<LostItemsDetails>> retrieveClaimedItemsDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var lostitems =lostItemsRegistryRepository.findByStatus(StatusEnum.CLAIMED,pageable);
        if (lostitems.isEmpty()) {
            throw new ResourceNotFoundException("Resource Not found Exception");// Return 404 Not Found if list is empty
        }
        return ResponseEntity.ok(lostitems);
    }

    @Override
    public String createRoles(User user) {
        userRepository.save(user);
        return "User created Successfully";
    }

    private void updateUserDetails(List<LostItemsDetails> toClaimLostItems, String userId, Integer claimQuantity) {
        toClaimLostItems.stream().forEach(lostItemsDetails -> {

            checkQuantity(lostItemsDetails,claimQuantity);
            var userDetails = assignUserDetails(lostItemsDetails,userId,claimQuantity);

            lostItemsRegistryRepository.save(lostItemsDetails);
            userDetailsServiceIntegration.createUserDetails(userDetails);
        });
    }

/*
    Logic to update the claimedquantity and CLAIMED status based on the different user inputs
*/
    private UserDetails assignUserDetails(LostItemsDetails lostItemsDetails, String userId, Integer claimQuantity) {
        UserDetails userDetails;
        userDetails = lostItemsDetails.getUserDetailsList().stream().filter(d-> d.getUserId().equalsIgnoreCase(userId)).findAny().orElse(null);
        if (userDetails != null) {
            userDetails.setClaimedQuantity(userDetails.getClaimedQuantity() + claimQuantity);
        }
        else{
            userDetails =userDetailsServiceIntegration.fetchUserDetails(userId);
            userDetails.setClaimedQuantity(claimQuantity);
            lostItemsDetails.getUserDetailsList().add(userDetails);
        }
        return userDetails;
    }

    private void checkQuantity(LostItemsDetails lostItemsDetails, Integer claimQuantity) {
        lostItemsDetails.setStatus(StatusEnum.TO_BE_CLAIMED);
        int existingClaimQuantity = 0;
        if(lostItemsDetails.getUserDetailsList() != null){
            existingClaimQuantity = lostItemsDetails.getUserDetailsList().stream().mapToInt(UserDetails::getClaimedQuantity).sum();
        }
        else{
            lostItemsDetails.setUserDetailsList(new ArrayList<>());
        }
        if(lostItemsDetails.getQuantity() == (existingClaimQuantity + claimQuantity)){
            lostItemsDetails.setStatus(StatusEnum.CLAIMED);
        }
    }

}
