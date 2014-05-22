package com.janja.pttminer.parser;

import java.util.ArrayList;

import com.janja.pttminer.data.Article;
import com.janja.pttminer.data.ArticleList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PttManager {
    private static PttManager pttManager;
    private Context context;
    private PttParser articleListParser;
    private PttParser articleContentParser;
    private ArrayList<ArticleList> articleLists;
    private ArrayList<Article> allArticles;
    private ArrayList<PttArticleListListener> pttArticleListListeners;
    private ArrayList<PttArticleContentListener> pttArticleContentListeners;
    private Article clickedArticle;
    private String firstArticleUrl;

    public interface PttArticleListListener {

        public void getPttDataFinish();

        public void getLatestPttDataFinish();

        public void getOldestPttDataFinish();
    }

    public interface PttArticleContentListener {

        public void getArticleContentFinish();
    }

    private PttManager(Context context) {
        this.context = context;
        articleListParser = new ArticleListParser();
        articleContentParser = new ArticleContentParser();
        articleLists = new ArrayList<ArticleList>();
        allArticles = new ArrayList<Article>();
        pttArticleListListeners = new ArrayList<PttArticleListListener>();
        pttArticleContentListeners = new ArrayList<PttArticleContentListener>();
    }

    public static PttManager getInstance(Context context) {
        if (pttManager == null) {
            pttManager = new PttManager(context);
        }
        return pttManager;
    }

    public PttParser getArticleListParser() {
        return articleListParser;
    }

    public PttParser getArticleContentParser() {
        return articleContentParser;
    }

    public ArrayList<ArticleList> getArticleLists() {
        return articleLists;
    }

    public ArrayList<Article> getAllArticles() {
        return allArticles;
    }

    public void updateAllArticles() {
        allArticles.clear();
        for (ArticleList articleList : articleLists) {
            for (Article article : articleList.getArticleContainer()) {
                allArticles.add(article);
            }
        }
    }

    public void getPttData() {
        Bundle args = new Bundle();
        args.putString(Command.COMMAND_NAME, Command.COMMAND_GET_PTT_DATA);
        context.startService(new Intent(context, PttParserService.class)
                .putExtras(args));
    }

    public void getLatestPttData() {
        Bundle args = new Bundle();
        args.putString(Command.COMMAND_NAME,
                Command.COMMAND_GET_LATEST_PTT_DATA);
        context.startService(new Intent(context, PttParserService.class)
                .putExtras(args));
    }

    public void getOldestPttData() {
        Bundle args = new Bundle();
        args.putString(Command.COMMAND_NAME,
                Command.COMMAND_GET_OLDEST_PTT_DATA);
        context.startService(new Intent(context, PttParserService.class)
                .putExtras(args));
    }

    public void getPttArticle() {
        Bundle args = new Bundle();
        args.putString(Command.COMMAND_NAME, Command.COMMAND_GET_PTT_ARTICLE);
        context.startService(new Intent(context, PttParserService.class)
                .putExtras(args));
    }

    public Article getClickedArticle() {
        return clickedArticle;
    }

    public void setClickedArticle(Article clickedArticle) {
        this.clickedArticle = clickedArticle;
    }

    public String getFirstArticleUrl() {
        return firstArticleUrl;
    }

    public void setFirstArticleUrl(String firstArticleUrl) {
        this.firstArticleUrl = firstArticleUrl;
    }

    public void clear() {
        articleLists.clear();
        allArticles.clear();
        pttArticleListListeners.clear();
        pttArticleContentListeners.clear();
        clickedArticle = null;
        firstArticleUrl = null;
    }

    public ArrayList<PttArticleListListener> getPttArticleListListeners() {
        return pttArticleListListeners;
    }

    public void setPttArticleListListener(PttArticleListListener listener) {
        pttArticleListListeners.add(listener);
    }

    public void removePttArticleListListener(PttArticleListListener listener) {
        pttArticleListListeners.remove(listener);
    }

    public ArrayList<PttArticleContentListener> getPttArticleContentListeners() {
        return pttArticleContentListeners;
    }

    public void setPttArticleContentListener(PttArticleContentListener listener) {
        pttArticleContentListeners.add(listener);
    }

    public void removePttArticleContentListener(
            PttArticleContentListener listener) {
        pttArticleContentListeners.remove(listener);
    }
}
