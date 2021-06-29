package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

//교환
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "country_post_info")
public class CountryPostInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer countryId;

    //국가코드
    @Column
    private String countryCode;

    //국가명
    @Column
    private String countryName;

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
