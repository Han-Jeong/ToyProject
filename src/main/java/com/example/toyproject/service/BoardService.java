
package com.example.toyproject.service;

import com.example.toyproject.dto.BoardDto;
import com.example.toyproject.dto.MemberInfoDto;

import java.util.List;

public interface BoardService {
    List<BoardDto> getBoardList(MemberInfoDto memberInfoDto);
    Long postBoard(BoardDto boardDto, MemberInfoDto memberInfoDto);
    BoardDto getBoard(Long id);
    void updateBoard(BoardDto boardDto, Long boardId);
}
