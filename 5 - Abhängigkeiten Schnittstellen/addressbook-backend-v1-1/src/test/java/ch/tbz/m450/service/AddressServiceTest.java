package ch.tbz.m450.service;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.tbz.m450.AddressTestData.address;
import static ch.tbz.m450.AddressTestData.date;
import static ch.tbz.m450.AddressTestData.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Service-Tests ohne H2 und ohne Spring-Kontext. Das Repository ist ein Mockito-Mock,
 * damit nur die Service-Logik geprueft wird: durchreichen an das Repository und
 * sortieren in getAll().
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<Address> addressCaptor;

    private Address meierAnna;
    private Address zuegerBeat;
    private Address ammannCarla;

    @BeforeEach
    void setUp() {
        meierAnna = named(1, "Anna", "Meier");
        zuegerBeat = named(2, "Beat", "Zueger");
        ammannCarla = named(3, "Carla", "Ammann");
    }

    @Test
    @DisplayName("save() gibt die Adresse unveraendert an das Repository weiter")
    void saveReichtAnRepositoryWeiter() {
        when(addressRepository.save(meierAnna)).thenReturn(meierAnna);

        Address saved = addressService.save(meierAnna);

        assertThat(saved).isSameAs(meierAnna);
        verify(addressRepository).save(addressCaptor.capture());
        assertThat(addressCaptor.getValue().getLastname()).isEqualTo("Meier");
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    @DisplayName("save() liefert zurueck, was das Repository liefert, nicht die Eingabe")
    void saveLiefertRepositoryErgebnis() {
        Address mitVergebenerId = named(99, "Anna", "Meier");
        when(addressRepository.save(any(Address.class))).thenReturn(mitVergebenerId);

        Address saved = addressService.save(meierAnna);

        assertThat(saved.getId()).isEqualTo(99);
    }

    @Test
    @DisplayName("getAll() sortiert die Adressen aus dem Repository")
    void getAllSortiertDieAdressen() {
        when(addressRepository.findAll()).thenReturn(List.of(zuegerBeat, meierAnna, ammannCarla));

        List<Address> result = addressService.getAll();

        assertThat(result).extracting(Address::getLastname)
                .containsExactly("Ammann", "Meier", "Zueger");
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("getAll() sortiert bei gleichem Nachnamen nach Vorname und Datum")
    void getAllSortiertMehrstufig() {
        Address meierZoe = named(4, "Zoe", "Meier");
        Address meierAnnaAlt = address(5, "Anna", "Meier", "+41 44 000 00 00", date(2020, 1, 1));
        when(addressRepository.findAll()).thenReturn(List.of(meierZoe, meierAnna, meierAnnaAlt));

        List<Address> result = addressService.getAll();

        assertThat(result).containsExactly(meierAnnaAlt, meierAnna, meierZoe);
    }

    @Test
    @DisplayName("getAll() liefert eine leere Liste, wenn das Repository nichts hat")
    void getAllLiefertLeereListe() {
        when(addressRepository.findAll()).thenReturn(List.of());

        assertThat(addressService.getAll()).isEmpty();
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("getAll() veraendert die Liste des Repositories nicht")
    void getAllVeraendertRepositoryListeNicht() {
        List<Address> vomRepository = List.of(zuegerBeat, meierAnna);
        when(addressRepository.findAll()).thenReturn(vomRepository);

        addressService.getAll();

        assertThat(vomRepository).containsExactly(zuegerBeat, meierAnna);
    }

    @Test
    @DisplayName("getAddress() gibt die gefundene Adresse zurueck")
    void getAddressLiefertGefundeneAdresse() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(meierAnna));

        Optional<Address> result = addressService.getAddress(1);

        assertThat(result).containsSame(meierAnna);
        verify(addressRepository).findById(1);
        verify(addressRepository, never()).findAll();
    }

    @Test
    @DisplayName("getAddress() gibt ein leeres Optional zurueck, wenn es die Id nicht gibt")
    void getAddressLiefertLeeresOptional() {
        when(addressRepository.findById(404)).thenReturn(Optional.empty());

        assertThat(addressService.getAddress(404)).isEmpty();
        verify(addressRepository).findById(404);
    }

    @Test
    @DisplayName("Jeder getAll()-Aufruf fragt das Repository neu, es wird nichts gecacht")
    void getAllFragtRepositoryJedesMal() {
        when(addressRepository.findAll()).thenReturn(List.of(meierAnna));

        addressService.getAll();
        addressService.getAll();

        verify(addressRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Fake: ein Repository mit HashMap statt H2 zeigt das Zusammenspiel von save und getAll")
    void fakeRepositoryStattDatenbank() {
        Map<Integer, Address> store = new HashMap<>();
        AddressRepository fake = mock(AddressRepository.class);
        when(fake.save(any(Address.class))).thenAnswer(invocation -> {
            Address toSave = invocation.getArgument(0);
            store.put(toSave.getId(), toSave);
            return toSave;
        });
        when(fake.findAll()).thenAnswer(invocation -> List.copyOf(store.values()));
        AddressService serviceMitFake = new AddressService(fake);

        serviceMitFake.save(zuegerBeat);
        serviceMitFake.save(meierAnna);
        serviceMitFake.save(ammannCarla);

        assertThat(serviceMitFake.getAll()).extracting(Address::getLastname)
                .containsExactly("Ammann", "Meier", "Zueger");
        assertThat(store).hasSize(3);
    }
}
