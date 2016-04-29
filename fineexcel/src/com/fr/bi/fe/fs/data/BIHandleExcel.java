package com.fr.bi.fe.fs.data;

import com.fr.bi.cal.analyze.session.BIDesignSessionIDInfo;
import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.stable.ColumnRow;
import com.fr.stable.html.Tag;
import com.fr.web.core.SessionIDInfor;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;

import java.io.*;


/**
 * @author young
 *
 */
public class BIHandleExcel {
    private static int ID = 0;
    // 基于并发的考虑，减1000，不用加锁
    private static int ID_MAX = Integer.MAX_VALUE - 1000;
    public static SessionIDInfor defalutSession = new BIDesignSessionIDInfo("0", new BIWeblet(), -999);

    protected static int _GENERATE_ID() {
        if (ID >= ID_MAX) {
            ID = 0;
        }

        return ++ID;
    }


    public Tag handleWithExcel(File excelFile, HttpServletRequest req, SessionIDInfor session) throws IOException{
        if(session == null){
            session = defalutSession;
        }
        Tag t = new Tag("div");
        if(excelFile.getAbsoluteFile().getName().endsWith(".xlsx")){
            /*XSSFWorkbook的构造方法中没有以 (nps.getRoot(), true) 作为参数的，
             *所以，导出的2007版本的Excel暂时无法使用NPOIFSFileSystem解析
             */
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFile));
                XSSFSheet sheet = workbook.getSheetAt(0);

                while (sheet.getNumMergedRegions() > 0) {
                    org.apache.poi.ss.util.CellRangeAddress ca = sheet.getMergedRegion(0);
                    sheet.removeMergedRegion(0);
                    int col = ca.getFirstColumn();
                    int col_ = ca.getLastColumn();
                    int row = ca.getFirstRow();
                    for (int i = 0; i < col_ - col; i++) {
                        sheet.getRow(row).getCell(col + i + 1).setCellValue(sheet.getRow(row).getCell(col).getStringCellValue() + String.valueOf(i + 1));
                        ;
                    }
                }

