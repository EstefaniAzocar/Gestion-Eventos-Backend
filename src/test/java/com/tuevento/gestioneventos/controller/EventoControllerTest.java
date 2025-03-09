package com.tuevento.gestioneventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuevento.gestioneventos.model.Evento;
import com.tuevento.gestioneventos.service.EventoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventoController.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoService eventoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void obtenerEventos_debeRetornarListaDeEventos() throws Exception {
        Evento evento1 = new Evento("Concierto Rock", "Musica en vivo", LocalDate.now(), "Madrid");
        Evento evento2 = new Evento("Feria de Tecnología", "Exposición de gadgets", LocalDate.now().plusDays(10), "Barcelona");

        Mockito.when(eventoService.obtenerTodosLosEventos()).thenReturn(Arrays.asList(evento1, evento2));

        mockMvc.perform(MockMvcRequestBuilders.get("/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void obtenerEventoPorId_debeRetornarEventoSiExiste() throws Exception {
        Evento evento = new Evento("Torneo de Ajedrez", "Competencia nacional", LocalDate.now().plusDays(5), "Sevilla");

        Mockito.when(eventoService.obtenerEventoPorId(1L)).thenReturn(Optional.of(evento));

        mockMvc.perform(MockMvcRequestBuilders.get("/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Torneo de Ajedrez"));
    }

    @Test
    void obtenerEventoPorId_debeRetornar404SiNoExiste() throws Exception {
        Mockito.when(eventoService.obtenerEventoPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/eventos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearEvento_debeRetornar201YEventoCreado() throws Exception {
        Evento evento = new Evento("Hackathon", "Competencia de programación", LocalDate.now().plusDays(15), "Valencia");

        Mockito.when(eventoService.guardarEvento(Mockito.any(Evento.class))).thenReturn(evento);

        mockMvc.perform(MockMvcRequestBuilders.post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Hackathon"));
    }

    @Test
    void actualizarEvento_debeRetornarEventoActualizadoSiExiste() throws Exception {
        Evento eventoExistente = new Evento("Festival de Cine", "Proyección de películas", LocalDate.now().plusDays(20), "Madrid");
        Evento eventoActualizado = new Evento("Festival de Cine 2025", "Proyección de películas actualizada", LocalDate.now().plusDays(30), "Madrid");

        Mockito.when(eventoService.obtenerEventoPorId(1L)).thenReturn(Optional.of(eventoExistente));
        Mockito.when(eventoService.guardarEvento(Mockito.any(Evento.class))).thenReturn(eventoActualizado);

        mockMvc.perform(MockMvcRequestBuilders.put("/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Festival de Cine 2025"));
    }

    @Test
    void actualizarEvento_debeRetornar404SiNoExiste() throws Exception {
        Evento eventoActualizado = new Evento("Evento Fantasma", "No existe", LocalDate.now().plusDays(30), "N/A");

        Mockito.when(eventoService.obtenerEventoPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/eventos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoActualizado)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarEvento_debeRetornar204SiExiste() throws Exception {
        Mockito.doNothing().when(eventoService).eliminarEvento(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/eventos/1"))
                .andExpect(status().isNoContent());
    }
}