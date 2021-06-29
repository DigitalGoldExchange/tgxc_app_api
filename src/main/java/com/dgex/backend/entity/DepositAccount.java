package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deposit_account")
public class DepositAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer depositId;

    @ApiModelProperty(value = "주소", example = "0.9")
    private String account;

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
