package guru.springframework.msscbrewery.web.controller;

import guru.springframework.msscbrewery.services.BeerService;
import guru.springframework.msscbrewery.web.model.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(BeerController.class)
public class BeerControllerTest {

    private static final String API_V_1_BEER = "/api/v1/beer/";

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getBeerByIdOk() throws Exception {
        final UUID id = UUID.randomUUID();
        given(beerService.getBeerById(any(UUID.class)))
                .willReturn(BeerDto.builder()
                        .id(id)
                        .beerName("Quilmes")
                        .beerStyle("Stout")
                        .upc(1213L)
                        .build());

        mockMvc.perform(get(API_V_1_BEER + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beerName", equalTo("Quilmes")));

        verify(beerService).getBeerById(any(UUID.class));

    }

    @Test
    public void handlePostOk() throws Exception {
        UUID id = UUID.randomUUID();
        final String beerName = "Andes";
        final String beerStyle = "IPA";
        final long upc = 1L;

        final BeerDto beerDto = BeerDto.builder()
                .beerName(beerName)
                .beerStyle(beerStyle)
                .upc(upc)
                .build();

        given(beerService.saveNewBeer(any(BeerDto.class)))
                .willReturn(BeerDto.builder()
                        .id(id)
                        .beerName(beerName)
                        .beerStyle(beerStyle)
                        .upc(upc)
                        .build());

        mockMvc.perform(post(API_V_1_BEER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDto)))
                .andExpect(status().isCreated());

        verify(beerService).saveNewBeer(any(BeerDto.class));

    }

    @Test
    public void handleUpdateOk() throws Exception {
        BeerDto beerToSaveDto = BeerDto.builder()
                .beerName("Andes")
                .beerStyle("Lager")
                .upc(12L)
                .build();

        final UUID id = UUID.randomUUID();
        given(beerService.updateBeer(any(UUID.class), any(BeerDto.class))).willReturn(BeerDto.builder()
                .id(id)
                .beerName("Andes")
                .beerStyle("Lager")
                .upc(12L)
                .build());

        mockMvc.perform(put(API_V_1_BEER + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerToSaveDto)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeer(any(UUID.class), any(BeerDto.class));
    }

    @Test
    public void deleteBeer() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete(API_V_1_BEER + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(beerService).should().deleteBeer(any(UUID.class));

    }

}
