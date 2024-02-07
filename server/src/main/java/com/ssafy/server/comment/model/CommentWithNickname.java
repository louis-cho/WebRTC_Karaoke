package com.ssafy.server.comment.model;


public class CommentWithNickname {
    private Comment comment;
    private String nickname;

    public CommentWithNickname(Comment comment, String nickname) {
        this.comment = comment;
        this.nickname = nickname;
    }

    public Comment getComment() {
        return comment;
    }

    public String getNickname() {
        return nickname;
    }
}