package guru.springframework.msscbrewery.web.controller.v2;

import guru.springframework.msscbrewery.services.v2.BeerServiceV2;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import guru.springframework.msscbrewery.web.model.v2.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static guru.springframework.msscbrewery.web.controller.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest(BeerControllerV2.class)
public class BeerControllerV2Test {

    private static final String API_V2_BEER = "/api/v2/beer";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BeerServiceV2 beerService;

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

        mockMvc.perform(get(API_V2_BEER + "/{beerId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beerName", equalTo("Quilmes")))
                .andDo(document("v2/beer-get",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of desired beer to get.")),
                        responseFields(
                                fieldWithPath("id").description("Id of beer").ignored(),
                                fieldWithPath("createdDate").description("Date Created").ignored(),
                                fieldWithPath("lastUpdatedDate").description("Date of last update").ignored(),
                                fieldWithPath("beerName").description("Beer name"),
                                fieldWithPath("beerStyle").description("Beer Style"),
                                fieldWithPath("upc").description("UPC of Beer")
                        )));

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

        ConstrainedFields fields = new ConstrainedFields(BeerDtoV2.class);

        mockMvc.perform(post(API_V2_BEER)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDto)))
                .andExpect(status().isCreated())
                .andDo(document("v2/beer-new",
                        requestFields(
                                fields.withPath("id").description("Id of beer").ignored(),
                                fields.withPath("createdDate").description("Date Created").ignored(),
                                fields.withPath("lastUpdatedDate").description("Date of last update").ignored(),
                                fields.withPath("beerName").description("Beer name"),
                                fields.withPath("beerStyle").description("Beer Style"),
                                fields.withPath("upc").description("UPC of Beer")
                        )));

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

        ConstrainedFields fields = new ConstrainedFields(BeerDtoV2.class);

        mockMvc.perform(put(API_V2_BEER + "/{beerId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerToSaveDto)))
                .andExpect(status().isNoContent())
                .andDo(document("v2/beer-update",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of desired beer to update.")),
                        requestFields(
                                fields.withPath("id").description("Id of beer").ignored(),
                                fields.withPath("createdDate").description("Date Created").ignored(),
                                fields.withPath("lastUpdatedDate").description("Date of last update").ignored(),
                                fields.withPath("beerName").description("Beer name"),
                                fields.withPath("beerStyle").description("Beer Style"),
                                fields.withPath("upc").description("UPC of Beer")
                        )));


        verify(beerService).updateBeer(any(UUID.class), any(BeerDtoV2.class));
    }

    @Test
    public void deleteBeer() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete(API_V2_BEER + "/{beerId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("v2/beer-delete",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of desired beer to delete."))));

        then(beerService).should().deleteBeer(any(UUID.class));
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constrainedDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constrainedDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints")
                    .value(StringUtils.collectionToDelimitedString(this.constrainedDescriptions
                            .descriptionsForProperty(path), ". ")));
        }

    }

}
