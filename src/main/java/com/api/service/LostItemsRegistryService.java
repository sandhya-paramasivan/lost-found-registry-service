package com.api.service;

import com.api.enums.StatusEnum;
import com.api.model.LostItemsDetails;
import com.api.model.UserDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final UserDetailsService userDetailsService;

    public LostItemsRegistryService(LostItemsRegistryRepository lostItemsRegistryRepository, UserDetailsService userDetailsService) {
        this.lostItemsRegistryRepository = lostItemsRegistryRepository;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public String processLostItemsDetails(MultipartFile lostItemsFile) throws IOException {

        Reader reader = new InputStreamReader(lostItemsFile.getInputStream());
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        List<LostItemsDetails> lostItems = constructLostItemEntity(csvParser);
        lostItemsRegistryRepository.saveAll(lostItems);

        return "Data processed successfully";
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

    @Override
    public ResponseEntity<List<LostItemsDetails>> retrieveLostItemsDetails(Pageable pageable) {
        List<LostItemsDetails> lostitems = lostItemsRegistryRepository.findByStatus(StatusEnum.LOST,pageable);
        if (lostitems.isEmpty()) {
            return ResponseEntity.notFound().build();  // Return 404 Not Found if list is empty
        }

        return ResponseEntity.ok(lostitems);
    }

    @Override
    public ResponseEntity<List<LostItemsDetails>>claimLostItems(String itemName, String place, String userId, Integer claimQuantity) {
        List<LostItemsDetails> lostItems =  lostItemsRegistryRepository.findByItemNameAndPlace(itemName,place);
        List<LostItemsDetails> toClaimLostItems = lostItems.stream().filter(lostItemsDetails-> !lostItemsDetails.getStatus().equals(StatusEnum.CLAIMED)).collect(Collectors.toList());
        updateUserDetails(toClaimLostItems,userId,claimQuantity);
        return ResponseEntity.ok(toClaimLostItems);
    }

    @Override
    public ResponseEntity<List<LostItemsDetails>> retrieveClaimedItemsDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<LostItemsDetails> lostitems =lostItemsRegistryRepository.findByStatus(StatusEnum.CLAIMED,pageable);
        if (lostitems.isEmpty()) {
            return ResponseEntity.notFound().build();  // Return 404 Not Found if list is empty
        }
        return ResponseEntity.ok(lostitems);
    }

    private void updateUserDetails(List<LostItemsDetails> toClaimLostItems, String userId, Integer claimQuantity) {
        toClaimLostItems.stream().forEach(lostItemsDetails -> {

            checkQuantity(lostItemsDetails,claimQuantity);
            UserDetails userDetails = assignUserDetails(lostItemsDetails,userId,claimQuantity);

            lostItemsRegistryRepository.save(lostItemsDetails);
            userDetailsService.createUserDetails(userDetails);
        });
    }

    private UserDetails assignUserDetails(LostItemsDetails lostItemsDetails, String userId, Integer claimQuantity) {
        UserDetails userDetails;
        userDetails = lostItemsDetails.getUserDetailsList().stream().filter(d-> d.getUserId().equalsIgnoreCase(userId)).findAny().orElse(null);
        if (userDetails != null) {
            userDetails.setClaimedQuantity(userDetails.getClaimedQuantity() + claimQuantity);
        }
        else{
            userDetails =userDetailsService.fetchUserDetails(userId);
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
