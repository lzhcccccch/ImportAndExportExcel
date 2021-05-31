package com.cnpc.excel.controller;

import com.cnpc.excel.service.impl.ExcelImportImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @packageName： com.cnpc.excel.controller
 * @className: ExcelImportController
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-08 16:10
 */
@RestController
@RequestMapping("/excel")
public class ExcelImportController {

    @Autowired
    private ExcelImportImpl service;

    @PostMapping("/import")
    public Map importExcel(@RequestParam("file") MultipartFile file) {
        Map<Integer, String> map = new HashMap<>();
        if (file!=null) {
            if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith("xls")) {
                map = service.importExcel(file);
            } else {
                map.put(502, "不支持该文件格式.");
            }
        } else {
            map.put(502, "文件为空,请正确上传文件.");
        }
        return map;
    }

    @GetMapping("/export")
    public Map exportExcel(String date, HttpServletResponse response) {
        return service.exportExcel(date, response);
    }

}
