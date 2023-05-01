package com.example.toyproject.repository;

import com.example.toyproject.domain.Member;
import com.example.toyproject.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByMember(Member member);
}
