USE visita;

-- Disable foreign key checks for bulk import
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- CLEAR ALL EXISTING DATA (required for re-running this script)
-- =============================================
DELETE FROM history;
DELETE FROM favorites;
DELETE FROM reviews;
DELETE FROM payments;
DELETE FROM bookings;
DELETE FROM tour_images;
DELETE FROM tours;
DELETE FROM users_roles;
DELETE FROM users;
DELETE FROM promotions;

-- =============================================
-- PROMOTIONS (For bookings)
-- =============================================
INSERT INTO promotions (promotion_id, code, description, discount_amount, discount_percent, end_date, is_active, quantity, start_date, version) VALUES
('promo-001', 'WELCOME10', 'Giảm giá chào mừng 10%', NULL, 10.00, '2026-12-31', 1, 100, '2026-01-01', 0),
('promo-002', 'SUMMER25', 'Khuyến mãi mùa hè giảm 25%', NULL, 25.00, '2026-08-31', 1, 50, '2026-06-01', 0),
('promo-003', 'VIP50K', 'Giảm giá VIP 50.000 VNĐ', 50000.00, NULL, '2026-12-31', 1, 30, '2026-01-01', 0),
('promo-004', 'NEWYEAR15', 'Giảm giá năm mới 15%', NULL, 15.00, '2026-02-28', 1, 200, '2026-01-01', 0),
('promo-005', 'FLASH100K', 'Flash sale giảm 100.000 VNĐ', 100000.00, NULL, '2026-03-31', 1, 20, '2026-03-01', 0);

