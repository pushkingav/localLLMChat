package com.apushkin.ai.localaicorechat.repository;

import com.apushkin.ai.localaicorechat.model.LoadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<LoadedDocument, Long> {
    boolean existsByFileNameAndContentHash(String fileName, String contentHash);
}
