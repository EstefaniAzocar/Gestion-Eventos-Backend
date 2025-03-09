package com.tuevento.gestioneventos.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void eventoValido_NoDebeGenerarErrores() {
        Evento evento = new Evento("Concierto", "Un gran concierto", LocalDate.now().plusDays(5), "Madrid");

        Set<ConstraintViolation<Evento>> violations = validator.validate(evento);

        assertTrue(violations.isEmpty(), "No debería haber errores de validación");
    }

    @Test
    void eventoConNombreVacio_DebeGenerarError() {
        Evento evento = new Evento("", "Descripción válida", LocalDate.now().plusDays(5), "Madrid");

        Set<ConstraintViolation<Evento>> violations = validator.validate(evento);

        assertFalse(violations.isEmpty());
        assertEquals("El nombre no puede estar vacío", violations.iterator().next().getMessage());
    }

    @Test
    void eventoConFechaPasada_DebeGenerarError() {
        Evento evento = new Evento("Concierto", "Descripción válida", LocalDate.now().minusDays(1), "Madrid");

        Set<ConstraintViolation<Evento>> violations = validator.validate(evento);

        assertFalse(violations.isEmpty());
        assertEquals("La fecha debe ser presente o futura", violations.iterator().next().getMessage());
    }
}

