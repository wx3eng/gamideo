package com.wx3eng.gamideo.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx3eng.gamideo.external.TwitchClient;
import com.wx3eng.gamideo.external.TwitchException;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GameServlet", urlPatterns = {"/game", "/Game"})
public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gameName = request.getParameter("game_name");
        TwitchClient client = new TwitchClient();
        response.setContentType("application/json;charset=UTF-8");
        try {
            // Return the dedicated game information if gameName is provided in the request URL, otherwise return the top (20) default number of games.
            if (gameName != null) {
                response.getWriter().print(new ObjectMapper().writeValueAsString(client.searchGameByName(gameName)));
            } else {
                response.getWriter().print(new ObjectMapper().writeValueAsString(client.getTopGames(0)));
            }
        } catch (TwitchException e) {
            throw new ServletException(e);
        }
    }
}

