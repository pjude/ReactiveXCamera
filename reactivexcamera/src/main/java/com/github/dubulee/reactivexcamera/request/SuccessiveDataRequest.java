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
public class SuccessiveDataRequest extends BaseRxCameraRequest implements OnReactiveXCameraPreviewFrameCallback {

    private boolean isInstallSuccessivePreviewCallback = false;

    private Subscriber<? super ReactiveXCameraData> successiveDataSubscriber = null;

    public SuccessiveDataRequest(ReactiveXCamera reactiveXCamera) {
        super(reactiveXCamera);
    }

    public Observable<ReactiveXCameraData> get() {

        return Observable.create(new Observable.OnSubscribe<ReactiveXCameraData>() {
            @Override
            public void call(final Subscriber<? super ReactiveXCameraData> subscriber) {
                successiveDataSubscriber = subscriber;
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                reactiveXCamera.uninstallPreviewCallback(SuccessiveDataRequest.this);
                isInstallSuccessivePreviewCallback = false;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!isInstallSuccessivePreviewCallback) {
                    reactiveXCamera.installPreviewCallback(SuccessiveDataRequest.this);
                    isInstallSuccessivePreviewCallback = true;
                }
            }
        }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                reactiveXCamera.uninstallPreviewCallback(SuccessiveDataRequest.this);
                isInstallSuccessivePreviewCallback = false;
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (successiveDataSubscriber != null && !successiveDataSubscriber.isUnsubscribed() && reactiveXCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                successiveDataSubscriber.onError(new CameraDataNullException());
            }
            ReactiveXCameraData cameraData = new ReactiveXCameraData();
            cameraData.cameraData = data;
            cameraData.rotateMatrix = reactiveXCamera.getRotateMatrix();
            successiveDataSubscriber.onNext(cameraData);
        }
    }
}
