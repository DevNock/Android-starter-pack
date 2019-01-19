package com.devnock.basearchitecture.base.viewmodel.livedata;

public class Event<T> {

    public static <T> Event<T> from(T content) {
        return new Event<T>(content);
    }

    private T content;
    private boolean isHandled = false;

    private Event(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if(isHandled) {
            return null;
        } else {
            isHandled = true;
            return content;
        }
    }

    public T getContent() {
        return content;
    }
}
