package com.example.base.service;

import com.example.base.entity.Flashcard;
import com.example.base.repository.FlashcardRepository;
import com.example.base.exception.ResourceNotFoundException;
import com.example.base.client.OpenRouterClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardService {
    
    @Autowired
    private FlashcardRepository flashcardRepository;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    public List<Flashcard> getAllFlashcards() {
        return flashcardRepository.findAll();
    }
    
    public Flashcard createFlashcard(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }

    public void deleteFlashcard(Long id) {
        System.out.println("Attempting to find flashcard with ID: " + id);  // 検索開始ログ
        Flashcard flashcard = flashcardRepository.findById(id)
            .orElseThrow(() -> {
                System.err.println("No flashcard found with ID: " + id);  // 見つからない場合のログ
                return new ResourceNotFoundException("Flashcard not found with id: " + id);
            });
        System.out.println("Found flashcard: " + flashcard);  // 見つかった場合のログ
        flashcardRepository.delete(flashcard);
        System.out.println("Deleted flashcard with ID: " + id);  // 削除完了ログ
    }

    public String generateSentenceFromCards(String folderId) {
        List<Flashcard> cards = flashcardRepository.findByFolderId(folderId);
        if (cards.isEmpty()) {
            throw new ResourceNotFoundException("No cards found for folder: " + folderId);
        }
        
        // カードの裏面の文字列を収集
        List<String> backContents = cards.stream()
            .map(Flashcard::getBackContent)
            .collect(Collectors.toList());
        
        // プロンプトを作成
        String prompt = "これらをすべて含む英文を作成して。" + 
            String.join(", ", backContents);
        
        // AIに問い合わせ
        return openRouterClient.generateText(prompt);
    }
} 