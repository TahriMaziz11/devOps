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
import tn.esprit.spring.entities.Voyageur;
import tn.esprit.spring.entities.dto.Voyageurdto;
import tn.esprit.spring.repository.VoyageurRepository;
import tn.esprit.spring.services.VoyageurServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class VoyageurTest {
    @Mock
    private VoyageurRepository voyageurRepository;
    @InjectMocks
    private VoyageurServiceImpl voyageurService;

    private Voyageur v1;

    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        this.v1 = new Voyageur();
        this.v1.setNomVoyageur("slouma");
        this.v1.setIdVoyageur(0L);
        modelMapper = new ModelMapper();
    }

    @Test
    public void testAjouterVoyageur() {
        init();
        when(voyageurRepository.save(any(Voyageur.class))).thenReturn(v1);
        Voyageurdto vrm = modelMapper.map(v1, Voyageurdto.class);
        Voyageur vnew = voyageurService.ajouterVoyageur(vrm);
        assertNotNull(vnew);
        assertThat(vnew.getIdVoyageur()).isZero();
    }

    @Test
    public void testModifierVoyageur() {
        init();
        v1.setNomVoyageur("boub");
        when(voyageurRepository.save(any(Voyageur.class))).thenReturn(v1);
        assertThat(v1.getNomVoyageur()).isEqualTo("boub");
    }

    @Test
    public void testRecupererAll() {
        init();
        List<Voyageur> list = new ArrayList<>();
        list.add(v1);
        when(voyageurRepository.findAll()).thenReturn(list);
        List<Voyageur> voyages = voyageurService.recupererAll();
        assertEquals(1, voyages.size());
        assertNotNull(voyages);
    }

    @Test
    public void testRecupererVoyageurParId() {
        init();
        when(voyageurRepository.save(any(Voyageur.class))).thenReturn(v1);
        Voyageurdto vdto = modelMapper.map(v1, Voyageurdto.class);
        Voyageur vnew = voyageurService.ajouterVoyageur(vdto);
        when(voyageurRepository.findById(anyLong())).thenReturn(Optional.of(v1));
        Voyageur existingVoyage = voyageurService.recupererVoyageParId(vnew.getIdVoyageur());
        assertNotNull(existingVoyage);
        assertThat(existingVoyage.getIdVoyageur()).isNotNull();
    }

    @Test
    public void testSupprimerVoyageur() {
        init();
        when(voyageurRepository.findById(0L)).thenReturn(Optional.of(v1));
        doNothing().when(voyageurRepository).deleteById(0L);
        voyageurService.supprimerVoyageur(v1);
        verify(voyageurRepository, times(1)).delete(v1);
    }
}
