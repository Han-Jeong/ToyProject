//package com.example.toyproject.service;
//
//import com.example.toyproject.domain.Member;
//import com.example.toyproject.dto.BoardDto;
//import com.example.toyproject.dto.SignInDto;
//import com.example.toyproject.repository.BoardRepository;
//import com.example.toyproject.repository.MemberRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//class MemberSerivceImplV2Test {
//    @Autowired
//    MemberService memberService;
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    BoardRepository boardRepository;
//    @Autowired
//    BoardService boardService;
//
//    @Test
//    void signIn() throws Exception{
//        Member build = Member.builder()
//                .name("aaa")
//                .email("gmail.com")
//                .password("bbb").build();
//
//        memberRepository.save(build);
//
//        SignInDto signInDto = new SignInDto("gmail.com", "bbb");
//        //Long memberId = memberService.signIn(signInDto);
//
//        //assertThat(memberId).isEqualTo(memberRepository.findById(memberId).get().getId());
//
//    }
////    @Test
////    void getBoardList() throws Exception{
////        Member build = Member.builder()
////                .name("aaa")
////                .email("gmail.com")
////                .password("bbb").build();
////
////        memberRepository.save(build);
////
////        SignInDto signInDto = new SignInDto("gmail.com", "bbb");
////        Long memberId = memberService.signIn(signInDto);
////        List<BoardDto> bookList = createBookList(memberRepository.findById(memberId));
////
////        for (BoardDto boardDto : bookList) {
////            boardService.postBoard(boardDto);
////        }
////
////        List<BoardDto> boards = boardService.getBoardList(build);
////        assertThat(boards.size()).isEqualTo(bookList.size());
////
////    }
//
//    private List<BoardDto> createBookList(Optional<Member> member) {
//        List<BoardDto> list = new ArrayList<>();
//        list.add(BoardDto.builder()
//                .member(member.get())
//                .title("title1")
//                .content("content1")
//                .rate(3)
//                .createdDate(LocalDateTime.now())
//                .author("author")
//                .isbn("1234-1234-1234-1234")
//                .dtype("B").build());
//        list.add(BoardDto.builder()
//                .member(member.get())
//                .title("title2")
//                .content("content2")
//                .rate(3)
//                .createdDate(LocalDateTime.now())
//                .author("author")
//                .isbn("1234-1234-1234-1234")
//                .dtype("B").build());
//        list.add(BoardDto.builder()
//                .member(member.get())
//                .title("title3")
//                .content("content3")
//                .rate(3)
//                .createdDate(LocalDateTime.now())
//                .author("author")
//                .isbn("1234-1234-1234-1234")
//                .dtype("B").build());
//        return list;
//    }
//}