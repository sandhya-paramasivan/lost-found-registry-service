package com.api.service;

import com.api.enums.StatusEnum;
import com.api.exception.ResourceNotFoundException;
import com.api.model.LostItemsDetails;
import com.api.repository.LostItemsRegistryRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
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

    private Pageable pageable;

    @BeforeEach
     void setup()  {
        MockitoAnnotations.openMocks(this);

        // Create a mock file (multipart) for testing
        String csvContent = "itemName;quantity;place\n" +
                "Laptop;1;Office\n" +
                "Phone;2;Home";
        lostItemsFile = new MockMultipartFile("file", "lost_items.csv", "text/csv", csvContent.getBytes());
        pageable = PageRequest.of(0, 10);

    }


    void givenValidFile_UploadAndSaveItems() throws IOException {
        when(csvParser.stream()).thenReturn(Arrays.asList(csvRecord, csvRecord).stream());
        when(csvRecord.get(0)).thenReturn("Laptop;1;Office").thenReturn("Phone;2;Home");
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(1).place("Office").build();
        // Simulate the repository save behavior
        when(lostItemsRegistryRepository.saveAll(Collections.singletonList(lostItemsDetails))).thenReturn(Collections.singletonList(lostItemsDetails));

        // Call the method to test
        ResponseEntity<String> responseEntity = lostItemsRegistryService.processLostItemsDetails(lostItemsFile);

        // Verify the repository save method was called with the correct list
        verify(lostItemsRegistryRepository, times(1)).saveAll(anyList());

        // Verify the result message
        assertEquals("Data processed successfully", responseEntity.getBody());

    }

    @Test
    void givenItemName_thenReturn200_claimlostItems() throws BadRequestException {
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(1).place("Office")
                .status(StatusEnum.LOST).userDetailsList(new ArrayList<>()).build();
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(lostItemsDetails);
        when(lostItemsRegistryRepository.findByItemNameAndPlace("Laptop", "Office"))
                .thenReturn(lostItemsDetailsList);
        lostItemsRegistryService.claimLostItems("Laptop", "Office","U1001" , 1);
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
        lostItemsRegistryService.retrieveLostItemsDetails(pageable);
    }

    @Test
    void givenPageAndSize_thenReturn404_retrieveLostItems(){
        when(lostItemsRegistryRepository.findByStatus(StatusEnum.LOST,pageable))
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
        when(lostItemsRegistryRepository.findByStatus(StatusEnum.LOST,pageable))
                .thenReturn( new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> lostItemsRegistryService.retrieveClaimedItemsDetails(0,10));
    }
}
