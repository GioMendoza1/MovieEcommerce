package edu.uci.ics.gamendo1.service.movies.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import edu.uci.ics.gamendo1.service.movies.MovieService;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Movies {
    public static boolean isUserAllowedToMakeRequest(String email, int plvl) {
        ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String idm = MovieService.getMovieConfigs().getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = MovieService.getMovieConfigs().getIdmConfigs().getPrivilegePath();
        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(idm).path(IDM_ENDPOINT_PATH);
        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email,plvl);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        // Check that status code of the request
        if (response.getStatus() == 200){
            ServiceLogger.LOGGER.info("Recieved status 200");
            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("jsonText: " + jsonText);
            //Still need to map
            ObjectMapper mapper = new ObjectMapper();
            try {
                PrivilegeResponseModel responseModel = mapper.readValue(jsonText, PrivilegeResponseModel.class);
                if (responseModel.getResultCode() == 141)
                    return false;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            ServiceLogger.LOGGER.info("Error");
        }
        return false;
    }

    public static boolean searchingMovieDB(MovieSearchRequestModel requestModel, MovieSearchResponseModel responseModel) {
        try {
            ServiceLogger.LOGGER.info("We are in searchingMovieDB");
            String selectParam = "movies.id, movies.title, movies.director, movies.year, movies.hidden, ratings.rating, ratings.numVotes";
            String query = "SELECT " + selectParam + " FROM movies, genres_in_movies, ratings, genres WHERE movies.id = genres_in_movies.movieId AND movies.id = ratings.movieId AND genres_in_movies.genreId = genres.id ";
            String whereVals[] = new String[5];
            int whereCount = 0;


            if (requestModel.getTitle() != null && !(requestModel.getTitle().equals("null"))) {
                query += "AND movies.title LIKE ? ";
                whereVals[whereCount] = "title";
                whereCount++;

            }

            if (requestModel.getGenre() != null && !(requestModel.getGenre().equals("null"))) {
                query += "AND genres.name LIKE ? ";
                whereVals[whereCount] = "genre";
                whereCount++;
            }

            if (requestModel.getYear() != null) {
                query += "AND movies.year = ? ";
                whereVals[whereCount] = "year";
                whereCount++;
            }

            if (requestModel.getDirector() != null && !(requestModel.getDirector().equals("null"))) {
                query += "AND movies.director LIKE ? ";
                whereVals[whereCount] = "director";
                whereCount++;
            }

            if (!requestModel.getIsHidden() || !requestModel.getHidden()) {
                query += "AND movies.hidden = ? ";
                whereVals[whereCount] = "hidden";
                whereCount++;
            }

            query += ("GROUP BY movies.id ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection() + " LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset());

            ServiceLogger.LOGGER.info("Going to attempt to connect using getCon");
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Passed getCon");

            for (int i = 1; i <= whereCount; i++) {
                if (whereVals[i - 1].equalsIgnoreCase("title")) {
                    ps.setString(i, requestModel.getTitle() + "%");
                } else if (whereVals[i - 1].equalsIgnoreCase("genre")) {
                    ps.setString(i, requestModel.getGenre() + "%");
                } else if (whereVals[i - 1].equalsIgnoreCase("year")) {
                    ps.setInt(i, requestModel.getYear());
                } else if (whereVals[i - 1].equalsIgnoreCase("director")) {
                    ps.setString(i, requestModel.getDirector() + "%");
                } else if (whereVals[i - 1].equalsIgnoreCase("hidden")) {
                    ps.setBoolean(i, false);
                }
            }

            ArrayList<MovieModel> movieList = new ArrayList<>();
            MovieModel[] tempList = new MovieModel[1];
            MovieModel tempMovie;

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            int counter = 0;

            while(rs.next())
            {
                if (Strings.isNullOrEmpty(rs.getString("movies.id")))
                    continue;
                tempMovie = new MovieModel();
                tempMovie.setMovieId(rs.getString("movies.id"));
                tempMovie.setTitle(rs.getString("movies.title"));
                tempMovie.setDirector(rs.getString("movies.director"));
                tempMovie.setYear(rs.getInt("movies.year"));
                tempMovie.setRating(rs.getFloat("ratings.rating"));
                tempMovie.setNumVotes(rs.getInt("ratings.numVotes"));
                if (rs.getBoolean("movies.hidden") != false)
                {
                    tempMovie.setHidden(rs.getBoolean("movies.hidden"));
                }
                movieList.add(tempMovie);
                counter++;
            }
            if (counter == 0) {
                responseModel.setResultCode(211);
                responseModel.setMessage("No movies found with search parameters.");
            }
            else{
                responseModel.setResultCode(210);
                responseModel.setMessage("Found movies with search parameters.");
                responseModel.setMovies(movieList.toArray(tempList));
            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean searchingMovieDBByID(String id, boolean userAllowed, MovieSearchByIDResponseModel responseModel)
    {
        String selectParam = "movies.id, movies.title, movies.director, movies.year, movies.backdrop_path, movies.budget, movies.overview, movies.poster_path, movies.revenue, movies.hidden, GROUP_CONCAT(DISTINCT genres_in_movies.genreId), GROUP_CONCAT(DISTINCT stars_in_movies.starId), ratings.rating, ratings.numVotes";
        String query = "SELECT " + selectParam +  " FROM movies LEFT JOIN genres_in_movies ON movies.id = genres_in_movies.movieId LEFT JOIN stars_in_movies ON movies.id = stars_in_movies.movieId LEFT JOIN ratings ON movies.id = ratings.movieId WHERE movies.id = ? GROUP BY movies.id;";
        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            String genres[];
            String stars[];
            int subCount;
            responseModel.setMovies(new MovieModelFull());
            if(rs.next())
            {
                responseModel.getMovies().setMovieId(rs.getString("movies.id"));
                responseModel.getMovies().setTitle(rs.getString("movies.title"));
                responseModel.getMovies().setDirector(rs.getString("movies.director"));
                responseModel.getMovies().setYear(rs.getInt("movies.year"));
                responseModel.getMovies().setBackdrop_path(rs.getString("movies.backdrop_path"));
                responseModel.getMovies().setBudget(rs.getInt("movies.budget"));
                responseModel.getMovies().setOverview(rs.getString("movies.overview"));
                responseModel.getMovies().setPoster_path(rs.getString("movies.poster_path"));
                responseModel.getMovies().setRevenue(rs.getInt("movies.revenue"));
                Boolean hidden = rs.getBoolean("movies.hidden");
                responseModel.getMovies().setRating(rs.getFloat("ratings.rating"));
                responseModel.getMovies().setNumVotes(rs.getInt("ratings.numVotes"));

                if (hidden && !userAllowed)
                {
                    //responseModel = new MovieSearchByIDResponseModel(141, "User has insufficient privilege.");
                    responseModel.setResultCode(141);
                    responseModel.setMessage("User has insufficient privilege.");
                    return true;
                }

                ResultSet rs2;

                if(!(Strings.isNullOrEmpty(rs.getString("GROUP_CONCAT(DISTINCT genres_in_movies.genreId)"))))
                {
                    genres = rs.getString("GROUP_CONCAT(DISTINCT genres_in_movies.genreId)").split(",");
                    responseModel.getMovies().setGenres(new GenreModel[genres.length]);
                    subCount = 0;
                    String query2 = "SELECT * FROM genres WHERE ";
                    for (int i = 0; i < genres.length; i++)
                    {
                        if (i == 0)
                        {
                            query2 += "id = ? ";
                        }
                        else
                        {
                            query2 += "OR id = ? ";
                        }
                    }
                    ps = MovieService.getCon().prepareStatement(query2);
                    for (int i = 0; i < genres.length; i++)
                    {
                        ps.setInt(i+1, Integer.parseInt(genres[i]));
                    }
                    ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                    rs2 = ps.executeQuery();
                    while(rs2.next())
                    {
                        responseModel.getMovies().getGenres()[subCount] = new GenreModel();
                        responseModel.getMovies().getGenres()[subCount].setId(rs2.getInt("id"));
                        responseModel.getMovies().getGenres()[subCount].setName(rs2.getString("name"));
                        subCount++;
                    }

                }
                else
                {
                    responseModel.getMovies().setGenres(new GenreModel[1]);
                }

                if (!(Strings.isNullOrEmpty(rs.getString("GROUP_CONCAT(DISTINCT stars_in_movies.starId)")))) {
                    stars = rs.getString("GROUP_CONCAT(DISTINCT stars_in_movies.starId)").split(",");
                    responseModel.getMovies().setStars(new StarModel[stars.length]);
                    subCount = 0;
                    String query3 = "SELECT * FROM stars WHERE ";
                    for (int i = 0; i < stars.length; i++)
                    {
                        if (i == 0)
                        {
                            query3 += "id = ? ";
                        }
                        else
                        {
                            query3 += "OR id = ? ";
                        }
                    }
                    ps = MovieService.getCon().prepareStatement(query3);
                    for (int i = 0; i < stars.length; i++)
                    {
                        ps.setString(i+1, stars[i]);
                    }
                    ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                    rs2 = ps.executeQuery();
                    while(rs2.next())
                    {
                        responseModel.getMovies().getStars()[subCount] = new StarModel();
                        responseModel.getMovies().getStars()[subCount].setId(rs2.getString("id"));
                        responseModel.getMovies().getStars()[subCount].setName(rs2.getString("name"));
                        responseModel.getMovies().getStars()[subCount].setBirthYear(rs2.getInt("birthYear"));
                        subCount++;
                    }
                }
                else{
                    responseModel.getMovies().setStars(new StarModel[1]);
                }
                responseModel.setResultCode(210);
                responseModel.setMessage("Found movies with search parameters.");


            }
            else
            {
                responseModel.setResultCode(211);
                responseModel.setMessage("No movies found with search parameters.");
            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();

        }
        return false;


    }

    public static boolean deleteMovieDB(String id, MovieResponseModel responseModel)
    {
        try{
            String query = "SELECT * FROM movies WHERE id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
            {
                responseModel.setResultCode(241);
                responseModel.setMessage("Could not remove movie.");
                return true;
            }
            else if (rs.getBoolean("hidden"))
            {
                responseModel.setResultCode(242);
                responseModel.setMessage("Movie has been already removed.");
                return true;
            }

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            String query2 = "UPDATE movies SET hidden = TRUE WHERE movies.id = ?";
            ps = MovieService.getCon().prepareStatement(query2);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int count = ps.executeUpdate();
            if(count > 0)
            {
                responseModel.setResultCode(240);
                responseModel.setMessage("Movie successfully removed.");
            }
            else{
                responseModel.setResultCode(241);
                responseModel.setMessage("Could not remove movie.");
            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean searchGenreDB(GenreSearchResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM genres";
            PreparedStatement ps = MovieService.getCon().prepareStatement("query");
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ArrayList<GenreModel> tempList = new ArrayList<>();
            GenreModel[] temp = new GenreModel[1];
            GenreModel tempModel;

            while(rs.next())
            {
                tempModel = new GenreModel();
                tempModel.setId(rs.getInt("id"));
                tempModel.setName(rs.getString("name"));
                tempList.add(tempModel);
            }

            responseModel.setGenres(tempList.toArray(temp));
            responseModel.setResultCode(219);
            responseModel.setMessage("Genres successfully retrieved.");
            return true;


        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;

    }

    public static boolean addGenreDB(GenreAddRequestModel requestModel, MovieResponseModel responseModel)
    {
        try
        {
            String query = "SELECT * FROM genres WHERE name = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getName());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                responseModel.setResultCode(218);
                responseModel.setMessage("Genre could not be added.");
                return true;
            }

            String query2 = "INSERT INTO genres(name) VALUES (?);";
            ps = MovieService.getCon().prepareStatement(query2);
            ps.setString(1, requestModel.getName());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());

            int count = ps.executeUpdate();

            if (count > 0)
            {
                responseModel.setResultCode(217);
                responseModel.setMessage("Genre successfully added.");
            }
            else
            {
                responseModel.setResultCode(218);
                responseModel.setMessage("Genre could not be added.");
            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean searchMovieGenresDB(String id, GenreSearchResponseModel responseModel)
    {
        try {
            String query = "SELECT genres.id, genres.name FROM genres_in_movies, genres WHERE genres_in_movies.genreId = genres.id AND genres_in_movies.movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            int counter = 0;
            ArrayList<GenreModel> tempList = new ArrayList<>();
            GenreModel[] temp = new GenreModel[1];
            GenreModel tempModel;
            while(rs.next())
            {
                tempModel = new GenreModel();
                tempModel.setId(rs.getInt("id"));
                tempModel.setName(rs.getString("name"));
                tempList.add(tempModel);
                counter++;
            }
            if (counter > 0)
            {
                responseModel.setResultCode(219);
                responseModel.setMessage("Genres successfully retrieved.");
                responseModel.setGenres(tempList.toArray(temp));
            }
            else
            {
                responseModel.setResultCode(211);
                responseModel.setMessage("No movies found with search parameters.");

            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;

    }

    public static boolean searchingStarDB(StarSearchRequestModel requestModel, StarSearchResponseModel responseModel)
    {
        try{
            String query = "SELECT stars.id, stars.name, stars.birthYear FROM movies, stars, stars_in_movies WHERE stars.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id";

            String whereVals[] = new String[3];
            int whereCount = 0;


            if (requestModel.getName() != null && !(requestModel.getName().equals("null"))) {
                query += "AND stars.name LIKE ? ";
                whereVals[whereCount] = "name";
                whereCount++;
            }
            if (requestModel.getBirthYear() != null)
            {
                query += "AND stars.birthYear = ? ";
                whereVals[whereCount] = "birthYear";
                whereCount++;
            }
            if (requestModel.getMovieTitle() != null && !(requestModel.getMovieTitle().equals("null")))
            {
                query += "AND movies.title LIKE ? ";
                whereVals[whereCount] = "title";
                whereCount++;
            }

            query += ("GROUP BY stars.id ORDER BY " + requestModel.getOrderby() + " " + requestModel.getDirection() + " LIMIT " + requestModel.getLimit() + " OFFSET " + requestModel.getOffset());

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Passed getCon");

            for (int i = 1; i <= whereCount; i++) {
                if (whereVals[i - 1].equalsIgnoreCase("title")) {
                    ps.setString(i, requestModel.getMovieTitle() + "%");
                } else if (whereVals[i - 1].equalsIgnoreCase("birthYear")) {
                    ps.setInt(i, requestModel.getBirthYear());
                } else if (whereVals[i - 1].equalsIgnoreCase("name")) {
                    ps.setString(i, requestModel.getName() + "%");
                }
            }
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            ArrayList<StarModel> starList = new ArrayList<>();
            StarModel[] tempList = new StarModel[1];
            StarModel tempStar;
            int counter = 0;

            while(rs.next()) {
                if (Strings.isNullOrEmpty(rs.getString("stars.id")))
                    continue;
                tempStar = new StarModel();
                tempStar.setId(rs.getString("stars.id"));
                tempStar.setName(rs.getString("stars.name"));
                tempStar.setBirthYear(rs.getInt("stars.birthYear"));
                starList.add(tempStar);
                counter++;
            }
            if (counter == 0) {
                responseModel.setResultCode(213);
                responseModel.setMessage("No stars found with search parameters.");
            }
            else{
                responseModel.setResultCode(212);
                responseModel.setMessage("Found stars with search parameters.");
                responseModel.setStars(starList.toArray(tempList));
            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean searchingStarByIDDB(String id, StarSearchByIDResponseModel responseModel)
    {
        try{
            String query = "SELECT * FROM stars where id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                responseModel.setStars(new StarModel());
                responseModel.getStars().setId(rs.getString("id"));
                responseModel.getStars().setName(rs.getString("name"));
                responseModel.getStars().setBirthYear(rs.getInt("birthYear"));
                responseModel.setResultCode(212);
                responseModel.setMessage("Found stars with search parameters.");
            }
            else
            {
                responseModel.setResultCode(213);
                responseModel.setMessage("No stars found with search parameters.");
            }
            return true;

        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addStarsinDB(StarAddStarsinRequestModel requestModel, MovieResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM movies where id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getMovieid());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
            {
                responseModel.setResultCode(211);
                responseModel.setMessage("No movies found with search parameters.");
                return true;
            }

            String query2 = "SELECT * FROM stars_in_movies WHERE movieId = ? AND starId = ?";
            ps = MovieService.getCon().prepareStatement(query2);
            ps.setString(1, requestModel.getMovieid());
            ps.setString(2, requestModel.getStarid());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                responseModel.setResultCode(232);
                responseModel.setMessage("Star already exists in movie.");
                return true;
            }

            String query3 = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?,?);";
            ps = MovieService.getCon().prepareStatement(query3);
            ps.setString(1, requestModel.getStarid());
            ps.setString(2, requestModel.getMovieid());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int successCount = ps.executeUpdate();
            if (successCount > 0)
            {
                responseModel.setResultCode(230);
                responseModel.setMessage("Star successfully added to movie.");
            }
            else
            {
                responseModel.setResultCode(231);
                responseModel.setMessage("Could not add star to movie.");
            }
            return true;
        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();

        }
        return false;
    }

    public static boolean updateRatingDB(UpdateRatingRequestModel requestModel, MovieResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM movies where id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                responseModel.setResultCode(211);
                responseModel.setMessage("No movies found with search parameters.");
                return true;
            }

            String query2 = "UPDATE ratings SET rating = ?, numVotes = numVotes + 1 WHERE movieId = ?";
            ps = MovieService.getCon().prepareStatement(query2);
            ps.setFloat(1, requestModel.getRating());
            ps.setString(2, requestModel.getId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int successCount = ps.executeUpdate();
            if (successCount > 0)
            {
                responseModel.setResultCode(250);
                responseModel.setMessage("Rating successfully updated.");
            }
            else
            {
                responseModel.setResultCode(251);
                responseModel.setMessage("Could not update rating.");
            }
            return true;
        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();

        }
        return false;
    }

    public static boolean addMovieDB(MovieAddRequestModel requestModel, MovieAddResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM movies where title = ? AND director = ? AND year = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getTitle());
            ps.setString(2, requestModel.getDirector());
            ps.setInt(3, requestModel.getYear());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                responseModel.setResultCode(216);
                responseModel.setMessage("Movie already exists.");
                return true;
            }

            String query2 = "SELECT COUNT(*) as total FROM movies WHERE id LIKE ?;";
            ps = MovieService.getCon().prepareStatement(query2);
            ps.setString(1, "cs00%");
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeQuery();
            rs.next();
            ServiceLogger.LOGGER.info("Query executed");
            String newID = "cs";
            int total = rs.getInt("total") + 1;
            String totalString = Integer.toString(total);
            for (int i = 0; i < (7 - totalString.length()); i++)
            {
                newID += "0";
            }
            newID += totalString;

            String whereVals[] = new String[5];
            int whereCount = 0;


            if (requestModel.getBackdrop_path() != null && !(requestModel.getBackdrop_path().equals("null"))) {
                whereVals[whereCount] = "backdrop_path";
                whereCount++;

            }

            if (requestModel.getBudget() != null) {
                whereVals[whereCount] = "budget";
                whereCount++;
            }

            if (requestModel.getOverview() != null && !(requestModel.getOverview().equals("null"))) {
                whereVals[whereCount] = "overview";
                whereCount++;
            }

            if (requestModel.getPoster_path() != null && !(requestModel.getPoster_path().equals("null"))) {
                whereVals[whereCount] = "poster_path";
                whereCount++;
            }

            if (requestModel.getRevenue() != null) {
                whereVals[whereCount] = "revenue";
                whereCount++;
            }

            String query3 = "INSERT INTO movies(id, title, director, year";
            for (int i = 0; i < whereCount; i++)
            {
                if (whereVals[i].equals("backdrop_path"))
                    query3 += ", backdrop_path";
                if (whereVals[i].equals("budget"))
                    query3 += ", budget";
                if (whereVals[i].equals("overview"))
                    query3 += ", overview";
                if (whereVals[i].equals("poster_path"))
                    query3 += ", poster_path";
                if (whereVals[i].equals("revenue"))
                    query3 += ", revenue";
            }
            query3 += ") VALUES (?,?,?,?";
            for (int i = 0; i < whereCount; i++)
                query3 += ",?";
            query3 += ");";
            ps = MovieService.getCon().prepareStatement(query3);
            ps.setString(1, newID);
            ps.setString(2, requestModel.getTitle());
            ps.setString(3, requestModel.getDirector());
            ps.setInt(4, requestModel.getYear());

            for (int i = 0; i < whereCount; i++)
            {
                if (whereVals[i].equals("backdrop_path"))
                    ps.setString(5 + i, requestModel.getBackdrop_path());
                if (whereVals[i].equals("budget"))
                    ps.setInt(5 + i, requestModel.getBudget());
                if (whereVals[i].equals("overview"))
                    ps.setString(5 + i, requestModel.getOverview());
                if (whereVals[i].equals("poster_path"))
                    ps.setString(5 + i, requestModel.getPoster_path());
                if (whereVals[i].equals("revenue"))
                    ps.setInt(5 + i, requestModel.getRevenue());
            }
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int successCount = ps.executeUpdate();

            if(successCount == 0)
            {
                responseModel.setResultCode(215);
                responseModel.setMessage("Could not add movie.");
                return true;
            }

            String query4 = "SELECT * FROM genres where name = ?";
            for (int i = 0; i < requestModel.getGenres().length; i++)
            {
                ps  = MovieService.getCon().prepareStatement(query4);
                ps.setString(1, requestModel.getGenres()[i].getName());
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                rs = ps.executeQuery();
                if (!rs.next())
                {
                    String query5 = "INSERT INTO genres(name) VALUES(?);";
                    ps = MovieService.getCon().prepareStatement(query5);
                    ps.setString(1, requestModel.getGenres()[i].getName());
                    ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                    ps.execute();
                    String query6 = "SELECT id FROM genres WHERE name = ?";
                    ps = MovieService.getCon().prepareStatement(query6);
                    ps.setString(1, requestModel.getGenres()[i].getName());
                    rs = ps.executeQuery();
                    rs.next();
                }
                int genreId = rs.getInt("id");
                String query6 = "INSERT INTO genres_in_movies(genreId, movieId) VALUES(?,?);";
                ps = MovieService.getCon().prepareStatement(query6);
                ps.setInt(1, genreId);
                ps.setString(2, newID);
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ps.execute();
            }

            String query5 = "INSERT INTO ratings(movieId, rating, numVotes) VALUES (?,0.0,0);";
            ps = MovieService.getCon().prepareStatement(query5);
            ps.setString(1, newID);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();

            responseModel.setResultCode(214);
            responseModel.setMessage("Movie successfully added.");
            responseModel.setMovieid(newID);



            responseModel.setGenreid(new int[requestModel.getGenres().length]);
            String query6 = "SELECT id FROM genres WHERE name = ?";
            int count = 0;
            while(count < requestModel.getGenres().length)
            {

                ps = MovieService.getCon().prepareStatement(query6);
                ps.setString(1, requestModel.getGenres()[count].getName());
                rs = ps.executeQuery();
                rs.next();
                responseModel.getGenreid()[count] = rs.getInt("id");
                count++;
            }
            Arrays.sort(responseModel.getGenreid());
            return true;




        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addStarDB(StarAddRequestModel requestModel, MovieResponseModel responseModel)
    {
        try{
            String query = "SELECT * FROM stars WHERE name = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                responseModel.setResultCode(222);
                responseModel.setMessage("Star already exists.");
                return true;
            }

            String query2 = "SELECT COUNT(*) as total FROM stars WHERE id LIKE ?";
            ps = MovieService.getCon().prepareStatement(query2);
            ps.setString(1, "ss00%");
            rs = ps.executeQuery();
            rs.next();
            String newID = "ss";
            int total = rs.getInt("total") + 1;
            String totalString = Integer.toString(total);
            for (int i = 0; i < (7 - totalString.length()); i++)
            {
                newID += "0";
            }
            newID += totalString;
            if (requestModel.getBirthYear() != null) {
                String query3 = "INSERT INTO stars(id, name, birthYear) VALUES (?,?,?);";
                ps = MovieService.getCon().prepareStatement(query3);
                ps.setString(1, newID);
                ps.setString(2, requestModel.getName());
                ps.setInt(3, requestModel.getBirthYear());
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            }
            else
            {
                String query3 = "INSERT INTO stars(id, name) VALUES (?,?);";
                ps = MovieService.getCon().prepareStatement(query3);
                ps.setString(1, newID);
                ps.setString(2, requestModel.getName());
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            }
            int successCount = ps.executeUpdate();
            if (successCount > 0)
            {
                responseModel.setResultCode(220);
                responseModel.setMessage("Star successfully added.");

            }
            else
            {
                responseModel.setResultCode(221);
                responseModel.setMessage("Could not add star.");
            }
            return true;
        }catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }


}
