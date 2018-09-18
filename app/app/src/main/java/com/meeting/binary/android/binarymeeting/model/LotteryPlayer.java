package com.meeting.binary.android.binarymeeting.model;

public class LotteryPlayer {
    private String name;
    private String prize;

    public LotteryPlayer() {
    }

    public LotteryPlayer(String name, String prize) {
        this.name = name;
        this.prize = prize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }
}
