package com.java.xuhaotian;

import android.text.SpannableString;

public class AskAnswerMsg {
    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;
    private SpannableString content;
    private int type;

    public AskAnswerMsg(SpannableString content, int type) {
        this.content = content;
        this.type = type;
    }

    public SpannableString getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
