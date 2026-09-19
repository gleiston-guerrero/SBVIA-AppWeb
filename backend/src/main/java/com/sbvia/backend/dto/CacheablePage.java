package com.sbvia.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Redis JSON-compatible page implementation that preserves the Spring Data
 * {@link Page} contract for cached entries. {@link PageImpl} provides no Jackson
 * constructor, so this class supplies the explicit constructor required to
 * rebuild pagination from a cached entry.
 *
 * @author Keitho_
 * @param <T> the type of elements in the cached page
 */
@JsonIgnoreProperties(
        value = {"pageable", "sort", "first", "last", "empty", "totalPages", "numberOfElements"},
        ignoreUnknown = true)
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
