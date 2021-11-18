package com.wx3eng.gamideo.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx3eng.gamideo.servlet.Game;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TwitchClient {
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;
    private static String TOKEN;
    private static String CLIENT_ID;

    private static void getConfig() throws IOException {
        Properties prop = new Properties();
        String propFileName = "configTwitch.properties";
        InputStream inputStream = TwitchClient.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        TOKEN = prop.getProperty("TOKEN");
        CLIENT_ID = prop.getProperty("CLIENT_ID");
    }

    private String buildTwitchAccessURL(String url, String gameName, int limit) {
        if (gameName.equals("")) {
            return String.format(url, limit);
        } else {
            try {
                gameName = URLEncoder.encode(gameName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return String.format(url, gameName);
        }
    }

    private String callTwitchAPI(String url) throws TwitchException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //anonymous class for in place implementation of interface
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode < 200 || responseCode >= 300) {
                    System.out.println("Response status: " + response.getStatusLine().getReasonPhrase());
                    throw new TwitchException("Failed to get result from Twitch API");
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new TwitchException("Failed to get result from Twitch API");
                }
                JSONObject obj = new JSONObject(EntityUtils.toString(entity));
                return obj.getJSONArray("data").toString();
            }
        };

        try {
            // Define the HTTP request, TOKEN and CLIENT_ID are used for user authentication on Twitch backend
            HttpGet request = new HttpGet(url);
            TwitchClient.getConfig();
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-Id", CLIENT_ID);
            return httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to get result from Twitch API");
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Game> getGameList(String data) throws TwitchException {
        //convert Json response format to java objects
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Game[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse game data from Twitch API");
        }
    }

    public List<Game> getTopGames(int limit) throws TwitchException {
        if (limit <= 0) {
            limit = DEFAULT_GAME_LIMIT;
        }
        return getGameList(callTwitchAPI(buildTwitchAccessURL(TOP_GAME_URL, "", limit)));
    }

    public Game searchGameByName(String gameName) throws TwitchException {
        List<Game> gameList = getGameList(callTwitchAPI(buildTwitchAccessURL(GAME_SEARCH_URL_TEMPLATE, gameName, 0)));
        if (gameList.size() != 0) {
            return gameList.get(0);
        }
        return null;
    }
}
