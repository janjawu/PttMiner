package com.janja.pttminer.activity;

import java.util.ArrayList;

import com.janja.pttminer.data.Article;
import com.janja.pttminer.data.ArticleContent;
import com.janja.pttminer.data.ArticleReply;
import com.janja.pttminer.parser.PttManager;
import com.janja.pttminer.R;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PttArticleActivity extends FragmentActivity implements
        PttManager.PttArticleContentListener {

    public static final String ARTICLE_POSITION = "ArticlePosition";

    private PttManager pttManager;
    private Article clickedArticle;
    private Handler handler;

    private TextView title;
    private TextView writer;
    private TextView time;
    private TextView mainContent;
    private TextView station;
    private TextView urlDescription;
    private LinearLayout replyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt_article);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        handler = new Handler();

        Bundle arguments = (Bundle) getIntent().getExtras();
        int position = arguments.getInt(ARTICLE_POSITION);

        pttManager = PttManager.getInstance(this);
        pttManager.getPttArticle();
        pttManager.setPttArticleContentListener(this);

        clickedArticle = pttManager.getAllArticles().get(position);
        pttManager.setClickedArticle(clickedArticle);
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        writer = (TextView) findViewById(R.id.writer);
        time = (TextView) findViewById(R.id.time);
        mainContent = (TextView) findViewById(R.id.main_content);
        station = (TextView) findViewById(R.id.station);
        urlDescription = (TextView) findViewById(R.id.url_description);
        replyLayout = (LinearLayout) findViewById(R.id.reply);
    }

    @Override
    protected void onDestroy() {
        pttManager.removePttArticleContentListener(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getArticleContentFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
    }

    private void updateUI() {
        ArticleContent articleContent = clickedArticle.getArticleContent();
        String titleText = clickedArticle.getTitle();
        String writerText = articleContent.getWriter();
        String timeText = articleContent.getTime();
        String mainContentText = articleContent.getMainContent();
        String stationText = articleContent.getStation();
        String urlText = articleContent.getUrlDescription();

        title.setText(titleText);
        writer.setText(writerText);
        time.setText(timeText);
        mainContent.setText(mainContentText);
        station.setText(stationText);
        urlDescription.setText(urlText);

        getReplyView(articleContent);

        urlDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(clickedArticle.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void getReplyView(ArticleContent aritleContents) {
        ArrayList<ArticleReply> replyContainer = aritleContents
                .getReplyContainer();

        for (ArticleReply reply : replyContainer) {
            View item = View.inflate(this, R.layout.ptt_article_reply_item,
                    null);
            TextView author = (TextView) item.findViewById(R.id.author);
            TextView time = (TextView) item.findViewById(R.id.time);
            TextView content = (TextView) item.findViewById(R.id.content);

            String type = reply.getType();
            if (type.contains("±À")) {
                author.setBackgroundColor(Color.argb(200, 0, 139, 0));
            } else if (type.contains("¼N")) {
                author.setBackgroundColor(Color.argb(200, 205, 0, 0));
            }
            author.setText(reply.getAuthor());
            time.setText(reply.getTime());
            content.setText(reply.getContent());
            replyLayout.addView(item);
        }
    }
}
