package com.example.toyproject.domain;

import com.example.toyproject.domain.Category;
import com.example.toyproject.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 100, nullable = false)
    private String title;
    @Column(nullable = false)
    private String madeBy;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    private int rate;
    @LastModifiedDate
    private LocalDateTime createdDate;
    
    @Enumerated(EnumType.STRING)
    private Category category;

    private String imageURL;

    // 연관관계 편의 메서드
    @Transactional
    public void setMember(Member member) {
        this.member = member;
        member.getBoards().add(this);
    }

    @Builder
    public Board(Long id, Member member,String title, String content, LocalDateTime createdDate,
                 int rate, String madeBy, Category category, String imageURL) {
        this.id=id;
        //this.member=member;
        //builder를 사용할 때 아예 연관관계 메서드를 추가하도록 설정
        setMember(member);
        this.title = title;
        this.content = content;
        this.rate=rate;
        this.madeBy=madeBy;
        this.createdDate = createdDate;
        this.category = category;
        this.imageURL = imageURL;
    }

    public void updateBoard(String title, String content, int rate, String madeBy, Category category, String imageURL) {
        this.title=title;
        this.content=content;
        this.rate = rate;
        this.madeBy=madeBy;
        this.category = category;
        this.imageURL=imageURL;
    }
}
