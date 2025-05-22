package com.kh.dtos;

import java.util.List;

public class PaginatedResponseDTO<T> {
    private List<T> results;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    // constructors, getters, setters

    public PaginatedResponseDTO(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        this.results = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
