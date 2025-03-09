package com.tuevento.gestioneventos.service;

import com.tuevento.gestioneventos.model.Evento;
import com.tuevento.gestioneventos.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService;

    private Evento evento;

    @BeforeEach
    void setUp() {
        evento = new Evento("Concierto", "Música en vivo", LocalDate.now().plusDays(10), "Auditorio");
    }

    @Test
    void obtenerTodosLosEventos_DebeRetornarListaDeEventos() {
        List<Evento> eventos = Arrays.asList(evento, new Evento("Conferencia", "Tech Talk", LocalDate.now().plusDays(5), "Sala A"));

        when(eventoRepository.findAll()).thenReturn(eventos);

        List<Evento> resultado = eventoService.obtenerTodosLosEventos();

        assertEquals(2, resultado.size());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    void obtenerEventoPorId_CuandoExiste_DebeRetornarEvento() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        Optional<Evento> resultado = eventoService.obtenerEventoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Concierto", resultado.get().getNombre());
        verify(eventoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerEventoPorId_CuandoNoExiste_DebeRetornarVacio() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Evento> resultado = eventoService.obtenerEventoPorId(99L);

        assertFalse(resultado.isPresent());
        verify(eventoRepository, times(1)).findById(99L);
    }

    @Test
    void guardarEvento_DebeRetornarEventoGuardado() {
        when(eventoRepository.save(evento)).thenReturn(evento);

        Evento resultado = eventoService.guardarEvento(evento);

        assertNotNull(resultado);
        assertEquals("Concierto", resultado.getNombre());
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    void actualizarEvento_CuandoExiste_DebeRetornarEventoActualizado() {
        Evento eventoActualizado = new Evento("Festival", "Festival de verano", LocalDate.now().plusDays(20), "Parque");

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoActualizado);

        Optional<Evento> eventoExistente = eventoService.obtenerEventoPorId(1L);
        assertTrue(eventoExistente.isPresent());
        eventoExistente.get().setNombre(eventoActualizado.getNombre());
        eventoExistente.get().setDescripcion(eventoActualizado.getDescripcion());
        eventoExistente.get().setFecha(eventoActualizado.getFecha());
        eventoExistente.get().setUbicacion(eventoActualizado.getUbicacion());

        Evento resultado = eventoService.guardarEvento(eventoExistente.get());

        assertNotNull(resultado);
        assertEquals("Festival", resultado.getNombre());
        verify(eventoRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    void eliminarEvento_CuandoExiste_DebeEliminarEvento() {
        when(eventoRepository.existsById(1L)).thenReturn(true);

        eventoService.eliminarEvento(1L);

        verify(eventoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarEvento_CuandoNoExiste_DebeLanzarExcepcion() {
        when(eventoRepository.existsById(99L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventoService.eliminarEvento(99L));

        assertEquals("El evento con ID 99 no existe", exception.getMessage());
        verify(eventoRepository, never()).deleteById(99L);
    }
}
