package com.dgex.backend.repository;

import com.dgex.backend.entity.UserPassportImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPassportImageRepository extends JpaRepository<UserPassportImage, Integer> {
}
