package com.meeting.binary.android.binarymeeting.message;

import com.meeting.binary.android.binarymeeting.model.Message;

/**
 * Created by meldi on 3/30/2018.
 */

public class Msg {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private int type;
    private Message mMessage;

    public Msg(Message message, int type) {
        this.type = type;

    }

    public int getType() {
        return type;
    }


}
