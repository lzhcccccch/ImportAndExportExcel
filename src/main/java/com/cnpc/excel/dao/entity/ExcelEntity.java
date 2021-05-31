package com.cnpc.excel.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @packageName： com.cnpc.excel.dao.entity
 * @className: ExcelEntity
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-09 09:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("pro_op_daily")
public class ExcelEntity {

    @TableField("id")
    private Long id;

    @TableField("pid")
    private Integer pid;

    @TableField("indicators")
    private String indicators;

    @TableField("plan_day")
    private String planDay;

    @TableField("plan_month")
    private String planMonth;

    @TableField("com_day")
    private String comDay;

    @TableField("com_month")
    private String comMonth;

    @TableField("more_day")
    private String moreDay;

    @TableField("more_month")
    private String moreMonth;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("import_date")
    private String importDate;

    public ExcelEntity(Object[] objects) {
        if (objects[0]!=null) {
            String pid = objects[0].toString();
            int index = pid.indexOf('.');
            this.pid = Integer.parseInt(pid.substring(0, index));
        } else {
            this.pid = null;
        }
        this.indicators = (String) objects[1];
        if (objects[2]==null) {
            this.planDay = null;
        } else {
            this.planDay = objects[2].toString();
        }
        if (objects[3]==null) {
            this.planMonth = null;
        } else {
            this.planMonth = objects[3].toString();
        }
        if (objects[4]==null) {
            this.comDay = null;
        } else {
            this.comDay = objects[4].toString();
        }
        if (objects[5]==null) {
            this.comMonth = null;
        } else {
            this.comMonth = objects[5].toString();
        }
        if (objects[6]==null) {
            this.moreDay = null;
        } else {
            this.moreDay = objects[6].toString();
        }
        if (objects[7]==null) {
            this.moreMonth = null;
        } else {
            this.moreMonth = objects[7].toString();
        }
        if (objects[8]==null) {
            this.remark = null;
        } else {
            this.remark = objects[8].toString();
        }
    }

}
