package com.wx3eng.gamideo.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx3eng.gamideo.Entity.Item;
import com.wx3eng.gamideo.db.MySQLException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DoGetResponseHelper {
    public static void doGetResponseHelper(HttpServletResponse response, Map<String, List<Item>> itemMap) throws IOException {
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(new ObjectMapper().writeValueAsString(itemMap));
        } catch (MySQLException e) {
            e.printStackTrace();
        }
    }
}
