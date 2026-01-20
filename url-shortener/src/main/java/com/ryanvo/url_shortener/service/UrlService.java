package com.ryanvo.url_shortener.service;

import com.ryanvo.url_shortener.model.UrlMapping;
import com.ryanvo.url_shortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final SnowflakeIdGenerator idGenerator;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
        // Node ID 1. In production, read this from environment variables.
        this.idGenerator = new SnowflakeIdGenerator(1);
    }

    public String shortenUrl(String originalUrl) {
        // 1. Generate unique ID (Pure Math, No DB)
        long id = idGenerator.nextId();

        // 2. Encode to Base62
        String shortCode = Base62Converter.encode(id);

        // 3. Persist
        UrlMapping mapping = new UrlMapping(id, originalUrl, shortCode);
        repository.save(mapping);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        // 1. Decode
        long id = Base62Converter.decode(shortCode);

        // 2. Fetch
        return repository.findById(id)
                .map(UrlMapping::getLongUrl)
                .orElseThrow(() -> new RuntimeException("URL not found for code: " + shortCode));
    }
}
