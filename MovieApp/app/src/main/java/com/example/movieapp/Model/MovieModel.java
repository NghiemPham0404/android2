package com.example.movieapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class MovieModel implements Parcelable {
    private int id;
    private String title;

    private String release_date;

    public MovieModel(int id, String title, String release_date, float vote_average, String poster_path, String backgrop_path, boolean adult, String overriew, List<Integer> gerne_ids) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.backdrop_path = backgrop_path;
        this.adult = adult;
        this.overview = overriew;
        this.gerne_ids = gerne_ids;
    }

    private float vote_average;

    protected MovieModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        release_date = in.readString();
        vote_average = in.readFloat();
        poster_path = in.readString();
        backdrop_path = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
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

    public List<Integer> getGerne_ids() {
        return gerne_ids;
    }

    private String poster_path;

    public MovieModel(int id, String title, String release_date, float vote_average, String poster_path, String backgrop_path) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.backdrop_path = backgrop_path;
    }

    private String backdrop_path;

    private boolean adult;

    private String overview;

    private List<Integer> gerne_ids;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeFloat(vote_average);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
    }
}
