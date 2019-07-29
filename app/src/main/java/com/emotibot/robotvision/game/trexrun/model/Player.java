package com.emotibot.robotvision.game.trexrun.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "players")
public class Player implements Parcelable {
    private String name;

    @PrimaryKey(autoGenerate = true)
    private long id;
    private int score = 0;
    private String imageInPlayingPath;
    private String imageHead;
    private String imageInFinalPath;
    private String qrCodeLink;
    private int rank;

    protected Player(Parcel in) {
        name = in.readString();
        id = in.readLong();
        score = in.readInt();
        imageInPlayingPath = in.readString();
        imageHead = in.readString();
        imageInFinalPath = in.readString();
        qrCodeLink = in.readString();
        rank = in.readInt();
        group = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(id);
        dest.writeInt(score);
        dest.writeString(imageInPlayingPath);
        dest.writeString(imageHead);
        dest.writeString(imageInFinalPath);
        dest.writeString(qrCodeLink);
        dest.writeInt(rank);
        dest.writeInt(group);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getImageHead() {
        return imageHead;
    }

    public void setImageHead(String imageHead) {
        this.imageHead = imageHead;
    }

    @Ignore
    private int group; // 0 is left , 1 is right;

    public Player() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImageInPlayingPath() {
        return imageInPlayingPath;
    }

    public void setImageInPlayingPath(String imageInPlayingPath) {
        this.imageInPlayingPath = imageInPlayingPath;
    }

    public String getImageInFinalPath() {
        return imageInFinalPath;
    }

    public void setImageInFinalPath(String imageInFinalPath) {
        this.imageInFinalPath = imageInFinalPath;
    }

    public String getQrCodeLink() {
        return qrCodeLink;
    }

    public void setQrCodeLink(String qrCodeLink) {
        this.qrCodeLink = qrCodeLink;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
