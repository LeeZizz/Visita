package com.visita.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.visita.dto.response.ChartDataResponse;
import com.visita.dto.response.DashboardStatsResponse;
import com.visita.dto.response.TransactionResponse;
import com.visita.entities.PaymentEntity;
import com.visita.entities.PaymentStatus;
import com.visita.enums.TimeGranularity;
import com.visita.repositories.BookingRepository;
import com.visita.repositories.PaymentRepository;
import com.visita.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

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

    public List<ChartDataResponse> getChartData(LocalDate startDate, LocalDate endDate, TimeGranularity granularity) {
        return generateChartData(startDate, endDate, granularity, (start, end) -> {
            BigDecimal revenue = paymentRepository.sumAmountByStatusAndPaymentDateBetween(
                    PaymentStatus.SUCCESS, start, end);
            return revenue != null ? revenue : BigDecimal.ZERO;
        });
    }

    public List<ChartDataResponse> getUserChartData(LocalDate startDate, LocalDate endDate,
            TimeGranularity granularity) {
        return generateChartData(startDate, endDate, granularity,
                (start, end) -> BigDecimal.valueOf(userRepository.countByCreatedAtBetween(start, end)));
    }

    public List<ChartDataResponse> getBookingChartData(LocalDate startDate, LocalDate endDate,
            TimeGranularity granularity) {
        return generateChartData(startDate, endDate, granularity,
                (start, end) -> BigDecimal.valueOf(bookingRepository.countByBookingDateBetween(start, end)));
    }

    private List<ChartDataResponse> generateChartData(
            LocalDate startDate,
            LocalDate endDate,
            TimeGranularity granularity,
            BiFunction<LocalDateTime, LocalDateTime, BigDecimal> dataFetcher) {

        // Defaults: last 12 months, MONTH granularity (backward compatible)
        if (granularity == null) {
            granularity = TimeGranularity.MONTH;
        }
        LocalDate now = LocalDate.now();
        if (endDate == null) {
            endDate = now;
        }
        if (startDate == null) {
            startDate = (granularity == TimeGranularity.MONTH)
                    ? now.minusMonths(11).withDayOfMonth(1)
                    : now.minusDays(6);
        }

        List<ChartDataResponse> data = new ArrayList<>();

        if (granularity == TimeGranularity.DAY) {
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            for (long i = 0; i <= daysBetween; i++) {
                LocalDate current = startDate.plusDays(i);
                LocalDateTime start = current.atStartOfDay();
                LocalDateTime end = current.plusDays(1).atStartOfDay().minusSeconds(1);

                BigDecimal value = dataFetcher.apply(start, end);
                String label = current.format(DAY_FORMATTER);
                data.add(ChartDataResponse.builder().label(label).value(value).build());
            }
        } else {
            YearMonth startMonth = YearMonth.from(startDate);
            YearMonth endMonth = YearMonth.from(endDate);
            long monthsBetween = ChronoUnit.MONTHS.between(startMonth, endMonth);
            for (long i = 0; i <= monthsBetween; i++) {
                YearMonth current = startMonth.plusMonths(i);
                LocalDateTime start = current.atDay(1).atStartOfDay();
                LocalDateTime end = current.atEndOfMonth().plusDays(1).atStartOfDay().minusSeconds(1);

                BigDecimal value = dataFetcher.apply(start, end);
                String label = current.format(MONTH_FORMATTER);
                data.add(ChartDataResponse.builder().label(label).value(value).build());
            }
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
