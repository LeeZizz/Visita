package com.visita.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "message_id")
	private String messageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private ChatSessionEntity session;

	@Lob
	@Column(nullable = false)
	private String content;

	@Column(name = "is_staff")
	private Boolean isStaff;

	@Column(name = "is_read")
	private Boolean isRead;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
