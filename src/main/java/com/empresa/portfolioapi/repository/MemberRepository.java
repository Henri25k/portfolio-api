package com.empresa.portfolioapi.repository;

import com.empresa.portfolioapi.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}