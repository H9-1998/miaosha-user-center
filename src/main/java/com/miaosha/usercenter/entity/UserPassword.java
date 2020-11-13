package com.miaosha.usercenter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @auhor: dhz
 * @date: 2020/11/13 14:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_password")
public class UserPassword {

    @Id
    private Integer id;

    private String encryptPassword;

    private Integer userId;
}
