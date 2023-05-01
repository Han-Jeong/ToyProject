package com.example.toyproject.service;

import com.example.toyproject.domain.Category;
import com.example.toyproject.domain.Member;
import com.example.toyproject.domain.Board;
import com.example.toyproject.dto.BoardDto;
import com.example.toyproject.dto.MemberInfoDto;
import com.example.toyproject.repository.BoardRepository;
import com.example.toyproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardServiceImplV2 implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<BoardDto> getBoardList(MemberInfoDto memberInfoDto) {
        Member memberByDto = findMemberByDto(memberInfoDto);
        List<Board> boardList = memberByDto.getBoards();        //member안의 board를 꺼내도록 수정
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (Board board : boardList) {
            BoardDto boardDto = EntityToBoardDto(board);
            boardDtoList.add(boardDto);
        }
        return boardDtoList;
    }

    @Transactional
    @Override
    public Long postBoard(BoardDto boardDto, MemberInfoDto memberInfoDto) {
        Member memberByDto = findMemberByDto(memberInfoDto);
        boardDto.setMember(memberByDto);
        Board board = boardDto.toEntity();
        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    @Override
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id).get();
        BoardDto boardDto = EntityToBoardDto(board);
        return boardDto;
    }

    @Transactional
    @Override
    public void updateBoard(BoardDto boardDto, Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        //enum형식 변환
        Category categoryByDto = null;
        if (boardDto.getCategory().equals("BOOK")) {
            categoryByDto = Category.BOOK;
        } else if (boardDto.getCategory().equals("MOVIE")) {
            categoryByDto = Category.MOVIE;
        }
        //update
        board.updateBoard(
                boardDto.getTitle(),
                boardDto.getContent(),
                boardDto.getRate(),
                boardDto.getCreator(),
                categoryByDto,
                boardDto.getImageURL()
        );
    }

    //dto를 통한 mapping 메서드
    private Member findMemberByDto(MemberInfoDto memberInfoDto) {
        List<Member> findMemberList = memberRepository
                .findByEmailAndPassword(memberInfoDto.getEmail(), memberInfoDto.getPassword());
        return findMemberList.stream().findFirst().get();
    }

    private BoardDto EntityToBoardDto(Board board) {
        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .member(board.getMember())
                .title(board.getTitle())
                .content(board.getContent())
                .createdDate(board.getCreatedDate())
                .rate(board.getRate())
                .creator(board.getMadeBy())
                .imageURL(board.getImageURL())
                .category(String.valueOf(board.getCategory()))
                .build();

        return boardDto;
    }

}


