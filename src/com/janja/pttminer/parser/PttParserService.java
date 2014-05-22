package com.janja.pttminer.parser;

import java.io.IOException;
import java.util.ArrayList;

import com.janja.pttminer.data.Article;
import com.janja.pttminer.data.ArticleContent;
import com.janja.pttminer.data.ArticleList;
import com.janja.pttminer.parser.PttManager.PttArticleContentListener;
import com.janja.pttminer.parser.PttManager.PttArticleListListener;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class PttParserService extends IntentService {

    private final static String TAG = "PttParserService";
    private PttManager pttManager;
    private PttParser articleListParser;
    private PttParser articleContentParser;
    private ArrayList<ArticleList> articleLists;
    private ArrayList<PttArticleListListener> pttArticleListListeners;
    private ArticleList firstArticleList;
    private ArticleList secondArticleList;
    private ArticleList oldestArticleList;
    private Article clickedArticle;

    private ArrayList<PttArticleContentListener> pttArticleContentListeners;

    public PttParserService() {
        super(TAG);
        pttManager = PttManager.getInstance(this);
        articleListParser = pttManager.getArticleListParser();
        articleContentParser = pttManager.getArticleContentParser();
        articleLists = pttManager.getArticleLists();
        pttArticleListListeners = pttManager.getPttArticleListListeners();
        if (articleLists.size() >= 2) {
            firstArticleList = articleLists.get(0);
            secondArticleList = articleLists.get(1);
            oldestArticleList = articleLists.get(articleLists.size() - 1);
        }
        pttArticleContentListeners = pttManager.getPttArticleContentListeners();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle arguments = (Bundle) intent.getExtras();
        String command = arguments.getString(Command.COMMAND_NAME);

        try {
            if (command.equals(Command.COMMAND_GET_PTT_DATA)) {
                getPttData();
            } else if (command.equals(Command.COMMAND_GET_LATEST_PTT_DATA)) {
                getLatestPttData();
            } else if (command.equals(Command.COMMAND_GET_OLDEST_PTT_DATA)) {
                getOldestPttData();
            } else if (command.equals(Command.COMMAND_GET_PTT_ARTICLE)) {
                clickedArticle = pttManager.getClickedArticle();
                getPttArticle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPttData() throws IOException {
        articleLists.clear();
        getFirstArticleList();
        getSecondArticleList();
        oldestArticleList = secondArticleList;
        pttManager.updateAllArticles();
        for (PttArticleListListener listener : pttArticleListListeners) {
            listener.getPttDataFinish();
        }
    }

    public void getLatestPttData() throws IOException {
        if (firstArticleList == null) {
            getPttData();
            for (PttArticleListListener listener : pttArticleListListeners) {
                listener.getLatestPttDataFinish();
            }
            return;
        }

        String lastFirstUrl = firstArticleList.getUrl();

        getFirstArticleList();
        String latestUrl = firstArticleList.getUrl();

        boolean firstArticleChanged = !lastFirstUrl.equals(latestUrl);
        if (firstArticleChanged) {
            ArticleList diffArticle = firstArticleList;
            int position = 0;
            do {
                String diffUrl = diffArticle.getLastUrl();
                diffArticle = (ArticleList) articleListParser
                        .parseHtml(diffUrl);
                articleLists.add(++position, diffArticle);
                if (position == 1) {
                    secondArticleList = diffArticle;
                }
            } while (!diffArticle.getUrl().equals(lastFirstUrl));
        }
        pttManager.updateAllArticles();
        for (PttArticleListListener listener : pttArticleListListeners) {
            listener.getLatestPttDataFinish();
        }
    }

    public void getOldestPttData() throws IOException {
        getOldestArticleList();
        pttManager.updateAllArticles();
        for (PttArticleListListener listener : pttArticleListListeners) {
            listener.getOldestPttDataFinish();
        }
    }

    public void getPttArticle() throws IOException {
        getArticleContent();
        for (PttArticleContentListener listener : pttArticleContentListeners) {
            listener.getArticleContentFinish();
        }
    }

    private void getFirstArticleList() throws IOException {
        String url = pttManager.getFirstArticleUrl();
        firstArticleList = (ArticleList) articleListParser.parseHtml(url);
        if (articleLists.size() > 0) {
            articleLists.remove(0);
        }
        articleLists.add(0, firstArticleList);
    }

    private void getSecondArticleList() throws IOException {
        if (firstArticleList == null) {
            return;
        }
        String url = firstArticleList.getLastUrl();

        boolean invalidUrl = url.equals("");
        if (invalidUrl) {
            secondArticleList = firstArticleList;
            return;
        }

        secondArticleList = (ArticleList) articleListParser.parseHtml(url);
        articleLists.add(secondArticleList);
    }

    private void getOldestArticleList() throws IOException {
        if (oldestArticleList == null) {
            return;
        }
        String url = oldestArticleList.getLastUrl();

        boolean invalidUrl = url.equals("");
        if (invalidUrl) {
            return;
        }

        oldestArticleList = (ArticleList) articleListParser.parseHtml(url);
        articleLists.add(oldestArticleList);
    }

    private void getArticleContent() throws IOException {
        String url = clickedArticle.getUrl();

        boolean invalidUrl = url.equals("");
        if (invalidUrl) {
            return;
        }

        ArticleContent articleContent = (ArticleContent) articleContentParser
                .parseHtml(url);

        clickedArticle.setArticleContent(articleContent);
    }
}
