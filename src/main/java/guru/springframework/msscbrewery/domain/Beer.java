package guru.springframework.msscbrewery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Beer {
    private UUID id;
    private String beerName;
    private BeerStyle beerStyle;
    private Long upc;
}
