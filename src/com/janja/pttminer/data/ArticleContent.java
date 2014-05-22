package com.janja.pttminer.data;

import java.util.ArrayList;

public class ArticleContent {
    private String writer;
    private String time;
    private String mainContent;
    private String station;
    private String urlDescription;
    private ArrayList<ArticleReply> replyContainer;

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMainContent() {
        return mainContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getUrlDescription() {
        return urlDescription;
    }

    public void setUrlDescription(String urlDescription) {
        this.urlDescription = urlDescription;
    }

    public ArrayList<ArticleReply> getReplyContainer() {
        return replyContainer;
    }

    public void setReplyContainer(ArrayList<ArticleReply> replyContainer) {
        this.replyContainer = replyContainer;
    }
}
