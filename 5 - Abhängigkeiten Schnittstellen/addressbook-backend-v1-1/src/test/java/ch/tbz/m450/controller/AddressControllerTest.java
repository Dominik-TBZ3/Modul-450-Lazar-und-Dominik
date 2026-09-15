package ch.tbz.m450.controller;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static ch.tbz.m450.AddressTestData.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Controller-Tests mit gemocktem Service. Geprueft werden die HTTP-Statuscodes und
 * dass der Controller nichts anderes macht als delegieren.
 */
@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private Address meierAnna;

    @BeforeEach
    void setUp() {
        meierAnna = named(1, "Anna", "Meier");
    }

    @Test
    @DisplayName("POST auf /address antwortet mit 201 und der gespeicherten Adresse")
    void createAddressAntwortetMit201() {
        when(addressService.save(meierAnna)).thenReturn(meierAnna);

        ResponseEntity<Address> response = addressController.createAddress(meierAnna);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(meierAnna);
        verify(addressService).save(meierAnna);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    @DisplayName("GET auf /address antwortet mit 200 und der Liste des Services")
    void getAddressesAntwortetMit200() {
        Address zuegerBeat = named(2, "Beat", "Zueger");
        when(addressService.getAll()).thenReturn(List.of(meierAnna, zuegerBeat));

        ResponseEntity<List<Address>> response = addressController.getAddresses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(meierAnna, zuegerBeat);
        verify(addressService).getAll();
    }

    @Test
    @DisplayName("GET auf /address antwortet auch bei leerer Liste mit 200")
    void getAddressesAntwortetAuchLeerMit200() {
        when(addressService.getAll()).thenReturn(List.of());

        ResponseEntity<List<Address>> response = addressController.getAddresses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET auf /address mit bekannter Id antwortet mit 200")
    void getAddressAntwortetMit200() {
        when(addressService.getAddress(1)).thenReturn(Optional.of(meierAnna));

        ResponseEntity<Address> response = addressController.getAddress(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(meierAnna);
        verify(addressService).getAddress(1);
    }

    @Test
    @DisplayName("GET auf /address mit unbekannter Id antwortet mit 404 und ohne Body")
    void getAddressAntwortetMit404() {
        when(addressService.getAddress(404)).thenReturn(Optional.empty());

        ResponseEntity<Address> response = addressController.getAddress(404);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(addressService).getAddress(404);
    }
}
