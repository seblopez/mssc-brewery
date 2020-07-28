package guru.springframework.msscbrewery.web.controller;

import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static guru.springframework.msscbrewery.web.controller.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerTest {

    private static final String API_V_1_CUSTOMER = "/api/v1/customer/";

    @Mock
    CustomerService customerService;

    @InjectMocks
    CustomerController customerController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void getByIdOk() throws Exception {
        final UUID id = UUID.randomUUID();
        final String name = "Pepe Biondi";
        given(customerService.getCustomerById(any(UUID.class)))
                .willReturn(CustomerDto.builder()
                        .id(id)
                        .name(name)
                        .build());

        mockMvc.perform(get(API_V_1_CUSTOMER + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(name)));

    }

    @Test
    public void handlePost() throws Exception {
        UUID id = UUID.randomUUID();
        final String name = "Pepe Biondi";
        final long upc = 1L;

        final CustomerDto customerDto = CustomerDto.builder()
                .name(name)
                .build();

        given(customerService.saveNewCustomer(any(CustomerDto.class)))
                .willReturn(CustomerDto.builder()
                        .id(id)
                        .name(name)
                        .build());

        mockMvc.perform(post(API_V_1_CUSTOMER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(customerDto)))
                .andExpect(status().isCreated());

        verify(customerService).saveNewCustomer(any(CustomerDto.class));
    }

    @Test
    public void handleUpdate() throws Exception {
        CustomerDto customerToSaveDto = CustomerDto.builder()
                .name("Pepe Biondi")
                .build();

        final UUID id = UUID.randomUUID();

        mockMvc.perform(put(API_V_1_CUSTOMER + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(customerToSaveDto)))
                .andExpect(status().isNoContent());

        then(customerService).should().updateCustomer(any(UUID.class), any(CustomerDto.class));
    }

    @Test
    public void deleteCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete(API_V_1_CUSTOMER + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(customerService).should().deleteCustomer(any(UUID.class));
    }
}
