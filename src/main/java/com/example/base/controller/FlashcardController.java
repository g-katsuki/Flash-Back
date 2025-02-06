package com.example.base.controller;

import javax.annotation.PostConstruct;
import com.example.base.entity.Flashcard;
import com.example.base.service.FlashcardService;
import com.example.base.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "https://next-flash-cards.netlify.app"
    },
    allowCredentials = "true"
)
public class FlashcardController {
    
    @Autowired
    private FlashcardService flashcardService;
    
    @GetMapping
    public ResponseEntity<List<Flashcard>> getAllFlashcards() {
        System.out.println("=== GET ALL FLASHCARDS REQUEST START ===");
        return ResponseEntity.ok(flashcardService.getAllFlashcards());
    }
    
    @PostMapping
    public ResponseEntity<Flashcard> createFlashcard(@RequestBody Flashcard flashcard) {
        return ResponseEntity.ok(flashcardService.createFlashcard(flashcard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable(value = "id", required = true) Long id) {
        try {
            System.out.println("=== DELETE REQUEST START ===");
            System.out.println("Path: /api/flashcards/" + id);
            System.out.println("Method: DELETE");
            
            flashcardService.deleteFlashcard(id);
            
            System.out.println("=== DELETE REQUEST SUCCESS ===");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            System.err.println("=== DELETE REQUEST ERROR ===");
            System.err.println("Type: ResourceNotFoundException");
            System.err.println("Message: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("=== DELETE REQUEST ERROR ===");
            System.err.println("Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/folder/{folderId}/generate-sentence")
    public ResponseEntity<String> generateSentence(@PathVariable String folderId) {
        try {
            String sentence = flashcardService.generateSentenceFromCards(folderId);
            return ResponseEntity.ok(sentence);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating sentence: " + e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("FlashcardController initialized with mappings:");
        System.out.println("DELETE mapping: /api/flashcards/{id}");
    }
} 