package com.dgex.backend.repository;

import com.dgex.backend.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    @Query("select nt FROM Notice nt where nt.title like :searchWord and nt.type =:type and nt.deleteDatetime is null")
    Page<Notice> findByTitleAndTypeAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, String type, Pageable pageable);
}
