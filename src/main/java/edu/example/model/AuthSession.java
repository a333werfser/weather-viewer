package edu.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Sessions")
public class AuthSession {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", referencedColumnName="Id")
    private User user;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    public AuthSession(User user, int expirationTime) {
        this.user = user;
        setExpiresAt(expirationTime);
    }

    public void setExpiresAt(int expirationTime) {
        this.expiresAt = LocalDateTime.now().plusMinutes(expirationTime);
    }

    public String getId() {
        return this.id.toString();
    }

}
