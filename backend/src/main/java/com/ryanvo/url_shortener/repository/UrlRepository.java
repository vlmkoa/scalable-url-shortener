package com.ryanvo.url_shortener.repository;

import com.ryanvo.url_shortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    // SELECT * FROM table WHERE short_code = ...
    Optional<UrlMapping> findByShortCode(String shortCode);
}
