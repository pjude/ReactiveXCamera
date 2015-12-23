package com.github.dubulee.reactivexcamera.error;

/**
 * Created by dubulee on 15/12/23.
 */
public class BindSurfaceFailedException extends Exception {

    public BindSurfaceFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
