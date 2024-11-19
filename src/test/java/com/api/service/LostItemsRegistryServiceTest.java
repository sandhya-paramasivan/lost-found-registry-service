package com.api.service;

import com.api.enums.StatusEnum;
import com.api.model.LostItemsDetails;
import com.api.repository.LostItemsRegistryRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
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
        String result = lostItemsRegistryService.processLostItemsDetails(lostItemsFile);

        // Verify the repository save method was called with the correct list
        verify(lostItemsRegistryRepository, times(1)).saveAll(anyList());

        // Verify the result message
        assertEquals("Data processed successfully", result);

    }


    @org.junit.jupiter.api.Test
    void givenPageAndSize_thenReturnList(){
        LostItemsDetails lostItemsDetails = LostItemsDetails.builder().itemName("Laptop").quantity(1).place("Office")
                .status(StatusEnum.LOST).userDetailsList(new ArrayList<>()).build();
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(lostItemsDetails);
        Mockito.mock(LostItemsRegistryRepository.class);
        when(lostItemsRegistryRepository.findByStatus(any(StatusEnum.class), any(Pageable.class)))
                .thenReturn(lostItemsDetailsList);



        // Call the service method
        ResponseEntity<List<LostItemsDetails>> response = lostItemsRegistryService.retrieveLostItemsDetails(pageable);




    }
}
