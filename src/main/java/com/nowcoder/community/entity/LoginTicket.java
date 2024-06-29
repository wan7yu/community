package com.nowcoder.community.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginTicket {
    private Integer id;
    private Integer userId;
    private String ticket;
    private Integer status;
    private Date expired;
}
