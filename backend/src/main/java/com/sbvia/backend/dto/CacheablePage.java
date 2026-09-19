package com.sbvia.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Página compatible con la serialización JSON de Redis.
 *
 * PageImpl no ofrece un constructor Jackson y falla al recuperar una entrada
 * cacheada. Esta clase conserva el mismo contrato Page y aporta el constructor
 * explícito requerido para reconstruir la paginación.
 *
 * @author Keitho_
 * @param <T> the type of elements in the cached page
 */
@JsonIgnoreProperties(
        value = {"pageable", "sort", "first", "last", "empty", "totalPages", "numberOfElements"},
        ignoreUnknown = true)
/**
 * Redis JSON-compatible page implementation that preserves the Spring Data
 * {@link Page} contract for cached entries.
 *
 * @param <T> the type of elements in the cached page
 */
public class CacheablePage<T> extends PageImpl<T> {

    /**
     * Reconstructs a page from a Redis cache entry through the explicit
     * Jackson constructor, which {@link PageImpl} does not provide.
     *
     * @param content the list of elements contained in this page
     * @param number the zero-based page number
     * @param size the page size
     * @param totalElements the total number of elements across all pages
     */
    @JsonCreator
    public CacheablePage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements) {
        super(content, PageRequest.of(number, Math.max(size, 1)), totalElements);
    }

    /**
     * Creates a cacheable page from an existing Spring Data {@link Page},
     * copying its content, pagination metadata, and total element count.
     *
     * @param page the source {@link Page} whose data is wrapped by this cacheable page
     */
    public CacheablePage(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }
}
