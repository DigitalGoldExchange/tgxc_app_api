package com.dgex.backend.repository;

import com.dgex.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByDeleteDatetimeIsNullAndEmailId(String emailId);

    User findByDeleteDatetimeIsNullAndPhoneNumber(String phoneNumber);

    User findByDeleteDatetimeIsNullAndEmailIdAndPhoneNumber(String emailId, String phoneNumber);

    User findByDeleteDatetimeIsNullAndEmailIdAndLevelIsNot(String emailId, String level);

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

    @Query("select us FROM User us where us.level = 'USER' and us.emailId like :searchWord")
    Page<User> findByEmailId(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select us FROM User us where us.level = 'USER' and us.name like :searchWord")
    Page<User> findByName(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select us FROM User us where us.level = 'USER' and us.phoneNumber like :searchWord")
    Page<User> findByPhoneNumber(@Param("searchWord") String searchWord, Pageable pageable);

    Page<User> findByLevel(String level, Pageable pageable);

    User findByDeleteDatetimeIsNullAndNameAndEmailId(String name, String emailId);

    User findByDeleteDatetimeIsNullAndIdentifyNumber(String identifyNumber);

    List<User> findByDeleteDatetimeIsNullAndKoreanYnIsAndPushTypeIs(String type, String pushType);
}
