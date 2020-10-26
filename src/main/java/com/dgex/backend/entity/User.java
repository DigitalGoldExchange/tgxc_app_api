package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

//회원
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer userId;

    //회원이름
    @Column
    private String name;

    //이메일(로그인아이디)
    @Column
    private String emailId;

    //비밀번호
    @Column
    private String password;

    //연락처
    @Column
    private String phoneNumber;

    //주소 ex) 경기도 김포시
    @Column
    private String address;

    //주소 ex) 상세주소
    @Column
    private String addressDetail;

    //주소 우편번호
    @Column
    private String zipCode;

    //생년월일
    @Column
    private String birthDay;

    //한국인여부
    @Column
    private String koreanYn;

    //권한
    @Column
    private String level;

    //메뉴권한
    @Column
    private String menuLevel;

    //상태(0:미인증 1:비활성화 2:활성화 3:탈퇴)
    @Column
    private Integer status;

    //인증키
    @Column
    private String signKey;

    //디바이스 토큰
    @Column
    private String deviceToken;

    //디바이스 종류
    @Column
    private String deviceType;

    @Column
    private Double totalTg;

    //등록일
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDatetime;

    //삭제일
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteDatetime;

    //수정일
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;


}
