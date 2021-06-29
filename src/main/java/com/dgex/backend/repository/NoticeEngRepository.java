package com.dgex.backend.repository;

import com.dgex.backend.entity.NoticeEng;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeEngRepository extends JpaRepository<NoticeEng, Integer> {
    Page<NoticeEng> findByDeleteDatetimeIsNull(Pageable pageable);
}
