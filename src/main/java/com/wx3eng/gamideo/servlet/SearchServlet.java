package com.wx3eng.gamideo.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx3eng.gamideo.external.TwitchClient;
import com.wx3eng.gamideo.external.TwitchException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gameId = request.getParameter("game_id");
        response.setContentType("application/json;charset=UTF-8");
        TwitchClient client = new TwitchClient();
        try {
            if (gameId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            response.getWriter().print(new ObjectMapper().writeValueAsString(client.searchItems(gameId)));
        } catch (TwitchException e) {
            throw new ServletException(e);
        }
    }
}
