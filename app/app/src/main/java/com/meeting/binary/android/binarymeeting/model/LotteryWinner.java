package com.meeting.binary.android.binarymeeting.model;

public class LotteryWinner {
    private String id;
    private String lotteryId;
    private String username;
    private String prize;

    public LotteryWinner() {
    }

    public LotteryWinner(String id, String lotteryId, String username, String prize) {
        this.id = id;
        this.lotteryId = lotteryId;
        this.username = username;
        this.prize = prize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }
}
