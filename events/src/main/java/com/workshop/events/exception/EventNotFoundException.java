package com.workshop.events.exception;

public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(String msg){
        super(msg);
    }
}
