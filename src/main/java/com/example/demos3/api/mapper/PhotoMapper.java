package com.example.demos3.api.mapper;

import com.example.demos3.api.dto.PhotoOutput;
import com.example.demos3.domain.model.Photo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PhotoMapper {
    private final ModelMapper modelMapper;

    public PhotoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PhotoOutput toOutput(Photo photo) {
        return this.modelMapper.map(photo, PhotoOutput.class);
    }
}