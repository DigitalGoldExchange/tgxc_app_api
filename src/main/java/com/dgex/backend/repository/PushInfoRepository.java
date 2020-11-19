package com.dgex.backend.repository;

import com.dgex.backend.entity.PushInfo;
import com.dgex.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushInfoRepository extends JpaRepository<PushInfo, Integer> {

    Integer countByDeleteDatetimeIsNullAndUserAndReadYn(User user, String readYn);

    List<PushInfo> findByDeleteDatetimeIsNullAndUser(User user);

    List<PushInfo> findByDeleteDatetimeIsNullAndUserOrderByCreateDatetimeDesc(User user);

}
