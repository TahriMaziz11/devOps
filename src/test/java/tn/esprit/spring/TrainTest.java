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
import tn.esprit.spring.entities.Voyageur;
import tn.esprit.spring.entities.dto.Traindto;
import tn.esprit.spring.repository.TrainRepository;
import tn.esprit.spring.repository.VoyageRepository;
import tn.esprit.spring.repository.VoyageurRepository;
import tn.esprit.spring.services.TrainServiceImpl;
import tn.esprit.spring.services.VoyageServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static tn.esprit.spring.entities.Ville.RADES;
import static tn.esprit.spring.entities.Ville.TUNIS;
import static tn.esprit.spring.entities.etatTrain.EN_ROUTE;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class TrainTest {
    @Mock
    private TrainRepository trainRepository;
    @Mock
    private VoyageRepository voyageRepository;
    @Mock
    private VoyageurRepository voyageurRepository;
    @InjectMocks
    private TrainServiceImpl trainService;
    @InjectMocks
    private VoyageServiceImpl voyageService;

    private Train t1;
    private Train t2;

    private Voyage v1;
    private Voyage v2;
    private Voyage v3;

    private Voyageur voyageur;
    private Voyageur voyageur2;
    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        this.t1 = new Train();
        this.t1.setIdTrain(0L);
        this.t1.setEtat(EN_ROUTE);
        this.t1.setNbPlaceLibre(5);
        this.t1.setCodeTrain(0L);

        this.t2 = new Train();
        this.t2.setIdTrain(1L);
        this.t2.setEtat(EN_ROUTE);
        this.t2.setNbPlaceLibre(2);
        this.t2.setCodeTrain(1L);

        this.v1 = new Voyage();
        this.v1.setIdVoyage(0L);
        this.v1.setCodeVoyage(0L);
        this.v1.setGareDepart(TUNIS);
        this.v1.setTrain(t1);
        this.v1.setGareArrivee(RADES);
        this.v1.setHeureDepart(16);
        this.v1.setDateArrivee(new Date(2022, 12, 10));

        this.v2 = new Voyage();
        this.v2.setIdVoyage(1L);
        this.v2.setCodeVoyage(1L);
        this.v2.setGareDepart(TUNIS);
        this.v2.setTrain(t2);
        this.v2.setGareArrivee(TUNIS);
        this.v2.setDateArrivee(new Date(2022, 12, 10));

        this.v3 = new Voyage();
        this.v3.setIdVoyage(2L);
        this.v3.setCodeVoyage(2L);
        this.v3.setGareDepart(RADES);
        this.v3.setTrain(t1);
        this.v3.setGareArrivee(TUNIS);

        this.voyageur = new Voyageur();
        this.voyageur.setIdVoyageur(0L);
        this.voyageur.setNomVoyageur("salah");

        this.voyageur2 = new Voyageur();
        this.voyageur2.setIdVoyageur(0L);
        this.voyageur2.setNomVoyageur("abslem");


        this.modelMapper = new ModelMapper();
    }

    @Test
    public void testAddTrain() {
        init();
        when(trainRepository.save(any(Train.class))).thenReturn(t1);
        Traindto trm = modelMapper.map(t1, Traindto.class);
        Train tnew = trainService.ajouterTrain(trm);
        assertNotNull(tnew);
        assertThat(tnew.getCodeTrain()).isZero();
    }

    @Test
    public void testTrainPlacesLibres() {
        init();
        List<Voyage> list = new ArrayList<>();
        list.add(v1);
        list.add(v2);
        when(trainRepository.findById(0L)).thenReturn(Optional.of(t1));
        when(voyageRepository.findAll()).thenReturn(list);
        int cpt = t1.getNbPlaceLibre() + t2.getNbPlaceLibre();
        int occ = list.size();
        int result = cpt/occ;
        assertThat(v1.getGareDepart()).isEqualTo(TUNIS);
        assertThat(v2.getGareDepart()).isEqualTo(TUNIS);
        assertThat(occ).isPositive();
        assertThat(trainService.trainPlacesLibres(TUNIS)).isEqualTo(result);
    }

    @Test
    public void testListerTrainsIndirects() {
        init();
        List<Train> lestrainsRes = new ArrayList<>();
        List<Voyage> lesVoyage = new ArrayList<>();
        lesVoyage.add(v1);
        lesVoyage.add(v3);
        when(voyageRepository.findAll()).thenReturn(lesVoyage);
        assertThat(v1.getGareDepart()).isEqualTo(TUNIS);
        assertThat(v3.getGareDepart()).isEqualTo(RADES);
        assertThat(v1.getGareArrivee()).isEqualTo(v3.getGareDepart());
        assertThat(v1.getGareArrivee()).isEqualTo(RADES);
        lestrainsRes.add(t1);
        lestrainsRes.add(t1);
        assertThat(lestrainsRes).hasSize(2);
    }

    @Test
    public void testAffecterTainAVoyageur() {
        init();
        Voyageur lesVoyageur;
        lesVoyageur = voyageur;
        List<Voyage> lesVoyages = new ArrayList<>();
        when(voyageurRepository.findById(anyLong())).thenReturn(Optional.of(lesVoyageur));
        when(voyageRepository.rechercheVoyage(TUNIS,RADES,16)).thenReturn(lesVoyages);
        assertThat(v1.getTrain().getNbPlaceLibre()).isPositive();
        lesVoyages.add(v1);
        v1.getTrain().setNbPlaceLibre(t1.getNbPlaceLibre() - 1);
        when(voyageRepository.save(v1)).thenReturn(v1);
    }



    @Test
    public void testTrainsEnGare() {
        init();
        List<Voyage> lesVoyages = new ArrayList<>();
        lesVoyages.add(v1);
        when(voyageRepository.findAll()).thenReturn(lesVoyages);
        assertThat("les trains sont " + lesVoyages.get(0).getTrain().getCodeTrain()).isEqualTo("les trains sont 0");
    }
}
