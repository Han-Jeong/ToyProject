
package com.example.toyproject.dto;

import com.example.toyproject.domain.Category;
import com.example.toyproject.domain.Member;
import com.example.toyproject.domain.Board;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {
    private Long id;
    private Member member;
    private String title;
    private String content;
    private int rate;
    private LocalDateTime createdDate;
    private String category;
    private String creator; // Book: author, Movie: director
    private String imageURL;

    public Board toEntity() {
        //dto에 가져온 value를 enum형식으로 변경
        Category categoryByDto = null;
        if (category.equals("BOOK")) {
            categoryByDto = Category.BOOK;
        } else if (category.equals("MOVIE")) {
            categoryByDto = Category.MOVIE;
        }
        Board board = Board.builder()
                .id(id)
                .member(member)
                .title(title)
                .content(content)
                .rate(rate)
                .createdDate(createdDate)
                .madeBy(creator)
                .imageURL(imageURL)
                .category(categoryByDto)
                .build();
        return board;
    }

    @Builder
    public BoardDto(Long id, Member member, String title, String content,
                    Integer rate, LocalDateTime createdDate, String creator, String imageURL, String category) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.rate = rate;
        this.creator = creator;
        this.imageURL = imageURL;
        this.category = category;
    }

}





