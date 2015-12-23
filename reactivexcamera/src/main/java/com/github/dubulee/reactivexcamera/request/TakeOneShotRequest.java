package com.github.dubulee.reactivexcamera.request;

import com.github.dubulee.reactivexcamera.OnReactiveXCameraPreviewFrameCallback;
import com.github.dubulee.reactivexcamera.ReactiveXCamera;
import com.github.dubulee.reactivexcamera.ReactiveXCameraData;
import com.github.dubulee.reactivexcamera.error.CameraDataNullException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * Created by dubulee on 15/12/23.
 */
public class TakeOneShotRequest extends BaseRxCameraRequest implements OnReactiveXCameraPreviewFrameCallback {

    private Subscriber<? super ReactiveXCameraData> subscriber = null;

    public TakeOneShotRequest(ReactiveXCamera reactiveXCamera) {
        super(reactiveXCamera);
    }

    @Override
    public Observable<ReactiveXCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCameraData>() {
            @Override
            public void call(Subscriber<? super ReactiveXCameraData> subscriber) {
                TakeOneShotRequest.this.subscriber = subscriber;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                reactiveXCamera.installOneShotPreviewCallback(TakeOneShotRequest.this);
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (subscriber != null && !subscriber.isUnsubscribed() && reactiveXCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                subscriber.onError(new CameraDataNullException());
            }
            ReactiveXCameraData reactiveXCameraData = new ReactiveXCameraData();
            reactiveXCameraData.cameraData = data;
            reactiveXCameraData.rotateMatrix = reactiveXCamera.getRotateMatrix();
            subscriber.onNext(reactiveXCameraData);
        }
    }
}
