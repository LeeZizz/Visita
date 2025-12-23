package com.visita.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "staff_id")
    private String staffId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "staffEntity", fetch = FetchType.LAZY)
    private List<ChatSessionEntity> chatSessions;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
