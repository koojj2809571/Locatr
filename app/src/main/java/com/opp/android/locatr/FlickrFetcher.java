package com.opp.android.locatr;

import android.location.Location;
import android.net.Uri;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.opp.android.locatr.GalleryBeans.PhotosBean.PhotoBean;

/**
 * Created by OPP on 2017/8/10.
 */

public class FlickrFetcher {
    private static final String TAG = "FlickrFetcher";

    private static final String API_KEY = "095207080a6729af611f894b6a42de5a";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public byte[] getUrlBytes(String urlSpec)throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlSpec)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().bytes();
    }

    public String getUrlString(String urlSpec)throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    private List<PhotoBean> parseItems(String jsonData){
        GalleryBeans galleryBeans = new Gson().fromJson(jsonData,GalleryBeans.class);
        return galleryBeans.getPhotos().getPhoto();
    }

    private String buildUrl(String method,String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method" , method);
        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text",query);
        }

        return uriBuilder.build().toString();
    }
    
    private String buildUrl(Location location){
        return ENDPOINT.buildUpon()
                .appendQueryParameter("method",SEARCH_METHOD)
                .appendQueryParameter("lat",location.getLatitude()+"")
                .appendQueryParameter("lon",location.getLongitude()+"")
                .build().toString();
    }
    
    public List<PhotoBean> searchPhotos(Location location){
        String url = buildUrl(location);
        return downloadGalleryItens(url);
    }

    private List<PhotoBean> downloadGalleryItens(String url) {
        List<PhotoBean> photos = new ArrayList<>();
        try {
            String jsonString = getUrlString(url);
            photos = parseItems(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photos;
    }
}
