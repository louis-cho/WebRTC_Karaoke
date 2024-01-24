package com.ssafy.server.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    // 생성자

    // Getter 및 Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // 기본 생성자 (JPA에서 필요)
    public User() {
    }

    // 생성자
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // toString, equals, hashCode 등 다른 메서드들도 필요에 따라 추가 가능
    @Override
    public String toString() {
        return "[User| username >> " + this.username + "\temail >> " + this.email + "\tid >> " + this.id + "]";
    }

}
