package com.ssafy.server.syncdata;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.like.model.Like;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "like_sync_data")
public class LikeSyncData implements Syncable, Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeId;

    @Column(name = "user_pk")
    private Integer userPk;
    @Column(name = "feed_id", insertable = false, updatable = false)
    private Integer feedId;
    @Column(name = "status")
    private boolean status;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "synced_to_db")
    private boolean syncedToDB;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    public LikeSyncData(int likeId, int feedId, int userPk, boolean status, boolean deleted) {
        this.likeId = likeId;
        this.feedId = feedId;
        this.userPk = userPk;
        this.status = status;
        this.isDeleted = deleted;
        this.syncedToDB = false;
    }

    // Constructors, getters, and setters

    @Override
    public boolean isSyncedToDB() {
        return syncedToDB;
    }

    @Override
    public void setSyncedToDB(boolean syncedToDB) {
        this.syncedToDB = syncedToDB;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LikeSyncData that = (LikeSyncData) obj;
        return Objects.equals(userPk, that.userPk) && Objects.equals(feedId, that.feedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPk, feedId);
    }

    // 추가: 직렬화 및 역직렬화 메서드
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(this.userPk);
        out.writeInt(this.feedId);
        out.writeBoolean(this.syncedToDB);

        ClassLoader classLoader = LikeSyncData.class.getClassLoader();
        System.out.println("Class is loaded by: " + classLoader);
        // 다른 필드에 대한 직렬화 작업 추가
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.userPk = in.readInt();
        this.feedId = in.readInt();
        this.syncedToDB = in.readBoolean();

        ClassLoader classLoader = LikeSyncData.class.getClassLoader();
        System.out.println("Class is loaded by: " + classLoader);
        // 다른 필드에 대한 역직렬화 작업 추가
    }
}