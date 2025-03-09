package com.tuevento.gestioneventos.service;

import com.tuevento.gestioneventos.model.Evento;
import com.tuevento.gestioneventos.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> obtenerTodosLosEventos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void eliminarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new IllegalArgumentException("El evento con ID " + id + " no existe");
        }
        eventoRepository.deleteById(id);
    }
}
