package com.realssoft.dulcefrio.api.exception;

import lombok.Getter;

@Getter
public class NoChangesException extends RuntimeException
{

    private final String title;
    private final String message;

    public NoChangesException(String title, String message) {
        super(message);
        this.title = title;
        this.message = message;
    }

}
