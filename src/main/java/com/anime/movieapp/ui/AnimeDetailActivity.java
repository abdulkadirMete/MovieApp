package com.anime.movieapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.movieapp.R;
import com.anime.movieapp.adapters.EpisodesAdapter;
import com.anime.movieapp.data.RealmHelper;
import com.anime.movieapp.data.SQliteHelper;
import com.anime.movieapp.models.Anime;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.realm.Realm;
import io.realm.RealmList;

public class AnimeDetailActivity extends AppCompatActivity implements EpisodesAdapter.OnEpisodeListener {

    private ImageView img_anime, img_cover;
    private TextView tv_name,tv_describtion;
    private Button btn_end;
    private NestedScrollView nsv;
    private RecyclerView rv_episode;
    private EpisodesAdapter adapter;
    private Anime anime;
    private Context context = this;
    private ToggleButton btn_bookmark;
    private SQliteHelper db;
    private List<String> epPageList,epNameList;
    private EpisodesAdapter.OnEpisodeListener onEpisodeListener = this;
    private ProgressBar pb_detail;
    private Realm realm;
    private RealmList<Integer> epHistoryList;
    private boolean bookmarkState = false;
    private String currentUrl;

    public static final String POST_URL ="https://www.anizm.tv/islem/fansub_ajax.asp";
    public static final String POST_INNER_URL ="https://www.anizm.tv/islem/video.asp";
    private List<String> fansubNameList,fansubList;
    private List<String> videoProviderNameList,videoProviderList;
    private StringBuilder fansubRequest,videoSourceRequest,absStreamUrl;
    private String outText;
    private Boolean lockState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_anime_detail);
        overridePendingTransition(R.anim.slide_top, R.anim.slide_bottom);

        init();
        setupEpisodes();
        checkBookmark();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        btn_end = findViewById(R.id.btn_end);
        nsv = findViewById(R.id.nsv);
        rv_episode = findViewById(R.id.rv_episode);
        img_anime = findViewById(R.id.detail_img_anime);
        img_cover = findViewById(R.id.detail_img_cover);
        tv_describtion = findViewById(R.id.detail_anime_desc);
        tv_name = findViewById(R.id.detail_anime_title);
        btn_bookmark = findViewById(R.id.btn_bookmark);
        pb_detail = findViewById(R.id.pb_detail);
        realm = Realm.getDefaultInstance();
        anime = (Anime) getIntent().getSerializableExtra("anime");
        tv_name.setText(anime.getName());
        RequestOptions options = new RequestOptions().fitCenter();
        Glide.with(this).load(anime.getCoverUrl()).apply(options).into(img_cover);
        Glide.with(this).load(anime.getImageUrl()).apply(options).into(img_anime);
        tv_describtion.setText(anime.getDescribtion());
        getSupportActionBar().setTitle(anime.getName());
        RealmHelper history = new RealmHelper(realm,anime.getKey());
        epHistoryList = history.getEpHistoryList();
        db = new SQliteHelper(context);

        epPageList = new ArrayList<>();
        epNameList = new ArrayList<>();
        videoProviderList = new ArrayList<>();
        videoProviderNameList = new ArrayList<>();


        btn_bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!btn_bookmark.isPressed()) {
                    return;
                }
                if (isChecked) {
                    bookmarkAdd();
                    bookmarkState = false;
                } else {
                    bookmarkRemove();
                    bookmarkState = true;
                }
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nsv.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    private void setupEpisodes() {
        new SetupEpisode().execute(anime.getPageUrl());
    }

    private void bookmarkAdd() {
        try {
            db.animeAdd(anime);
            Toast.makeText(context, R.string.saveToast, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bookmarkRemove() {
        try {
            String key = anime.getKey();
//            db.onUpgrade(db.getWritableDatabase(),1,2);
            db.animeDelete(key);

            Toast.makeText(context, R.string.unsaveToast, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkBookmark() {
        String key = anime.getKey();
        btn_bookmark.setChecked(db.isChecked(key));
    }

    private class SetupEpisode extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String page_url = strings[0];
                Elements tag_a;
                Document doc = Jsoup.connect(page_url).get();
                Elements episodes = doc.select("div.four");
                for (int i = 0; i < episodes.size(); i++) {
                    tag_a = episodes.get(i).getElementsByTag("a");
                    epPageList.add(tag_a.attr("abs:href"));
                    epNameList.add(tag_a.text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new EpisodesAdapter(context,epNameList,onEpisodeListener,epHistoryList);
            rv_episode.setAdapter(adapter);
            rv_episode.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            pb_detail.setVisibility(View.GONE);

        }

    }

    @Override
    public void onEpClick(int position) {
        currentUrl = epPageList.get(position);
        RealmHelper realmHelper = new RealmHelper(context,realm,anime.getKey(),position);
        realmHelper.push();
        new WatchAbleState().execute(currentUrl);

    }

    @Override
    public void finish() {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("bookmarkState", bookmarkState);
            setResult(RESULT_OK, returnIntent);
            super.finish();
    }

    public class getPageSource extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            try {
                URL url = new URL(param[1]);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                String urlParameters = param[0];
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
                connection.setRequestProperty("ACCEPT-LANGUAGE","tr,tr-TR;q=0.9,en-US;q=0.8,en;q=0.7");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());

                dStream.writeBytes(urlParameters);
                dStream.flush();

                dStream.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line="";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null){
                    responseOutput.append(line);
                }
                br.close();
                outText = responseOutput.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!lockState){
                new getVideoSources().execute(outText);

            }else{
                new getStreamSource().execute(outText);
                lockState = false;
            }
        }
    }

    private class getFansubs extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                fansubList = new ArrayList<>();
                fansubNameList = new ArrayList<>();
                Document doc = Jsoup.connect(currentUrl).get();
                Elements fansubElements = doc.select("div#fansec a");
                for (int i = 0; i < fansubElements.size(); i++) {
                    fansubList.add(fansubElements.get(i).attr("translator"));
                    fansubNameList.add(fansubElements.get(i).text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if(fansubNameList.size()>0){
                final String[] simpleArray = fansubNameList.toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getText(R.string.fansubChoice));
                builder.setItems(simpleArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cleanFansubRequest(fansubList.get(which));

                        if(fansubRequest.length()>0){
                            new getPageSource().execute(fansubRequest.toString(),POST_URL);
                        }else{
                            Toast.makeText(context,getText(R.string.vidoUrlNotFound),Toast.LENGTH_SHORT).show();
                        }

                    }
                }).show();



                super.onPostExecute(aVoid);
            }else{
                Toast.makeText(context,getText(R.string.fansubNotFound),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cleanFansubRequest(String rawRequest){
        fansubRequest = new StringBuilder();
        if(rawRequest.contains(",")){
        String[] funcNumbers = rawRequest.replaceAll("[^0-9 | ^,]", "").replace(" ","").split(",");
            fansubRequest.append("SeriID=" + funcNumbers[0])
                    .append("&BolumID=" + funcNumbers[1])
                    .append("&CevirmenID=" + funcNumbers[2])
                    .append("&Sira=" + funcNumbers[3]);
        }

    }

    private void cleanVideoSourcesRequest(String videoSourceFunc){
        videoSourceRequest = new StringBuilder();
        String videoNumber = videoSourceFunc.replaceAll("[^0-9]","").replace(" ","");
        videoSourceRequest.append("ID=" + videoNumber );
    }

    private class getVideoSources extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Document doc = Jsoup.parse(strings[0]);
            Elements videoProviders = doc.select("a[onclick]");
            for (int i = 0; i < videoProviders.size(); i++) {
                videoProviderList.add(videoProviders.get(i).attr("onclick"));
                videoProviderNameList.add(videoProviders.get(i).getElementsByTag("a").text());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(videoProviderNameList.size()>0) {
                final String[] simpleArray = videoProviderNameList.toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getText(R.string.videoProvideChoice));
                builder.setItems(simpleArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(videoProviderNameList.get(which) != "Güncellenecek"){
                            cleanVideoSourcesRequest(videoProviderList.get(which));
                            lockState = true;
                            new getPageSource().execute(videoSourceRequest.toString(), POST_INNER_URL);
                        }
                    }
                }).show();
            }else{
                Toast.makeText(context,getText(R.string.providersNotFound),Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }

    private class getStreamSource extends AsyncTask<String,Void,Void>{
        private Boolean lockState = false;
        @Override
        protected Void doInBackground(String... param) {
            absStreamUrl = new StringBuilder();
            Document doc = Jsoup.parse(param[0]);
            if(doc.select("iframe").size()>0){
                Elements source_urls = doc.select("iframe");
                String mStreamUrl = source_urls.get(0).attr("src");
                lockState = true;
                if(!mStreamUrl.contains("https:")){
                    absStreamUrl.append("https:")
                            .append(mStreamUrl);
                }else{
                    absStreamUrl.append(mStreamUrl);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(lockState) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", absStreamUrl.toString());
                startActivity(intent);
                lockState = false;
            }else{
                Toast.makeText(context,getText(R.string.vidoUrlNotFound),Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);

        }
    }

    private class WatchAbleState extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... param) {
            try {
                videoProviderList.clear();
                videoProviderNameList.clear();
                Document doc =  Jsoup.connect(param[0]).get();
                Elements iframe = doc.select("iframe");
                Elements videoProviders = doc.select("a[onclick]");

                if(iframe.size()>1){//bu satır anizme göre değişebilir fansub sayfası ve video provider sayfası arasındaki fark iframe farkıdır.
                    for (int i = 0; i < videoProviders.size(); i++) {
                        videoProviderList.add(videoProviders.get(i).attr("onclick"));
                        videoProviderNameList.add(videoProviders.get(i).getElementsByTag("a").text());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(videoProviderList.size()>0){
                final String[] simpleArray = videoProviderNameList.toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Video Kaynağı Seçimi");
                builder.setItems(simpleArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(videoProviderNameList.get(which) != "Güncellenecek"){
                            cleanVideoSourcesRequest(videoProviderList.get(which));
                            lockState = true;
                            new getPageSource().execute(videoSourceRequest.toString(), POST_INNER_URL);
                        }
                    }
                }).show();
            }else{
                new getFansubs().execute();
            }
            super.onPostExecute(aVoid);

        }
    }

}
