package com.example.movieapp.data.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieModel implements Parcelable, Comparable<MovieModel> {
    private int id;
    private String title;

    private String release_date;

    private String backdrop_path;

    private boolean adult;

    private String overview;

    private float vote_average;

    private String poster_path;
    private List<Genre> genres;

    private Videos videos;
    private Credits credits;

    public int runtime;

    public String videoUrl;
    public String videoUrl720;
    public Date favorTime;
    private String duration;

    private List<CountryModel> production_countries;
    private ExternalLinkModel external_ids;


    public String getFavorTime() {
        if(favorTime!=null)
            return favorTime.toString();
        else
            return new Date(System.currentTimeMillis()).toGMTString();
    }


    protected MovieModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        release_date = in.readString();
        backdrop_path = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        vote_average = in.readFloat();
        poster_path = in.readString();
        genres = in.createTypedArrayList(Genre.CREATOR);
        runtime = in.readInt();
        duration = in.readString();
        videoUrl = in.readString();
        videoUrl720 = in.readString();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };


    public String getHistoryDate() {
        if (duration == null)
            return null;
        else
            return duration.split("-")[1];
    }

    public String getHistory(){
        if(duration == null)
            return null;
        else {
            String year_month_day_split  =  getHistoryDate();
            String[] date_split = year_month_day_split.split("/");
            int year = Integer.parseInt(date_split[0]);
            int month = Integer.parseInt(date_split[1]);
            int day = Integer.parseInt(date_split[2]);

            Date history_date = new Date(year-1900, month-1, day);
            Date today = new Date(System.currentTimeMillis());

            SimpleDateFormat df;
            if(history_date.getYear() == today.getYear()){
                if(history_date.getMonth() == today.getMonth()){
                    if(today.getDate() == day){
                        return "Today";
                    }else if(today.getDate()-7<= day){
                        return "This week";
                    }else{
                        return "This month";
                    }
                }else{
                    df = new SimpleDateFormat("MMMM ");
                    return df.format(history_date);
                }
            }else{
                df = new SimpleDateFormat("MMMM yyyy");
                return  df.format(history_date);
            }
        }
    }

    public long getPlayBackPositition(){
        if(duration == null) {
            return 0;
        }
        else return Long.parseLong(duration.split("-")[0]);
    }

    public String convertMillisecondsToHMmSs(long milliseconds) {
        long seconds = milliseconds/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h,m,s);
    }

    public String getPlayBackPositionString(){
        return convertMillisecondsToHMmSs(this.getPlayBackPositition());
    }

    public MovieModel(int id, String title, String release_date, float vote_average, String poster_path, String backgrop_path, boolean adult, String overriew, List<Genre> genres, int runtime, Videos videos, Credits credits, List<CountryModel> production_countries, ExternalLinkModel external_ids) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.backdrop_path = backgrop_path;
        this.adult = adult;
        this.overview = overriew;
        this.genres = genres;
        this.runtime = runtime;
        this.videos =videos;
        this.credits = credits;
        this.production_countries = production_countries;
        this.external_ids = external_ids;
    }

    public String getProductionCountry(){
        if(production_countries!=null){
            if(production_countries.size()>0){
                return production_countries.get(0).getName();
            }else{
                return "";
            }
        }else{
            return null;
        }
    }

    public String getPublishDate(){
        try {
            String[] date_split = this.getRelease_date().split(",");
            int year = Integer.parseInt(date_split[date_split.length-1]);
            return year + "";
        } catch (Exception e) {
            try {
                String year =this.getRelease_date().split("-")[0];
                return year;
            }catch (Exception ex){
                return getRelease_date();
            }
        }
    }

    public String getRating(){
        float rating = Math.round(getVote_average()*10)*1.0f/10;
        return rating+"";
    }

    public String getMaxDurationTime(){
        if(getRuntime() > 60){
            int hour = getRuntime()/60;
            int min_less = getRuntime()%60;
            String hour_str = (hour > 0)? hour + "h " : null;
            return hour_str + min_less + "m";
        }else
            return this.runtime+"m";
    }

    public String getGenresString(){
        String genres_str = "";
        if(genres!=null){
            for(Genre genre : genres){
                if(genres_str.equalsIgnoreCase("")){
                    genres_str+=genre.getName();
                }else{
                    genres_str+=", "+genre.getName();
                }
            }
        }
        return genres_str;
    }

    public String getGenresIdString(){
        String genres_str = "";
        for(Genre genre : genres){
            if(genres_str.equalsIgnoreCase("")){
                genres_str+=genre.getId();
            }else{
                genres_str+=","+genre.getId();
            }
        }
        return genres_str;
    }

    public String getTrailer(){
        if(getVideos()==null)
            return null;
        else {
            if (!getVideos().getResults().isEmpty()) {
                for (int i = 0; i < getVideos().getResults().size(); i++) {
                    if (getVideos().getResults().get(i).getType().equalsIgnoreCase("Trailer")) {
                        return "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + getVideos().getResults().get(i).getKey() + "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay='true'; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen=\"true\"></iframe>";
                    }
                }
                return "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + getVideos().getResults().get(0).getKey() + "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay='true'; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen=\"true\"></iframe>";
            }else{
                return null;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeString(backdrop_path);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeFloat(vote_average);
        dest.writeString(poster_path);
        dest.writeTypedList(genres);
        dest.writeInt(runtime);
        dest.writeString(duration);
        dest.writeString(videoUrl);
        dest.writeString(videoUrl720);
    }

    @Override
    public int compareTo(MovieModel o) {
        return this.getTitle().compareTo(o.getTitle());
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Genre implements  Parcelable{
        private int id;
        private String name;

        protected Genre(Parcel in) {
            id = in.readInt();
            name = in.readString();
        }

        public static final Creator<Genre> CREATOR = new Creator<Genre>() {
            @Override
            public Genre createFromParcel(Parcel in) {
                return new Genre(in);
            }

            @Override
            public Genre[] newArray(int size) {
                return new Genre[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
                dest.writeInt(id);
                dest.writeString(name);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Videos{
        List<VideoModel> results;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Credits{
        public List<PersonModel> cast;
        public List<PersonModel> crew;
    }
}
