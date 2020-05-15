package com.multiple.data.sources.domain.sys;

import lombok.Data;

import java.util.Date;

/**
 * @author admin
 */
@Data
public class SysUser {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人姓名
     */
    private String createName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人姓名
     */
    private String updateName;

    /**
     * 删除状态 0 正常 1删除
     */
    private Integer delState;

    /**
     * 备注
     */
    private String remarks;
}
