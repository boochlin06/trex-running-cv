package com.emotibot.robotvision.game.trexrun;

public class SinglePlayerResult {
    int positionIndex = -1; // 0 is left , 1 is right player
    int emotionIndex = -1;
    int pos_x = -1;
    int pos_y = -1;
    int width, height;
    boolean isLipOpen = false;

    public boolean isLipOpen() {
        return isLipOpen;
    }

    public void setLipOpen(boolean lipOpen) {
        isLipOpen = lipOpen;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public int getPos_y() {
        return pos_y;
    }

    public void setPos_y(int pos_y) {
        this.pos_y = pos_y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public int getEmotionIndex() {
        return emotionIndex;
    }

    public void setEmotionIndex(int emotionIndex) {
        this.emotionIndex = emotionIndex;
    }

    public int getPos_x() {
        return pos_x;
    }

    public void setPos_x(int pos_x) {
        this.pos_x = pos_x;
    }
}
