package com.github.dubulee.reactivexcamera.request;

import com.github.dubulee.reactivexcamera.ReactiveXCamera;
import com.github.dubulee.reactivexcamera.ReactiveXCameraData;

import rx.Observable;

/**
 * Created by dubulee on 15/12/23.
 */
public abstract class BaseRxCameraRequest {

    protected ReactiveXCamera reactiveXCamera;

    public BaseRxCameraRequest(ReactiveXCamera reactiveXCamera) {
        this.reactiveXCamera = reactiveXCamera;
    }

    public abstract Observable<ReactiveXCameraData> get();
}
