package com.janja.pttminer.parser;

import java.util.ArrayList;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import com.janja.pttminer.data.ArticleContent;
import com.janja.pttminer.data.ArticleReply;

public class ArticleContentParser extends PttParser {

    @Override
    protected Object parseLogic(TagNode rootNode) {
        ArticleContent articleContent = new ArticleContent();
        try {
            String[] header = parseHeader(rootNode);
            articleContent.setWriter(header[0]);
            articleContent.setTime(header[1]);

            String allContent = getAllContent(rootNode);
            String startOfMainContent = header[1];

            String mainContent = parseMainContent(allContent,
                    startOfMainContent);
            articleContent.setMainContent(mainContent);

            String station = parseStation(allContent);
            articleContent.setStation(station);

            String urlDescription = parseUrlDescription(allContent);
            articleContent.setUrlDescription(urlDescription);

            ArrayList<ArticleReply> replyContainer = parseReply(allContent);
            articleContent.setReplyContainer(replyContainer);
        } catch (XPatherException e) {
            e.printStackTrace();
        }

        return articleContent;
    }

    private String getAllContent(TagNode rootNode) throws XPatherException {
        String allContent = "";

        Object[] elements = rootNode.evaluateXPath("//div[@id='main-content']");

        if (elements.length > 0) {
            TagNode contentNode = (TagNode) elements[0];
            allContent = contentNode.getText().toString();
        }
        return allContent;
    }

    private String[] parseHeader(TagNode rootNode) throws XPatherException {
        String[] header = { "", "" };

        Object[] spanTags = rootNode
                .evaluateXPath("//div[@id='main-content']//span[@class='article-meta-tag']");

        Object[] spanValues = rootNode
                .evaluateXPath("//div[@id='main-content']//span[@class='article-meta-value']");

        if (spanValues == null) {
            throw new NullPointerException(
                    "Because spanValues is null, we can't parse header");
        }

        for (int i = 0; i < spanTags.length; i++) {
            TagNode spanTag = (TagNode) spanTags[i];
            String spanTagText = spanTag.getText().toString();
            if (spanTagText.contains("作者")) {
                TagNode spanValue = (TagNode) spanValues[i];
                header[0] = spanValue.getText().toString();
            } else if (spanTagText.contains("時間")) {
                TagNode spanValue = (TagNode) spanValues[i];
                header[1] = spanValue.getText().toString();
            }
        }

        return header;
    }

    private String parseMainContent(String allContent, String startOfMainContent) {
        String mainContent = "";

        int start = allContent.indexOf(startOfMainContent);
        int end = allContent.indexOf("※ 發信站", start);
        boolean vaild = start != -1 && end != -1;
        if (vaild) {
            start = start + startOfMainContent.length();
            mainContent = allContent.substring(start, end);
        }
        return mainContent;
    }

    private String parseStation(String allContent) {
        String station = "";
        int start = allContent.indexOf("※ 發信站");
        int end = allContent.indexOf("※ 文章網址", start);
        boolean vaild = start != -1 && end != -1;
        if (vaild) {
            station = allContent.substring(start, end - 1);
        }
        return station;
    }

    private String parseUrlDescription(String allContent) {
        String urlDescription = "";
        int start = allContent.indexOf("※ 文章網址");
        int end = allContent.indexOf("html", start);
        boolean vaild = start != -1 && end != -1;
        if (vaild) {
            end = end + "html".length();
            urlDescription = allContent.substring(start, end);
        }
        return urlDescription;
    }

    private ArrayList<ArticleReply> parseReply(String allContent) {
        ArrayList<ArticleReply> replyContainer = new ArrayList<ArticleReply>();
        String replies = "";

        int after = allContent.indexOf("※ 文章網址");
        int start = allContent.indexOf("html", after);
        int end = allContent.length() - 1;
        boolean vaild = start != -1;
        if (vaild) {
            start = start + "html".length();
            replies = allContent.substring(start, end);
        }

        String[] rows = replies.split("\n");
        for (String row : rows) {
            int colonStart = row.indexOf(":");
            int specificStart = row.indexOf("※ ");
            boolean validRow = row.contains(":")
                    && (!row.contains("※ ") || specificStart > colonStart);
            if (validRow) {
                ArticleReply articleReply = new ArticleReply();

                String type = row.substring(0, 1);
                articleReply.setType(type);

                int startAuthor = row.indexOf(type) + type.length();
                int endAuthor = row.indexOf(":");
                String author = row.substring(startAuthor, endAuthor).trim();
                articleReply.setAuthor(author);

                int endTime = row.length();
                int startTime = endTime - 11;
                String time = row.substring(startTime, endTime);
                articleReply.setTime(time);

                int startContent = row.indexOf(":") + 1;
                int endContent = row.indexOf(time, startContent);
                String content = row.substring(startContent, endContent);
                articleReply.setContent(content);

                replyContainer.add(articleReply);
            }
        }
        return replyContainer;
    }
}
