package com.example.austproject;

import java.util.List;

public class MusicItem {
    private String name;
    private int resource;
    private String avatar;
    private List<String> lyric; // Đổi từ String sang List<String>

    public MusicItem(String name, int resource, String avatar, List<String> lyric) {
        this.name = name;
        this.resource = resource;
        this.avatar = avatar;
        this.lyric = lyric;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<String> getLyric() { // Getter mới cho lyric dạng danh sách
        return lyric;
    }

    public int getResource() {
        return resource;
    }
}
