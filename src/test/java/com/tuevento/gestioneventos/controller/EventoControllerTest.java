package com.tuevento.gestioneventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuevento.gestioneventos.model.Evento;
import com.tuevento.gestioneventos.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventoControllerTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @Autowired
    private EventoService eventoService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/eventos";
    }

    @Test
    void obtenerEventos_debeRetornarListaDeEventos() {
        eventoService.guardarEvento(new Evento("Concierto Rock", "Musica en vivo", LocalDate.now(), "Madrid"));
        eventoService.guardarEvento(new Evento("Feria de Tecnología", "Exposición de gadgets", LocalDate.now().plusDays(10), "Barcelona"));

        Evento[] eventos = restTemplate.getForObject(baseUrl, Evento[].class);

        assertThat(eventos).isNotNull();
        assertThat(eventos.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void obtenerEventoPorId_debeRetornarEventoSiExiste() {
        Evento evento = eventoService.guardarEvento(new Evento("Torneo de Ajedrez", "Competencia nacional", LocalDate.now().plusDays(5), "Sevilla"));

        Evento respuesta = restTemplate.getForObject(baseUrl + "/" + evento.getId(), Evento.class);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getNombre()).isEqualTo("Torneo de Ajedrez");
    }

    @Test
    void crearEvento_debeRetornar201YEventoCreado() throws Exception {
        Evento evento = new Evento("Hackathon", "Competencia de programación", LocalDate.now().plusDays(15), "Valencia");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Evento respuesta = restTemplate.postForObject(baseUrl, evento, Evento.class);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getNombre()).isEqualTo("Hackathon");
    }

    @Test
    void eliminarEvento_debeRetornar204SiExiste() {
        Evento evento = eventoService.guardarEvento(new Evento("Evento a eliminar", "Será eliminado", LocalDate.now(), "Madrid"));

        restTemplate.delete(baseUrl + "/" + evento.getId());

        assertThat(eventoService.obtenerEventoPorId(evento.getId())).isEmpty();
    }
}

