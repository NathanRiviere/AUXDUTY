package com.example.auxduty.firebaseHelpers;

public class songInfo {
    public String artist;
    public String songName;
    public String genre;
    public int year;

    public songInfo() {}
    public songInfo(String artist, String songName, String genre, int year) {
        this.artist = artist;
        this.songName = songName;
        this.genre = genre;
        this.year = year;
    }
}
