package com.cnpc.excel.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @packageName： com.cnpc.excel.application.service
 * @className: IExcelImport
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-09 10:00
 */
public interface IExcelImport {

    public Map importExcel(MultipartFile file);

    public Map exportExcel(String date, HttpServletResponse response);

}
