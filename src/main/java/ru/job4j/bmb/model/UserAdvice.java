package ru.job4j.bmb.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "mb_user_advice")
public class UserAdvice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_advice")
    private boolean advice;

    public UserAdvice() {
    }

    public UserAdvice(User user, boolean advice) {
        this.user = user;
        this.advice = advice;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public boolean getAdvice() {
        return advice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAdvice(boolean advice) {
        this.advice = advice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAdvice that = (UserAdvice) o;
        return advice == that.advice && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, advice);
    }
}
