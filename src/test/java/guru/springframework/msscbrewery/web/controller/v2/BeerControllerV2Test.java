package guru.springframework.msscbrewery.web.controller.v2;

import guru.springframework.msscbrewery.services.v2.BeerServiceV2;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import guru.springframework.msscbrewery.web.model.v2.BeerStyleEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.MessageFormat;
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

public class BeerControllerV2Test {

    private static final String API_V2_BEER = "/api/v2/beer";

    MockMvc mockMvc;

    @Mock
    BeerServiceV2 beerService;

    @InjectMocks
    BeerControllerV2 beerController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
    }

    @Test
    public void getBeerOk() throws Exception {
        final UUID id = UUID.randomUUID();
        given(beerService.getBeerById(any(UUID.class)))
                .willReturn(BeerDtoV2.builder()
                        .id(id)
                        .beerName("Quilmes")
                        .beerStyle(BeerStyleEnum.ALE)
                        .upc(1213L)
                        .build());

        mockMvc.perform(get(MessageFormat.format("{0}/{1}",API_V2_BEER, id))
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
        final long upc = 1L;

        final BeerDtoV2 beerDto = BeerDtoV2.builder()
                .beerName(beerName)
                .beerStyle(BeerStyleEnum.IPA)
                .upc(upc)
                .build();

        given(beerService.saveNewBeer(any(BeerDtoV2.class)))
                .willReturn(BeerDtoV2.builder()
                        .id(id)
                        .beerName(beerName)
                        .beerStyle(BeerStyleEnum.IPA)
                        .upc(upc)
                        .build());

        mockMvc.perform(post(API_V2_BEER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDto)))
                .andExpect(status().isCreated());

        verify(beerService).saveNewBeer(any(BeerDtoV2.class));
    }

    @Test
    public void handlePostFailsInvalidUpc() throws Exception {
        UUID id = UUID.randomUUID();
        final String beerName = "Andes";
        final long upc = -1L;

        final BeerDtoV2 beerDto = BeerDtoV2.builder()
                .beerName(beerName)
                .beerStyle(BeerStyleEnum.IPA)
                .upc(upc)
                .build();

        given(beerService.saveNewBeer(any(BeerDtoV2.class)))
                .willReturn(BeerDtoV2.builder()
                        .id(id)
                        .beerName(beerName)
                        .beerStyle(BeerStyleEnum.IPA)
                        .upc(upc)
                        .build());

        mockMvc.perform(post(API_V2_BEER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void handlePostFailsNullBody() throws Exception {
        UUID id = UUID.randomUUID();
        final String beerName = "Andes";
        final long upc = -1L;

        given(beerService.saveNewBeer(any(BeerDtoV2.class)))
                .willReturn(BeerDtoV2.builder()
                        .id(id)
                        .beerName(beerName)
                        .beerStyle(BeerStyleEnum.IPA)
                        .upc(upc)
                        .build());

        mockMvc.perform(post(API_V2_BEER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(null)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void handleUpdate() throws Exception {
        BeerDtoV2 beerToSaveDto = BeerDtoV2.builder()
                .beerName("Andes")
                .beerStyle(BeerStyleEnum.LAGER)
                .upc(12L)
                .build();

        final UUID id = UUID.randomUUID();
        given(beerService.updateBeer(any(UUID.class), any(BeerDtoV2.class))).willReturn(BeerDtoV2.builder()
                .id(id)
                .beerName("Andes")
                .beerStyle(BeerStyleEnum.LAGER)
                .upc(12L)
                .build());

        mockMvc.perform(put(MessageFormat.format("{0}/{1}",API_V2_BEER, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerToSaveDto)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeer(any(UUID.class), any(BeerDtoV2.class));
    }

    @Test
    public void deleteBeer() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete(MessageFormat.format("{0}/{1}",API_V2_BEER, id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(beerService).should().deleteBeer(any(UUID.class));
    }
}
