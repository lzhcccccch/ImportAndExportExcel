package com.cnpc.excel.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cnpc.excel.dao.entity.ExcelEntity;
import com.cnpc.excel.dao.mysql.ExcelMapper;
import com.cnpc.excel.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @packageName： com.cnpc.excel.dao
 * @className: ExcelImportDao
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-09 10:04
 */
@Repository
public class ExcelImportDao {

    @Autowired
    private ExcelMapper mapper;

    /**
     *  保存 Excel 数据
     * @param list
     * @return
     */
    public int saveExcel(List<ExcelEntity> list) {
        if (list!=null) {
            delExcel(DateUtils.getNowDate(new Date()));
            return mapper.saveExcel(list);
        }
        return 0;
    }

    /**
     * @description: 获取当天数据
     * @param: [date]
     * @return: java.util.List<com.cnpc.excel.dao.entity.ExcelEntity>
     * @author: liuzhichao 2020-12-18 14:32
     */
    public List<ExcelEntity> getData(String date) {
        if (!StringUtils.isEmpty(date)) {
            QueryWrapper<ExcelEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("import_date", date);
            List<ExcelEntity> list = mapper.selectList(wrapper);
            return list;
        }
        return null;
    }

    public int delExcel(String date) {
        return mapper.delExcel(date);
    }

}
