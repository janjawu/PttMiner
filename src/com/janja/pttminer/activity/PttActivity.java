package com.janja.pttminer.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.janja.kit.view.SwipeRefreshListView;
import com.janja.pttminer.data.Article;
import com.janja.pttminer.parser.PttManager;
import com.janja.pttminer.R;

public class PttActivity extends FragmentActivity implements
        PttManager.PttArticleListListener,
        SwipeRefreshListView.SwipeRefreshListViewListener, OnItemClickListener {

    public static final String ACTIVITY_NAME = "ActivityName";
    public static final String ACTIVITY_STOCK = "Stock";
    public static final String ACTIVITY_NBA = "Nba";
    public static final String ACTIVITY_NBA_FILM = "NbaFilm";

    private PttManager pttManager;
    private Handler handler;
    private SwipeRefreshListView listView;
    private PttFullListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pttManager = PttManager.getInstance(this);
        handler = new Handler();

        listView = (SwipeRefreshListView) findViewById(R.id.listView);
        adapter = new PttFullListAdapter(this);
        listView.setAdapter(adapter);
        listView.setSwipeRefreshListViewListener(this);
        listView.setAutoLoadEnable(true);
        listView.setOnItemClickListener(this);

        Bundle arguments = (Bundle) getIntent().getExtras();
        String activityName = arguments.getString(ACTIVITY_NAME);
        String url = "";
        if (activityName.equals(ACTIVITY_NBA)) {
            actionBar.setTitle(R.string.ptt_nba);
            url = "http://www.ptt.cc/bbs/NBA/index.html";
        } else if (activityName.equals(ACTIVITY_NBA_FILM)) {
            actionBar.setTitle(R.string.ptt_nba_film);
            url = "http://www.ptt.cc/bbs/NBA_Film/index.html";
        } else if (activityName.equals(ACTIVITY_STOCK)) {
            actionBar.setTitle(R.string.ptt_stock);
            url = "http://www.ptt.cc/bbs/Stock/index.html";
        }

        pttManager.setPttArticleListListener(this);
        pttManager.setFirstArticleUrl(url);
        pttManager.getPttData();
    }

    @Override
    protected void onDestroy() {
        pttManager.clear();
        super.onDestroy();
    }

    @Override
    public void getPttDataFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listView.setRefreshTime(getSystemTime());
                adapter.updatePttFullData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void getLatestPttDataFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listView.stopRefresh();
                listView.setRefreshTime(getSystemTime());
                adapter.updatePttFullData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void getOldestPttDataFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listView.stopLoadMore();
                adapter.updatePttFullData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRefresh() {
        pttManager.getLatestPttData();
    }

    @Override
    public void onLoadMore() {
        pttManager.getOldestPttData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Article article = pttManager.getAllArticles().get(position - 1);
        Boolean validUrl = !article.getUrl().equals("");
        if (validUrl) {
            Bundle args = new Bundle();
            args.putInt(PttArticleActivity.ARTICLE_POSITION, position - 1);
            Intent intent = new Intent(this, PttArticleActivity.class)
                    .putExtras(args);
            startActivity(intent);
        }
    }

    private String getSystemTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private class PttFullListAdapter extends BaseAdapter {

        Context context;
        private ArrayList<Article> allArticles;

        public PttFullListAdapter(Context context) {
            this.context = context;
            this.allArticles = new ArrayList<Article>();
            updatePttFullData();
        }

        @Override
        public int getCount() {
            return allArticles.size();
        }

        @Override
        public Object getItem(int position) {
            return allArticles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            if (item == null) {
                item = View.inflate(context, R.layout.ptt_full_list_item, null);
            }

            TextView type = (TextView) item.findViewById(R.id.article_type);
            TextView replyCount = (TextView) item
                    .findViewById(R.id.article_reply_count);
            TextView title = (TextView) item.findViewById(R.id.article_title);
            TextView author = (TextView) item.findViewById(R.id.article_author);
            TextView date = (TextView) item.findViewById(R.id.article_date);

            String typeText = allArticles.get(position).getType();
            String replyCountText = allArticles.get(position).getNrec();
            String titleText = allArticles.get(position).getTitle();
            String authorText = allArticles.get(position).getAuthor();
            String dateText = allArticles.get(position).getDate();

            type.setText(typeText);
            replyCount.setText(replyCountText);
            title.setText(titleText);
            author.setText(authorText);
            date.setText(dateText);

            return item;
        }

        private void updatePttFullData() {
            allArticles.clear();
            for (Article article : pttManager.getAllArticles()) {
                allArticles.add(article);
            }
        }
    }

}
