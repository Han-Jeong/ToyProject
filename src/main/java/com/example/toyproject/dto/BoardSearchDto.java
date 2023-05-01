package com.example.toyproject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BoardSearchDto {
    private String title;
    private String creator;
    private String imageURL;

    @Builder
    public BoardSearchDto(String title, String creator, String imageURL) {
        this.title = title;
        this.creator = creator;
        this.imageURL = imageURL;
    }
}
