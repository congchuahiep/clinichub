package com.kh.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class PaginatedResult<T> {
    private List<T> results;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    // constructors, getters, setters

    public PaginatedResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.results = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil(totalElements / (double) pageSize);
    }

    public PaginatedResult(List<T> content, int pageNumber, int pageSize) {
        this.results = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
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

    public Stream<T> stream() {
        return results.stream();
    }

    /**
     * Phương thức này dùng để đổi một PaginatedResult chứa kiểu R thành kiểu -> T
     */
    public <R> PaginatedResult<R> mapTo(Function<T, R> constructor) {
        List<R> mappedItems = this.stream()
                .map(constructor)
                .toList();

        return new PaginatedResult<>(mappedItems, this.pageNumber, this.pageSize, this.totalElements);
    }


}
