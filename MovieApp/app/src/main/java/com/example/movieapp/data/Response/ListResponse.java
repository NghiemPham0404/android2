package com.example.movieapp.data.Response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListResponse<T> {
    List<T> data;
    boolean success;
    int status;
    int page;
    int total_pages;
    int total_elements;
}
