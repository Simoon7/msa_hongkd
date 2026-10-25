package com.example.msa.ordering.repository;

import com.example.msa.ordering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, Long> {
    List<Ordering> findByMemberIdOrderByCreatedTimeDesc(Long memberId);
}