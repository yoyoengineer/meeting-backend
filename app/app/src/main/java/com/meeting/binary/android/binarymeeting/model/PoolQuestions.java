package com.meeting.binary.android.binarymeeting.model;

import java.util.List;

/**
 * Created by meldi on 3/20/2018.
 */

public class PoolQuestions {

    private String mTitle;
    private List<String> mQuestion;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<String> getQuestion() {
        return mQuestion;
    }

    public void setQuestion(List<String> question) {
        mQuestion = question;
    }
}
