package com.github.dubulee.reactivexcamera.error;

/**
 * Created by dubulee on 15/12/23.
 */
public class StartPreviewFailedException extends Exception {

    public StartPreviewFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
