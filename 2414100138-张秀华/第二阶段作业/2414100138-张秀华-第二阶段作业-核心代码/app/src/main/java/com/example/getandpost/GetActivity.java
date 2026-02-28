package com.example.getandpost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class GetActivity extends AppCompatActivity {

    private ImageView dogImag;
    private Button btRefresh;
    private String strData;
    private String ImagDataURL;
    private ProgressBar pb;
    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            //接收消息
            if (msg.what == 0) {
                strData = (String) msg.obj;//获取到资源的URL字符串
                try {
                    JSONObject jsonObject = new JSONObject(strData);
                    ImagDataURL = jsonObject.optString("message");
                    new DownloadImageTask(dogImag).execute(ImagDataURL);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get);
        dogImag = findViewById(R.id.iv_dog);
        btRefresh = findViewById(R.id.bt_refresh);
        pb = findViewById(R.id.pb1);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        //按钮刷新
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String stringFromNet = getImagUrl();

                        //发送消息
                        Message message = new Message();
                        message.what = 0;
                        message.obj = stringFromNet;
                        mHandler.sendMessage(message);

                    }
                }).start();
                pb.setVisibility(View.VISIBLE);
                Toast.makeText(GetActivity.this, "开始加载图片啦！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //加载图片
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public DownloadImageTask(ImageView imageView) {
            this.mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageUrl = strings[0];

            try {
                java.net.URL url = new java.net.URL(imageUrl);
                java.io.InputStream inputStream = url.openStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
            pb.setVisibility(View.GONE);
        }
    }


    public static String getImagUrl() {
        String DogImagUrl = NetUtil.ADDRESS;
        String ImagResult = NetUtil.doGet(DogImagUrl);
        return decodeUnicode(ImagResult);
    }


    //解码
    public static String decodeUnicode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }
}