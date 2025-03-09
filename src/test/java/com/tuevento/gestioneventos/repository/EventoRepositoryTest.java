package com.tuevento.gestioneventos.repository;

import com.tuevento.gestioneventos.model.Evento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventoRepositoryTest {

    @Autowired
    private EventoRepository eventoRepository;

    @Test
    void guardarYRecuperarEvento() {
        Evento evento = new Evento("Concierto", "Un gran concierto", LocalDate.now().plusDays(5), "Madrid");
        Evento eventoGuardado = eventoRepository.save(evento);

        Optional<Evento> eventoRecuperado = eventoRepository.findById(eventoGuardado.getId());

        assertTrue(eventoRecuperado.isPresent());
        assertEquals("Concierto", eventoRecuperado.get().getNombre());
    }

    @Test
    void eliminarEvento_DebeSerEliminado() {
        Evento evento = new Evento("Festival", "Festival de verano", LocalDate.now().plusDays(10), "Barcelona");
        Evento eventoGuardado = eventoRepository.save(evento);

        eventoRepository.deleteById(eventoGuardado.getId());

        Optional<Evento> eventoEliminado = eventoRepository.findById(eventoGuardado.getId());

        assertFalse(eventoEliminado.isPresent());
    }
}