                //得到所有行中最长的作为最终列数
                int columnCount = 0;
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    if (sheet.getRow(i) != null) {
                        columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                    }
                }

                Tag table = new Tag("table");
                int tableId = _GENERATE_ID();
                table.cls("x-table fr-bi-excel-table preview-table");
                table.attr("id", String.valueOf(tableId));
                table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                Tag tbody = new Tag("tbody");
                int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                for (int row = 0; row <= rowCount; row++) {
                    Tag tr = new Tag("tr");
                    tr.attr("id", "r-" + row + "0");
                    XSSFRow rr = sheet.getRow(row);
                    if (rr != null) {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            XSSFRow r = sheet.getRow(row);
                            XSSFCell c = r.getCell(column);
                            if (c != null) {
                                sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                            } else {
                                td.sub("");
                            }
                            tr.sub(td);
                        }
                    } else {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            td.sub("");
                            tr.sub(td);
                        }
                    }
                    tbody.sub(tr);
                }
                table.sub(tbody);
                t.sub(table);
                FileOutputStream os = new FileOutputStream(excelFile);
                workbook.write(os);
                os.close();
            }catch (Exception e){
                try {
                    NPOIFSFileSystem nps = new NPOIFSFileSystem(new FileInputStream(excelFile));
                    HSSFWorkbook workbook = new HSSFWorkbook(nps.getRoot(), true);
                    HSSFSheet sheet = workbook.getSheetAt(0);

                    while(sheet.getNumMergedRegions() > 0){
                        Region ca = sheet.getMergedRegionAt(0);
                        sheet.removeMergedRegion(0);
                        int col = ca.getColumnFrom();
                        int col_ = ca.getColumnTo();
                        int row = ca.getRowFrom();
                        int row_ = ca.getRowTo();
                        for(int i = 0; i < col_ - col; i ++){
                            sheet.getRow(row).getCell(col + i + 1).setCellValue(sheet.getRow(row).getCell(col).getStringCellValue() + String.valueOf(i + 1));;
                        }
                    }

                    //得到所有行中最长的作为最终列数
                    int columnCount = 0;
                    for(int i = 0; i <= sheet.getLastRowNum(); i++){
                        if(sheet.getRow(i) != null){
                            columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                        }
                    }

                    Tag table = new Tag("table");
                    int tableId = _GENERATE_ID();
                    table.cls("x-table fr-bi-excel-table preview-table");
                    table.attr("id", String.valueOf(tableId));
                    table.css("table-layout", "fixed").css("position", "absolute").css("left","0px");
                    Tag tbody = new Tag("tbody");
                    int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                    for(int row = 0; row <= rowCount; row ++){
                        Tag tr = new Tag("tr");
                        tr.attr("id", "r-" + row + "0");
                        HSSFRow rr = sheet.getRow(row);
                        if( rr != null){
                            for(int column = 0; column < columnCount; column ++){
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                HSSFRow r = sheet.getRow(row);
                                HSSFCell c = r.getCell(column);
                                if(c != null){
                                    sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                    td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                                }else{
                                    td.sub("");
                                }
                                tr.sub(td);
                            }
                        }else{
                            for(int column = 0; column < columnCount; column ++){
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                td.sub("");
                                tr.sub(td);
                            }
                        }
                        tbody.sub(tr);
                    }
                    table.sub(tbody);
                    t.sub(table);
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                    os.close();
//    				is.close();
                } catch (Exception e1) {
                    t.sub(this.createExceptionTag());
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }else{
            try {
                NPOIFSFileSystem nps = new NPOIFSFileSystem(new FileInputStream(excelFile));
                HSSFWorkbook workbook = new HSSFWorkbook(nps.getRoot(), true);
                HSSFSheet sheet = workbook.getSheetAt(0);

                while(sheet.getNumMergedRegions() > 0){
                    Region ca = sheet.getMergedRegionAt(0);
                    sheet.removeMergedRegion(0);
                    int col = ca.getColumnFrom();
                    int col_ = ca.getColumnTo();
                    int row = ca.getRowFrom();
                    int row_ = ca.getRowTo();
                    for(int i = 0; i < col_ - col; i ++){
                        sheet.getRow(row).getCell(col + i + 1).setCellValue(sheet.getRow(row).getCell(col).getStringCellValue() + String.valueOf(i + 1));;
                    }
                }

                //得到所有行中最长的作为最终列数
                int columnCount = 0;
                for(int i = 0; i <= sheet.getLastRowNum(); i++){
                    if(sheet.getRow(i) != null){
                        columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                    }
                }

                Tag table = new Tag("table");
                int tableId = _GENERATE_ID();
                table.cls("x-table fr-bi-excel-table preview-table");
                table.attr("id", String.valueOf(tableId));
                table.css("table-layout", "fixed").css("position", "absolute").css("left","0px");
                Tag tbody = new Tag("tbody");
                int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                for(int row = 0; row <= rowCount; row ++){
                    Tag tr = new Tag("tr");
                    tr.attr("id", "r-" + row + "0");
                    HSSFRow rr = sheet.getRow(row);
                    if( rr != null){
                        for(int column = 0; column < columnCount; column ++){
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            HSSFRow r = sheet.getRow(row);
                            HSSFCell c = r.getCell(column);
                            if(c != null){
                                sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                            }else{
                                td.sub("");
                            }
                            tr.sub(td);
                        }
                    }else{
                        for(int column = 0; column < columnCount; column ++){
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            td.sub("");
                            tr.sub(td);
                        }
                    }
                    tbody.sub(tr);
                }
                table.sub(tbody);
                t.sub(table);
                FileOutputStream os = new FileOutputStream(excelFile);
                workbook.write(os);
                os.close();
//    				is.close();
            } catch (Exception e) {
                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFile));
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    while (sheet.getNumMergedRegions() > 0) {
                        org.apache.poi.ss.util.CellRangeAddress ca = sheet.getMergedRegion(0);
                        sheet.removeMergedRegion(0);
                        int col = ca.getFirstColumn();
                        int col_ = ca.getLastColumn();
                        int row = ca.getFirstRow();
                        for (int i = 0; i < col_ - col; i++) {
                            sheet.getRow(row).getCell(col + i + 1).setCellValue(sheet.getRow(row).getCell(col).getStringCellValue() + String.valueOf(i + 1));
                            ;
                        }
                    }

                    //得到所有行中最长的作为最终列数
                    int columnCount = 0;
                    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                        if (sheet.getRow(i) != null) {
                            columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                        }
                    }

                    Tag table = new Tag("table");
                    int tableId = _GENERATE_ID();
                    table.cls("x-table fr-bi-excel-table preview-table");
                    table.attr("id", String.valueOf(tableId));
                    table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                    Tag tbody = new Tag("tbody");
                    int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                    for (int row = 0; row <= rowCount; row++) {
                        Tag tr = new Tag("tr");
                        tr.attr("id", "r-" + row + "0");
                        XSSFRow rr = sheet.getRow(row);
                        if (rr != null) {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                XSSFRow r = sheet.getRow(row);
                                XSSFCell c = r.getCell(column);
                                if (c != null) {
                                    sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                    td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                                } else {
                                    td.sub("");
                                }
                                tr.sub(td);
                            }
                        } else {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                td.sub("");
                                tr.sub(td);
                            }
                        }
                        tbody.sub(tr);
                    }
                    table.sub(tbody);
                    t.sub(table);
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                    os.close();
                }catch (Exception e1){
                    t.sub(this.createExceptionTag());
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return t;
    }

    public Tag getFullExcel4Choose(File excelFile, HttpServletRequest req, SessionIDInfor session ) throws IOException{

        if(session == null){
            session = defalutSession;
        }
        Tag t = new Tag("div");
        if(excelFile.getAbsoluteFile().getName().endsWith(".xlsx")){
            try {
                InputStream is = new FileInputStream(excelFile);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                XSSFSheet sheet = workbook.getSheetAt(0);

                Tag table = new Tag("table");
                int tableId = _GENERATE_ID();
                table.cls("x-table fr-bi-excel-table preview-table");
                table.attr("id", String.valueOf(tableId));
                table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                Tag tbody = new Tag("tbody");

                //得到所有行中最长的作为最终列数
                int columnCount = 0;
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    if (sheet.getRow(i) != null) {
                        columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                    }
                }

                for (int row = 0; row <= sheet.getLastRowNum(); row++) {
                    Tag tr = new Tag("tr");
                    tr.attr("id", "r-" + row + "0");
                    XSSFRow rr = sheet.getRow(row);
                    if (rr != null) {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            XSSFRow r = sheet.getRow(row);
                            XSSFCell c = r.getCell(column);
                            if (c != null) {
                                sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                            } else {
                                td.sub("");
                            }
                            tr.sub(td);
                        }
                    } else {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            td.sub("");
                            tr.sub(td);
                        }
                    }
                    tbody.sub(tr);
                }
                table.sub(tbody);
                t.sub(table);
                FileOutputStream os = new FileOutputStream(excelFile);
                workbook.write(os);
                os.close();
                is.close();
            }catch (Exception e) {
                try {
                    NPOIFSFileSystem nps = new NPOIFSFileSystem(new FileInputStream(excelFile));
                    HSSFWorkbook workbook = new HSSFWorkbook(nps.getRoot(), true);
                    HSSFSheet sheet = workbook.getSheetAt(0);

                    Tag table = new Tag("table");
                    int tableId = _GENERATE_ID();
                    table.cls("x-table fr-bi-excel-table preview-table");
                    table.attr("id", String.valueOf(tableId));
                    table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                    Tag tbody = new Tag("tbody");

                    //得到所有行中最长的作为最终列数
                    int columnCount = 0;
                    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                        if (sheet.getRow(i) != null) {
                            columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                        }
                    }

                    for (int row = 0; row <= sheet.getLastRowNum(); row++) {
                        Tag tr = new Tag("tr");
                        tr.attr("id", "r-" + row + "0");
                        HSSFRow rr = sheet.getRow(row);
                        if (rr != null) {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                HSSFRow r = sheet.getRow(row);
                                HSSFCell c = r.getCell(column);
                                if (c != null) {
                                    sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                    td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                                } else {
                                    td.sub("");
                                }
                                tr.sub(td);
                            }
                        } else {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                td.sub("");
                                tr.sub(td);
                            }
                        }
                        tbody.sub(tr);
                    }
                    table.sub(tbody);
                    t.sub(table);
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                    os.close();
                } catch (Exception e1) {
                    t.sub(this.createExceptionTag());
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }else {
            try {
                NPOIFSFileSystem nps = new NPOIFSFileSystem(new FileInputStream(excelFile));
                HSSFWorkbook workbook = new HSSFWorkbook(nps.getRoot(), true);
                HSSFSheet sheet = workbook.getSheetAt(0);

                Tag table = new Tag("table");
                int tableId = _GENERATE_ID();
                table.cls("x-table fr-bi-excel-table preview-table");
                table.attr("id", String.valueOf(tableId));
                table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                Tag tbody = new Tag("tbody");

                //得到所有行中最长的作为最终列数
                int columnCount = 0;
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    if (sheet.getRow(i) != null) {
                        columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                    }
                }

                for (int row = 0; row <= sheet.getLastRowNum(); row++) {
                    Tag tr = new Tag("tr");
                    tr.attr("id", "r-" + row + "0");
                    HSSFRow rr = sheet.getRow(row);
                    if (rr != null) {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            HSSFRow r = sheet.getRow(row);
                            HSSFCell c = r.getCell(column);
                            if (c != null) {
                                sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                            } else {
                                td.sub("");
                            }
                            tr.sub(td);
                        }
                    } else {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            td.sub("");
                            tr.sub(td);
                        }
                    }
                    tbody.sub(tr);
                }
                table.sub(tbody);
                t.sub(table);
                FileOutputStream os = new FileOutputStream(excelFile);
                workbook.write(os);
                os.close();
            } catch (Exception e) {
                try {
                    InputStream is = new FileInputStream(excelFile);
                    XSSFWorkbook workbook = new XSSFWorkbook(is);
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    Tag table = new Tag("table");
                    int tableId = _GENERATE_ID();
                    table.cls("x-table fr-bi-excel-table preview-table");
                    table.attr("id", String.valueOf(tableId));
                    table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                    Tag tbody = new Tag("tbody");

                    //得到所有行中最长的作为最终列数
                    int columnCount = 0;
                    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                        if (sheet.getRow(i) != null) {
                            columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                        }
                    }

                    for (int row = 0; row <= sheet.getLastRowNum(); row++) {
                        Tag tr = new Tag("tr");
                        tr.attr("id", "r-" + row + "0");
                        XSSFRow rr = sheet.getRow(row);
                        if (rr != null) {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                XSSFRow r = sheet.getRow(row);
                                XSSFCell c = r.getCell(column);
                                if (c != null) {
                                    sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                    td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                                } else {
                                    td.sub("");
                                }
                                tr.sub(td);
                            }
                        } else {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                td.sub("");
                                tr.sub(td);
                            }
                        }
                        tbody.sub(tr);
                    }
                    table.sub(tbody);
                    t.sub(table);
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                    os.close();
                    is.close();
                }catch (Exception e1){
                    t.sub(this.createExceptionTag());
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return t;

    }

    public Tag getPartExcel4Preview(File excelFile, HttpServletRequest req, SessionIDInfor session ) throws IOException{

        if(session == null){
            session = defalutSession;
        }
        Tag t = new Tag("div");
        if(excelFile.getAbsoluteFile().getName().endsWith(".xlsx")){
            try {
                InputStream is = new FileInputStream(excelFile);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Tag table = new Tag("table");
                int tableId = _GENERATE_ID();
                table.cls("x-table fr-bi-excel-table preview-table");
                table.attr("id", String.valueOf(tableId));
                table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                Tag tbody = new Tag("tbody");
                int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                //得到所有行中最长的作为最终列数
                int columnCount = 0;
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    if (sheet.getRow(i) != null) {
                        columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                    }
                }
                for (int row = 0; row <= rowCount; row++) {
                    Tag tr = new Tag("tr");
                    tr.attr("id", "r-" + row + "0");
                    XSSFRow rr = sheet.getRow(row);
                    if (rr != null) {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            XSSFRow r = sheet.getRow(row);
                            XSSFCell c = r.getCell(column);
                            if (c != null) {
                                sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                            } else {
                                td.sub("");
                            }
                            tr.sub(td);
                        }
                    } else {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            td.sub("");
                            tr.sub(td);
                        }
                    }
                    tbody.sub(tr);
                }
                table.sub(tbody);
                t.sub(table);
                FileOutputStream os = new FileOutputStream(excelFile);
                workbook.write(os);
                os.close();
                is.close();
            }catch (Exception e) {
                try {
                    NPOIFSFileSystem nps = new NPOIFSFileSystem(new FileInputStream(excelFile));
                    HSSFWorkbook workbook = new HSSFWorkbook(nps.getRoot(), true);
                    HSSFSheet sheet = workbook.getSheetAt(0);
                    Tag table = new Tag("table");
                    int tableId = _GENERATE_ID();
                    table.cls("x-table fr-bi-excel-table preview-table");
                    table.attr("id", String.valueOf(tableId));
                    table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                    Tag tbody = new Tag("tbody");
                    //得到所有行中最长的作为最终列数
                    int columnCount = 0;
                    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                        if (sheet.getRow(i) != null) {
                            columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                        }
                    }
                    int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                    for (int row = 0; row <= rowCount; row++) {
                        Tag tr = new Tag("tr");
                        tr.attr("id", "r-" + row + "0");
                        HSSFRow rr = sheet.getRow(row);
                        if (rr != null) {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                HSSFRow r = sheet.getRow(row);
                                HSSFCell c = r.getCell(column);
                                if (c != null) {
                                    sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                    td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                                } else {
                                    td.sub("");
                                }
                                tr.sub(td);
                            }
                        } else {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                td.sub("");
                                tr.sub(td);
                            }
                        }
                        tbody.sub(tr);
                    }
                    table.sub(tbody);
                    t.sub(table);
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                    os.close();
                } catch (Exception e1) {
                    t.sub(this.createExceptionTag());
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }else {
            try {
                NPOIFSFileSystem nps = new NPOIFSFileSystem(new FileInputStream(excelFile));
                HSSFWorkbook workbook = new HSSFWorkbook(nps.getRoot(), true);
                HSSFSheet sheet = workbook.getSheetAt(0);
                Tag table = new Tag("table");
                int tableId = _GENERATE_ID();
                table.cls("x-table fr-bi-excel-table preview-table");
                table.attr("id", String.valueOf(tableId));
                table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                Tag tbody = new Tag("tbody");
                //得到所有行中最长的作为最终列数
                int columnCount = 0;
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    if (sheet.getRow(i) != null) {
                        columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                    }
                }
                int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                for (int row = 0; row <= rowCount; row++) {
                    Tag tr = new Tag("tr");
                    tr.attr("id", "r-" + row + "0");
                    HSSFRow rr = sheet.getRow(row);
                    if (rr != null) {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            HSSFRow r = sheet.getRow(row);
                            HSSFCell c = r.getCell(column);
                            if (c != null) {
                                sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                            } else {
                                td.sub("");
                            }
                            tr.sub(td);
                        }
                    } else {
                        for (int column = 0; column < columnCount; column++) {
                            Tag td = new Tag("td");
                            td.attr("id",
                                    ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                    .attr("row", "" + row).attr("col", "" + column);
                            td.sub("");
                            tr.sub(td);
                        }
                    }
                    tbody.sub(tr);
                }
                table.sub(tbody);
                t.sub(table);
                FileOutputStream os = new FileOutputStream(excelFile);
                workbook.write(os);
                os.close();
            } catch (Exception e) {
                try {
                    InputStream is = new FileInputStream(excelFile);
                    XSSFWorkbook workbook = new XSSFWorkbook(is);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    Tag table = new Tag("table");
                    int tableId = _GENERATE_ID();
                    table.cls("x-table fr-bi-excel-table preview-table");
                    table.attr("id", String.valueOf(tableId));
                    table.css("table-layout", "fixed").css("position", "absolute").css("left", "0px");
                    Tag tbody = new Tag("tbody");
                    int rowCount = sheet.getLastRowNum() > 50 ? 50 : sheet.getLastRowNum();
                    //得到所有行中最长的作为最终列数
                    int columnCount = 0;
                    for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                        if (sheet.getRow(i) != null) {
                            columnCount = columnCount > sheet.getRow(i).getLastCellNum() ? columnCount : sheet.getRow(i).getLastCellNum();
                        }
                    }
                    for (int row = 0; row <= rowCount; row++) {
                        Tag tr = new Tag("tr");
                        tr.attr("id", "r-" + row + "0");
                        XSSFRow rr = sheet.getRow(row);
                        if (rr != null) {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                XSSFRow r = sheet.getRow(row);
                                XSSFCell c = r.getCell(column);
                                if (c != null) {
                                    sheet.getRow(row).getCell(column).setCellType(Cell.CELL_TYPE_STRING);
                                    td.sub(sheet.getRow(row).getCell(column).getStringCellValue());
                                } else {
                                    td.sub("");
                                }
                                tr.sub(td);
                            }
                        } else {
                            for (int column = 0; column < columnCount; column++) {
                                Tag td = new Tag("td");
                                td.attr("id",
                                        ColumnRow.valueOf(column, row) + "-" + 0 + "-" + tableId)
                                        .attr("row", "" + row).attr("col", "" + column);
                                td.sub("");
                                tr.sub(td);
                            }
                        }
                        tbody.sub(tr);
                    }
                    table.sub(tbody);
                    t.sub(table);
                    FileOutputStream os = new FileOutputStream(excelFile);
                    workbook.write(os);
                    os.close();
                    is.close();
                }catch (Exception e1){
                    t.sub(this.createExceptionTag());
                    e1.printStackTrace();
                }

                e.printStackTrace();
            }
        }
        return t;

    }
    public Tag createExceptionTag(){
        Tag div = new Tag("div");
        div.sub(new Tag("br"));
        div.sub(new Tag("h3").sub("出错了，可能是由以下原因导致："));
        Tag ul = new Tag("ul");
        ul.sub(new Tag("li").sub("1、Excel太大（超过50M）."));
        ul.sub(new Tag("li").sub("2、Excel中含有复杂函数或者VBA编程等."));
        ul.sub(new Tag("li").sub("3、Excel是由不同版本间修改后缀名而来，如03版本的.xls修改为07版本的.xlsx."));
        ul.sub(new Tag("li").sub("4、Excel过于特殊导致后台程序无法处理."));
        div.sub(ul);
        div.sub(new Tag("h3").sub("建议："));
        Tag ul_ = new Tag("ul");
        ul_.sub(new Tag("li").sub("1、太大的Excel很可能是由于含有较多的格式或者其他位置元素，可以将该Excel另存为新的Excel再导入."));
        ul_.sub(new Tag("li").sub("2、检查Excel中是否含有VBA."));
        ul_.sub(new Tag("li").sub("3、避免重命名后缀名而来的Excel."));
        ul_.sub(new Tag("li").sub("4、选择你需要的部分Excel，另存为新的Excel再导入，成功率将大大提高."));
        div.sub(ul_);
        return div;
    }
}