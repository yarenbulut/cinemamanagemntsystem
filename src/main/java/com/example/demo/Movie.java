package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String summary;
    private String posterUrl;
    private int genre;

    public Movie(int id, String title, String summary, String posterUrl, int  genre) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.posterUrl = posterUrl;
        this.genre = genre;
    }

    public Movie() {

    }


    public String getTitle() { return title; }
    public int getGenre() { return genre; }

    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public void setTitle(String title) {
        this.title = title;
    }


    public List<String> searchByGenre(String genreName) {
        List<String> movies = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String SELECT_QUERY = "SELECT m.movie_id, m.title, m.summary, m.poster_url, g.genre_name " +
                    "FROM firmms.movies m " +
                    "JOIN firmms.genre g ON m.genre = g.genre_id " +
                    "WHERE g.genre_name = ? ";

            PreparedStatement statement = connection.prepareStatement(SELECT_QUERY);
            statement.setString(1, genreName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                movies.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<String> searchByPartialName(String partialName) {
        List<String> movies = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String SELECT_QUERY = "SELECT m.movie_id, m.title, m.summary, m.poster_url " +
                    "FROM firmms.movies m " +
                    "WHERE m.title LIKE ? ";

            PreparedStatement statement = connection.prepareStatement(SELECT_QUERY);
            statement.setString(1, "%" + partialName + "%");

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                movies.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<String> searchByFullname(String fullname) {
        List<String> movies = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String SELECT_QUERY = "SELECT m.movie_id, m.title, m.summary, m.poster_url " +
                    "FROM firmms.movies m " +
                    "WHERE m.title LIKE ? ";

            PreparedStatement statement = connection.prepareStatement(SELECT_QUERY);
            statement.setString(1,fullname);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                movies.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public Movie selectedMovie(String selectedMovie) {
        Movie movie = new Movie();
        try (Connection connection = Database.getConnection()) {
            String SELECT_QUERY = "SELECT m.movie_id, m.title, m.summary, m.poster_url " +
                    "FROM firmms.movies m " +
                    "WHERE m.title = ? ";

            PreparedStatement statement = connection.prepareStatement(SELECT_QUERY);
            statement.setString(1, selectedMovie);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                movie.setId(rs.getInt("movie_id"));
                movie.setTitle(rs.getString("title"));
                movie.setSummary(rs.getString("summary"));
                movie.setPosterUrl(rs.getString("poster_url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }

}
