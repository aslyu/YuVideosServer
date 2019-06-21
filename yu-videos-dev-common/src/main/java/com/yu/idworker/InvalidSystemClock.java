package com.yu.idworker;

@SuppressWarnings("serial")
public class InvalidSystemClock extends RuntimeException {
    public InvalidSystemClock(String message) {
        super(message);
    }
}
