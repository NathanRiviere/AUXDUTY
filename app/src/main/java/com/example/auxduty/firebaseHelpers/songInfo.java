package com.example.auxduty.firebaseHelpers;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public class songInfo {
    public String artist;
    public String songName;
    public String genre;
    public int year;
    public int priority;

    public songInfo() {
    }

    public songInfo(String artist, String songName, String genre, int year) {
        this.artist = artist;
        this.songName = songName;
        this.genre = genre;
        this.year = year;
        this.priority = 0;
    }

    public songInfo(String artist, String songName) {
        this.artist = artist;
        this.songName = songName;
        this.genre = "";
        this.year = -1;
        this.priority = -1;
    }

    public songInfo(String artist, String songName, int priority) {
        this.artist = artist;
        this.songName = songName;
        this.priority = priority;
        this.year = -1;
        this.genre = "null";
    }
}
