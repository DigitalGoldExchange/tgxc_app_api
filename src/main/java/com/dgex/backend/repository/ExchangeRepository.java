package com.dgex.backend.repository;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ExchangeRepository extends JpaRepository<Exchange, Integer> {

    Page<Exchange> findByDeleteDatetimeIsNull(Pageable pageable);

    List<Exchange> findByDeleteDatetimeIsNullAndUser(User user);

    Exchange findByDeleteDatetimeIsNullAndTxIdAndAmount(String txId, Double amount);

    List<Exchange> findByDeleteDatetimeIsNullAndUserAndTradeType(User user, String type);


    @Query("select ex FROM Exchange ex where ex.deleteDatetime is null and ex.user.emailId like :searchWord")
    Page<Exchange> findByEmailIdAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select ex FROM Exchange ex where ex.deleteDatetime is null and ex.user.name like :searchWord")
    Page<Exchange> findByNameAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select ex FROM Exchange ex where ex.deleteDatetime is null and ex.reqNumber like :searchWord")
    Page<Exchange> findByReqNumberAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);

    @Query("select ex FROM Exchange ex where ex.deleteDatetime is null and ex.status like :searchWord")
    Page<Exchange> findByStatusAndDeleteDatetimeIsNull(@Param("searchWord") String searchWord, Pageable pageable);
}
