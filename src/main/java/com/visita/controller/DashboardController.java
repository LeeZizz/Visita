package com.visita.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.response.ApiResponse;
import com.visita.dto.response.ChartDataResponse;
import com.visita.dto.response.DashboardStatsResponse;
import com.visita.dto.response.TransactionResponse;
import com.visita.enums.TimeGranularity;
import com.visita.services.DashboardService;
import com.visita.services.ExcelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admins/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ExcelService excelService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> getStats() {
        ApiResponse<DashboardStatsResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(dashboardService.getStats());
        return apiResponse;
    }

    @GetMapping("/chart")
    public ApiResponse<List<ChartDataResponse>> getChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TimeGranularity granularity) {
        ApiResponse<List<ChartDataResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(dashboardService.getChartData(startDate, endDate, granularity));
        return apiResponse;
    }

    @GetMapping("/chart/users")
    public ApiResponse<List<ChartDataResponse>> getUserChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TimeGranularity granularity) {
        ApiResponse<List<ChartDataResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(dashboardService.getUserChartData(startDate, endDate, granularity));
        return apiResponse;
    }

    @GetMapping("/chart/bookings")
    public ApiResponse<List<ChartDataResponse>> getBookingChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TimeGranularity granularity) {
        ApiResponse<List<ChartDataResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(dashboardService.getBookingChartData(startDate, endDate, granularity));
        return apiResponse;
    }

    @GetMapping("/transactions")
    public ApiResponse<List<TransactionResponse>> getRecentTransactions() {
        ApiResponse<List<TransactionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(dashboardService.getRecentTransactions());
        return apiResponse;
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportData() {
        List<TransactionResponse> transactions = dashboardService.getAllTransactionsForExport();
        ByteArrayInputStream in = excelService.exportTransactionsToExcel(transactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=dashboard_data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
