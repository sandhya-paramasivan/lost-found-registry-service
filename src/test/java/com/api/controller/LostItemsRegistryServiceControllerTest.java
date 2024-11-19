package com.api.controller;


import com.api.enums.StatusEnum;
import com.api.model.LostItemsDetails;
import com.api.service.LostItemsRegistryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.api.UnitTestGlobalConstants.VALID_HEADER_FILE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataMongo
@WebMvcTest(controllers = LostItemsRegistryServiceController.class)
class LostItemsRegistryServiceControllerTest {

    @MockBean
    LostItemsRegistryService lostItemsRegistryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenValidFile_thenReturn200x() throws Exception {

        MockMultipartFile lostItemsFile = new MockMultipartFile("lostItemsFile",
                "ItemDetailsInfo.csv",
                "multipart/form-data",
                "This is a test file content".getBytes());

        when(lostItemsRegistryService.processLostItemsDetails(lostItemsFile)).thenReturn("Data processed successfully");

        mockMvc.perform(multipart ("/lostItemsRegistryService/v1/uploadData")
                        .file(lostItemsFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .headers(VALID_HEADER_FILE))
                        .andExpect(status().isOk());
    }

    @Test
    void givenInValidFile_thenReturn400x() throws Exception {

        MockMultipartFile lostItemsFile = new MockMultipartFile("lostItemsFile",
                "ItemDetailsInfo.txt",
                "text/plain",
                "This is a test file content".getBytes());

        mockMvc.perform(multipart ("/lostItemsRegistryService/v1/uploadData")
                        .file(lostItemsFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .headers(VALID_HEADER_FILE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getLostItems_DataAvailable_thenReturn200x() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(lostItemsRegistryService.retrieveLostItemsDetails(pageable)).thenReturn(ResponseEntity.ok(constructMockLostItemDetails()));
        mockMvc.perform(get("/lostItemsRegistryService/v1/retrieveLostItemsDetails"))
                .andExpect(status().isOk())  // Assert the response status is 200 OK
                .andExpect(jsonPath("$[0].itemName").value("Laptop"))
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[0].place").value("Taxi"))
                .andExpect(jsonPath("$[0].status").value("LOST"));

    }

    @Test
    void getLostItems_DataUnAvailable_thenReturn400x() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(lostItemsRegistryService.retrieveLostItemsDetails(pageable)).thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/lostItemsRegistryService/v1/retrieveLostItemsDetails"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void getClaimedLostItems_DataAvailable_thenReturn200x() throws Exception {

        when(lostItemsRegistryService.retrieveClaimedItemsDetails(0,10)).thenReturn(ResponseEntity.ok(constructMockClaimedItemDetails()));
        mockMvc.perform(get("/lostItemsRegistryService/v1/retrieveClaimedItemsDetails"))
                .andExpect(status().isOk())  // Assert the response status is 200 OK
                .andExpect(jsonPath("$[0].itemName").value("Laptop"))
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[0].place").value("Taxi"))
                .andExpect(jsonPath("$[0].status").value("CLAIMED"));

    }

    @Test
    void getClaimedLostItems_DataUnAvailable_thenReturn400x() throws Exception {

        when(lostItemsRegistryService.retrieveClaimedItemsDetails(0,10)).thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/lostItemsRegistryService/v1/retrieveClaimedItemsDetails"))
                .andExpect(status().is4xxClientError());

    }

    private  List<LostItemsDetails> constructMockLostItemDetails() {
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(LostItemsDetails.builder().id(UUID.randomUUID().toString()).
                itemName("Laptop").place("Taxi").quantity(1).status(StatusEnum.LOST).build());
        lostItemsDetailsList.add( LostItemsDetails.builder().id(UUID.randomUUID().toString()).
                itemName("Headphones").place("Railway Station").quantity(2).status(StatusEnum.LOST).build());
        return lostItemsDetailsList;
    }

    private  List<LostItemsDetails> constructMockClaimedItemDetails() {
        List<LostItemsDetails> lostItemsDetailsList = new ArrayList<>();
        lostItemsDetailsList.add(LostItemsDetails.builder().id(UUID.randomUUID().toString()).
                itemName("Laptop").place("Taxi").quantity(1).status(StatusEnum.CLAIMED).build());
        lostItemsDetailsList.add( LostItemsDetails.builder().id(UUID.randomUUID().toString()).
                itemName("Headphones").place("Railway Station").quantity(2).status(StatusEnum.CLAIMED).build());
        return lostItemsDetailsList;
    }

}
