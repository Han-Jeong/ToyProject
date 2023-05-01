package com.example.toyproject.repository;

import com.example.toyproject.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByEmailAndPassword(String email, String password);

    Optional<Member> findByEmail(String email);
 }
