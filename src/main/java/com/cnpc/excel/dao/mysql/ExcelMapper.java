package com.cnpc.excel.dao.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cnpc.excel.dao.entity.ExcelEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @packageName： com.cnpc.excel.dao
 * @className: ExcelMapper
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-09 09:53
 */
@Mapper
public interface ExcelMapper extends BaseMapper<ExcelEntity> {

    @Insert("<script>" +
            "INSERT INTO pro_op_daily(pid,indicators,plan_day,plan_month,com_day,com_month,more_day,more_month,remark,create_time,update_time,import_date)" +
            "VALUES" +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "(#{item.pid},#{item.indicators},#{item.planDay},#{item.planMonth},#{item.comDay},#{item.comMonth},#{item.moreDay},#{item.moreMonth},#{item.remark},#{item.createTime},#{item.updateTime},#{item.importDate})" +
            "</foreach>" +
            "</script>")
    public int saveExcel(List<ExcelEntity> list);

    @Delete("<script>DELETE FROM pro_op_daily WHERE import_date=#{date}</script>")
    public int delExcel(String date);

    //public List<ExcelEntity> getData(String date);

}
