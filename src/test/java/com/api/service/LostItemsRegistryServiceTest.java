package com.api.service;

import com.api.enums.StatusEnum;
import com.api.exception.InternalServerException;
import com.api.exception.ResourceNotFoundException;
import com.api.model.LostItemsDetails;
import com.api.model.UserDetails;
import com.api.repository.LostItemsRegistryRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LostItemsRegistryServiceTest {

    @InjectMocks
    private LostItemsRegistryService lostItemsRegistryService;

    private MockMultipartFile lostItemsFile;

    @Mock
    private LostItemsRegistryRepository lostItemsRegistryRepository;

    @Mock
    private CSVParser csvParser;  // Mock the CSVParser

    @Mock
    private CSVRecord csvRecord;  // Mock a CSVRecord (representing a row in the CSV file)

    @Mock
    private UserDetailsServiceIntegration userDetailsServiceIntegration;

    @Mock
    private Pageable pageable;

    @Test
    void givenValidFile_UploadAndSaveItems_500Error() throws IOException {
        String csvContent = "itemName;quantity;place\n" +
                "Laptop;1;Office\n" +
                "Phone;2;Airport";
        lostItemsFile = new MockMultipartFile("file", "lost_items.csv", "text/csv", csvContent.getBytes());

        when(csvParser.stream()).thenReturn(Arrays.asList(csvRecord, csvRecord).stream());
        when(csvRecord.get(0)).thenReturn("Laptop;1;Office").thenReturn("Phone;2;Home");
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(1).place("Office").build();
        // Simulate the repository save behavior
        when(lostItemsRegistryRepository.saveAll(Collections.singletonList(lostItemsDetails))).thenReturn(Collections.singletonList(lostItemsDetails));

        // Call the method to test
        assertThrows(InternalServerException.class, () ->  lostItemsRegistryService.processLostItemsDetails(lostItemsFile));

    }

    @Test
    void givenItemName_thenReturn200_claimlostItems() throws BadRequestException {
        UserDetails userDetails = UserDetails.builder().userId("U1001").username("Sandhya").claimedQuantity(1).build();
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(3).place("Office")
                .status(StatusEnum.LOST).userDetailsList(Arrays.asList(userDetails)).build();
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(lostItemsDetails);
        when(lostItemsRegistryRepository.findByItemNameAndPlace("Laptop", "Office"))
                .thenReturn(lostItemsDetailsList);
        ResponseEntity<List<LostItemsDetails>> response = lostItemsRegistryService.claimLostItems("Laptop", "Office","U1001" , 1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void givenItemName_thenReturn400_claimlostItems() throws BadRequestException {
        lenient().when(lostItemsRegistryRepository.findByItemNameAndPlace("Laptop", "Office"))
                .thenReturn(new ArrayList<>());
        assertThrows(BadRequestException.class, () ->  lostItemsRegistryService.claimLostItems("Laptop", "Office","U1001" , 1));
    }

    @Test
    void givenPageAndSize_thenReturn200_retrieveLostItems(){
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(1).place("Office")
                .status(StatusEnum.LOST).userDetailsList(new ArrayList<>()).build();
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(lostItemsDetails);
        when(lostItemsRegistryRepository.findByStatus(eq(StatusEnum.LOST), any(Pageable.class)))
                .thenReturn(lostItemsDetailsList);
        ResponseEntity<List<LostItemsDetails>> listofItems =lostItemsRegistryService.retrieveLostItemsDetails(pageable);
        assertNotNull(listofItems);
        verify(lostItemsRegistryRepository, times(1)).findByStatus(eq(StatusEnum.LOST), any(Pageable.class));
        assertEquals(200, listofItems.getStatusCodeValue());
    }

    @Test
    void givenPageAndSize_thenReturn404_retrieveLostItems(){
        lenient().when(lostItemsRegistryRepository.findByStatus(StatusEnum.LOST,pageable))
                .thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> lostItemsRegistryService.retrieveLostItemsDetails(pageable));
    }

    @Test
    void givenPageAndSize_thenReturn200_retrieveClaimedItems(){
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(1).place("Office")
                .status(StatusEnum.CLAIMED).userDetailsList(new ArrayList<>()).build();
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(lostItemsDetails);
        when(lostItemsRegistryRepository.findByStatus(eq(StatusEnum.CLAIMED), any(Pageable.class)))
                .thenReturn(lostItemsDetailsList);
       lostItemsRegistryService.retrieveClaimedItemsDetails(0,10);
    }

    @Test
    void givenPageAndSize_thenReturn404_retrieveClaimedItems(){
        lenient().when(lostItemsRegistryRepository.findByStatus(StatusEnum.LOST,pageable))
                .thenReturn( new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> lostItemsRegistryService.retrieveClaimedItemsDetails(0,10));
    }
}
