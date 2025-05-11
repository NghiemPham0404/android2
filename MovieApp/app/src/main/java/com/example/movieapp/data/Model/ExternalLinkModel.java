package com.example.movieapp.data.Model;

public class ExternalLinkModel {
    int id;
    private String facebook_id;

    public int getId() {
        return id;
    }
    private String  instagram_id;
    private String  tiktok_id;
    private  String  twitter_id;
    private String  youtube_id;

    public ExternalLinkModel(int id, String facebook_id, String instagram_id, String tiktok_id, String twitter_id, String youtube_id) {
        this.id = id;
        this.facebook_id = facebook_id;
        this.instagram_id = instagram_id;
        this.tiktok_id = tiktok_id;
        this.twitter_id = twitter_id;
        this.youtube_id = youtube_id;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public String getInstagram_id() {
        return instagram_id;
    }

    public String getTiktok_id() {
        return tiktok_id;
    }

    public String getTwitter_id() {
        return twitter_id;
    }

    public String getYoutube_id() {
        return youtube_id;
    }


}
