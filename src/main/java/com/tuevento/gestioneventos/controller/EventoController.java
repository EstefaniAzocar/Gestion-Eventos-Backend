package com.tuevento.gestioneventos.controller;

import com.tuevento.gestioneventos.model.Evento;
import com.tuevento.gestioneventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "http://localhost:5173") // Permite llamadas desde el frontend
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public List<Evento> obtenerEventos() {
        return eventoService.obtenerTodosLosEventos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEventoPorId(@PathVariable Long id) {
        Optional<Evento> evento = eventoService.obtenerEventoPorId(id);
        return evento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Evento> crearEvento(@Valid @RequestBody Evento evento) {
        Evento nuevoEvento = eventoService.guardarEvento(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEvento); // Devuelve 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(@PathVariable Long id, @Valid @RequestBody Evento eventoDetalles) {
        return eventoService.obtenerEventoPorId(id)
                .map(evento -> {
                    evento.setNombre(eventoDetalles.getNombre());
                    evento.setDescripcion(eventoDetalles.getDescripcion());
                    evento.setFecha(eventoDetalles.getFecha());
                    evento.setUbicacion(eventoDetalles.getUbicacion());
                    Evento eventoActualizado = eventoService.guardarEvento(evento);
                    return ResponseEntity.ok(eventoActualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }
}