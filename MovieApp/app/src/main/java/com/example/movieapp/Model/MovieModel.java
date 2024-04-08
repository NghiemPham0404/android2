package com.example.movieapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

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

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration){
        this.duration = duration;
    }
    private String duration;


    public MovieModel(int id, String title, String release_date, float vote_average, String poster_path, String backgrop_path, boolean adult, String overriew, List<Genre> genres, int runtime, Videos videos, Credits credits) {
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
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public float getVote_average() {
        return vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackgrop_path() {
        return backdrop_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverriew() {
        return overview;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public int getRuntime() {
        return runtime;
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
        for(Genre genre : genres){
           if(genres_str.equalsIgnoreCase("")){
                genres_str+=genre.getName();
           }else{
                genres_str+=", "+genre.getName();
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


    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public Videos getVideos() {
        return videos;
    }

    public String getTrailer(){
        if(getVideos()==null)
            return null;
        else {
            if (getVideos().getResults().size() > 0) {
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

    public Credits getCredits() {
        return credits;
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
    }

    @Override
    public int compareTo(MovieModel o) {
        return this.getTitle().compareTo(o.getTitle());
    }


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

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Genre(int id, String name){
            this.id = id;
            this.name = name;
        }

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

    public class Videos{
        public List<VideoModel> getResults() {
            return results;
        }

        List<VideoModel> results;

        public Videos(List<VideoModel> results){
            this.results = results;
        }
    }

    public class Credits{
        public List<CastModel> cast;

        public Credits(List<CastModel> cast, List<CastModel> crew) {
            this.cast = cast;
            this.crew = crew;
        }

        public List<CastModel> crew;
        public List<CastModel> getCast() {
            return cast;
        }

        public List<CastModel> getCrew() {
            return crew;
        }
    }
}