-- =============================================
-- CHUNK 1: USERS 1-10 (Admin, Staff, Users)
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-001', '123 Đường Admin, Quận 1, TP.HCM', '2025-01-01 08:00:00', '1985-03-15', 'admin@visita.vn', 'Nguyễn Văn Admin', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0901234567', '2025-01-01 08:00:00', 'admin'),
('user-002', '456 Đường Staff, Quận 3, TP.HCM', '2025-01-02 09:00:00', '1990-06-20', 'staff@visita.vn', 'Trần Thị Staff One', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0902345678', '2025-01-02 09:00:00', 'staff1'),
('user-003', '789 Hẻm Staff, Quận 5, TP.HCM', '2025-01-03 10:00:00', '1988-09-10', 'staff2@visita.vn', 'Lê Văn Staff Two', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0903456789', '2025-01-03 10:00:00', 'staff2'),
('user-004', '12 Nguyễn Huệ, Quận 1, TP.HCM', '2025-01-10 11:30:00', '1995-02-14', 'user@gmail.com', 'Phạm Minh Tuấn', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0904567890', '2025-01-10 11:30:00', 'minhtuan'),
('user-005', '34 Lê Lợi, Quận 1, TP.HCM', '2025-01-11 14:00:00', '1992-07-22', 'user2@gmail.com', 'Võ Thị Lan', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0905678901', '2025-01-11 14:00:00', 'thilan'),
('user-006', '56 Hai Bà Trưng, Quận 3, TP.HCM', '2025-01-12 09:15:00', '1998-11-05', 'user3@gmail.com', 'Hoàng Đức Anh', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0906789012', '2025-01-12 09:15:00', 'ducanh'),
('user-007', '78 Pasteur, Quận 1, TP.HCM', '2025-01-13 16:45:00', '1993-04-18', 'user4@gmail.com', 'Nguyễn Thị Mai', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0907890123', '2025-01-13 16:45:00', 'thimai'),
('user-008', '90 Đồng Khởi, Quận 1, TP.HCM', '2025-01-14 10:30:00', '1996-08-30', 'user5@gmail.com', 'Trần Văn Hùng', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0908901234', '2025-01-14 10:30:00', 'vanhung'),
('user-009', '123 CMT8, Quận 10, TP.HCM', '2025-01-15 13:00:00', '1991-12-25', 'user6@gmail.com', 'Lê Thị Hồng', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0909012345', '2025-01-15 13:00:00', 'thihong'),
('user-010', '456 Lý Tự Trọng, Quận 1, TP.HCM', '2025-01-16 08:00:00', '1997-01-08', 'user7@gmail.com', 'Phan Văn Khánh', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0910123456', '2025-01-16 08:00:00', 'vankhanh');

-- User roles for chunk 1 (No changes needed)
INSERT INTO users_roles (user_id, role_name) VALUES
('user-001', 'ADMIN'),
('user-002', 'STAFF'),
('user-003', 'STAFF'),
('user-004', 'USER'),
('user-005', 'USER'),
('user-006', 'USER'),
('user-007', 'USER'),
('user-008', 'USER'),
('user-009', 'USER'),
('user-010', 'USER');

-- =============================================
-- CHUNK 2: USERS 11-20
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-011', '789 Nguyễn Trãi, Quận 5, TP.HCM', '2025-01-17 11:00:00', '1994-03-12', 'user8@gmail.com', 'Bùi Thị Ngọc', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0911234567', '2025-01-17 11:00:00', 'thingoc'),
('user-012', '321 Trần Hưng Đạo, Quận 5, TP.HCM', '2025-01-18 15:30:00', '1989-05-28', 'user9@gmail.com', 'Đặng Văn Phúc', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0912345678', '2025-01-18 15:30:00', 'vanphuc'),
('user-013', '654 An Dương Vương, Quận 6, TP.HCM', '2025-01-19 09:45:00', '1996-10-15', 'user10@gmail.com', 'Ngô Thị Thảo', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0913456789', '2025-01-19 09:45:00', 'thithao'),
('user-014', '987 Ba Tháng Hai, Quận 10, TP.HCM', '2025-01-20 14:20:00', '1993-08-07', 'user11@gmail.com', 'Trương Minh Đức', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0914567890', '2025-01-20 14:20:00', 'minhduc'),
('user-015', '147 Phạm Ngũ Lão, Quận 1, TP.HCM', '2025-01-21 10:10:00', '1999-02-20', 'user12@gmail.com', 'Cao Thị Linh', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0915678901', '2025-01-21 10:10:00', 'thilinh'),
('user-016', '258 Cách Mạng Tháng 8, Quận 3, TP.HCM', '2025-01-22 16:00:00', '1990-11-11', 'user13@gmail.com', 'Vũ Văn Long', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0916789012', '2025-01-22 16:00:00', 'vanlong'),
('user-017', '369 Lê Văn Sỹ, Phú Nhuận, TP.HCM', '2025-01-23 12:30:00', '1995-06-03', 'user14@gmail.com', 'Đinh Thị Hương', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0917890123', '2025-01-23 12:30:00', 'thihuong'),
('user-018', '480 Hoàng Văn Thụ, Tân Bình, TP.HCM', '2025-01-24 08:45:00', '1988-04-25', 'user15@gmail.com', 'Lý Minh Quang', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0918901234', '2025-01-24 08:45:00', 'minhquang'),
('user-019', '591 Nguyễn Văn Cừ, Quận 5, TP.HCM', '2025-01-25 13:15:00', '1997-09-18', 'user16@gmail.com', 'Mai Thị Phương', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0919012345', '2025-01-25 13:15:00', 'thiphuong'),
('user-020', '702 Võ Văn Tần, Quận 3, TP.HCM', '2025-01-26 17:00:00', '1992-12-01', 'user17@gmail.com', 'Trịnh Văn Hào', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0920123456', '2025-01-26 17:00:00', 'vanhao');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-011', 'USER'),
('user-012', 'USER'),
('user-013', 'USER'),
('user-014', 'USER'),
('user-015', 'USER'),
('user-016', 'USER'),
('user-017', 'USER'),
('user-018', 'USER'),
('user-019', 'USER'),
('user-020', 'USER');

-- =============================================
-- CHUNK 3: USERS 21-30
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-021', '813 Nguyễn Đình Chiểu, Quận 3, TP.HCM', '2025-01-27 09:00:00', '1994-07-14', 'user18@gmail.com', 'Dương Thị Yến', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0921234567', '2025-01-27 09:00:00', 'thiyen'),
('user-022', '924 Điện Biên Phủ, Bình Thạnh, TP.HCM', '2025-01-28 11:30:00', '1991-03-22', 'user19@gmail.com', 'Hà Văn Nam', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0922345678', '2025-01-28 11:30:00', 'vannam'),
('user-023', '135 Xô Viết Nghệ Tĩnh, Bình Thạnh, TP.HCM', '2025-01-29 14:00:00', '1998-09-08', 'user20@gmail.com', 'Lâm Thị Thu', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0923456789', '2025-01-29 14:00:00', 'thithu'),
('user-024', '246 Nguyễn Kiệm, Gò Vấp, TP.HCM', '2025-01-30 16:45:00', '1993-05-17', 'user21@gmail.com', 'Đỗ Minh Trí', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0924567890', '2025-01-30 16:45:00', 'minhtri'),
('user-025', '357 Quang Trung, Gò Vấp, TP.HCM', '2025-01-31 10:00:00', '1996-11-29', 'user22@gmail.com', 'Nguyễn Thị Cẩm', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0925678901', '2025-01-31 10:00:00', 'thicam'),
('user-026', '468 Lê Đức Thọ, Gò Vấp, TP.HCM', '2025-02-01 08:30:00', '1990-02-14', 'user23@gmail.com', 'Trần Văn Bình', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0926789012', '2025-02-01 08:30:00', 'vanbinh'),
('user-027', '579 Phan Văn Trị, Gò Vấp, TP.HCM', '2025-02-02 12:15:00', '1995-08-06', 'user24@gmail.com', 'Lê Thị Đào', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0927890123', '2025-02-02 12:15:00', 'thidao'),
('user-028', '680 Nguyễn Thái Sơn, Gò Vấp, TP.HCM', '2025-02-03 15:00:00', '1987-04-20', 'user25@gmail.com', 'Phạm Văn Cường', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0928901234', '2025-02-03 15:00:00', 'vancuong'),
('user-029', '791 Nguyễn Thị Thập, Quận 7, TP.HCM', '2025-02-04 09:45:00', '1999-10-12', 'user26@gmail.com', 'Võ Thị Em', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0929012345', '2025-02-04 09:45:00', 'thiem'),
('user-030', '802 Huỳnh Tấn Phát, Quận 7, TP.HCM', '2025-02-05 13:30:00', '1992-06-25', 'user27@gmail.com', 'Hoàng Văn Giang', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0930123456', '2025-02-05 13:30:00', 'vangiang');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-021', 'USER'),
('user-022', 'USER'),
('user-023', 'USER'),
('user-024', 'USER'),
('user-025', 'USER'),
('user-026', 'USER'),
('user-027', 'USER'),
('user-028', 'USER'),
('user-029', 'USER'),
('user-030', 'USER');

-- =============================================
-- CHUNK 4: USERS 31-40
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-031', '15 Trần Phú, Đà Nẵng', '2025-02-06 10:00:00', '1994-01-08', 'user28@gmail.com', 'Nguyễn Văn Hà', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0931234567', '2025-02-06 10:00:00', 'vanha'),
('user-032', '26 Bạch Đằng, Đà Nẵng', '2025-02-07 11:00:00', '1997-04-15', 'user29@gmail.com', 'Trần Thị Ích', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0932345678', '2025-02-07 11:00:00', 'thiich'),
('user-033', '37 Nguyễn Văn Linh, Đà Nẵng', '2025-02-08 14:30:00', '1991-07-22', 'user30@gmail.com', 'Lê Văn Khoa', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0933456789', '2025-02-08 14:30:00', 'vankhoa'),
('user-034', '48 Hoàng Diệu, Đà Nẵng', '2025-02-09 16:00:00', '1988-10-30', 'user31@gmail.com', 'Phạm Thị Liên', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0934567890', '2025-02-09 16:00:00', 'thilien'),
('user-035', '59 Lê Duẩn, Đà Nẵng', '2025-02-10 09:15:00', '1995-02-18', 'user32@gmail.com', 'Võ Văn Mạnh', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0935678901', '2025-02-10 09:15:00', 'vanmanh'),
('user-036', '70 Phan Châu Trinh, Hội An', '2025-02-11 12:00:00', '1993-05-25', 'user33@gmail.com', 'Bùi Thị Nhung', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0936789012', '2025-02-11 12:00:00', 'thinhung'),
('user-037', '81 Hai Bà Trưng, Hội An', '2025-02-12 15:30:00', '1996-08-11', 'user34@gmail.com', 'Đặng Văn Oanh', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0937890123', '2025-02-12 15:30:00', 'vanoanh'),
('user-038', '92 Nguyễn Thái Học, Hội An', '2025-02-13 08:45:00', '1990-11-03', 'user35@gmail.com', 'Ngô Thị Phương', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0938901234', '2025-02-13 08:45:00', 'thiphuong2'),
('user-039', '103 Cửa Đại, Hội An', '2025-02-14 11:20:00', '1998-03-27', 'user36@gmail.com', 'Trương Văn Quyết', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0939012345', '2025-02-14 11:20:00', 'vanquyet'),
('user-040', '114 An Bàng, Hội An', '2025-02-15 14:00:00', '1992-09-14', 'user37@gmail.com', 'Cao Thị Rồng', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0940123456', '2025-02-15 14:00:00', 'thirong');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-031', 'USER'),
('user-032', 'USER'),
('user-033', 'USER'),
('user-034', 'USER'),
('user-035', 'USER'),
('user-036', 'USER'),
('user-037', 'USER'),
('user-038', 'USER'),
('user-039', 'USER'),
('user-040', 'USER');

-- =============================================
-- CHUNK 5: USERS 41-50
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-041', '125 Hùng Vương, Huế', '2025-02-16 10:30:00', '1994-06-20', 'user38@gmail.com', 'Vũ Văn Sơn', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0941234567', '2025-02-16 10:30:00', 'vanson'),
('user-042', '136 Lê Lợi, Huế', '2025-02-17 13:00:00', '1997-12-07', 'user39@gmail.com', 'Đinh Thị Tuyết', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0942345678', '2025-02-17 13:00:00', 'thituyet'),
('user-043', '147 Nguyễn Huệ, Huế', '2025-02-18 16:00:00', '1989-04-13', 'user40@gmail.com', 'Lý Văn Uy', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0943456789', '2025-02-18 16:00:00', 'vanuy'),
('user-044', '158 Trần Cao Vân, Huế', '2025-02-19 09:00:00', '1995-10-28', 'user41@gmail.com', 'Mai Thị Vân', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0944567890', '2025-02-19 09:00:00', 'thivan'),
('user-045', '169 Đội Cung, Huế', '2025-02-20 11:45:00', '1991-01-15', 'user42@gmail.com', 'Trịnh Văn Xuân', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0945678901', '2025-02-20 11:45:00', 'vanxuan'),
('user-046', '180 Phạm Ngũ Lão, Huế', '2025-02-21 14:30:00', '1998-07-02', 'user43@gmail.com', 'Dương Thị Yến', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0946789012', '2025-02-21 14:30:00', 'thiyen2'),
('user-047', '191 Điện Biên Phủ, Huế', '2025-02-22 08:15:00', '1993-11-19', 'user44@gmail.com', 'Hà Văn Dũng', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0947890123', '2025-02-22 08:15:00', 'vanzung'),
('user-048', '202 Lê Thánh Tôn, Nha Trang', '2025-02-23 12:00:00', '1996-02-25', 'user45@gmail.com', 'Lâm Thị Ánh', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0948901234', '2025-02-23 12:00:00', 'thianh2'),
('user-049', '213 Trần Phú, Nha Trang', '2025-02-24 15:30:00', '1990-08-10', 'user46@gmail.com', 'Đỗ Văn Bảo', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0949012345', '2025-02-24 15:30:00', 'vanbao'),
('user-050', '224 Nguyễn Thiện Thuật, Nha Trang', '2025-02-25 10:00:00', '1987-05-18', 'user47@gmail.com', 'Nguyễn Thị Chi', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0950123456', '2025-02-25 10:00:00', 'thichi');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-041', 'USER'),
('user-042', 'USER'),
('user-043', 'USER'),
('user-044', 'USER'),
('user-045', 'USER'),
('user-046', 'USER'),
('user-047', 'USER'),
('user-048', 'USER'),
('user-049', 'USER'),
('user-050', 'USER');

-- =============================================
-- CHUNK 6: USERS 51-60
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-051', '235 Yersin, Nha Trang', '2025-02-26 11:00:00', '1994-12-03', 'user48@gmail.com', 'Trần Văn Dũng', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0951234567', '2025-02-26 11:00:00', 'vandung'),
('user-052', '246 Hùng Vương, Nha Trang', '2025-02-27 14:00:00', '1991-06-16', 'user49@gmail.com', 'Lê Thị Gấm', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0952345678', '2025-02-27 14:00:00', 'thigam'),
('user-053', '257 Nguyễn Thị Minh Khai, Đà Lạt', '2025-02-28 09:30:00', '1998-09-21', 'user50@gmail.com', 'Phạm Văn Hải', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0953456789', '2025-02-28 09:30:00', 'vanhai'),
('user-054', '268 Phan Đình Phùng, Đà Lạt', '2025-03-01 12:15:00', '1993-03-08', 'user51@gmail.com', 'Võ Thị Khánh', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0954567890', '2025-03-01 12:15:00', 'thikhanh'),
('user-055', '279 Trần Phú, Đà Lạt', '2025-03-02 15:00:00', '1996-11-14', 'user52@gmail.com', 'Hoàng Văn Luân', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0955678901', '2025-03-02 15:00:00', 'vanluan'),
('user-056', '290 Lê Đại Hành, Đà Lạt', '2025-03-03 08:00:00', '1989-04-27', 'user53@gmail.com', 'Bùi Thị Mỹ', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0956789012', '2025-03-03 08:00:00', 'thimy'),
('user-057', '301 Nguyễn Chí Thanh, Đà Lạt', '2025-03-04 10:45:00', '1995-07-09', 'user54@gmail.com', 'Đặng Văn Nghị', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0957890123', '2025-03-04 10:45:00', 'vannghi'),
('user-058', '312 Hoàng Văn Thụ, Đà Lạt', '2025-03-05 13:30:00', '1992-10-22', 'user55@gmail.com', 'Ngô Thị Oanh', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0958901234', '2025-03-05 13:30:00', 'thioanh'),
('user-059', '323 Xuân Hương, Đà Lạt', '2025-03-06 16:00:00', '1997-01-05', 'user56@gmail.com', 'Trương Văn Phú', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0959012345', '2025-03-06 16:00:00', 'vanphu'),
('user-060', '334 Cam Ly, Đà Lạt', '2025-03-07 09:15:00', '1988-06-18', 'user57@gmail.com', 'Cao Thị Quyên', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0960123456', '2025-03-07 09:15:00', 'thiquyen');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-051', 'USER'),
('user-052', 'USER'),
('user-053', 'USER'),
('user-054', 'USER'),
('user-055', 'USER'),
('user-056', 'USER'),
('user-057', 'USER'),
('user-058', 'USER'),
('user-059', 'USER'),
('user-060', 'USER');

-- =============================================
-- CHUNK 7: USERS 61-70
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-061', '345 Nguyễn Trãi, Cần Thơ', '2025-03-08 11:00:00', '1994-08-30', 'user58@gmail.com', 'Vũ Văn Rạng', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0961234567', '2025-03-08 11:00:00', 'vanrang'),
('user-062', '356 Trần Hưng Đạo, Cần Thơ', '2025-03-09 14:30:00', '1991-02-12', 'user59@gmail.com', 'Đinh Thị Sương', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0962345678', '2025-03-09 14:30:00', 'thisuong'),
('user-063', '367 Hai Bà Trưng, Cần Thơ', '2025-03-10 08:45:00', '1998-05-25', 'user60@gmail.com', 'Lý Văn Tài', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0963456789', '2025-03-10 08:45:00', 'vantai'),
('user-064', '378 Lê Lợi, Cần Thơ', '2025-03-11 12:00:00', '1993-11-07', 'user61@gmail.com', 'Mai Thị Uyên', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0964567890', '2025-03-11 12:00:00', 'thiuyen'),
('user-065', '389 Phan Đình Phùng, Cần Thơ', '2025-03-12 15:15:00', '1996-04-19', 'user62@gmail.com', 'Trịnh Văn Vũ', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0965678901', '2025-03-12 15:15:00', 'vanvu'),
('user-066', '400 Hòa Bình, Cần Thơ', '2025-03-13 10:00:00', '1989-09-02', 'user63@gmail.com', 'Dương Thị Xuân', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0966789012', '2025-03-13 10:00:00', 'thixuan'),
('user-067', '411 Mậu Thân, Cần Thơ', '2025-03-14 13:45:00', '1995-12-14', 'user64@gmail.com', 'Hà Văn Yên', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0967890123', '2025-03-14 13:45:00', 'vanyen'),
('user-068', '422 Xô Viết Nghệ Tĩnh, Cần Thơ', '2025-03-15 16:30:00', '1992-03-28', 'user65@gmail.com', 'Lâm Thị Duyên', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0968901234', '2025-03-15 16:30:00', 'thizuyen'),
('user-069', '12 Hoàng Hoa Thám, Vũng Tàu', '2025-03-16 09:00:00', '1997-07-10', 'user66@gmail.com', 'Đỗ Văn An', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0969012345', '2025-03-16 09:00:00', 'vanan'),
('user-070', '23 Thùy Vân, Vũng Tàu', '2025-03-17 11:30:00', '1990-10-23', 'user67@gmail.com', 'Nguyễn Thị Băng', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0970123456', '2025-03-17 11:30:00', 'thibang');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-061', 'USER'),
('user-062', 'USER'),
('user-063', 'USER'),
('user-064', 'USER'),
('user-065', 'USER'),
('user-066', 'USER'),
('user-067', 'USER'),
('user-068', 'USER'),
('user-069', 'USER'),
('user-070', 'USER');

-- =============================================
-- CHUNK 8: USERS 71-80
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-071', '34 Lê Hồng Phong, Vũng Tàu', '2025-03-18 14:00:00', '1994-01-05', 'user68@gmail.com', 'Trần Văn Cao', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0971234567', '2025-03-18 14:00:00', 'vancao'),
('user-072', '45 Võ Thị Sáu, Vũng Tàu', '2025-03-19 08:30:00', '1991-05-18', 'user69@gmail.com', 'Lê Thị Dịu', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0972345678', '2025-03-19 08:30:00', 'thidiu'),
('user-073', '56 Ba Cu, Vũng Tàu', '2025-03-20 11:15:00', '1998-08-31', 'user70@gmail.com', 'Phạm Văn Gia', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0973456789', '2025-03-20 11:15:00', 'vangia'),
('user-074', '67 Nguyễn An Ninh, Vũng Tàu', '2025-03-21 14:45:00', '1993-12-13', 'user71@gmail.com', 'Võ Thị Hương', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0974567890', '2025-03-21 14:45:00', 'thihuong2'),
('user-075', '78 Trần Phú, Vũng Tàu', '2025-03-22 10:00:00', '1996-04-26', 'user72@gmail.com', 'Hoàng Văn Ích', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0975678901', '2025-03-22 10:00:00', 'vanich'),
('user-076', '89 Bình Giã, Vũng Tàu', '2025-03-23 13:30:00', '1989-08-08', 'user73@gmail.com', 'Bùi Thị Kim', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0976789012', '2025-03-23 13:30:00', 'thikim'),
('user-077', '100 Lê Lai, Vũng Tàu', '2025-03-24 16:00:00', '1995-11-20', 'user74@gmail.com', 'Đặng Văn Lợi', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0977890123', '2025-03-24 16:00:00', 'vanloi'),
('user-078', '15 Phan Bội Châu, Phú Quốc', '2025-03-25 09:00:00', '1992-02-03', 'user75@gmail.com', 'Ngô Thị Mùa', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0978901234', '2025-03-25 09:00:00', 'thimua'),
('user-079', '26 Trần Hưng Đạo, Phú Quốc', '2025-03-26 12:30:00', '1997-06-15', 'user76@gmail.com', 'Trương Văn Nghĩa', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0979012345', '2025-03-26 12:30:00', 'vanghia'),
('user-080', '37 Nguyễn Trung Trực, Phú Quốc', '2025-03-27 15:00:00', '1988-09-28', 'user77@gmail.com', 'Cao Thị Pha', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0980123456', '2025-03-27 15:00:00', 'thipha');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-071', 'USER'),
('user-072', 'USER'),
('user-073', 'USER'),
('user-074', 'USER'),
('user-075', 'USER'),
('user-076', 'USER'),
('user-077', 'USER'),
('user-078', 'USER'),
('user-079', 'USER'),
('user-080', 'USER');

-- =============================================
-- CHUNK 9: USERS 81-90
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-081', '48 Dương Đông, Phú Quốc', '2025-03-28 10:30:00', '1994-03-11', 'user78@gmail.com', 'Vũ Văn Quý', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0981234567', '2025-03-28 10:30:00', 'vanquy'),
('user-082', '59 Bãi Vòng, Phú Quốc', '2025-03-29 13:00:00', '1991-07-24', 'user79@gmail.com', 'Đinh Thị Rùa', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0982345678', '2025-03-29 13:00:00', 'thirua'),
('user-083', '70 An Thới, Phú Quốc', '2025-03-30 16:30:00', '1998-10-06', 'user80@gmail.com', 'Lý Văn Sâm', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0983456789', '2025-03-30 16:30:00', 'vansam'),
('user-084', '81 Hàm Ninh, Phú Quốc', '2025-03-31 09:15:00', '1993-01-19', 'user81@gmail.com', 'Mai Thị Trà', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0984567890', '2025-03-31 09:15:00', 'thitra'),
('user-085', '92 Cửa Cạn, Phú Quốc', '2025-04-01 12:00:00', '1996-05-02', 'user82@gmail.com', 'Trịnh Văn Út', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0985678901', '2025-04-01 12:00:00', 'vanut'),
('user-086', '103 Gành Dầu, Phú Quốc', '2025-04-02 14:45:00', '1989-11-15', 'user83@gmail.com', 'Dương Thị Vy', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0986789012', '2025-04-02 14:45:00', 'thivy'),
('user-087', '12 Đông Dư, Hà Nội', '2025-04-03 08:30:00', '1995-04-28', 'user84@gmail.com', 'Hà Văn Xanh', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0987890123', '2025-04-03 08:30:00', 'vanxanh'),
('user-088', '23 Hàng Đào, Hà Nội', '2025-04-04 11:00:00', '1992-08-10', 'user85@gmail.com', 'Lâm Thị Yến', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0988901234', '2025-04-04 11:00:00', 'thiyen3'),
('user-089', '34 Hàng Bạc, Hà Nội', '2025-04-05 14:15:00', '1997-12-23', 'user86@gmail.com', 'Đỗ Văn Duẩn', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0989012345', '2025-04-05 14:15:00', 'vanzuan'),
('user-090', '45 Hàng Gai, Hà Nội', '2025-04-06 16:00:00', '1988-03-06', 'user87@gmail.com', 'Nguyễn Thị An', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0990123456', '2025-04-06 16:00:00', 'thian2');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-081', 'USER'),
('user-082', 'USER'),
('user-083', 'USER'),
('user-084', 'USER'),
('user-085', 'USER'),
('user-086', 'USER'),
('user-087', 'USER'),
('user-088', 'USER'),
('user-089', 'USER'),
('user-090', 'USER');

-- =============================================
-- CHUNK 10: USERS 91-100
-- =============================================
INSERT INTO users (user_id, address, created_at, dob, email, full_name, gender, is_active, password, phone, updated_at, username) VALUES
('user-091', '56 Hoàn Kiếm, Hà Nội', '2025-04-07 10:00:00', '1994-06-19', 'user88@gmail.com', 'Trần Văn Bình', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0991234567', '2025-04-07 10:00:00', 'vanbinh2'),
('user-092', '67 Ba Đình, Hà Nội', '2025-04-08 13:30:00', '1991-09-02', 'user89@gmail.com', 'Lê Thị Cẩm', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0992345678', '2025-04-08 13:30:00', 'thicam2'),
('user-093', '78 Tây Hồ, Hà Nội', '2025-04-09 09:45:00', '1998-01-15', 'user90@gmail.com', 'Phạm Văn Đức', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0993456789', '2025-04-09 09:45:00', 'vanduc'),
('user-094', '89 Cầu Giấy, Hà Nội', '2025-04-10 12:15:00', '1993-04-28', 'user91@gmail.com', 'Võ Thị Em', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0994567890', '2025-04-10 12:15:00', 'thiem2'),
('user-095', '100 Đống Đa, Hà Nội', '2025-04-11 15:00:00', '1996-08-10', 'user92@gmail.com', 'Hoàng Văn Phong', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0995678901', '2025-04-11 15:00:00', 'vanphong'),
('user-096', '111 Hai Bà Trưng, Hà Nội', '2025-04-12 08:00:00', '1989-11-23', 'user93@gmail.com', 'Bùi Thị Giang', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0996789012', '2025-04-12 08:00:00', 'thigiang'),
('user-097', '122 Long Biên, Hà Nội', '2025-04-13 11:30:00', '1995-02-05', 'user94@gmail.com', 'Đặng Văn Hiếu', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0997890123', '2025-04-13 11:30:00', 'vanhieu'),
('user-098', '133 Thanh Xuân, Hà Nội', '2025-04-14 14:45:00', '1992-06-18', 'user95@gmail.com', 'Ngô Thị Ích', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0998901234', '2025-04-14 14:45:00', 'thiich2'),
('user-099', '144 Hà Đông, Hà Nội', '2025-04-15 10:00:00', '1997-09-01', 'user96@gmail.com', 'Trương Văn Kiên', 'MALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0999012345', '2025-04-15 10:00:00', 'vankien'),
('user-100', '155 Nam Từ Liêm, Hà Nội', '2025-04-16 13:00:00', '1988-12-14', 'user97@gmail.com', 'Cao Thị Loan', 'FEMALE', 1, '$2a$10$lc6FNmgsJedsqTz/9RxBEuADsRrcL5E1YaDZ4YOLQG1PWdSV2lPQW', '0900012345', '2025-04-16 13:00:00', 'thiloan');

INSERT INTO users_roles (user_id, role_name) VALUES
('user-091', 'USER'),
('user-092', 'USER'),
('user-093', 'USER'),
('user-094', 'USER'),
('user-095', 'USER'),
('user-096', 'USER'),
('user-097', 'USER'),
('user-098', 'USER'),
('user-099', 'USER'),
('user-100', 'USER');

-- =============================================
-- TOURS CHUNK 1: TOURS 1-25
-- =============================================
INSERT INTO tours (tour_id, availability, capacity, category, description, destination, duration, end_date, is_active, itinerary, price_adult, price_child, region, start_date, title, version, staff_id) VALUES
('tour-001', 30, 30, 'BEACH', 'Khám phá những bãi biển tuyệt đẹp của đảo Phú Quốc với làn nước trong vắt và bãi cát trắng.', 'Phú Quốc', '3 ngày 2 đêm', '2026-06-30', 1, 'Ngày 1: Đến nơi, thư giãn trên bãi biển\nNgày 2: Tham quan các đảo, lặn biển\nNgày 3: Khởi hành về', 2500000.00, 1250000.00, 'SOUTH', '2026-03-01', 'Kỳ nghỉ thiên đường biển Phú Quốc', 0, 'user-002'),
('tour-002', 25, 25, 'CULTURE', 'Khám phá kinh thành Huế cổ kính và di sản văn hóa phong phú.', 'Huế', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Đại Nội, Chùa Thiên Mụ\nNgày 2: Lăng tẩm Hoàng gia, khởi hành về', 1800000.00, 900000.00, 'CENTRAL', '2026-01-15', 'Tour Di sản Cố đô Huế', 0, 'user-003'),
('tour-003', 20, 20, 'ADVENTURE', 'Đi bộ xuyên qua những ruộng bậc thang và bản làng dân tộc tại Sapa.', 'Sapa', '4 ngày 3 đêm', '2026-10-31', 1, 'Ngày 1: Đến nơi, bản Cát Cát\nNgày 2: Đi bộ thung lũng Mường Hoa\nNgày 3: Bản Tả Van\nNgày 4: Khởi hành về', 3200000.00, 1600000.00, 'NORTH', '2026-04-01', 'Phiêu lưu vùng cao Sapa', 0, 'user-002'),
('tour-004', 40, 40, 'CITY', 'Trải nghiệm cuộc sống về đêm sôi động và ẩm thực đường phố tại TP. Hồ Chí Minh.', 'TP. Hồ Chí Minh', '1 ngày', '2026-12-31', 1, 'Sáng: Địa đạo Củ Chi\nChiều: Tham quan thành phố\nTối: Tour ẩm thực đường phố', 1200000.00, 600000.00, 'SOUTH', '2026-01-01', 'Khám phá thành phố Sài Gòn', 0, 'user-003'),
('tour-005', 15, 15, 'NATURE', 'Du ngoạn qua những núi đá vôi hùng vĩ của Vịnh Hạ Long.', 'Vịnh Hạ Long', '2 ngày 1 đêm', '2026-11-30', 1, 'Ngày 1: Du thuyền, thăm hang động, chèo thuyền kayak\nNgày 2: Ngắm bình minh, khởi hành về', 4500000.00, 2250000.00, 'NORTH', '2026-02-01', 'Du thuyền cao cấp Hạ Long', 0, 'user-002'),
('tour-006', 35, 35, 'FOOD', 'Hành trình ẩm thực qua phố cổ Hội An.', 'Hội An', '1 ngày', '2026-12-31', 1, 'Sáng: Tham quan chợ, lớp học nấu ăn\nChiều: Tour đi bộ thưởng thức ẩm thực', 950000.00, 475000.00, 'CENTRAL', '2026-01-01', 'Khám phá ẩm thực Hội An', 0, 'user-003'),
('tour-007', 25, 25, 'EXPLORATION', 'Khám phá những hang động bí ẩn và sông ngầm tại Phong Nha.', 'Phong Nha', '3 ngày 2 đêm', '2026-09-30', 1, 'Ngày 1: Động Thiên Đường\nNgày 2: Động Phong Nha, đi thuyền\nNgày 3: Hang Tối, đu dây (zip-line)', 2800000.00, 1400000.00, 'CENTRAL', '2026-03-15', 'Thám hiểm hang động Phong Nha', 0, 'user-002'),
('tour-008', 30, 30, 'BEACH', 'Thư giãn trên những bãi biển nguyên sơ của Nha Trang với các môn thể thao dưới nước.', 'Nha Trang', '4 ngày 3 đêm', '2026-08-31', 1, 'Ngày 1: Đến nơi\nNgày 2: Tham quan đảo\nNgày 3: Lặn biển, spa\nNgày 4: Khởi hành về', 3500000.00, 1750000.00, 'CENTRAL', '2026-05-01', 'Khu nghỉ dưỡng biển Nha Trang', 0, 'user-003'),
('tour-009', 20, 20, 'CULTURE', 'Tham quan thủ đô Hà Nội lịch sử và di sản nghìn năm văn hiến.', 'Hà Nội', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Phố cổ, Lăng Bác\nNgày 2: Văn Miếu, Múa rối nước', 1600000.00, 800000.00, 'NORTH', '2026-01-10', 'Khám phá di sản Hà Nội', 0, 'user-002'),
('tour-010', 18, 18, 'ADVENTURE', 'Đi xe máy qua cung đường vòng Hà Giang tuyệt đẹp.', 'Hà Giang', '5 ngày 4 đêm', '2026-10-15', 1, 'Ngày 1-5: Hành trình xe máy hùng vĩ qua các đèo núi và bản làng dân tộc', 5500000.00, 2750000.00, 'NORTH', '2026-04-15', 'Phượt xe máy Hà Giang', 0, 'user-003'),
('tour-011', 45, 45, 'CITY', 'Khám phá các điểm tham quan hiện đại của Đà Nẵng và các kỳ quan lân cận.', 'Đà Nẵng', '3 ngày 2 đêm', '2026-12-31', 1, 'Ngày 1: Cầu Vàng, Bà Nà Hills\nNgày 2: Ngũ Hành Sơn, Biển Mỹ Khê\nNgày 3: Cầu Rồng, khởi hành về', 2200000.00, 1100000.00, 'CENTRAL', '2026-02-01', 'Tour khám phá Đà Nẵng', 0, 'user-002'),
('tour-012', 22, 22, 'NATURE', 'Khám phá cao nguyên Đà Lạt tuyệt đẹp với vườn hoa và thác nước.', 'Đà Lạt', '3 ngày 2 đêm', '2026-11-30', 1, 'Ngày 1: Vườn hoa, Biệt thự Hằng Nga (Crazy House)\nNgày 2: Thác nước, đồi chè cà phê\nNgày 3: Thung lũng Tình yêu, khởi hành về', 2400000.00, 1200000.00, 'CENTRAL', '2026-03-01', 'Kỳ nghỉ cao nguyên Đà Lạt', 0, 'user-003'),
('tour-013', 28, 28, 'FOOD', 'Trải nghiệm ẩm thực đường phố và nhà hàng cao cấp tại TP. Hồ Chí Minh.', 'TP. Hồ Chí Minh', '1 ngày', '2026-12-31', 1, 'Sáng: Tham quan chợ\nChiều: Lớp học nấu ăn\nTối: Trải nghiệm ăn tối sang trọng', 1100000.00, 550000.00, 'SOUTH', '2026-01-01', 'Tour ẩm thực Sài Gòn', 0, 'user-002'),
('tour-014', 16, 16, 'EXPLORATION', 'Khám phá chợ nổi và sông nước miền Tây.', 'Cần Thơ', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Chợ nổi Cái Răng, vườn trái cây\nNgày 2: Kênh rạch, làng quê', 1900000.00, 950000.00, 'SOUTH', '2026-01-15', 'Phiêu lưu Đồng bằng sông Cửu Long', 0, 'user-003'),
('tour-015', 32, 32, 'BEACH', 'Tắm nắng và lướt sóng tại những bãi biển xinh đẹp của Vũng Tàu.', 'Vũng Tàu', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Bãi Sau, hải đăng\nNgày 2: Bãi Trước, hải sản, khởi hành về', 1500000.00, 750000.00, 'SOUTH', '2026-02-01', 'Nghỉ dưỡng biển Vũng Tàu', 0, 'user-002'),
('tour-016', 24, 24, 'CULTURE', 'Cảm nhận vẻ đẹp quyến rũ của phố cổ Hội An.', 'Hội An', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Đi bộ tham quan phố cổ, làm đèn lồng\nNgày 2: Làng rau Trà Quế, khởi hành về', 1700000.00, 850000.00, 'CENTRAL', '2026-01-20', 'Trải nghiệm Phố cổ Hội An', 0, 'user-003'),
('tour-017', 12, 12, 'ADVENTURE', 'Chinh phục nóc nhà Đông Dương - Đỉnh Fansipan.', 'Sapa', '2 ngày 1 đêm', '2026-09-30', 1, 'Ngày 1: Cáp treo, đỉnh Fansipan\nNgày 2: Bản làng, khởi hành về', 2900000.00, 1450000.00, 'NORTH', '2026-05-01', 'Chinh phục đỉnh Fansipan', 0, 'user-002'),
('tour-018', 38, 38, 'CITY', 'Sự pha trộn giữa hiện đại và truyền thống trong tour tham quan Hà Nội.', 'Hà Nội', '1 ngày', '2026-12-31', 1, 'Cả ngày: Phố cổ, bảo tàng, hồ, ẩm thực đường phố', 800000.00, 400000.00, 'NORTH', '2026-01-01', 'Điểm nổi bật thành phố Hà Nội', 0, 'user-003'),
('tour-019', 26, 26, 'NATURE', 'Khám phá Thác Bản Giốc tuyệt đẹp trên biên giới Việt - Trung.', 'Cao Bằng', '3 ngày 2 đêm', '2026-10-31', 1, 'Ngày 1: Di chuyển đến Cao Bằng\nNgày 2: Thác Bản Giốc, Động Ngườm Ngao\nNgày 3: Trở về', 2600000.00, 1300000.00, 'NORTH', '2026-04-01', 'Hành trình Thác Bản Giốc', 0, 'user-002'),
('tour-020', 20, 20, 'FOOD', 'Thưởng thức những món ngon nhất của ẩm thực miền Trung tại Huế.', 'Huế', '1 ngày', '2026-12-31', 1, 'Sáng: Tham quan chợ\nChiều: Thưởng thức ẩm thực cung đình\nTối: Tour ẩm thực đường phố', 880000.00, 440000.00, 'CENTRAL', '2026-01-01', 'Tour Ẩm thực Cung đình Huế', 0, 'user-003'),
('tour-021', 30, 30, 'EXPLORATION', 'Hành trình đến vẻ đẹp hoang sơ của ruộng bậc thang Mù Cang Chải.', 'Mù Cang Chải', '3 ngày 2 đêm', '2026-10-15', 1, 'Ngày 1: Di chuyển, bản làng địa phương\nNgày 2: Đi bộ ngắm ruộng bậc thang\nNgày 3: Trở về', 2700000.00, 1350000.00, 'NORTH', '2026-09-01', 'Ruộng bậc thang Mù Cang Chải', 0, 'user-002'),
('tour-022', 35, 35, 'BEACH', 'Trải nghiệm thiên đường đảo tại Côn Đảo.', 'Côn Đảo', '4 ngày 3 đêm', '2026-11-30', 1, 'Ngày 1: Bay đến, tắm biển\nNgày 2-3: Lặn biển, di tích lịch sử\nNgày 4: Khởi hành về', 5800000.00, 2900000.00, 'SOUTH', '2026-03-15', 'Trốn nóng đảo Côn Đảo', 0, 'user-003'),
('tour-023', 22, 22, 'CULTURE', 'Khám phá văn hóa Chăm và Thánh địa Mỹ Sơn.', 'Quảng Nam', '1 ngày', '2026-12-31', 1, 'Cả ngày: Di tích Mỹ Sơn, biểu diễn văn hóa Chăm', 1400000.00, 700000.00, 'CENTRAL', '2026-01-01', 'Di sản Chăm Mỹ Sơn', 0, 'user-002'),
('tour-024', 18, 18, 'ADVENTURE', 'Chèo thuyền kayak và leo núi tại đảo Cát Bà.', 'Cát Bà', '3 ngày 2 đêm', '2026-10-31', 1, 'Ngày 1: Đến nơi, chèo kayak\nNgày 2: Leo núi, Vịnh Lan Hạ\nNgày 3: Đi bộ đường dài (trekking), khởi hành về', 3100000.00, 1550000.00, 'NORTH', '2026-05-15', 'Phiêu lưu Đảo Cát Bà', 0, 'user-003'),
('tour-025', 40, 40, 'CITY', 'Gói trải nghiệm toàn diện TP. Hồ Chí Minh.', 'TP. Hồ Chí Minh', '3 ngày 2 đêm', '2026-12-31', 1, 'Ngày 1: Lịch sử chiến tranh\nNgày 2: Chợ, Phố người Hoa\nNgày 3: Củ Chi, khởi hành về', 2000000.00, 1000000.00, 'SOUTH', '2026-01-01', 'Trải nghiệm trọn vẹn Sài Gòn', 0, 'user-002');

-- =============================================
-- TOURS CHUNK 2: TOURS 26-50
-- =============================================
INSERT INTO tours (tour_id, availability, capacity, category, description, destination, duration, end_date, is_active, itinerary, price_adult, price_child, region, start_date, title, version, staff_id) VALUES
('tour-026', 28, 28, 'NATURE', 'Khám phá những khu rừng nguyên sinh của Vườn quốc gia Cúc Phương.', 'Ninh Bình', '2 ngày 1 đêm', '2026-11-30', 1, 'Ngày 1: Trung tâm cứu hộ linh trưởng, cây cổ thụ\nNgày 2: Thăm hang động, khởi hành về', 1850000.00, 925000.00, 'NORTH', '2026-03-01', 'Khu bảo tồn thiên nhiên Cúc Phương', 0, 'user-003'),
('tour-027', 32, 32, 'FOOD', 'Thưởng thức hương vị độc đáo của ẩm thực Quảng Bình.', 'Quảng Bình', '1 ngày', '2026-12-31', 1, 'Cả ngày: Chợ địa phương, nấu ăn truyền thống, tiệc hải sản', 780000.00, 390000.00, 'CENTRAL', '2026-01-01', 'Hành trình ẩm thực Quảng Bình', 0, 'user-002'),
('tour-028', 24, 24, 'EXPLORATION', 'Khám phá những viên ngọc ẩn giấu của Ninh Bình - Quần thể Tràng An.', 'Ninh Bình', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Đi thuyền Tràng An, Chùa Bái Đính\nNgày 2: Tam Cốc, Hang Múa', 2100000.00, 1050000.00, 'NORTH', '2026-02-15', 'Khám phá di sản Ninh Bình', 0, 'user-003'),
('tour-029', 20, 20, 'BEACH', 'Kỳ nghỉ yên bình trên những bãi biển núi lửa đảo Lý Sơn.', 'Quảng Ngãi', '3 ngày 2 đêm', '2026-09-30', 1, 'Ngày 1: Tàu cao tốc, tắm biển\nNgày 2: Cảnh quan núi lửa, chùa chiền\nNgày 3: Khởi hành về', 2950000.00, 1475000.00, 'CENTRAL', '2026-04-01', 'Nghỉ dưỡng đảo Lý Sơn', 0, 'user-002'),
('tour-030', 36, 36, 'CULTURE', 'Trải nghiệm cuộc sống làng quê Việt Nam truyền thống tại Đường Lâm.', 'Hà Nội', '1 ngày', '2026-12-31', 1, 'Cả ngày: Làng cổ, nhà truyền thống, ăn trưa địa phương', 720000.00, 360000.00, 'NORTH', '2026-01-01', 'Làng cổ Đường Lâm', 0, 'user-003'),
('tour-031', 15, 15, 'ADVENTURE', 'Chèo thuyền vượt thác tại cao nguyên Đà Lạt.', 'Đà Lạt', '1 ngày', '2026-10-31', 1, 'Cả ngày: Hướng dẫn an toàn, phiêu lưu vượt thác, dừng chân tại thác nước', 1650000.00, 825000.00, 'CENTRAL', '2026-05-01', 'Phiêu lưu vượt thác Đà Lạt', 0, 'user-002'),
('tour-032', 42, 42, 'CITY', 'Khám phá Hoàng thành Thăng Long cổ kính tại Hà Nội.', 'Hà Nội', '1 ngày', '2026-12-31', 1, 'Cả ngày: Hoàng thành, khu khảo cổ, bảo tàng', 650000.00, 325000.00, 'NORTH', '2026-01-01', 'Tham quan Hoàng thành Thăng Long', 0, 'user-003'),
('tour-033', 26, 26, 'NATURE', 'Ngắm chim và du lịch sinh thái tại Vườn quốc gia U Minh Thượng.', 'Kiên Giang', '2 ngày 1 đêm', '2026-11-30', 1, 'Ngày 1: Khám phá vùng đất ngập nước, ngắm chim\nNgày 2: Rừng tràm, khởi hành về', 2300000.00, 1150000.00, 'SOUTH', '2026-03-15', 'Phiêu lưu sinh thái U Minh', 0, 'user-002'),
('tour-034', 30, 30, 'FOOD', 'Trải nghiệm thiên đường hải sản tại Phan Thiết.', 'Phan Thiết', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Thăm làng chài, ăn tối hải sản\nNgày 2: Nhà thùng nước mắm, chợ', 1450000.00, 725000.00, 'SOUTH', '2026-01-15', 'Tour hải sản Phan Thiết', 0, 'user-003'),
('tour-035', 22, 22, 'EXPLORATION', 'Hành trình qua cung đường cà phê Tây Nguyên.', 'Buôn Ma Thuột', '3 ngày 2 đêm', '2026-10-31', 1, 'Ngày 1: Đồn điền cà phê\nNgày 2: Thác Dray Nur, Buôn Đôn (làng voi)\nNgày 3: Trở về', 2550000.00, 1275000.00, 'CENTRAL', '2026-04-01', 'Cung đường cà phê Tây Nguyên', 0, 'user-002'),
('tour-036', 38, 38, 'BEACH', 'Trải nghiệm đồi cát và bãi biển Mũi Né.', 'Phan Thiết', '3 ngày 2 đêm', '2026-12-31', 1, 'Ngày 1: Đồi cát đỏ\nNgày 2: Đồi cát trắng, Suối Tiên\nNgày 3: Tắm biển, khởi hành về', 2200000.00, 1100000.00, 'SOUTH', '2026-02-01', 'Combo Biển và Đồi cát Mũi Né', 0, 'user-003'),
('tour-037', 18, 18, 'CULTURE', 'Khám phá di sản Khmer tại Trà Vinh.', 'Trà Vinh', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Chùa Khmer, Ao Bà Om\nNgày 2: Làng nghề truyền thống, khởi hành về', 1580000.00, 790000.00, 'SOUTH', '2026-01-20', 'Di sản Khmer Trà Vinh', 0, 'user-002'),
('tour-038', 25, 25, 'ADVENTURE', 'Đu dây vượt thác (Canyoning) tại Đà Lạt.', 'Đà Lạt', '1 ngày', '2026-09-30', 1, 'Cả ngày: Vượt thác, nhảy vách đá, đu dây xuống thác', 1950000.00, 975000.00, 'CENTRAL', '2026-05-15', 'Thử thách vượt thác Đà Lạt', 0, 'user-003'),
('tour-039', 34, 34, 'CITY', 'Khám phá di sản kiến trúc Pháp tại thành phố Đà Lạt.', 'Đà Lạt', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Biệt thự cổ, Nhà ga xe lửa\nNgày 2: Dinh Bảo Đại, chợ', 1750000.00, 875000.00, 'CENTRAL', '2026-01-01', 'Di sản kiến trúc Đà Lạt', 0, 'user-002'),
('tour-040', 20, 20, 'NATURE', 'Bãi biển hoang sơ và lặn ngắm san hô tại Đảo Bình Ba.', 'Khánh Hòa', '2 ngày 1 đêm', '2026-10-31', 1, 'Ngày 1: Tàu ra đảo, tắm biển, lặn biển\nNgày 2: Thăm bè nuôi tôm hùm, khởi hành về', 2650000.00, 1325000.00, 'CENTRAL', '2026-04-15', 'Khám phá Đảo Bình Ba', 0, 'user-003'),
('tour-041', 28, 28, 'FOOD', 'Tour đi bộ thưởng thức Phở và ẩm thực đường phố Hà Nội.', 'Hà Nội', '1 ngày', '2026-12-31', 1, 'Sáng: Thưởng thức Phở, Phố cổ\nChiều: Bún chả, cà phê trứng\nTối: Chợ đêm', 550000.00, 275000.00, 'NORTH', '2026-01-01', 'Tour ẩm thực đường phố Hà Nội', 0, 'user-002'),
('tour-042', 16, 16, 'EXPLORATION', 'Khám phá làng nổi và rừng tràm tại An Giang.', 'An Giang', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Làng nổi, Rừng tràm Trà Sư\nNgày 2: Núi Sam, khởi hành về', 1880000.00, 940000.00, 'SOUTH', '2026-02-01', 'Cuộc sống sông nước An Giang', 0, 'user-003'),
('tour-043', 40, 40, 'BEACH', 'Trải nghiệm nghỉ dưỡng trọn gói tại Quy Nhơn.', 'Quy Nhơn', '4 ngày 3 đêm', '2026-11-30', 1, 'Ngày 1-4: Thư giãn bãi biển, Kỳ Co, Eo Gió, trị liệu spa', 4200000.00, 2100000.00, 'CENTRAL', '2026-03-01', 'Thiên đường biển Quy Nhơn', 0, 'user-002'),
('tour-044', 24, 24, 'CULTURE', 'Hành trình hành hương Chùa Hương.', 'Hà Nội', '1 ngày', '2026-12-31', 1, 'Cả ngày: Đi thuyền, cáp treo, quần thể chùa, làm lễ chiêm bái', 1150000.00, 575000.00, 'NORTH', '2026-01-15', 'Hành hương Chùa Hương', 0, 'user-003'),
('tour-045', 14, 14, 'ADVENTURE', 'Dù lượn trên ruộng bậc thang Mù Cang Chải.', 'Mù Cang Chải', '2 ngày 1 đêm', '2026-10-15', 1, 'Ngày 1: Huấn luyện, chuyến bay đầu tiên\nNgày 2: Bay đường dài, chụp ảnh', 3800000.00, 1900000.00, 'NORTH', '2026-09-15', 'Dù lượn Mù Cang Chải', 0, 'user-002'),
('tour-046', 36, 36, 'CITY', 'Trải nghiệm thành phố Cần Thơ và chợ nổi.', 'Cần Thơ', '2 ngày 1 đêm', '2026-12-31', 1, 'Ngày 1: Tham quan thành phố, chợ đêm\nNgày 2: Chợ nổi Cái Răng, khởi hành về', 1380000.00, 690000.00, 'SOUTH', '2026-01-01', 'Khám phá thành phố Cần Thơ', 0, 'user-003'),
('tour-047', 22, 22, 'NATURE', 'Thám hiểm động vật hoang dã Vườn quốc gia Cát Tiên.', 'Đồng Nai', '3 ngày 2 đêm', '2026-11-30', 1, 'Ngày 1: Đến nơi, xem thú đêm\nNgày 2: Trạm cứu hộ gấu, Bàu Sấu\nNgày 3: Ngắm chim, khởi hành về', 2850000.00, 1425000.00, 'SOUTH', '2026-04-01', 'Safari hoang dã Cát Tiên', 0, 'user-002'),
('tour-048', 30, 30, 'FOOD', 'Hội thảo làm Bánh Mì Việt Nam.', 'TP. Hồ Chí Minh', '1 ngày', '2026-12-31', 1, 'Sáng: Đi chợ\nChiều: Lớp học làm Bánh Mì, nếm thử', 680000.00, 340000.00, 'SOUTH', '2026-01-01', 'Lớp học làm Bánh Mì', 0, 'user-003'),
('tour-049', 26, 26, 'EXPLORATION', 'Khám phá vùng hang động Sơn Đoòng (các hang lân cận).', 'Quảng Bình', '3 ngày 2 đêm', '2026-09-30', 1, 'Ngày 1: Hang Tú Làn\nNgày 2: Cắm trại Hang Én\nNgày 3: Ra khỏi hang, khởi hành về', 6500000.00, 3250000.00, 'CENTRAL', '2026-05-01', 'Thám hiểm hang động Quảng Bình', 0, 'user-002'),
('tour-050', 35, 35, 'BEACH', 'Kỳ nghỉ thư giãn tại thiên đường biển Phú Yên.', 'Phú Yên', '3 ngày 2 đêm', '2026-12-31', 1, 'Ngày 1: Gành Đá Đĩa, tắm biển\nNgày 2: Vịnh Vũng Rô, Núi Đá Bia\nNgày 3: Khởi hành về', 2350000.00, 1175000.00, 'CENTRAL', '2026-02-15', 'Kỳ nghỉ biển Phú Yên', 0, 'user-003');

-- =============================================
-- TOUR IMAGES (Sample images for tours)
-- =============================================
INSERT INTO tour_images (image_id, description, image_url, tour_id) VALUES
('img-001', 'Biển hoàng hôn Phú Quốc', 'https://images.unsplash.com/photo-1634480231310-6713653aab61', 'tour-001'),
('img-002', 'Kinh đô Huế', 'https://images.unsplash.com/photo-1761150284400-35d6fa7ab9d6', 'tour-002'),
('img-003', 'Sapa lúa cỏ', 'https://images.unsplash.com/photo-1570366583862-f91883984fde', 'tour-003'),
('img-004', 'Cao ốc Hồ Chí Minh', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab', 'tour-004'),
('img-005', 'Du thuyền Hạ Long', 'https://images.unsplash.com/photo-1732272933438-e04c490a293d', 'tour-005'),
('img-006', 'Lồng đèn Hội An', 'https://images.unsplash.com/photo-1563569612597-0d441cb5d203', 'tour-006'),
('img-007', 'Động Phong Nha', 'https://images.unsplash.com/photo-1719461208440-ae18bcc471bb', 'tour-007'),
('img-008', 'Đảo Nha Trang', 'https://images.unsplash.com/photo-1627613263750-08049d862e04', 'tour-008'),
('img-009', 'Phố cổ Hà Nội', 'https://images.unsplash.com/photo-1764959136028-104ebd1d3f20', 'tour-009'),
('img-010', 'Đèo Hà Giang', 'https://images.unsplash.com/photo-1536511671359-849531c0a576', 'tour-010');

-- =============================================
-- TOUR IMAGES CHUNK 2: TOURS 11-30
-- =============================================
INSERT INTO tour_images (image_id, description, image_url, tour_id) VALUES
('img-011', 'Cầu Vàng Bà Nà Hills', 'https://images.unsplash.com/photo-1729475979660-dad41de16cb9', 'tour-011'),
('img-012', 'Thác nước Đà Lạt hùng vĩ', 'https://images.unsplash.com/photo-1693966717905-6e196761ec60', 'tour-012'),
('img-013', 'Bánh mì Sài Gòn đặc biệt', 'https://images.unsplash.com/photo-1662490881538-dc9419784bc3', 'tour-013'),
('img-014', 'Chợ nổi Cái Răng tấp nập', 'https://images.unsplash.com/photo-1705589244475-7ebbc0d3a842', 'tour-014'),
('img-015', 'Ngọn hải đăng Vũng Tàu', 'https://images.unsplash.com/photo-1522679056866-8dbbc8774a9d', 'tour-015'),
('img-016', 'Chùa Cầu Hội An', 'https://images.unsplash.com/photo-1758178673635-ce32cd04858c', 'tour-016'),
('img-017', 'Biển mây trên đỉnh Fansipan', 'https://images.unsplash.com/photo-1694486184022-1f53243df8d0', 'tour-017'),
('img-018', 'Văn Miếu Quốc Tử Giám', 'https://images.unsplash.com/photo-1627785740415-278ba9cc393a', 'tour-018'),
('img-019', 'Thác Bản Giốc mùa nước đổ', 'https://images.unsplash.com/photo-1643704899105-82bf12dc9088', 'tour-019'),
('img-020', 'Mâm cơm cung đình Huế', 'https://images.unsplash.com/photo-1535924681871-7bde1c30fe1d', 'tour-020'),
('img-021', 'Mùa lúa chín Mù Cang Chải', 'https://images.unsplash.com/photo-1695203253181-47ef1a72c58c', 'tour-021'),
('img-022', 'Bãi biển Côn Đảo hoang sơ', 'https://images.unsplash.com/photo-1710943010177-1f7d2bee458e', 'tour-022'),
('img-023', 'Thánh địa Mỹ Sơn cổ kính', 'https://images.unsplash.com/photo-1701707858660-0a40ccdb18d5', 'tour-023'),
('img-024', 'Vịnh Lan Hạ Cát Bà', 'https://images.unsplash.com/photo-1722601505317-428fb18a4ae6', 'tour-024'),
('img-025', 'Nhà thờ Đức Bà Sài Gòn', 'https://images.unsplash.com/photo-1576653764709-7d175299e6d9', 'tour-025'),
('img-026', 'Đường mòn rừng Cúc Phương', 'https://images.unsplash.com/photo-1599933595372-bb876160e63b', 'tour-026'),
('img-027', 'Hang Sơn Đoòng kỳ vĩ', 'https://images.unsplash.com/photo-1681350336257-4de2646e3eeb', 'tour-027'),
('img-028', 'Bến thuyền Tràng An', 'https://images.unsplash.com/photo-1626743656249-5d8fa287b941', 'tour-028'),
('img-029', 'Cổng Tò Vò đảo Lý Sơn', 'https://images.unsplash.com/photo-1581994948860-e52bb04aafa6', 'tour-029'),
('img-030', 'Cổng làng cổ Đường Lâm', 'https://images.unsplash.com/photo-1608753529548-3898cb559f48', 'tour-030');

-- =============================================
-- TOUR IMAGES CHUNK 3: TOURS 31-50
-- =============================================
INSERT INTO tour_images (image_id, description, image_url, tour_id) VALUES
('img-031', 'Chèo thuyền vượt thác Đà Lạt', 'https://images.unsplash.com/photo-1530866495561-507c9faab2ed', 'tour-031'),
('img-032', 'Hoàng thành Thăng Long cổ kính', 'https://images.unsplash.com/photo-1758798757841-6c4216038069', 'tour-032'),
('img-033', 'Rừng ngập mặn U Minh Thượng', 'https://images.unsplash.com/photo-1739612548624-f92c3f9bdf58', 'tour-033'),
('img-034', 'Hải sản Phan Thiết tươi ngon', 'https://images.unsplash.com/photo-1759853680061-050794cdf480', 'tour-034'),
('img-035', 'Vườn cà phê Buôn Ma Thuột bạt ngàn', 'https://images.unsplash.com/photo-1673280167171-d2a3303dff1f', 'tour-035'),
('img-036', 'Đồi cát trắng Mũi Né', 'https://images.unsplash.com/photo-1587890799275-ba5614bf3d2b', 'tour-036'),
('img-037', 'Kiến trúc Chùa Khmer Trà Vinh', 'https://images.unsplash.com/photo-1739498502986-5360cfb18628', 'tour-037'),
('img-038', 'Thử thách đu dây vượt thác Datanla', 'https://images.unsplash.com/photo-1762251477949-36ea3a770780', 'tour-038'),
('img-039', 'Biệt thự kiến trúc Pháp tại Đà Lạt', 'https://images.unsplash.com/photo-1577171322136-e0f38df8e2f1', 'tour-039'),
('img-040', 'Đặc sản tôm hùm Đảo Bình Ba', 'https://images.unsplash.com/photo-1672858502422-ab27ac933910', 'tour-040'),
('img-041', 'Phở bò gia truyền Hà Nội', 'https://images.unsplash.com/photo-1718942900361-d01a1ee8d077', 'tour-041'),
('img-042', 'Rừng tràm Trà Sư mùa nước nổi', 'https://images.unsplash.com/photo-1580630873708-e0475b1856c4', 'tour-042'),
('img-043', 'Eo Gió Quy Nhơn hùng vĩ', 'https://images.unsplash.com/photo-1603030595909-bd702543c190', 'tour-043'),
('img-044', 'Suối Yến đường vào Chùa Hương', 'https://images.unsplash.com/photo-1756821771911-67d194a96dd7', 'tour-044'),
('img-045', 'Bay dù lượn trên mùa vàng Mù Cang Chải', 'https://images.unsplash.com/photo-1665477116539-0bab93c41e87', 'tour-045'),
('img-046', 'Bến Ninh Kiều Cần Thơ về đêm', 'https://images.unsplash.com/photo-1680711211916-195a00308786', 'tour-046'),
('img-047', 'Khám phá rừng Nam Cát Tiên', 'https://images.unsplash.com/photo-1696780465546-2ab5288251d2', 'tour-047'),
('img-048', 'Ổ bánh mì Việt Nam giòn rụm', 'https://images.unsplash.com/photo-1562147600-ee6e0707973b', 'tour-048'),
('img-049', 'Cắm trại trong hang động Quảng Bình', 'https://images.unsplash.com/photo-1696215106449-5058da0ca1b6', 'tour-049'),
('img-050', 'Gành Đá Đĩa Phú Yên độc đáo', 'https://images.unsplash.com/photo-1662622600433-f31acfd11e04', 'tour-050');

-- =============================================
-- BOOKINGS (30 sample bookings)
-- =============================================
INSERT INTO bookings (booking_id, booking_date, num_adults, num_children, phone, special_request, status, total_price, promotion_id, staff_id, tour_id, user_id) VALUES
('book-001', '2025-12-01 10:00:00', 2, 1, '0904567890', 'Vui lòng chuẩn bị món chay', 'COMPLETED', 6250000.000, NULL, NULL, 'tour-001', 'user-004'),
('book-002', '2025-12-02 11:00:00', 2, 0, '0905678901', NULL, 'COMPLETED', 3600000.000, NULL, NULL, 'tour-002', 'user-005'),
('book-003', '2025-12-03 14:00:00', 4, 2, '0906789012', 'Cần thêm hướng dẫn viên cho trẻ em', 'COMPLETED', 16000000.000, 'promo-001', NULL, 'tour-003', 'user-006'),
('book-004', '2025-12-05 09:30:00', 2, 0, '0907890123', NULL, 'COMPLETED', 2400000.000, NULL, NULL, 'tour-004', 'user-007'),
('book-005', '2025-12-06 15:00:00', 2, 2, '0908901234', 'Thuốc chống say sóng', 'COMPLETED', 13500000.000, NULL, NULL, 'tour-005', 'user-008'),
('book-006', '2025-12-08 10:30:00', 3, 0, '0909012345', NULL, 'CONFIRMED', 2850000.000, NULL, NULL, 'tour-006', 'user-009'),
('book-007', '2025-12-10 11:00:00', 2, 1, '0910123456', NULL, 'CONFIRMED', 7000000.000, NULL, NULL, 'tour-007', 'user-010'),
('book-008', '2025-12-12 08:00:00', 4, 0, '0911234567', 'Phòng hướng biển', 'PENDING', 14000000.000, 'promo-002', NULL, 'tour-008', 'user-011'),
('book-009', '2025-12-14 09:00:00', 2, 0, '0912345678', NULL, 'CONFIRMED', 3200000.000, NULL, NULL, 'tour-009', 'user-012'),
('book-010', '2025-12-15 14:30:00', 1, 0, '0913456789', 'Người lái xe có kinh nghiệm', 'PENDING', 5500000.000, NULL, NULL, 'tour-010', 'user-013'),
('book-011', '2025-12-16 10:00:00', 2, 2, '0914567890', NULL, 'CONFIRMED', 6600000.000, NULL, 'user-002', 'tour-011', 'user-014'),
('book-012', '2025-12-17 11:30:00', 3, 1, '0915678901', 'Đam mê nhiếp ảnh', 'CONFIRMED', 8400000.000, NULL, NULL, 'tour-012', 'user-015'),
('book-013', '2025-12-18 13:00:00', 2, 0, '0916789012', 'Dị ứng thực phẩm - không ăn hải sản vỏ cứng', 'PENDING', 2200000.000, NULL, NULL, 'tour-013', 'user-016'),
('book-014', '2025-12-19 08:30:00', 4, 0, '0917890123', NULL, 'CONFIRMED', 7600000.000, 'promo-003', 'user-003', 'tour-014', 'user-017'),
('book-015', '2025-12-20 15:00:00', 2, 1, '0918901234', NULL, 'CONFIRMED', 3750000.000, NULL, NULL, 'tour-015', 'user-018'),
('book-016', '2025-12-21 09:45:00', 2, 0, '0919012345', 'Chuyến đi kỷ niệm ngày cưới', 'CONFIRMED', 3400000.000, NULL, NULL, 'tour-016', 'user-019'),
('book-017', '2025-12-22 11:00:00', 1, 0, '0920123456', 'Người leo núi có kinh nghiệm', 'PENDING', 2900000.000, NULL, NULL, 'tour-017', 'user-020'),
('book-018', '2025-12-23 10:30:00', 6, 0, '0921234567', 'Chuyến đi của công ty', 'CONFIRMED', 4800000.000, 'promo-004', 'user-002', 'tour-018', 'user-021'),
('book-019', '2025-12-24 14:00:00', 2, 0, '0922345678', NULL, 'CONFIRMED', 5200000.000, NULL, NULL, 'tour-019', 'user-022'),
('book-020', '2025-12-25 09:00:00', 4, 2, '0923456789', 'Chuyến đi gia đình', 'CONFIRMED', 4400000.000, NULL, NULL, 'tour-020', 'user-023'),
('book-021', '2025-12-26 13:30:00', 2, 0, '0924567890', NULL, 'PENDING', 5400000.000, NULL, NULL, 'tour-021', 'user-024'),
('book-022', '2025-12-27 11:00:00', 2, 2, '0925678901', 'Cần dụng cụ lặn biển', 'CONFIRMED', 17400000.000, NULL, NULL, 'tour-022', 'user-025'),
('book-023', '2025-12-28 15:30:00', 3, 0, '0926789012', NULL, 'CONFIRMED', 4200000.000, NULL, 'user-003', 'tour-023', 'user-026'),
('book-024', '2025-12-29 08:00:00', 2, 0, '0927890123', 'Người thích phiêu lưu', 'PENDING', 6200000.000, NULL, NULL, 'tour-024', 'user-027'),
('book-025', '2025-12-30 10:00:00', 4, 2, '0928901234', NULL, 'CONFIRMED', 10000000.000, 'promo-001', NULL, 'tour-025', 'user-028'),
('book-026', '2026-01-02 09:30:00', 2, 0, '0929012345', NULL, 'PENDING', 3700000.000, NULL, NULL, 'tour-026', 'user-029'),
('book-027', '2026-01-03 14:00:00', 3, 0, '0930123456', 'Người yêu thích tour ẩm thực', 'CONFIRMED', 2340000.000, NULL, NULL, 'tour-027', 'user-030'),
('book-028', '2026-01-04 11:30:00', 2, 1, '0931234567', NULL, 'CONFIRMED', 5250000.000, NULL, 'user-002', 'tour-028', 'user-031'),
('book-029', '2026-01-05 16:00:00', 2, 0, '0932345678', 'Người yêu biển', 'PENDING', 5900000.000, NULL, NULL, 'tour-029', 'user-032'),
('book-030', '2026-01-06 10:00:00', 5, 0, '0933456789', 'Nhóm tìm hiểu văn hóa', 'CONFIRMED', 3600000.000, 'promo-004', 'user-003', 'tour-030', 'user-033');

-- =============================================
-- BOOKINGS CHUNK 2: BOOKINGS 31-60
-- =============================================
INSERT INTO bookings (booking_id, booking_date, num_adults, num_children, phone, special_request, status, total_price, promotion_id, staff_id, tour_id, user_id) VALUES
('book-031', '2026-01-07 09:15:00', 4, 0, '0934567890', 'Yêu cầu hướng dẫn viên nói tiếng Anh', 'CONFIRMED', 6600000.000, NULL, NULL, 'tour-031', 'user-034'),
('book-032', '2026-01-08 14:30:00', 2, 2, '0935678901', NULL, 'COMPLETED', 1950000.000, NULL, NULL, 'tour-032', 'user-035'),
('book-033', '2026-01-09 10:00:00', 2, 0, '0936789012', 'Không ăn cay', 'PENDING', 4600000.000, NULL, NULL, 'tour-033', 'user-036'),
('book-034', '2026-01-09 11:45:00', 6, 1, '0937890123', 'Đặt bàn tiệc hải sản lớn', 'CONFIRMED', 9425000.000, 'promo-002', NULL, 'tour-034', 'user-037'),
('book-035', '2026-01-09 15:00:00', 2, 0, '0938901234', 'Thích cà phê rang xay nhẹ', 'CONFIRMED', 5100000.000, NULL, 'user-002', 'tour-035', 'user-038'),
('book-036', '2026-01-09 08:30:00', 3, 2, '0939012345', 'Cần xe đưa đón tại khách sạn', 'PENDING', 8800000.000, NULL, NULL, 'tour-036', 'user-039'),
('book-037', '2026-01-09 13:00:00', 2, 0, '0940123456', NULL, 'CONFIRMED', 3160000.000, NULL, NULL, 'tour-037', 'user-040'),
('book-038', '2026-01-09 09:00:00', 4, 0, '0941234567', 'Nhóm thích mạo hiểm', 'CONFIRMED', 7800000.000, 'promo-003', NULL, 'tour-038', 'user-041'),
('book-039', '2026-01-09 10:30:00', 2, 1, '0942345678', NULL, 'CANCELLED', 4375000.000, NULL, NULL, 'tour-039', 'user-042'),
('book-040', '2026-01-09 16:15:00', 2, 0, '0943456789', 'Dị ứng phấn hoa', 'CONFIRMED', 5300000.000, NULL, NULL, 'tour-040', 'user-043'),
('book-041', '2026-01-09 11:00:00', 1, 0, '0944567890', NULL, 'COMPLETED', 550000.000, NULL, NULL, 'tour-041', 'user-044'),
('book-042', '2026-01-09 14:00:00', 2, 2, '0945678901', 'Cần áo phao trẻ em', 'PENDING', 5640000.000, NULL, 'user-003', 'tour-042', 'user-045'),
('book-043', '2026-01-09 09:30:00', 2, 0, '0946789012', 'Gói tuần trăng mật', 'CONFIRMED', 8400000.000, NULL, NULL, 'tour-043', 'user-046'),
('book-044', '2026-01-09 08:00:00', 10, 0, '0947890123', 'Đoàn hành hương Phật giáo', 'CONFIRMED', 11500000.000, 'promo-001', NULL, 'tour-044', 'user-047'),
('book-045', '2026-01-09 12:30:00', 1, 0, '0948901234', 'Muốn thuê máy quay GoPro', 'PENDING', 3800000.000, NULL, NULL, 'tour-045', 'user-048'),
('book-046', '2026-01-09 15:30:00', 2, 1, '0949012345', NULL, 'CONFIRMED', 3450000.000, NULL, NULL, 'tour-046', 'user-049'),
('book-047', '2026-01-09 10:00:00', 2, 0, '0950123456', 'Yêu thích chụp ảnh thiên nhiên', 'CONFIRMED', 5700000.000, NULL, 'user-002', 'tour-047', 'user-050'),
('book-048', '2026-01-09 13:45:00', 4, 0, '0951234567', 'Không ăn rau mùi (ngò)', 'COMPLETED', 2720000.000, NULL, NULL, 'tour-048', 'user-051'),
('book-049', '2026-01-09 09:00:00', 6, 0, '0952345678', 'Đoàn thám hiểm chuyên nghiệp', 'PENDING', 39000000.000, NULL, NULL, 'tour-049', 'user-052'),
('book-050', '2026-01-09 11:15:00', 2, 2, '0953456789', 'Phòng thông nhau (connecting room)', 'CONFIRMED', 7050000.000, 'promo-004', NULL, 'tour-050', 'user-053'),
('book-051', '2026-01-09 14:30:00', 2, 0, '0954567890', NULL, 'CONFIRMED', 5000000.000, NULL, NULL, 'tour-001', 'user-054'),
('book-052', '2026-01-09 08:45:00', 3, 1, '0955678901', 'Cần xe lăn hỗ trợ', 'PENDING', 6300000.000, NULL, NULL, 'tour-002', 'user-055'),
('book-053', '2026-01-09 10:30:00', 2, 0, '0956789012', 'Check-in trễ', 'CONFIRMED', 6400000.000, NULL, NULL, 'tour-003', 'user-056'),
('book-054', '2026-01-09 16:00:00', 4, 0, '0957890123', NULL, 'COMPLETED', 4800000.000, NULL, NULL, 'tour-004', 'user-057'),
('book-055', '2026-01-09 09:00:00', 2, 0, '0958901234', 'Kỷ niệm sinh nhật', 'CONFIRMED', 9000000.000, 'promo-002', 'user-003', 'tour-005', 'user-058'),
('book-056', '2026-01-09 13:00:00', 2, 1, '0959012345', NULL, 'CONFIRMED', 2375000.000, NULL, NULL, 'tour-006', 'user-059'),
('book-057', '2026-01-09 11:30:00', 5, 0, '0960123456', 'Nhóm sinh viên nghiên cứu', 'PENDING', 14000000.000, NULL, NULL, 'tour-007', 'user-060'),
('book-058', '2026-01-09 15:15:00', 2, 2, '0961234567', 'Cần cũi em bé', 'CONFIRMED', 10500000.000, NULL, NULL, 'tour-008', 'user-061'),
('book-059', '2026-01-09 08:30:00', 2, 0, '0962345678', NULL, 'CANCELLED', 3200000.000, NULL, NULL, 'tour-009', 'user-062'),
('book-060', '2026-01-09 10:00:00', 1, 0, '0963456789', 'Muốn thuê xe máy tự lái', 'CONFIRMED', 5500000.000, NULL, NULL, 'tour-010', 'user-063');

-- =============================================
-- BOOKINGS CHUNK 3: BOOKINGS 61-90
-- =============================================
INSERT INTO bookings (booking_id, booking_date, num_adults, num_children, phone, special_request, status, total_price, promotion_id, staff_id, tour_id, user_id) VALUES
('book-061', '2025-12-08 09:00:00', 4, 2, '0964567890', 'Gia đình có người già', 'CONFIRMED', 13200000.000, NULL, 'user-002', 'tour-011', 'user-064'),
('book-062', '2025-12-10 14:15:00', 2, 0, '0965678901', 'Yêu cầu phòng tầng cao', 'CONFIRMED', 4800000.000, NULL, NULL, 'tour-012', 'user-065'),
('book-063', '2025-12-12 10:30:00', 2, 0, '0966789012', NULL, 'PENDING', 2200000.000, NULL, NULL, 'tour-013', 'user-066'),
('book-064', '2025-12-14 08:45:00', 2, 0, '0967890123', 'Kỷ niệm Valentine', 'CONFIRMED', 3800000.000, NULL, NULL, 'tour-014', 'user-067'),
('book-065', '2025-12-16 16:00:00', 6, 2, '0968901234', 'Đặt tiệc BBQ bãi biển', 'CONFIRMED', 10500000.000, 'promo-002', NULL, 'tour-015', 'user-068'),
('book-066', '2025-12-18 11:20:00', 2, 1, '0969012345', NULL, 'PENDING', 4250000.000, NULL, NULL, 'tour-016', 'user-069'),
('book-067', '2025-12-20 13:45:00', 3, 0, '0970123456', 'Cần thuê giày leo núi', 'CONFIRMED', 8700000.000, NULL, 'user-003', 'tour-017', 'user-070'),
('book-068', '2025-12-22 09:30:00', 2, 0, '0971234567', NULL, 'CANCELLED', 1600000.000, NULL, NULL, 'tour-018', 'user-071'),
('book-069', '2025-12-25 15:10:00', 4, 0, '0972345678', 'Yêu cầu hướng dẫn viên nữ', 'CONFIRMED', 10400000.000, NULL, NULL, 'tour-019', 'user-072'),
('book-070', '2025-12-28 10:00:00', 2, 2, '0973456789', 'Không cay cho trẻ em', 'CONFIRMED', 2640000.000, NULL, NULL, 'tour-020', 'user-073'),
('book-071', '2025-12-30 08:30:00', 2, 0, '0974567890', 'Đam mê nhiếp ảnh', 'PENDING', 5400000.000, NULL, NULL, 'tour-021', 'user-074'),
('book-072', '2025-03-05 14:45:00', 2, 0, '0975678901', 'Gói trăng mật cao cấp', 'CONFIRMED', 11600000.000, 'promo-003', NULL, 'tour-022', 'user-075'),
('book-073', '2025-03-08 09:15:00', 5, 0, '0976789012', 'Đoàn khách nước ngoài', 'CONFIRMED', 7000000.000, NULL, 'user-002', 'tour-023', 'user-076'),
('book-074', '2025-03-10 11:30:00', 4, 0, '0977890123', 'Cần hỗ trợ mang vác hành lý', 'CONFIRMED', 12400000.000, NULL, NULL, 'tour-024', 'user-077'),
('book-075', '2025-03-12 16:30:00', 2, 1, '0978901234', NULL, 'PENDING', 5000000.000, NULL, NULL, 'tour-025', 'user-078'),
('book-076', '2025-03-15 10:00:00', 2, 0, '0979012345', 'Yêu thích thiên nhiên', 'CONFIRMED', 3700000.000, NULL, NULL, 'tour-026', 'user-079'),
('book-077', '2025-03-18 13:20:00', 3, 0, '0980123456', NULL, 'CONFIRMED', 2340000.000, NULL, NULL, 'tour-027', 'user-080'),
('book-078', '2025-03-20 08:50:00', 2, 2, '0981234567', 'Cần 2 giường đôi', 'CONFIRMED', 6300000.000, 'promo-005', NULL, 'tour-028', 'user-081'),
('book-079', '2025-03-25 14:00:00', 2, 0, '0982345678', NULL, 'CANCELLED', 5900000.000, NULL, NULL, 'tour-029', 'user-082'),
('book-080', '2025-03-28 09:45:00', 10, 2, '0983456789', 'Tour học tập cho học sinh', 'CONFIRMED', 7920000.000, 'promo-001', 'user-003', 'tour-030', 'user-083'),
('book-081', '2025-04-02 11:15:00', 4, 0, '0984567890', 'Nhóm bạn thân', 'PENDING', 6600000.000, NULL, NULL, 'tour-031', 'user-084'),
('book-082', '2025-04-05 15:30:00', 2, 0, '0985678901', 'Yêu cầu hướng dẫn viên am hiểu lịch sử', 'CONFIRMED', 1300000.000, NULL, NULL, 'tour-032', 'user-085'),
('book-083', '2025-04-10 08:00:00', 2, 1, '0986789012', NULL, 'CONFIRMED', 5750000.000, NULL, NULL, 'tour-033', 'user-086'),
('book-084', '2025-04-15 13:45:00', 6, 0, '0987890123', 'Đặt trước thực đơn hải sản', 'CONFIRMED', 8700000.000, NULL, NULL, 'tour-034', 'user-087'),
('book-085', '2025-04-20 10:30:00', 2, 0, '0988901234', 'Muốn tham quan nhà máy cà phê', 'PENDING', 5100000.000, NULL, 'user-002', 'tour-035', 'user-088'),
('book-086', '2025-04-25 16:00:00', 2, 2, '0989012345', 'Cần xe Jeep tham quan đồi cát', 'CONFIRMED', 6600000.000, NULL, NULL, 'tour-036', 'user-089'),
('book-087', '2025-04-30 09:00:00', 4, 0, '0990123456', 'Dịp lễ 30/4', 'CONFIRMED', 6320000.000, NULL, NULL, 'tour-037', 'user-090'),
('book-088', '2025-05-02 14:15:00', 3, 0, '0991234567', 'Thích mạo hiểm', 'CONFIRMED', 5850000.000, NULL, NULL, 'tour-038', 'user-091'),
('book-089', '2025-05-10 11:00:00', 2, 0, '0992345678', 'Phòng view hồ Xuân Hương', 'CONFIRMED', 3500000.000, NULL, NULL, 'tour-039', 'user-092'),
('book-090', '2025-05-15 08:30:00', 2, 1, '0993456789', 'Cần kính bơi cận', 'PENDING', 6625000.000, NULL, NULL, 'tour-040', 'user-093');

-- =============================================
-- Thanh toán (Cho các booking đã hoàn thành)
-- =============================================
INSERT INTO payments (payment_id, amount, payment_date, payment_method, status, transaction_id, booking_id) VALUES
('pay-001', 6250000.00, '2025-12-01 10:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20251201001', 'book-001'),
('pay-002', 3600000.00, '2025-12-02 11:30:00', 'MOMO', 'SUCCESS', 'MOMO20251202001', 'book-002'),
('pay-003', 14400000.00, '2025-12-03 14:30:00', 'CASH', 'SUCCESS', 'CASH20251203001', 'book-003'),
('pay-004', 2400000.00, '2025-12-05 10:00:00', 'PAYPAL', 'SUCCESS', 'PAYP20251205001', 'book-004'),
('pay-005', 13500000.00, '2025-12-06 15:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20251206001', 'book-005'),
('pay-006', 2850000.00, '2025-12-08 11:00:00', 'MOMO', 'SUCCESS', 'MOMO20251208001', 'book-006'),
('pay-007', 7000000.00, '2025-12-10 11:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20251210001', 'book-007'),
('pay-009', 3200000.00, '2025-12-14 09:30:00', 'BANK_TRANSFER', 'SUCCESS', 'BANK20251214001', 'book-009'),
('pay-011', 6600000.00, '2025-12-16 10:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20251216001', 'book-011'),
('pay-012', 8400000.00, '2025-12-17 12:00:00', 'DIRECT', 'SUCCESS', 'DIR20251217001', 'book-012'),
('pay-014', 7550000.00, '2025-12-19 09:00:00', 'MOMO', 'SUCCESS', 'MOMO20251219001', 'book-014'),
('pay-015', 3750000.00, '2025-12-20 15:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20251220001', 'book-015'),
('pay-016', 3400000.00, '2025-12-21 10:15:00', 'BANK_TRANSFER', 'SUCCESS', 'BANK20251221001', 'book-016'),
('pay-018', 4080000.00, '2025-12-23 11:00:00', 'PAYPAL', 'SUCCESS', 'PAYP20251223001', 'book-018'),
('pay-019', 5200000.00, '2025-12-24 14:30:00', 'DIRECT', 'SUCCESS', 'DIR20251224001', 'book-019'),
('pay-020', 4400000.00, '2025-12-25 09:30:00', 'MOMO', 'SUCCESS', 'MOMO20251225001', 'book-020'),
('pay-022', 17400000.00, '2025-12-27 11:30:00', 'DIRECT', 'SUCCESS', 'DIR20251227001', 'book-022'),
('pay-023', 4200000.00, '2025-12-28 16:00:00', 'PAYPAL', 'SUCCESS', 'PAYP20251228001', 'book-023'),
('pay-025', 9000000.00, '2025-12-30 10:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20251230001', 'book-025'),
('pay-027', 2340000.00, '2026-01-03 14:30:00', 'MOMO', 'SUCCESS', 'MOMO20260103001', 'book-027'),
('pay-028', 5250000.00, '2026-01-04 12:00:00', 'PAYPAL', 'SUCCESS', 'PAYP20260104001', 'book-028'),
('pay-030', 3060000.00, '2026-01-06 10:30:00', 'DIRECT', 'SUCCESS', 'DIR20260106001', 'book-030');

-- =============================================
-- Thanh toán (Cho các booking đã hoàn thành)
-- =============================================
INSERT INTO payments (payment_id, amount, payment_date, payment_method, status, transaction_id, booking_id) VALUES
('pay-031', 6600000.00, '2026-01-07 09:45:00', 'PAYPAL', 'SUCCESS', 'PAYP20260107001', 'book-031'),
('pay-032', 1950000.00, '2026-01-08 15:00:00', 'MOMO', 'SUCCESS', 'MOMO20260108001', 'book-032'),
('pay-034', 7068750.00, '2026-01-10 12:15:00', 'PAYPAL', 'SUCCESS', 'PAYP20260110001', 'book-034'), -- Giảm 25% (promo-002)
('pay-035', 5100000.00, '2026-01-10 15:30:00', 'DIRECT', 'SUCCESS', 'DIR20260111001', 'book-035'),
('pay-037', 3160000.00, '2026-01-10 13:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260113001', 'book-037'),
('pay-038', 7750000.00, '2026-01-10 09:30:00', 'MOMO', 'SUCCESS', 'MOMO20260114001', 'book-038'), -- Giảm 50k (promo-003)
('pay-040', 5300000.00, '2026-01-09 16:45:00', 'DIRECT', 'SUCCESS', 'DIR20260116001', 'book-040'),
('pay-041', 550000.00, '2026-01-09 11:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260117001', 'book-041'),
('pay-043', 8400000.00, '2026-01-09 10:00:00', 'DIRECT', 'SUCCESS', 'DIR20260119001', 'book-043'),
('pay-044', 10350000.00, '2026-01-09 08:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260120001', 'book-044'), -- Giảm 10% (promo-001)
('pay-046', 3450000.00, '2026-01-09 16:00:00', 'PAYPAL', 'SUCCESS', 'PAYP20260122001', 'book-046'),
('pay-047', 5700000.00, '2026-01-09 10:30:00', 'DIRECT', 'SUCCESS', 'DIR20260123001', 'book-047'),
('pay-048', 2720000.00, '2026-01-09 14:15:00', 'PAYPAL', 'SUCCESS', 'PAYP20260124001', 'book-048'),
('pay-050', 5992500.00, '2026-01-09 11:45:00', 'DIRECT', 'SUCCESS', 'DIR20260126001', 'book-050'), -- Giảm 15% (promo-004)
('pay-051', 5000000.00, '2026-01-09 15:00:00', 'PAYPAL', 'SUCCESS', 'PAYP20260127001', 'book-051'),
('pay-053', 6400000.00, '2026-01-09 11:00:00', 'DIRECT', 'SUCCESS', 'DIR20260129001', 'book-053'),
('pay-054', 4800000.00, '2026-01-09 16:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260130001', 'book-054'),
('pay-055', 6750000.00, '2026-01-09 09:30:00', 'DIRECT', 'SUCCESS', 'DIR20260201001', 'book-055'), -- Giảm 25% (promo-002)
('pay-056', 2375000.00, '2026-01-09 13:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260202001', 'book-056'),
('pay-058', 10500000.00, '2026-01-09 15:45:00', 'DIRECT', 'SUCCESS', 'DIR20260204001', 'book-058'),
('pay-060', 5500000.00, '2026-01-09 10:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260206001', 'book-060'),
('pay-061', 13200000.00, '2026-01-09 09:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260208001', 'book-061'),
('pay-062', 4800000.00, '2026-01-09 14:45:00', 'DIRECT', 'SUCCESS', 'DIR20260210001', 'book-062'),
('pay-064', 3800000.00, '2026-01-09 09:15:00', 'MOMO', 'SUCCESS', 'MOMO20260214001', 'book-064'),
('pay-065', 7875000.00, '2026-01-09 16:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260216001', 'book-065'), -- Giảm 25% (promo-002)
('pay-067', 8700000.00, '2026-01-09 14:15:00', 'PAYPAL', 'SUCCESS', 'PAYP20260220001', 'book-067'),
('pay-069', 10400000.00, '2026-01-09 15:40:00', 'DIRECT', 'SUCCESS', 'DIR20260225001', 'book-069'),
('pay-070', 2640000.00, '2026-01-09 10:30:00', 'MOMO', 'SUCCESS', 'MOMO20260228001', 'book-070'),
('pay-072', 11550000.00, '2026-01-09 15:15:00', 'PAYPAL', 'SUCCESS', 'PAYP20260305001', 'book-072'), -- Giảm 50k (promo-003)
('pay-073', 7000000.00, '2026-01-09 09:45:00', 'PAYPAL', 'SUCCESS', 'PAYP20260308001', 'book-073'),
('pay-074', 12400000.00, '2026-01-09 12:00:00', 'DIRECT', 'SUCCESS', 'DIR20260310001', 'book-074'),
('pay-076', 3700000.00, '2026-01-09 10:30:00', 'MOMO', 'SUCCESS', 'MOMO20260315001', 'book-076'),
('pay-077', 2340000.00, '2026-01-09 13:50:00', 'PAYPAL', 'SUCCESS', 'PAYP20260318001', 'book-077'),
('pay-078', 6200000.00, '2026-01-09 09:20:00', 'DIRECT', 'SUCCESS', 'DIR20260320001', 'book-078'), -- Giảm 100k (promo-005)
('pay-080', 7128000.00, '2026-01-09 10:15:00', 'DIRECT', 'SUCCESS', 'DIR20260328001', 'book-080'), -- Giảm 10% (promo-001)
('pay-082', 1300000.00, '2026-01-09 16:00:00', 'MOMO', 'SUCCESS', 'MOMO20260405001', 'book-082'),
('pay-083', 5750000.00, '2026-01-09 08:30:00', 'PAYPAL', 'SUCCESS', 'PAYP20260410001', 'book-083'),
('pay-084', 8700000.00, '2026-01-09 14:15:00', 'DIRECT', 'SUCCESS', 'DIR20260415001', 'book-084'),
('pay-086', 6600000.00, '2026-01-09 16:30:00', 'DIRECT', 'SUCCESS', 'DIR20260425001', 'book-086'),
('pay-087', 6320000.00, '2026-01-09 09:30:00', 'MOMO', 'SUCCESS', 'MOMO20260430001', 'book-087'),
('pay-088', 5850000.00, '2026-01-09 14:45:00', 'PAYPAL', 'SUCCESS', 'PAYP20260502001', 'book-088'),
('pay-089', 3500000.00, '2026-01-09 11:30:00', 'DIRECT', 'SUCCESS', 'DIR20260510001', 'book-089');

-- =============================================
-- ĐÁNH GIÁ (Cho các booking đã hoàn thành)
-- =============================================
INSERT INTO reviews (review_id, comment, created_at, is_visible, rating, booking_id, tour_id, user_id) VALUES
('rev-001', 'Trải nghiệm tuyệt vời! Những bãi biển thật hoang sơ và hướng dẫn viên rất am hiểu. Rất đáng để đi!', '2025-12-10 14:00:00', 1, 5, 'book-001', 'tour-001', 'user-004'),
('rev-002', 'Các di tích lịch sử tuyệt đẹp. Đại Nội Huế đẹp ngoạn mục. Khâu tổ chức rất tốt.', '2025-12-12 10:30:00', 1, 5, 'book-002', 'tour-002', 'user-005'),
('rev-003', 'Trải nghiệm trekking đáng kinh ngạc! Ruộng bậc thang đẹp tuyệt vời. Hơi thử thách một chút nhưng rất xứng đáng.', '2025-12-15 16:00:00', 1, 4, 'book-003', 'tour-003', 'user-006'),
('rev-004', 'Tour thành phố khá ổn nhưng đi hơi vội. Tuy nhiên ẩm thực đường phố rất ngon!', '2025-12-14 11:00:00', 1, 4, 'book-004', 'tour-004', 'user-007'),
('rev-005', 'Không thể nào quên! Vịnh Hạ Long ở ngoài còn đẹp hơn trong ảnh. Du thuyền rất sang trọng.', '2025-12-16 09:00:00', 1, 5, 'book-005', 'tour-005', 'user-008');

-- =============================================
-- REVIEWS CHUNK 2: REVIEWS 6-15
-- =============================================
INSERT INTO reviews (review_id, comment, created_at, is_visible, rating, booking_id, tour_id, user_id) VALUES
('rev-006', 'Chuyến đi ẩm thực thật sự mở mang tầm mắt! Cao lầu và Bánh mì ở Hội An ngon xuất sắc.', '2025-12-15 13:00:00', 1, 5, 'book-006', 'tour-006', 'user-009'),
('rev-007', 'Động Phong Nha đẹp hùng vĩ. Hướng dẫn viên rất chuyên nghiệp và chu đáo suốt hành trình.', '2025-12-18 16:30:00', 1, 5, 'book-007', 'tour-007', 'user-010'),
('rev-008', 'Kỳ nghỉ tuyệt vời cho cả gia đình. Bãi biển Nha Trang sạch, nước trong xanh. Dịch vụ khách sạn tốt.', '2025-12-20 09:15:00', 1, 4, 'book-008', 'tour-008', 'user-011'),
('rev-009', 'Hà Nội cổ kính và bình yên. Rất thích không khí ở Phố Cổ và Văn Miếu.', '2025-12-19 10:00:00', 1, 5, 'book-009', 'tour-009', 'user-012'),
('rev-010', 'Cung đường Hà Giang thực sự thử thách nhưng cảnh quan thì không đâu sánh bằng. Một trải nghiệm nhớ đời!', '2025-12-21 18:00:00', 1, 5, 'book-010', 'tour-010', 'user-013'),
('rev-011', 'Đà Nẵng rất đáng sống. Cầu Vàng đẹp như trong ảnh. Đồ ăn miền Trung hơi cay nhưng ngon.', '2025-12-22 14:00:00', 1, 4, 'book-011', 'tour-011', 'user-014'),
('rev-012', 'Đà Lạt mùa này hoa nở rất đẹp. Không khí se lạnh rất thích. Sẽ quay lại lần sau.', '2025-12-25 11:30:00', 1, 5, 'book-012', 'tour-012', 'user-015'),
('rev-013', 'Tour ẩm thực Sài Gòn rất đa dạng. Hướng dẫn viên đưa đi nhiều quán ngon trong hẻm mà khách du lịch ít biết.', '2025-12-24 19:45:00', 1, 4, 'book-013', 'tour-013', 'user-016'),
('rev-014', 'Chợ nổi Cái Răng tấp nập và thú vị. Trái cây tại vườn rất tươi ngon.', '2025-12-26 15:20:00', 1, 4, 'book-014', 'tour-014', 'user-017'),
('rev-015', 'Chuyến đi ngắn ngày đến Vũng Tàu rất thư giãn. Hải sản tươi và giá cả hợp lý.', '2025-12-27 10:10:00', 1, 3, 'book-015', 'tour-015', 'user-018');

-- =============================================
-- REVIEWS CHUNK 3: REVIEWS 16-25
-- =============================================
INSERT INTO reviews (review_id, comment, created_at, is_visible, rating, booking_id, tour_id, user_id) VALUES
('rev-016', 'Phố cổ Hội An về đêm lung linh huyền ảo với hàng ngàn chiếc đèn lồng. Không khí rất lãng mạn.', '2025-12-23 20:00:00', 1, 5, 'book-016', 'tour-016', 'user-019'),
('rev-017', 'Chinh phục đỉnh Fansipan là một trải nghiệm không thể nào quên. Hệ thống cáp treo rất hiện đại và an toàn.', '2025-12-24 16:30:00', 1, 5, 'book-017', 'tour-017', 'user-020'),
('rev-018', 'Tour tham quan Hà Nội rất thú vị. Hướng dẫn viên giải thích lịch sử rất cuốn hút. Kem Tràng Tiền rất ngon!', '2025-12-25 18:00:00', 1, 4, 'book-018', 'tour-018', 'user-021'),
('rev-019', 'Thác Bản Giốc hùng vĩ đúng như lời đồn. Đường đi hơi xa nhưng cảnh đẹp biên giới rất xứng đáng.', '2025-12-27 14:00:00', 1, 5, 'book-019', 'tour-019', 'user-022'),
('rev-020', 'Ẩm thực Huế quá tuyệt vời! Các món bánh bèo, nậm, lọc đều rất ngon và rẻ. Sẽ quay lại để ăn tiếp.', '2025-12-26 19:00:00', 1, 5, 'book-020', 'tour-020', 'user-023'),
('rev-021', 'Ruộng bậc thang Mù Cang Chải đẹp như một bức tranh. Người dân địa phương rất thân thiện và hiếu khách.', '2025-12-29 10:00:00', 1, 5, 'book-021', 'tour-021', 'user-024'),
('rev-022', 'Côn Đảo là thiên đường nghỉ dưỡng thật sự. Biển trong vắt, bãi cát trắng mịn và rất yên bình.', '2025-12-30 15:30:00', 1, 5, 'book-022', 'tour-022', 'user-025'),
('rev-023', 'Thánh địa Mỹ Sơn mang vẻ đẹp cổ kính và bí ẩn. Show diễn văn hóa Chăm pa rất ấn tượng.', '2025-12-30 13:00:00', 1, 4, 'book-023', 'tour-023', 'user-026'),
('rev-024', 'Vịnh Lan Hạ hoang sơ và ít đông đúc hơn Hạ Long. Chèo kayak qua các hang động là hoạt động vui nhất.', '2026-01-02 11:00:00', 1, 4, 'book-024', 'tour-024', 'user-027'),
('rev-025', 'Một cái nhìn toàn diện về Sài Gòn. Bảo tàng Chứng tích Chiến tranh rất xúc động. Địa đạo Củ Chi là điểm nhấn.', '2026-01-02 17:00:00', 1, 5, 'book-025', 'tour-025', 'user-028');

-- =============================================
-- REVIEWS CHUNK 4: REVIEWS 26-35
-- =============================================
INSERT INTO reviews (review_id, comment, created_at, is_visible, rating, booking_id, tour_id, user_id) VALUES
('rev-026', 'Vườn quốc gia Cúc Phương rất trong lành. Cây chò ngàn năm thực sự ấn tượng. Một chuyến đi về nguồn tuyệt vời.', '2026-01-05 09:00:00', 1, 4, 'book-026', 'tour-026', 'user-029'),
('rev-027', 'Ẩm thực Quảng Bình rất đậm đà và cay nồng. Các loại bánh lọc, bánh nậm ở đây ngon hơn hẳn những nơi khác.', '2026-01-05 13:30:00', 1, 5, 'book-027', 'tour-027', 'user-030'),
('rev-028', 'Tràng An đẹp như một bức tranh thủy mặc. Ngồi thuyền ngắm cảnh rất thư giãn, các bác lái đò rất thân thiện.', '2026-01-07 10:15:00', 1, 5, 'book-028', 'tour-028', 'user-031'),
('rev-029', 'Đảo Lý Sơn vẫn còn rất hoang sơ. Nước biển xanh ngắt. Tỏi Lý Sơn mua về làm quà ai cũng khen.', '2026-01-08 14:00:00', 1, 4, 'book-029', 'tour-029', 'user-032'),
('rev-030', 'Làng cổ Đường Lâm mang lại cảm giác hoài niệm. Tương bần và chè lam ở đây rất ngon. Không gian yên bình.', '2026-01-07 16:00:00', 1, 4, 'book-030', 'tour-030', 'user-033'),
('rev-031', 'Tour chèo thuyền vượt thác ở Đà Lạt cực kỳ kích thích! Hướng dẫn viên đảm bảo an toàn rất tốt.', '2026-01-08 11:30:00', 1, 5, 'book-031', 'tour-031', 'user-034'),
('rev-032', 'Hoàng thành Thăng Long rộng lớn và chứa đựng nhiều giá trị lịch sử. Khu khảo cổ học rất ấn tượng.', '2026-01-09 15:45:00', 1, 4, 'book-032', 'tour-032', 'user-035'),
('rev-033', 'Vườn quốc gia U Minh Thượng mùa nước nổi rất đẹp. Đi vỏ lãi vào rừng tràm ngắm chim là trải nghiệm thú vị.', '2026-01-11 09:00:00', 1, 4, 'book-033', 'tour-033', 'user-036'),
('rev-034', 'Thiên đường hải sản là đây! Mực một nắng và sò điệp nướng mỡ hành quá ngon. Giá cả phải chăng.', '2026-01-12 12:30:00', 1, 5, 'book-034', 'tour-034', 'user-037'),
('rev-035', 'Tìm hiểu về quy trình làm cà phê rất hay. Cảnh thác Dray Nur hùng vĩ. Cà phê Buôn Ma Thuột đúng là danh bất hư truyền.', '2026-01-14 10:00:00', 1, 5, 'book-035', 'tour-035', 'user-038');

-- =============================================
-- Yêu thích (Cho các tour được yêu thích)
-- =============================================
INSERT INTO favorites (favorite_id, created_at, tour_id, user_id) VALUES
('fav-001', '2025-11-15 10:00:00', 'tour-001', 'user-004'),
('fav-002', '2025-11-16 11:30:00', 'tour-005', 'user-004'),
('fav-003', '2025-11-17 14:00:00', 'tour-003', 'user-005'),
('fav-004', '2025-11-18 09:15:00', 'tour-010', 'user-006'),
('fav-005', '2025-11-19 16:00:00', 'tour-022', 'user-007'),
('fav-006', '2025-11-20 10:30:00', 'tour-001', 'user-008'),
('fav-007', '2025-11-21 13:00:00', 'tour-005', 'user-009'),
('fav-008', '2025-11-22 08:45:00', 'tour-012', 'user-010'),
('fav-009', '2025-11-23 15:30:00', 'tour-008', 'user-011'),
('fav-010', '2025-11-24 11:00:00', 'tour-049', 'user-012');

-- =============================================
-- LỊCH SỬ (Cho các tour được xem)
-- =============================================
INSERT INTO history (history_id, action_type, timestamp, tour_id, user_id) VALUES
('hist-001', 'VIEW', '2025-11-15 10:00:00', 'tour-001', 'user-004'),
('hist-002', 'VIEW', '2025-11-15 10:15:00', 'tour-005', 'user-004'),
('hist-003', 'VIEW', '2025-11-16 11:30:00', 'tour-003', 'user-005'),
('hist-004', 'VIEW', '2025-11-17 14:00:00', 'tour-010', 'user-006'),
('hist-005', 'VIEW', '2025-11-18 09:15:00', 'tour-022', 'user-007'),
('hist-006', 'VIEW', '2025-11-19 16:00:00', 'tour-001', 'user-008'),
('hist-007', 'VIEW', '2025-11-20 10:30:00', 'tour-005', 'user-009'),
('hist-008', 'VIEW', '2025-11-21 13:00:00', 'tour-012', 'user-010'),
('hist-009', 'VIEW', '2025-11-22 08:45:00', 'tour-008', 'user-011'),
('hist-010', 'VIEW', '2025-11-23 15:30:00', 'tour-049', 'user-012');

-- Re-enable foreign key checks after import
SET FOREIGN_KEY_CHECKS = 1;
