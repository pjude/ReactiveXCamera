package com.github.dubulee.reactivexcamera.error;

/**
 * Created by dubulee on 15/12/23.
 */
public class CameraDataNullException extends Exception {

    public CameraDataNullException() {
        super("the camera data is null");
    }
}
