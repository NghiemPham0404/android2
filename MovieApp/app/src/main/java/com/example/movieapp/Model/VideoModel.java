package com.example.movieapp.Model;

public class VideoModel {

    private String id;
    private String key;

    public String getUrl() {
        return url;
    }
    public String getUrl720(){
        return url720;
    }
    private String url;
    private String url720;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    private String type;

    public VideoModel(String id, String key, String type) {
        this.id = id;
        this.key = key;
        this.type = type;
    }

    public VideoModel(String url, String url720){
        this.url = url;
        this.url720 = url;
    }

}
