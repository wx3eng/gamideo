package com.wx3eng.gamideo.db;

public class MySQLException  extends RuntimeException{
    public MySQLException(String errorMessage) {
        super(errorMessage);
    }
}
