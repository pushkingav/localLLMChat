package com.apushkin.ai.localaicorechat.service;

import com.apushkin.ai.localaicorechat.model.LoadedDocument;
import com.apushkin.ai.localaicorechat.repository.DocumentRepository;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class DocumentLoaderService implements CommandLineRunner {
    private final DocumentRepository documentRepository;
    private final ResourcePatternResolver resolver;
    private final VectorStore vectorStore;

    public DocumentLoaderService(DocumentRepository documentRepository, ResourcePatternResolver resolver,
            VectorStore vectorStore) {
        this.documentRepository = documentRepository;
        this.resolver = resolver;
        this.vectorStore = vectorStore;
    }

    @SneakyThrows
    public void loadDocuments() {
        List<Resource> resources = Arrays.stream(resolver.getResources("classpath:/knowledgebase/**/*.txt")).toList();
        resources.stream()
                .map(resource -> Pair.of(resource, calcContentHash(resource)))
                .filter(pair -> !documentRepository
                    .existsByFilenameAndContentHash(pair.getFirst().getFilename(), pair.getSecond()))
                .forEach(pair -> {
                    List<Document> documents = new TextReader(pair.getFirst()).get();
                    TokenTextSplitter textSplitter = TokenTextSplitter.builder().withChunkSize(500).build();
                    List<Document> chunks = textSplitter.apply(documents);
                    vectorStore.accept(chunks);

                    LoadedDocument loadedDocument = LoadedDocument.builder()
                            .documentType("txt")
                            .chunkCount(chunks.size())
                            .filename(pair.getFirst().getFilename())
                            .contentHash(pair.getSecond())
                            .build();

                    documentRepository.save(loadedDocument);
                });
    }

    @SneakyThrows
    private String calcContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }

    @Override
    public void run(String... args) throws Exception {
        loadDocuments();
    }
}
