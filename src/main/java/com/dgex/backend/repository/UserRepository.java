package com.dgex.backend.repository;

import com.dgex.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByDeleteDatetimeIsNullAndEmailId(String emailId);

    User findByDeleteDatetimeIsNullAndEmailIdAndSignKey(String emailId, String signKey);

    Page<User> findByDeleteDatetimeIsNull(Pageable pageable);

    Page<User> findByDeleteDatetimeIsNullAndLevel(String level, Pageable pageable);

    User findByDeleteDatetimeIsNullAndEmailIdAndPassword(String emailId, String password);

    @Query("select us FROM User us where us.level = 'USER' and us.deleteDatetime is null and us.emailId like :searchWord")
    Page<User> findByEmailIdAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select us FROM User us where us.level = 'USER' and us.deleteDatetime is null and us.name like :searchWord")
    Page<User> findByNameAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select us FROM User us where us.level = 'USER' and us.deleteDatetime is null and us.phoneNumber like :searchWord")
    Page<User> findByPhoneNumberAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);

    User findByDeleteDatetimeIsNullAndNameAndEmailId(String name, String emailId);
}
