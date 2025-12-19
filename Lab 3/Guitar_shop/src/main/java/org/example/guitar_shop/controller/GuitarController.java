package org.example.guitar_shop.controller;

import org.example.guitar_shop.model.Guitar;
import org.example.guitar_shop.repository.GuitarRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guitars")
@CrossOrigin(origins = "*")
public class GuitarController {

    private final GuitarRepository repository;

    public GuitarController(GuitarRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Guitar> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Guitar add(@RequestBody Guitar guitar) {
        return repository.save(guitar);
    }
}