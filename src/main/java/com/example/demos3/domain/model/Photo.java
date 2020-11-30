package com.example.demos3.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Photo {
    private String fileName;
    private String description;
    private String contentType;
    private Long size;
}
