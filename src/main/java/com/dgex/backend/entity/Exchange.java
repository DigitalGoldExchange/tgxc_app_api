package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

//교환
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exchange")
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer exchangeId;

    //회원
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    //회원
    @ManyToOne
    @JoinColumn(name = "exchangeStoreId")
    private ExchangeStore exchangeStore;

    //신청번호
    @Column
    private String reqNumber;

    //신청수량
    @Column
    private String amount;

    //진행상황
    @Column
    private String status;

    //반려사유
    @Column
    private String note;

    //거래타입
    @Column
    private String tradeType;

    //txId
    @Column
    private String txId;

    //txId time
    @Column
    private String txIdDatetime;

    //walletAddr
    @Column
    private String walletAddr;

    @Column
    private String exchangeMethod;

    @Column
    private String reqQty;

    @Column
    private String reqType;


    //등록일
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDatetime;

    //수정일
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;

    //삭제일
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteDatetime;




}
