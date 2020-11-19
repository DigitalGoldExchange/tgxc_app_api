package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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

    //상태(0:미인증 1:비활성화 2:활성화 3:탈퇴 4:KYC확인)
    @Column
    private Integer status;

    //인증키
    @Column
    private String signKey;

    //인증키
    @Column
    private String signYn;

    //인증키
    @Column
    private String otpKey;

    //인증키
    @Column
    private String identifyNumber;

    //디바이스 토큰
    @Column
    private String deviceToken;

    //디바이스 종류
    @Column
    private String deviceType;

    //푸시(A:모두, M:중요, D:거부
    @Column
    private String pushType;

    @Column
    private String totalTg;

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


//    @JsonIgnore
//    @ApiModelProperty(hidden = true)
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> authorities = new HashSet<>(1);
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//
//        return authorities;
//    }
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Override
//    public String getPassword() {
//        return this.password;
//    }
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Override
//    public String getUsername() {
//        return this.name;
//    }
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}
