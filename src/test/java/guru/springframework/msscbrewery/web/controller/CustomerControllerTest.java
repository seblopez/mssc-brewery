package guru.springframework.msscbrewery.web.controller;

import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.MessageFormat;
import java.util.UUID;

import static guru.springframework.msscbrewery.web.controller.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    private static final String API_V1_CUSTOMER = "/api/v1/customer";

    @MockBean
    CustomerService customerService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getByIdOk() throws Exception {
        final UUID id = UUID.randomUUID();
        final String name = "Pepe Biondi";
        given(customerService.getCustomerById(any(UUID.class)))
                .willReturn(CustomerDto.builder()
                        .id(id)
                        .name(name)
                        .build());

        mockMvc.perform(get(MessageFormat.format("{0}/{1}", API_V1_CUSTOMER, id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(name)));

    }

    @Test
    public void handlePostOk() throws Exception {
        UUID id = UUID.randomUUID();
        final String name = "Pepe Biondi";

        final CustomerDto customerDto = CustomerDto.builder()
                .name(name)
                .build();

        given(customerService.saveNewCustomer(any(CustomerDto.class)))
                .willReturn(CustomerDto.builder()
                        .id(id)
                        .name(name)
                        .build());

        mockMvc.perform(post(API_V1_CUSTOMER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(customerDto)))
                .andExpect(status().isCreated());

        verify(customerService).saveNewCustomer(any(CustomerDto.class));
    }

    @Test
    public void handlePostInvalidName() throws Exception {
        UUID id = UUID.randomUUID();
        final String name = "Pe";

        final CustomerDto customerDto = CustomerDto.builder()
                .name(name)
                .build();

        given(customerService.saveNewCustomer(any(CustomerDto.class)))
                .willReturn(CustomerDto.builder()
                        .id(id)
                        .name(name)
                        .build());

        mockMvc.perform(post(API_V1_CUSTOMER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(customerDto)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(0)).saveNewCustomer(any(CustomerDto.class));

    }

    @Test
    public void handleUpdateOk() throws Exception {
        CustomerDto customerToSaveDto = CustomerDto.builder()
                .name("Pepe Biondi")
                .build();

        final UUID id = UUID.randomUUID();

        mockMvc.perform(put(MessageFormat.format("{0}/{1}", API_V1_CUSTOMER, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(customerToSaveDto)))
                .andExpect(status().isNoContent());

        then(customerService).should().updateCustomer(any(UUID.class), any(CustomerDto.class));
    }

    @Test
    public void handleUpdateNameTooLarge() throws Exception {
        CustomerDto customerToSaveDto = CustomerDto.builder()
                .name("Pepe Biondi fksjflsks lflksdflkñsdflksdfkldhsfkjsfkjdskljfhdskjfsakbkbwerkjskjbsakfbsndbcsbfjksasfdsfsdbf")
                .build();

        final UUID id = UUID.randomUUID();

        mockMvc.perform(put(MessageFormat.format("{0}/{1}", API_V1_CUSTOMER, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(customerToSaveDto)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(0)).saveNewCustomer(any(CustomerDto.class));
    }

    @Test
    public void deleteCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete(MessageFormat.format("{0}/{1}", API_V1_CUSTOMER, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(customerService).should().deleteCustomer(any(UUID.class));
    }
}
