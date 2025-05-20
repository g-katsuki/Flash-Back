package com.example.base.service;

import com.example.base.entity.Folder;
import com.example.base.repository.FolderRepository;
import com.example.base.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {
    
    @Autowired
    private FolderRepository folderRepository;
    
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }
    
    public Folder createFolder(Folder folder) {
        return folderRepository.save(folder);
    }
    
    public void deleteFolder(Long id) {
        Folder folder = folderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found with id: " + id));
        folderRepository.delete(folder);
    }
} 