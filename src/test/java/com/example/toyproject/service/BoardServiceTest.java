
package com.example.toyproject.service;

import com.example.toyproject.domain.Member;
import com.example.toyproject.dto.BoardDto;
import com.example.toyproject.dto.MemberInfoDto;
import com.example.toyproject.dto.SignUpDto;
import com.example.toyproject.repository.BoardRepository;
import com.example.toyproject.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @AfterEach
    public void clearAll() {
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void postAndGetBoard() {
        // given
        SignUpDto signUpDto = createMember();
        Long memberId = memberService.signUp(signUpDto);
        Member member = memberRepository.findById(memberId).get();
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName()).build();
        BoardDto boardDto = createBook(member);



        // when
        Long postBoardId = boardService.postBoard(boardDto, memberInfoDto);
        BoardDto getBoardDto = boardService.getBoard(postBoardId);

        List<BoardDto> boardList = boardService.getBoardList(memberInfoDto);
        for (BoardDto dto : boardList) {
            System.out.println("===" +  dto.getId() + "===");
            System.out.println("title = " + dto.getTitle());
            System.out.println("author = " + dto.getCreator());
        }

        System.out.println("postBoardId = " + postBoardId);

        // then
        assertThat(getBoardDto.getId()).isEqualTo(postBoardId);
    }

    @Test
    public void updateBoard() {
        // given
        SignUpDto signUpDto = createMember();
        Long memberId = memberService.signUp(signUpDto);
        Member member = memberRepository.findById(memberId).get();
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName()).build();


        BoardDto boardDto = createBook(member);
        Long postBoardId = boardService.postBoard(boardDto, memberInfoDto);

        // when
        BoardDto getBoardDto = boardService.getBoard(postBoardId);
        getBoardDto.setTitle("update title");
        boardService.updateBoard(getBoardDto, postBoardId);

        // then
        BoardDto updateBoardDto = boardService.getBoard(getBoardDto.getId());
        assertThat(updateBoardDto.getTitle()).isEqualTo("update title");
    }

    @Test
    public void boardList() {
        // given
        SignUpDto signUpDto = createMember();
        Long memberId = memberService.signUp(signUpDto);
        Member member = memberRepository.findById(memberId).get();
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName()).build();

        List<BoardDto> bookList = createBookList(member);
        for (BoardDto boardDto : bookList) {
            boardService.postBoard(boardDto, memberInfoDto);
        }
        assertThat(boardService.getBoardList(memberInfoDto).size()).isEqualTo(3);
    }

    private BoardDto createBook(Member member) {
        return BoardDto.builder()
                .member(member)
                .title("title")
                .content("content")
                .rate(3)
                .createdDate(LocalDateTime.now())
                .creator("author")
                .imageURL("1234-1234-1234-1234")
                .category("BOOK").build();
    }

    private List<BoardDto> createBookList(Member member) {
        List<BoardDto> list = new ArrayList<>();
        list.add(BoardDto.builder()
                .member(member)
                .title("test title1")
                .content("content1")
                .rate(3)
                .createdDate(LocalDateTime.now())
                .creator("author")
                .imageURL("1234-1234-1234-1234")
                .category("BOOK").build());
        list.add(BoardDto.builder()
                .member(member)
                .title("test title2")
                .content("content2")
                .rate(3)
                .createdDate(LocalDateTime.now())
                .creator("author")
                .imageURL("1234-1234-1234-1234")
                .category("BOOK").build());
        list.add(BoardDto.builder()
                .member(member)
                .title("test title3")
                .content("content3")
                .rate(3)
                .createdDate(LocalDateTime.now())
                .creator("author")
                .imageURL("1234-1234-1234-1234")
                .category("BOOK").build());
        return list;
    }

    private SignUpDto createMember() {
        return SignUpDto.builder()
                .email("testtest@gmail.com")
                .password("12345678")
                .name("test name")
                .build();
    }
}

