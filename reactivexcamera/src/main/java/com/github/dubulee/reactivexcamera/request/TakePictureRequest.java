package com.github.dubulee.reactivexcamera.request;

import android.hardware.Camera;

import com.github.dubulee.reactivexcamera.ReactiveXCamera;
import com.github.dubulee.reactivexcamera.ReactiveXCameraData;
import com.github.dubulee.reactivexcamera.error.TakePictureFailedException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by mugku on 15/12/23.
 */
public class TakePictureRequest extends BaseRxCameraRequest {

    private Func shutterAction;
    private boolean isContinuePreview;

    public TakePictureRequest(ReactiveXCamera reactiveXCamera, Func shutterAction, boolean isContinuePreview) {
        super(reactiveXCamera);
        this.shutterAction = shutterAction;
        this.isContinuePreview = true;
    }

    @Override
    public Observable<ReactiveXCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCameraData>() {
            @Override
            public void call(final Subscriber<? super ReactiveXCameraData> subscriber) {
                reactiveXCamera.getNativeCamera().takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        if (shutterAction != null) {
                            shutterAction.call();
                        }
                    }
                }, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                    }
                }, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (isContinuePreview) {
                            reactiveXCamera.startPreview().doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    subscriber.onError(throwable);
                                }
                            }).subscribe();
                        }
                        if (data != null) {
                            ReactiveXCameraData reactiveXCameraData = new ReactiveXCameraData();
                            reactiveXCameraData.cameraData = data;
                            reactiveXCameraData.rotateMatrix = reactiveXCamera.getRotateMatrix();
                            subscriber.onNext(reactiveXCameraData);

                        } else {
                            subscriber.onError(new TakePictureFailedException("cannot get take picture data"));
                        }
                    }
                });
            }
        });
    }
}
