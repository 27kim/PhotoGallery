package com.d27.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG = FlickrFetchr.class.getSimpleName();
    private static final String API_KEY = "470195bba985c84bd483cb5d75524b89";

    List<GalleryItem> items = new ArrayList<>();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream is = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + " : with " + urlSpec + " / code : " + connection.getResponseCode());
            }

            int bytesRead = 0;

            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/").buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON : " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
//            parseItems(items, jsonObject);
            parseItems_gson(jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Falied to fech items", e);
        } catch (JSONException e) {
            Log.e(TAG, "Falied to parse JSON", e);
            e.printStackTrace();
        }
        return items;

    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) {
        try {
            JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
            JSONArray photsJsonArray = photosJsonObject.getJSONArray("photo");

            for (int i = 0; i < photsJsonArray.length(); i++) {
                JSONObject photoJsonObject = photsJsonArray.getJSONObject(i);
                GalleryItem item = new GalleryItem();
                item.setId(photoJsonObject.getString("id"));
                item.setCaption(photoJsonObject.getString("title"));
                if (!photoJsonObject.has("url_s")) {
                    continue;
                }
                item.setUrl(photoJsonObject.getString("url_s"));
                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void parseItems_gson(String jsonString) {
        List<GalleryItem> data = Arrays.asList(new Gson().fromJson(jsonString, GalleryItem[].class));

        items = data;
    }
}