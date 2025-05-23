package com.kh.dtos;

import com.kh.pojo.Specialty;

public class SpecialtyDTO {
    private Long id;
    private String name;

    public SpecialtyDTO() {
    }

    public SpecialtyDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SpecialtyDTO(Specialty specialty) {
        this.id = specialty.getId();
        this.name = specialty.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
