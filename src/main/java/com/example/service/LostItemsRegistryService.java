package com.example.service;

import com.example.enums.StatusEnum;
import com.example.model.LostItemsDetails;
import com.example.model.UserDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.repository.LostItemsRegistryRepository;

@Service
@Slf4j
public class LostItemsRegistryService implements ILostItemsRegistryService {


    private final LostItemsRegistryRepository lostItemsRegistryRepository;

    public LostItemsRegistryService(LostItemsRegistryRepository lostItemsRegistryRepository) {
        this.lostItemsRegistryRepository = lostItemsRegistryRepository;
    }


    @Override
    public String processLostItemsDetails(MultipartFile lostItemsFile) {
        // Use InputStreamReader to read the file from MultipartFile
        try (Reader reader = new InputStreamReader(lostItemsFile.getInputStream());
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            List<LostItemsDetails> lostItems = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                    String[] value = record.get(0).split(";");
                     LostItemsDetails lostItemsDetails= LostItemsDetails.builder().itemName(value[0])
                            .quantity(Integer.parseInt(value[1]))
                            .place(value[2]).status(StatusEnum.NEW).build();
                    lostItems.add(lostItemsDetails);
                    lostItemsRegistryRepository.save(lostItemsDetails);
                }

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    @Override
    public List<LostItemsDetails> retrieveLostItemsDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lostItemsRegistryRepository.findAll(pageable).getContent();
    }

    @Override
    public List<LostItemsDetails> claimLostItems(String itemName, String place, String userId, Integer claimQuantity) {
        List<LostItemsDetails> lostItems =  lostItemsRegistryRepository.findByItemNameAndPlace(itemName,place);
        List<LostItemsDetails> claimLostItems = lostItems.stream().filter(lostItemsDetails-> !lostItemsDetails.getStatus().equals(StatusEnum.CLAIMED)).collect(Collectors.toList());
        updateUserDetails(claimLostItems,userId,claimQuantity);
        return claimLostItems;
    }

    private void updateUserDetails(List<LostItemsDetails> claimLostItems, String userId, Integer claimQuantity) {
        claimLostItems.stream().forEach(lostItemsDetails -> {
            checkforQuantity(lostItemsDetails);
            UserDetails userDetails = UserDetails.builder().userId(userId).claimedQuantity(claimQuantity).build();
            lostItemsDetails.getUserDetailsList().add(userDetails);
            if(lostItemsDetails.getQuantity() == claimQuantity){
                lostItemsDetails.setStatus(StatusEnum.CLAIMED);
            }
            lostItemsRegistryRepository.save(lostItemsDetails);
        });
    }

    private void checkforQuantity(LostItemsDetails lostItemsDetails) {
        if(lostItemsDetails.getUserDetailsList() == null){
            lostItemsDetails.setUserDetailsList(new ArrayList<>());
        }
    }

}
