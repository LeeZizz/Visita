package com.visita.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "session_id")
	private String sessionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id")
	private UserEntity staff; // Nhân viên hỗ trợ (có thể null)

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('OPEN','CLOSED')")
	private ChatSessionStatus status;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ChatMessageEntity> messages;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
