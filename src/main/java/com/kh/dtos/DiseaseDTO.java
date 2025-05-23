package com.kh.dtos;

import com.kh.pojo.Disease;

public class DiseaseDTO {
    private Long id;

    private String name;

    private String description;

    public DiseaseDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public DiseaseDTO(Disease disease) {
        this.id = disease.getId();
        this.name = disease.getName();
        this.description = disease.getDescription();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
