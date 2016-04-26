package com.github.dubulee.reactivexcamera.request;

import com.github.dubulee.reactivexcamera.ReactiveXCamera;
import com.github.dubulee.reactivexcamera.ReactiveXCameraData;

import rx.Observable;

/**
 * Created by mugku on 15/12/23.
 * CameraRequestBuilder can help you get the camera preview frame data in different way
 */
public class ReactiveXCameraRequestBuilder {

    private ReactiveXCamera reactiveXCamera;

    public ReactiveXCameraRequestBuilder(ReactiveXCamera reactiveXCamera) {
        this.reactiveXCamera = reactiveXCamera;
    }

    /**
     * successive camera preview frame data
     * @return Observable contained the camera data
     */
    public Observable<ReactiveXCameraData> successiveDataRequest() {
        return new SuccessiveDataRequest(reactiveXCamera).get();
    }

    /**
     * periodic camera preview frame data
     * @param intervalMills the interval of the preview frame data will return, in millseconds
     * @returni Observable contained the camera data
     */
    public Observable<ReactiveXCameraData> periodicDataRequest(long intervalMills) {
        return new PeriodicDataRequest(reactiveXCamera, intervalMills).get();
    }

    /**
     * only one shot camera data, encapsulated the setOneShotPreviewCallback
     * @return Observable contained the camera data
     */
    public Observable<ReactiveXCameraData> oneShotRequest() {
        return new TakeOneShotRequest(reactiveXCamera).get();
    }

    /**
     * take picture request, after call, will stop camera preview just like {@code Camera.takePicture}
     * @param isContinuePreview if continue preview after picture is captured
     * @param shutterAction call when the image is captured, it will be invoked before retrieve the actual image data
     * @return
     */
    public Observable<ReactiveXCameraData> takePictureRequest(boolean isContinuePreview, Func shutterAction) {
        return new TakePictureRequest(reactiveXCamera, shutterAction, isContinuePreview).get();
    }
}
