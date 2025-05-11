package com.example.movieapp.data.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractionDTO {
    int user_id;
    int movie_id;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class InteractionDAOExtended extends InteractionDTO {
        boolean favor;
        String play_progress;
        Date time_favor;
        String url;

        public String getHistoryDate(){
            if(play_progress == null)
                return null;
            else
                return play_progress.split("-")[1];
        }

        public String getHistory(){
            if(play_progress == null)
                return null;
            else {
                String year_month_day_split  = getHistoryDate();
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
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class InteractionDAOReview extends InteractionDTO {
        float rating;
        String review;
        Date time;
        String name;
        String avatar;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class InteractionDAOReviewAdd extends InteractionDTO {
        String review;
        float rating;
    }
}
