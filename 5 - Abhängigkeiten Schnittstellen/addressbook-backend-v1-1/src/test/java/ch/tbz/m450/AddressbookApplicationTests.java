package ch.tbz.m450;

import ch.tbz.m450.controller.AddressController;
import ch.tbz.m450.repository.AddressRepository;
import ch.tbz.m450.service.AddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstest fuer die Anwendungsklasse: der Spring-Kontext startet und die
 * Beans sind samt H2 verdrahtet. Hier wird bewusst nichts weggemockt.
 */
@SpringBootTest
class AddressbookApplicationTests {

    @Autowired
    private AddressController addressController;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("Der Spring-Kontext startet und verdrahtet alle Beans")
    void contextLoads() {
        assertThat(addressController).isNotNull();
        assertThat(addressService).isNotNull();
        assertThat(addressRepository).isNotNull();
    }
}
