package com.dgex.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_exchange_image")
public class UserExchangeImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer userExchangeId;

    //회원
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    //회원
    @ManyToOne
    @JoinColumn(name = "exchangeId")
    private Exchange exchange;

    @Column
    private String profileImagePath;

    @Column
    private String identifyCardPath;

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

    @Transient
    @ApiModelProperty(value = "이미지")
    private MultipartFile profileImage;

    @Transient
    @ApiModelProperty(value = "신분증")
    private MultipartFile identifyCard;
}
