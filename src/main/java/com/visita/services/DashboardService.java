package com.visita.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.visita.dto.response.ChartDataResponse;
import com.visita.dto.response.DashboardStatsResponse;
import com.visita.dto.response.TransactionResponse;
import com.visita.entities.PaymentEntity;
import com.visita.entities.PaymentStatus;
import com.visita.repositories.BookingRepository;
import com.visita.repositories.PaymentRepository;
import com.visita.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public DashboardStatsResponse getStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDateTime lastDayLastMonth = firstDayThisMonth.minusSeconds(1);

        // 1. Revenue
        BigDecimal currentMonthRevenue = paymentRepository.sumAmountByStatusAndPaymentDateBetween(
                PaymentStatus.SUCCESS, firstDayThisMonth, now);
        if (currentMonthRevenue == null)
            currentMonthRevenue = BigDecimal.ZERO;

        BigDecimal lastMonthRevenue = paymentRepository.sumAmountByStatusAndPaymentDateBetween(
                PaymentStatus.SUCCESS, firstDayLastMonth, lastDayLastMonth);
        if (lastMonthRevenue == null)
            lastMonthRevenue = BigDecimal.ZERO;

        Double revenueGrowth = calculateGrowth(currentMonthRevenue.doubleValue(), lastMonthRevenue.doubleValue());

        // 2. New Users
        long currentMonthUsers = userRepository.countByCreatedAtBetween(firstDayThisMonth, now);
        long lastMonthUsers = userRepository.countByCreatedAtBetween(firstDayLastMonth, lastDayLastMonth);
        Double userGrowth = calculateGrowth((double) currentMonthUsers, (double) lastMonthUsers);

        // 3. Total Bookings
        long currentMonthBookings = bookingRepository.countByBookingDateBetween(firstDayThisMonth, now);
        long lastMonthBookings = bookingRepository.countByBookingDateBetween(firstDayLastMonth, lastDayLastMonth);
        Double bookingGrowth = calculateGrowth((double) currentMonthBookings, (double) lastMonthBookings);

        // 4. Active Users
        long activeUsers = userRepository.countByIsActiveTrue();

        return DashboardStatsResponse.builder()
                .totalRevenue(currentMonthRevenue)
                .revenueGrowth(revenueGrowth)
                .newUsers(currentMonthUsers)
                .userGrowth(userGrowth)
                .totalBookings(currentMonthBookings)
                .bookingGrowth(bookingGrowth)
                .activeUsers(activeUsers)
                .build();
    }

    public List<ChartDataResponse> getChartData() {
        // Revenue Chart (Last 12 months)
        List<ChartDataResponse> data = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1).minusSeconds(1);

            BigDecimal revenue = paymentRepository.sumAmountByStatusAndPaymentDateBetween(
                    PaymentStatus.SUCCESS, start, end);
            if (revenue == null)
                revenue = BigDecimal.ZERO;

            String label = YearMonth.from(start).format(DateTimeFormatter.ofPattern("MM/yyyy"));
            data.add(ChartDataResponse.builder().label(label).value(revenue).build());
        }
        return data;
    }

    public List<ChartDataResponse> getUserChartData() {
        List<ChartDataResponse> data = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1).minusSeconds(1);

            long count = userRepository.countByCreatedAtBetween(start, end);

            String label = YearMonth.from(start).format(DateTimeFormatter.ofPattern("MM/yyyy"));
            data.add(ChartDataResponse.builder().label(label).value(BigDecimal.valueOf(count)).build());
        }
        return data;
    }

    public List<ChartDataResponse> getBookingChartData() {
        List<ChartDataResponse> data = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1).minusSeconds(1);

            long count = bookingRepository.countByBookingDateBetween(start, end);

            String label = YearMonth.from(start).format(DateTimeFormatter.ofPattern("MM/yyyy"));
            data.add(ChartDataResponse.builder().label(label).value(BigDecimal.valueOf(count)).build());
        }
        return data;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return paymentRepository.findTop10ByStatusOrderByPaymentDateDesc(PaymentStatus.SUCCESS)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getAllTransactionsForExport() {
        // Re-using the logic but perhaps fetching ALL or a large number for export
        // Actually, let's just fetch all 'SUCCESS' payments sort desc
        // For now, using the top 10 logic is wrong for EXPORT.
        // But user said "api xuất dữ liệu...".
        // I will create a method to find ALL success payments for export or maybe all
        // payments.
        // Let's assume all SUCCESS payments for now.
        // But PaymentRepository doesn't have a findAllByStatus method yet.
        // I will verify if I should add it or just use custom query.
        // I'll stick to findTop10 for 'Recent' but for export I need more.
        // Let's just create a quick findAllByStatusOrderByPaymentDateDesc in Repository
        // or
        // just fetch list by JpaRepository logic.
        return paymentRepository.findAll().stream() // or filter by status
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .sorted((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()))
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private Double calculateGrowth(Double current, Double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100;
    }

    private TransactionResponse mapToTransactionResponse(PaymentEntity payment) {
        return TransactionResponse.builder()
                .transactionId(payment.getTransactionId())
                .userId(payment.getBooking().getUser().getUserId())
                .userName(payment.getBooking().getUser().getFullName())
                .userEmail(payment.getBooking().getUser().getEmail())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }
}
