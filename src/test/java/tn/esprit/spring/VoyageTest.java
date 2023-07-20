package tn.esprit.spring;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit4.SpringRunner;
import tn.esprit.spring.entities.Train;
import tn.esprit.spring.entities.Voyage;
import tn.esprit.spring.entities.dto.Voyagedto;
import tn.esprit.spring.repository.TrainRepository;
import tn.esprit.spring.repository.VoyageRepository;
import tn.esprit.spring.services.VoyageServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static tn.esprit.spring.entities.Ville.RADES;
import static tn.esprit.spring.entities.Ville.TUNIS;
import static tn.esprit.spring.entities.etatTrain.EN_ROUTE;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class VoyageTest {
    @Mock
    private VoyageRepository voyageRepository;
    @Mock
    private TrainRepository trainRepository;
    @InjectMocks
    private VoyageServiceImpl voyageService;
    private Voyage v1;
    private Train t1;

    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        this.v1 = new Voyage();
        this.v1.setIdVoyage(0L);
        this.v1.setCodeVoyage(0L);
        this.v1.setGareDepart(TUNIS);
        //this.v1.setTrain(t1);
        this.v1.setGareArrivee(RADES);
        this.v1.setHeureDepart(16);
        this.v1.setDateArrivee(new Date(2022, 12, 10));

        this.t1 = new Train();
        this.t1.setIdTrain(0L);
        this.t1.setEtat(EN_ROUTE);
        this.t1.setNbPlaceLibre(5);
        this.t1.setCodeTrain(0L);

        modelMapper = new ModelMapper();
    }

    @Test
    public void testAjouterVoyage() {
        init();
        when(voyageRepository.save(any(Voyage.class))).thenReturn(v1);
        Voyagedto vrm = modelMapper.map(v1, Voyagedto.class);
        Voyage vnew = voyageService.ajouterVoyage(vrm);
        assertNotNull(vnew);
        assertThat(vnew.getIdVoyage()).isZero();
    }

    @Test
    public void testModifierVoyage() {
        init();
        v1.setGareDepart(RADES);
        when(voyageRepository.save(any(Voyage.class))).thenReturn(v1);
        assertThat(v1.getGareDepart()).isEqualTo(RADES);
    }

    @Test
    public void testAffecterTrainAVoyage() {
        init();
        when(voyageRepository.findById(0L)).thenReturn(Optional.of(v1));
        when(trainRepository.findById(0L)).thenReturn(Optional.of(t1));
        assertThat(t1).isNotNull();
        assertThat(v1).isNotNull();
        v1.setTrain(t1);
        voyageRepository.save(v1);
        assertThat(v1.getTrain()).isEqualTo(t1);
    }

    @Test
    public void testRecupererAll() {
        init();
        List<Voyage> list = new ArrayList<>();
        list.add(v1);
        when(voyageRepository.findAll()).thenReturn(list);
        List<Voyage> voyages = voyageService.recupererAll();
        assertEquals(1, voyages.size());
        assertNotNull(voyages);
    }

    @Test
    public void testRecupererVoyageParId() {
        init();
        when(voyageRepository.save(any(Voyage.class))).thenReturn(v1);
        Voyagedto vdto = modelMapper.map(v1, Voyagedto.class);
        Voyage vnew = voyageService.ajouterVoyage(vdto);
        when(voyageRepository.findById(anyLong())).thenReturn(Optional.of(v1));
        Voyage existingVoyage = voyageService.recupererVoyageParId(vnew.getIdVoyage());
        assertNotNull(existingVoyage);
        assertThat(existingVoyage.getIdVoyage()).isNotNull();
    }

    @Test
    public void testSupprimerVoyage() {
        init();
        when(voyageRepository.findById(0L)).thenReturn(Optional.of(v1));
        doNothing().when(voyageRepository).deleteById(anyLong());
        voyageService.supprimerVoyage(v1);
        verify(voyageRepository, times(1)).deleteById(anyLong());
    }
}
