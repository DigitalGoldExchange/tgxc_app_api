package com.dgex.backend.repository;

import com.dgex.backend.entity.NoticeKr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeKrRepository extends JpaRepository<NoticeKr, Integer> {
    Page<NoticeKr> findByDeleteDatetimeIsNull(Pageable pageable);
}
