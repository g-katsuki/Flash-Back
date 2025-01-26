package com.example.base.service;

import com.example.base.entity.Flashcard;
import com.example.base.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashcardService {
    
    @Autowired
    private FlashcardRepository flashcardRepository;
    
    public List<Flashcard> getAllFlashcards() {
        return flashcardRepository.findAll();
    }
    
    public Flashcard createFlashcard(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }
} 