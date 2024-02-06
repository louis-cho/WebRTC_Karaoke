package com.ssafy.server.user.model;

import com.ssafy.server.auth.model.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor

@Entity(name = "user")
@Getter
@Setter
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk")
    private int userPk;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_key", length = 36)
    private UUID userKey;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    private char role;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "introduction")
    private String introduction;

    @OneToMany
    private List<RefreshToken> refreshToken;

    // Constructors, getters, setters, etc.

    public User() {
        // Default constructor required by JPA
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "[User] >> " + this.userPk + "," + this.userKey + "," + this.nickname +"\n";
    }
}
