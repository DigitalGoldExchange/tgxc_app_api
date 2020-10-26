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
@Table(name = "push_info")
public class PushInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer pushInfoId;

    //회원
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    //제목
    @Column
    @ApiModelProperty(value = "제목", required = true)
    private String title;

    // 내용
    @Column
    @ApiModelProperty(value = "내용", required = true)
    private String contents;

    //전송여부
    @Column
    @ApiModelProperty(value = "읽음 여부", required = true)
    private String readYn;

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



}
