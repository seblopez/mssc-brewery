package guru.springframework.msscbrewery.web.controller;

import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private static final String API_V1_CUSTOMER = "/api/v1/customer";
    private final CustomerService customerService;
    private final String host;
    private final String port;

    public CustomerController(CustomerService customerService, @Value("${beer.app.host}") String host, @Value("${beer.app.port}") String port) {
        this.customerService = customerService;
        this.host = host;
        this.port = port;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getById(@PathVariable UUID customerId) {
        return new ResponseEntity<>(customerService.getCustomerById(customerId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity handlePost(@Valid @RequestBody CustomerDto customerDto) {
        CustomerDto savedDto = customerService.saveNewCustomer(customerDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", MessageFormat.format("{0}:{1}{2}/{3}", this.host, this.port, API_V1_CUSTOMER, savedDto.getId()));
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleUpdate(@PathVariable UUID customerId, @Valid @RequestBody CustomerDto customerDto) {
        customerService.updateCustomer(customerId, customerDto);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable UUID customerId) {
        customerService.deleteCustomer(customerId);
    }

}
