package com.market.feed;

import com.market.feed.model.SyntheticForList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sf.xts.api.sdk.marketdata.master.MasterResponseFO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static void exportToExcel(List<MasterResponseFO> instruments, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Instruments");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Exchange Segment", "Exchange Instrument ID", "Instrument Type", "Exchange Segment ID", "Name",
            "Description", "Series", "Name with Series", "Instrument ID", "Price Band High",
            "Price Band Low", "Freeze Qty", "Tick Size", "Lot Size", "Multiplier",
            "Underlying Instrument ID", "Underlying Index Name", "Strike Price", "Contract Expiration",
            "Option Type", "Price Numerator", "Price Denominator", "Display Name", "Instrument Key"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        int rowNum = 1;
        for (MasterResponseFO instrument : instruments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(instrument.getExchangeSegment());
            row.createCell(1).setCellValue(instrument.getExchangeInstrumentID());
            row.createCell(2).setCellValue(instrument.getInstrumentType());
            row.createCell(3).setCellValue(instrument.getExchangeSegmentId());
            row.createCell(4).setCellValue(instrument.getName());
            row.createCell(5).setCellValue(instrument.getDescription() != null ? instrument.getDescription() : "null");
            row.createCell(6).setCellValue(instrument.getSeries());
            row.createCell(7).setCellValue(instrument.getNameWithSeries());
            row.createCell(8).setCellValue(instrument.getInstrumentID());
            row.createCell(9).setCellValue(instrument.getPriceBandHigh());
            row.createCell(10).setCellValue(instrument.getPriceBandLow());
            row.createCell(11).setCellValue(instrument.getFreezeQty());
            row.createCell(12).setCellValue(instrument.getTickSize());
            row.createCell(13).setCellValue(instrument.getLotSize());
            row.createCell(14).setCellValue(instrument.getMultiplier());
            row.createCell(15).setCellValue(instrument.getUnderlyingInstrumentId());
            row.createCell(16).setCellValue(instrument.getUnderlyingIndexName());
            row.createCell(17).setCellValue(instrument.getStrikePrice());
            row.createCell(18).setCellValue(instrument.getContractExpiration());
            row.createCell(19).setCellValue(instrument.getOptionType() != null ? instrument.getOptionType() : "null");
            row.createCell(20).setCellValue(instrument.getPriceNumerator());
            row.createCell(21).setCellValue(instrument.getPriceDenominator());
            row.createCell(22).setCellValue(instrument.getDisplayName());
            row.createCell(23).setCellValue(instrument.getInstrumentKey());
           System.out.println(rowNum);
        }

        // Write to file
        try (
        	FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeToExcel(List<SyntheticForList> dataList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Synthetic Data");

        String[] headers = {
                "sPrice", "name", "price", "ancherCE", "ancherPE", "spotCE", "spotPE", "future",
                "ancherATM", "spotATM", "ancherParity", "timestamp", "exchangeSegment",
                "exchangeInstrumentId", "exchangeTimestamp", "lut"
        };

        // Header Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Data Rows
        int rowNum = 1;
        for (SyntheticForList item : dataList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(item.getSPrice());
            row.createCell(1).setCellValue(item.getName());
            row.createCell(2).setCellValue(item.getPrice());
            row.createCell(3).setCellValue(item.getAncherCE());
            row.createCell(4).setCellValue(item.getAncherPE());
            row.createCell(5).setCellValue(item.getSpotCE());
            row.createCell(6).setCellValue(item.getSpotPE());
            row.createCell(7).setCellValue(item.getFuture());
            row.createCell(8).setCellValue(item.getAncherATM() != null ? item.getAncherATM() : 0);
            row.createCell(9).setCellValue(item.getSpotATM() != null ? item.getSpotATM() : 0);
            row.createCell(10).setCellValue(item.getAncherParity());
            row.createCell(11).setCellValue(item.getTimestamp());
            row.createCell(12).setCellValue(item.getExchangeSegment());
            row.createCell(13).setCellValue(item.getExchangeInstrumentId());
            row.createCell(14).setCellValue(item.getExchangeTimestamp() != null ? item.getExchangeTimestamp() : 0);
            row.createCell(15).setCellValue(item.getLut() != null ? item.getLut() : 0);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }
}

