package com.github.dubulee.reactivexcamera.error;

/**
 * Created by mugku on 15/12/23.
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
