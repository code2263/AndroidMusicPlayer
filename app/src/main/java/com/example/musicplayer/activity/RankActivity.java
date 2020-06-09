package com.example.musicplayer.activity;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayListAdapter;
import com.example.musicplayer.entity.MusicItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度排行榜歌曲列表页面
 */
public class RankActivity extends AppCompatActivity {
    private ImageView bg_view;
    private ImageView img_view;
    private TextView title_text;
    private TextView updateTime_text;
    private TextView desc_text;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        bg_view = findViewById(R.id.rank_billboard_bg_view);
        img_view = findViewById(R.id.rank_billboard_img_view);
        title_text = findViewById(R.id.rank_billboard_title_text);
        updateTime_text = findViewById(R.id.rank_billboard_updateTime_text);
        desc_text = findViewById(R.id.rank_billboard_desc_text);
        recyclerView = findViewById(R.id.rank_recycler);

        List<MusicItem> list = new ArrayList<>();
        PlayListAdapter adapter = new PlayListAdapter(RankActivity.this, R.layout.item_play_list, list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RankActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Integer type = Integer.valueOf(getIntent().getStringExtra("type"));
        new task().execute(type);

        // 返回按钮点击事件(返回上一页)
        ImageButton back_button = findViewById(R.id.rank_back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                            Log.e("rank_back_btn: ", e.toString());
                        }
                    }
                }.start();
            }
        });
    }

    class task extends AsyncTask<Integer, Object, List> {

        @Override
        protected List doInBackground(Integer... integers) {
            List<MusicItem> list = new ArrayList<>();
            // 获取榜单页面数据
            Map<String, Object> bill = getBill(integers[0], 10, 0);
            JSONArray song_list = (JSONArray) bill.get("song_list");
            // 轮询获取封装item
            for(int i = 0; i < song_list.length(); i++){
                try{
                    JSONObject o = song_list.getJSONObject(i);
                    int song_id = o.getInt("song_id");
                    String title = o.getString("title");
                    String author = o.getString("author");
                    Map<String, Object> onlineMusic = getOnlineMusic(song_id);
                    // 将获取的图片url转为bitmap,避免将耗时操作留到Adapter
                    Bitmap bitmap = getBitmap((String) onlineMusic.get("pic_radio"));
                    // 本地音乐为毫秒级的duration,这里乘1000同化
                    Integer duration = ((Integer) onlineMusic.get("duration")) * 1000;
                    list.add(new MusicItem(song_id, title, author, false, bitmap, (String) onlineMusic.get("file_link"), duration));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(bill.get("name"), bill.get("update_date"), bill.get("comment"), bill.get("bg"), bill.get("img"));
            return list;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // 设置组件数据
            title_text.setText(values[0].toString());
            updateTime_text.setText("最近更新: " + values[1].toString());
            desc_text.setText(values[2].toString());
            bg_view.setImageBitmap((Bitmap) values[3]);
            img_view.setImageBitmap((Bitmap) values[4]);
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            // 更新列表
            PlayListAdapter adapter = new PlayListAdapter(RankActivity.this, R.layout.item_play_list, list);
            recyclerView.setAdapter(adapter);
        }

        private Map<String, Object> getBill(int type, int size, int offset) {
            Map<String, Object> map = new HashMap<>();
            try {
                URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&method=baidu.ting.billboard.billList&type=" + type + "&size=" + size + "&offset=" + offset);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                // 将返回的数据转为json格式
                JSONObject jsonObject = jsonParse(inputStream);
                JSONArray song_list = jsonObject.getJSONArray("song_list");
                JSONObject billboard = jsonObject.getJSONObject("billboard");
                // 获取需要的数据
                map.put("song_list", song_list);
                map.put("name", billboard.getString("name"));
                map.put("update_date", billboard.getString("update_date"));
                map.put("comment", billboard.getString("comment"));
                map.put("img", getBitmap(billboard.getString("pic_s640")));
                map.put("bg", getBitmap(billboard.getString("pic_s640")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        private Map<String, Object> getOnlineMusic(int id){
            Map<String, Object> map = new HashMap<>();
            try {
                URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&method=baidu.ting.song.play&songid=" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                // 将返回的数据转为json格式
                JSONObject jsonObject = jsonParse(inputStream);
                JSONObject songinfo = jsonObject.getJSONObject("songinfo");
                JSONObject bitrate = jsonObject.getJSONObject("bitrate");
                // 获取需要的数据
                map.put("file_link", bitrate.getString("file_link"));
                map.put("duration", bitrate.getInt("file_duration"));
                map.put("pic_radio", songinfo.getString("pic_radio"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        private Bitmap getBitmap(String url){
            Bitmap bitmap = null;
            try {
                // 连接网络获取图片
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                // 转化stream流为bitmap
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        private JSONObject jsonParse(InputStream inputStream) {
            JSONObject jsonObject = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                //转换成字符串
                String result = "";
                StringBuilder builder = new StringBuilder();
                while ((result = reader.readLine()) != null) {
                    builder.append(result);
                }
                jsonObject = new JSONObject(builder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}
