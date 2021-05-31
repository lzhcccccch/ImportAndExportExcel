package com.cnpc.excel.service.impl;

import com.cnpc.excel.dao.ExcelImportDao;
import com.cnpc.excel.dao.entity.ExcelEntity;
import com.cnpc.excel.service.IExcelImport;
import com.cnpc.excel.util.DateUtils;
import com.cnpc.excel.util.DownloadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @packageName： com.cnpc.excel.application
 * @className: ExcelImportImpl
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-09 09:59
 */
@Service
@Slf4j
public class ExcelImportImpl implements IExcelImport {

    /**
     *  对象数(行数)
     */
    public static final int LIST_SIZE = 35;

    /**
     *  字段数
     */
    public static final int FIELD_NUM = 9;

    public static final String SHEET_TITLE = "每日运行报告";
    public static final String[] TABLE_HEADER_1 = {"序号", "指标", "计划", "完成", "超欠", "备注"};
    public static final String[] TABLE_HEADER_2 = {"一、生产指标", "月度", "日均", "当日", "月累计", "日均", "月度"};

    @Autowired
    private ExcelImportDao dao;

    @Override
    public Map importExcel(MultipartFile file) {

        Map<Integer, String> map = new HashMap<>();

        if (file==null) {
            map.put(502, "文件为空,请正确上传文件.");
            return map;
        }
        Workbook wb = null;
        boolean flag = true; // 文件是否存在异常标记变量
        try {
            if (file.getOriginalFilename().endsWith(".xlsx")) {
                wb = new XSSFWorkbook(file.getInputStream());
            } else if (file.getOriginalFilename().endsWith("xls")) {
                wb = WorkbookFactory.create(file.getInputStream());
            }
        } catch (IOException e) {
            flag = false;
            log.info("文件读取异常.");
            e.printStackTrace();
        } finally {
            if (flag==false) {
                map.put(502, "文件读取异常.");
                return map;
            }
        }
        List<ExcelEntity> list = new ArrayList<>(LIST_SIZE);
        Sheet sheet = wb.getSheetAt(0);
        Date date = new Date();
        String nowDate = DateUtils.getNowDate(date);
        for (Row row : sheet) {
            if (row.getRowNum()>=4 && row.getRowNum()<=34) { // 读取第 5-35 行的数据
                Object[] obj = new Object[FIELD_NUM];
                for (int i = 0; i < FIELD_NUM; i++) {
                    obj[i] = getValue(row.getCell(i));
                }
                if (hasValue(obj)) {
                    ExcelEntity entity = new ExcelEntity(obj);
                    entity.setCreateTime(date);
                    entity.setUpdateTime(date);
                    entity.setImportDate(nowDate);
                    list.add(entity);
                }
            }
        }
        int result = dao.saveExcel(list);
        String res = "成功插入数据" + result + "条";
        map.put(200, res);
        return map;
    }

