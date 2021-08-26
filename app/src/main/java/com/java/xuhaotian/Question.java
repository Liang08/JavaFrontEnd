package com.java.xuhaotian;

public class Question {
    private String qBody, qAnswer, a, b, c, d;
    private int id;

    public Question(String qBody, String qAnswer, String a, String b, String c, String d, int id) {
        this.qBody = qBody;
        this.qAnswer = qAnswer;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.id = id;
    }

    public String getQBody() { return qBody; }
    public String getQAnswer() { return qAnswer; }
    public String getA() { return a; }
    public String getB() { return b; }
    public String getC() { return c; }
    public String getD() { return d; }
    public int getId() { return id; }
}
