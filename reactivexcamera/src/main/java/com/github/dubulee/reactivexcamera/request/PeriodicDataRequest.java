package com.github.dubulee.reactivexcamera.request;

import com.github.dubulee.reactivexcamera.OnReactiveXCameraPreviewFrameCallback;
import com.github.dubulee.reactivexcamera.ReactiveXCamera;
import com.github.dubulee.reactivexcamera.ReactiveXCameraData;
import com.github.dubulee.reactivexcamera.error.CameraDataNullException;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by dubulee on 15/12/23.
 */
public class PeriodicDataRequest extends BaseRxCameraRequest implements OnReactiveXCameraPreviewFrameCallback {

    private static final String TAG = "MicroMsg.PeriodicDataRequest";

    private long intervalMills;

    private boolean isInstallCallback = false;

    private Subscriber<? super ReactiveXCameraData> subscriber = null;

    private ReactiveXCameraData currentData = new ReactiveXCameraData();

    private long lastSendDataTimestamp = 0;

    public PeriodicDataRequest(ReactiveXCamera reactiveXCamera, long intervalMills) {
        super(reactiveXCamera);
        this.intervalMills = intervalMills;
    }

    @Override
    public Observable<ReactiveXCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCameraData>() {
            @Override
            public void call(final Subscriber<? super ReactiveXCameraData> subscriber) {
                PeriodicDataRequest.this.subscriber = subscriber;
                subscriber.add(Schedulers.newThread().createWorker().schedulePeriodically(new Action0() {
                    @Override
                    public void call() {
                        if (currentData.cameraData != null && !subscriber.isUnsubscribed() && reactiveXCamera.isOpenCamera()) {
                            subscriber.onNext(currentData);
                        }
                    }
                }, 0, intervalMills, TimeUnit.MILLISECONDS));

            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                reactiveXCamera.uninstallPreviewCallback(PeriodicDataRequest.this);
                isInstallCallback = false;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!isInstallCallback) {
                    reactiveXCamera.installPreviewCallback(PeriodicDataRequest.this);
                    isInstallCallback = true;
                }
            }
        }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                reactiveXCamera.uninstallPreviewCallback(PeriodicDataRequest.this);
                isInstallCallback = false;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (subscriber != null && !subscriber.isUnsubscribed() && reactiveXCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                subscriber.onError(new CameraDataNullException());
            }
            currentData.cameraData = data;
            currentData.rotateMatrix = reactiveXCamera.getRotateMatrix();
        }
    }
}
