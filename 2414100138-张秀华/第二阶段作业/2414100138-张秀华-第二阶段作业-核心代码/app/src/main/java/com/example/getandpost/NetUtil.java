package com.example.getandpost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class NetUtil {

    public static String ADDRESS = "https://dog.ceo/api/breeds/image/random";
    public static String POST_ADDRESS = "https://jsonplaceholder.typicode.com/posts";


    public static String doGet(String url) {

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            URL requestUr1 = new URL(url);
            httpURLConnection = (HttpURLConnection) requestUr1.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

            if (builder.length() == 0) {
                return null;
            }

            bookJSONString = builder.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookJSONString;
    }


    public static boolean doPost(String urlStr) {
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        boolean result = false;

        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);

            String postJson = "{\n" +
                    "  \"userId\": 1,\n" +
                    "  \"title\": \"创建新帖子\",\n" +
                    "  \"body\": \"内容\"\n" +
                    "}";

            outputStream = urlConnection.getOutputStream();
            outputStream.write(postJson.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            int code = urlConnection.getResponseCode();
            if (code == 201) {
                result = true;
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public static String paramMapToString(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, String>> entries = paramMap.entrySet();

        for (Map.Entry<String, String> entry :
                entries) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();

    }


}
