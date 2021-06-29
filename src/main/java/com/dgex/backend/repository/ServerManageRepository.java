package com.dgex.backend.repository;

import com.dgex.backend.entity.ServerManage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerManageRepository extends JpaRepository<ServerManage, Integer> {

    ServerManage findByDeleteDatetimeIsNull();
}
