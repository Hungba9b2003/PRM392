package com.example.austproject;

import java.util.List;

public class MusicItem {
    private String name;
    private String resource;
    private String avatar;
    private List<String> lyric;

    // Constructor đầy đủ (giữ nguyên)
    public MusicItem(String name, String resource, String avatar, List<String> lyric) {
        this.name = name;
        this.resource = resource;
        this.avatar = avatar;
        this.lyric = lyric;
    }

    // ✨ Constructor mới chỉ chứa tên playlist
    public MusicItem(String name) {
        this.name = name;
        this.resource = "";
        this.avatar = null;
        this.lyric = null;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<String> getLyric() {
        return lyric;
    }

    public String getResource() {
        return resource;
    }
}
