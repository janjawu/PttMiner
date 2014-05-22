package com.janja.pttminer.parser;

import java.util.ArrayList;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import com.janja.pttminer.data.Article;
import com.janja.pttminer.data.ArticleList;

public class ArticleListParser extends PttParser {

    @Override
    protected Object parseLogic(TagNode rootNode) {
        ArticleList articleList = new ArticleList(url);
        try {
            String title = parseTitle(rootNode);
            articleList.setTitle(title);

            String[] urls = parseUrls(rootNode);
            articleList.setNextUrl(urls[0]);
            articleList.setLastUrl(urls[1]);

            ArrayList<Article> articleContainer = parseArticles(rootNode);
            articleList.setArticleContainer(articleContainer);

        } catch (XPatherException e) {
            e.printStackTrace();
        }

        return articleList;
    }

    private String parseTitle(TagNode rootNode) {
        String title = "";

        Object[] elements = rootNode.getElementsByName("title", true);

        if (elements.length > 0) {
            TagNode titleNode = (TagNode) elements[0];
            title = titleNode.getText().toString();
        }

        return title;
    }

    private String[] parseUrls(TagNode rootNode) throws XPatherException {
        String[] urls = { "", "" };

        Object[] elements = rootNode
                .evaluateXPath("//div[@id='action-bar-container']//div[@class='btn-group pull-right']");

        for (Object element : elements) {
            TagNode urlNode = (TagNode) element;

            for (TagNode aNode : urlNode.getElementListByName("a", false)) {

                String value = aNode.getText().toString();
                boolean isNextUrl = value.contains("下頁");
                boolean isLastUrl = value.contains("上頁");

                String link = "http://www.ptt.cc/"
                        + aNode.getAttributeByName("href");

                if (isNextUrl && link != null) {
                    urls[0] = link;
                } else if (isLastUrl && link != null) {
                    urls[1] = link;
                }
            }
        }
        return urls;
    }

    private ArrayList<Article> parseArticles(TagNode rootNode)
            throws XPatherException {
        ArrayList<Article> articleContainer = new ArrayList<Article>();

        Object[] elements = rootNode
                .evaluateXPath("//div[@class='r-list-container bbs-screen']/div[@class='r-ent']");

        for (Object element : elements) {

            TagNode articleNode = (TagNode) element;

            String nrec = parseArticleNrec(articleNode);
            String title = parseArticleTitle(articleNode);
            String type = getArticleType(title);
            String link = parseArticleLink(articleNode);
            String date = parseArticleDate(articleNode);
            String author = parseArticleAuthor(articleNode);

            Article article = new Article();
            article.setNrec(nrec);
            article.setTitle(title);
            article.setType(type);
            article.setUrl(link);
            article.setDate(date);
            article.setAuthor(author);

            articleContainer.add(0, article);
        }
        return articleContainer;
    }

    private String parseArticleNrec(TagNode articleNode)
            throws XPatherException {
        String targetXPath = "/div[@class='nrec']";
        return getNodeText(articleNode, targetXPath);
    }

    private String parseArticleTitle(TagNode articleNode)
            throws XPatherException {
        String targetXPath = "/div[@class='title']";
        return getNodeText(articleNode, targetXPath).trim();
    }

    private String parseArticleLink(TagNode articleNode)
            throws XPatherException {
        String link = "";
        String targetXPath = "/div[@class='title']";

        Object[] elements = articleNode.evaluateXPath(targetXPath);

        if (elements.length > 0) {
            TagNode titleNode = (TagNode) elements[0];
            for (TagNode aNode : titleNode.getElementListByName("a", false)) {
                link = "http://www.ptt.cc/" + aNode.getAttributeByName("href");
            }
        }

        return link;
    }

    private String parseArticleDate(TagNode articleNode)
            throws XPatherException {
        String targetXPath = "//div[@class='date']";
        return getNodeText(articleNode, targetXPath);
    }

    private String parseArticleAuthor(TagNode articleNode)
            throws XPatherException {
        String targetXPath = "//div[@class='author']";
        return getNodeText(articleNode, targetXPath);
    }

    private String getNodeText(TagNode tagNode, String xPathKey)
            throws XPatherException {
        String text = "";

        Object[] elements = tagNode.evaluateXPath(xPathKey);

        if (elements.length > 0) {
            TagNode childNode = (TagNode) elements[0];
            text = childNode.getText().toString();
        }

        return text;
    }

    private String getArticleType(String title) {
        String type = "";

        if (title.contains("(本文已被刪除)")) {
            return type;
        }

        int start = title.indexOf("[");
        int end = title.indexOf("]");
        if (start != -1 && end != -1) {
            type = title.substring(start + 1, end);
        }
        return type;
    }
}
