package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

//교환방
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exchange_method")
public class ExchangeMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer exchangeMethodId;

    @ApiModelProperty(value = "수령방법", example = "방문수령")
    private String name;

    //활성화여부
    @Column
    @ApiModelProperty(value = "활성화여부", required = true)
    private String dispYn;

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