    @Override
    public Map exportExcel(String date, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();

        // 1.创建 Excel 对象 [XSSFWorkbook/SXSSFWorkbook(2007 之后的版本) HSSFWorkbook(2007 之前的版本,65535行数据)]
        // XSSFWorkbook 突破 65535 行数据限制但是会引起 OOM 异常, SXSSFWorkbook 可避免 OOM 异常 --- 二者均是 1048576 行数据
        XSSFWorkbook wb = new XSSFWorkbook();
        //SXSSFWorkbook wb = new SXSSFWorkbook(100); // 在内存中保存 100 行数据
        // 2.创建 sheet 对象
        XSSFSheet sheet = wb.createSheet(SHEET_TITLE);
        //SXSSFSheet sheet = wb.createSheet(SHEET_TITLE);
        // 3.定义一些可复用对象
        int rowIndex = 0; // 行索引
        int cellIndex = 0; // 单元格索引
        Row row = null;
        Cell cell = null;
        CellStyle commonStyle = wb.createCellStyle(); // 通用单元格样式
        commonStyle.setAlignment(HorizontalAlignment.CENTER); //横向居中
        commonStyle.setVerticalAlignment(VerticalAlignment.CENTER); //纵向居中
        // 4.设置列宽 (列索引 列宽*256)
        for (int i = 0; i < FIELD_NUM; i++) {
            sheet.setColumnWidth(i, 15*256);
        }
        // 5.创建大标题行
        row = sheet.createRow(rowIndex++); // 使用的是 0 使用完自增
        // 设置大标题行高
        row.setHeightInPoints(24f);
        // 6.创建大标题行单元格
        cell = row.createCell(cellIndex);
        // 7.合并大标题行单元格 [参数为要合并的区域的(第一行, 最后一行, 第一列, 最后一列)]
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,8));
        // 8.设置大标题行内容
        cell.setCellValue(SHEET_TITLE);
        // 设置大标题行样式
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short)18);
        //font.setBold(true); //加粗
        headerStyle.setFont(font);
        cell.setCellStyle(headerStyle);

        // 9.创建并设置小标题行(该表格有多个小标题行)
        // 第二行
        row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(17.4f);
        cell = row.createCell(cellIndex);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
        cell.setCellValue("单位：万吨、亿方");
        cell.setCellStyle(commonStyle);
        // 第三行
        row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(17f);
        for (int i = 0, j = 0; i < FIELD_NUM; i++, j++) {
            cell = row.createCell(cellIndex++);
            cell.setCellValue(TABLE_HEADER_1[j]);
            if (i==2 || i==4 || i==6) {
                sheet.addMergedRegion(new CellRangeAddress(2, 2, i, i+1));
                i++;
            }
            cell.setCellStyle(commonStyle);
        }
        cellIndex = 0; // 目前值为 8,置为 0
        // 第四行
        row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(17f);
        for (int i = 0, j = 0; i < FIELD_NUM; i++, j++) {
            cell = row.createCell(cellIndex++);
            if (j<TABLE_HEADER_2.length) {
                cell.setCellValue(TABLE_HEADER_2[j]);
            }
            if (i==0) {
                sheet.addMergedRegion(new CellRangeAddress(3, 3, i, i+1));
                i++;
            }
            cell.setCellStyle(commonStyle);
        }
        cellIndex = 0; // 目前值为 8,置为 0
        // 10.获取填充表格数据
        List<ExcelEntity> list = getData(date);
        // 11.遍历数据并将数据填充进表格
        if (list!=null && list.size()>0) {
            for (ExcelEntity item : list) {
                row = sheet.createRow(rowIndex++);
                row.setHeightInPoints(17f);
                // 给单元格赋值
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getPid());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getIndicators());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getPlanDay());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getPlanMonth());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getComDay());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getComMonth());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getMoreDay());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getMoreMonth());
                cell.setCellStyle(commonStyle);
                cell = row.createCell(cellIndex++);
                cell.setCellValue(item.getRemark());
                cell.setCellStyle(commonStyle);
                cellIndex = 0; // 目前值为 8,置为 0
            }
            //最后 下载文件 字节数组的输出流 它可存可取 带缓冲区
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                wb.write(bos);
                new DownloadUtils().download(bos, response, SHEET_TITLE+".xlsx");
                bos.close();
                wb.close();
                result.put("message", "文件正常,流操作已关闭");
            } catch (IOException e) {
                result.put("message", "文件输出异常");
                e.printStackTrace();
            }
            return result;
        } else {
            result.put("message", "暂无当前日期的数据,请检查输入日期是否有效");
        }
        return null;
    }

    /**
     * 获取单元格内的数据,并进行格式转换
     * @param cell
     * @return
     */
    private Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:// 数值和日期均是此类型,需进一步判断
                if (DateUtil.isCellDateFormatted(cell)) {
                    //是日期类型
                    return cell.getDateCellValue();
                } else {
                    //是数值类型
                    return cell.getNumericCellValue();
                }
            default:
                return null;
        }
    }

    /**
     *  判断某行的值是否有效, 如果填充数据全为空则返回 false
     * @param obj
     * @return
     */
    public boolean hasValue(Object[] obj) {
        boolean flag = false;
        if (obj!=null) {
            for (int i = 2; i < obj.length; i++) {
                if (obj[i]!=null) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     *  获取数据库中的数据, 导出 Excel 数据
     * @param date 当前日期
     * @return
     */
    public List<ExcelEntity> getData(String date) {
        if (!StringUtils.isEmpty(date)) {
            List<ExcelEntity> list = dao.getData(date);
            return list;
        }
        return null;
    }

}
