package com.example.base.controller;

import com.example.base.entity.Flashcard;
import com.example.base.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
@CrossOrigin(origins = "*")
public class FlashcardController {
    
    @Autowired
    private FlashcardService flashcardService;
    
    @GetMapping
    public ResponseEntity<List<Flashcard>> getAllFlashcards() {
        return ResponseEntity.ok(flashcardService.getAllFlashcards());
    }
    
    @PostMapping
    public ResponseEntity<Flashcard> createFlashcard(@RequestBody Flashcard flashcard) {
        return ResponseEntity.ok(flashcardService.createFlashcard(flashcard));
    }
} 