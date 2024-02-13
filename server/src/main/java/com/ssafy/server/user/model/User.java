package com.ssafy.server.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ssafy.server.auth.model.entity.RefreshToken;
import com.ssafy.server.song.model.entity.SingLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor

@Getter
@Setter
@Entity(name = "user")
@Table(name = "user")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk")
    private int userPk;


    @Type(type = "uuid-char")
    @Column(name = "user_key", length = 36)
    private UUID userKey;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    private Character role;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "introduction")
    private String introduction;

    @JsonBackReference
    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshToken;

    // Constructors, getters, setters, etc.

    public User() {
        // Default constructor required by JPA
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

 }
