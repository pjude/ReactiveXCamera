package com.github.dubulee.reactivexcamera;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.TextureView;

import com.github.dubulee.reactivexcamera.config.ReactiveXCameraConfig;
import com.github.dubulee.reactivexcamera.request.ReactiveXCameraRequestBuilder;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by mugku on 15/12/23.
 * The ReactiveXCamera library interface
 */
public class ReactiveXCamera {

    private static final String TAG = ReactiveXCamera.class.getSimpleName();

    private ReactiveXCameraInternal cameraInternal = new ReactiveXCameraInternal();

    private Matrix rotateMatrix = null;

    /**
     * open the camera
     *
     * @param context
     * @param config
     * @return
     */
    public static Observable<ReactiveXCamera> open(final Context context, final ReactiveXCameraConfig config) {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCamera>() {
            @Override
            public void call(Subscriber<? super ReactiveXCamera> subscriber) {
                ReactiveXCamera reactiveXCamera = new ReactiveXCamera(context, config);
                if (reactiveXCamera.cameraInternal.openCameraInternal()) {
                    subscriber.onNext(reactiveXCamera);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(reactiveXCamera.cameraInternal.openCameraException());
                }
            }
        });
    }

    /**
     * open camera and start preview, bind a {@link SurfaceView}
     *
     * @param context
     * @param config
     * @param surfaceView
     * @return
     */
    public static Observable<ReactiveXCamera> openAndStartPreview(Context context, ReactiveXCameraConfig config, final SurfaceView surfaceView) {
        return open(context, config).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
            @Override
            public Observable<ReactiveXCamera> call(ReactiveXCamera reactiveXCamera) {
                return reactiveXCamera.bindSurface(surfaceView);
            }
        }).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
            @Override
            public Observable<ReactiveXCamera> call(ReactiveXCamera reactiveXCamera) {
                return reactiveXCamera.startPreview();
            }
        });
    }

    /**
     * open camera and start preview, bind a {@link TextureView}
     *
     * @param context
     * @param config
     * @param textureView
     * @return
     */
    public static Observable<ReactiveXCamera> openAndStartPreview(Context context, ReactiveXCameraConfig config, final TextureView textureView) {
        return open(context, config).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
            @Override
            public Observable<ReactiveXCamera> call(ReactiveXCamera reactiveXCamera) {
                return reactiveXCamera.bindTexture(textureView);
            }
        }).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
            @Override
            public Observable<ReactiveXCamera> call(ReactiveXCamera reactiveXCamera) {
                return reactiveXCamera.startPreview();
            }
        });
    }

    private ReactiveXCamera(Context context, ReactiveXCameraConfig config) {
        this.cameraInternal = new ReactiveXCameraInternal();
        this.cameraInternal.setConfig(config);
        this.cameraInternal.setContext(context);
        rotateMatrix = new Matrix();
        rotateMatrix.postRotate(config.cameraOrien, 0.5f, 0.5f);
    }

    public Matrix getRotateMatrix() {
        return rotateMatrix;
    }

    /**
     * bind a {@link SurfaceView} as the camera preview surface
     *
     * @param surfaceView
     * @return
     */
    public Observable<ReactiveXCamera> bindSurface(final SurfaceView surfaceView) {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCamera>() {
            @Override
            public void call(Subscriber<? super ReactiveXCamera> subscriber) {
                boolean result = cameraInternal.bindSurfaceInternal(surfaceView);
                if (result) {
                    subscriber.onNext(ReactiveXCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.bindSurfaceFailedException());
                }
            }
        });
    }

    /**
     * bind a {@link TextureView} as the camera preview surface
     *
     * @param textureView
     * @return
     */
    public Observable<ReactiveXCamera> bindTexture(final TextureView textureView) {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCamera>() {
            @Override
            public void call(Subscriber<? super ReactiveXCamera> subscriber) {
                boolean result = cameraInternal.bindTextureInternal(textureView);
                if (result) {
                    subscriber.onNext(ReactiveXCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.bindSurfaceFailedException());
                }
            }
        });
    }

    /**
     * start preview, must be called after bindTexture or bindSurface
     *
     * @return
     */
    public Observable<ReactiveXCamera> startPreview() {
        return Observable.create(new Observable.OnSubscribe<ReactiveXCamera>() {
            @Override
            public void call(Subscriber<? super ReactiveXCamera> subscriber) {
                boolean result = cameraInternal.startPreviewInternal();
                if (result) {
                    subscriber.onNext(ReactiveXCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.startPreviewFailedException());
                }
            }
        });
    }

    /**
     * close the camera, return an Observable as the result
     *
     * @return
     */
    public Observable<Boolean> closeCameraWithResult() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(cameraInternal.closeCameraInternal());
                subscriber.onCompleted();
            }
        });
    }

    /**
     * return a {@link ReactiveXCameraRequestBuilder} which you can request the camera preview frame data
     *
     * @return
     */
    public ReactiveXCameraRequestBuilder request() {
        return new ReactiveXCameraRequestBuilder(this);
    }

    /**
     * directly close the camera
     *
     * @return true if close success
     */
    public boolean closeCamera() {
        return cameraInternal.closeCameraInternal();
    }

    public boolean isOpenCamera() {
        return cameraInternal.isOpenCamera();
    }

    public boolean isBindSurface() {
        return cameraInternal.isBindSurface();
    }

    /**
     * the config of this camera
     *
     * @return
     */
    public ReactiveXCameraConfig getConfig() {
        return cameraInternal.getConfig();
    }

    /**
     * the native {@link Camera} object
     *
     * @return
     */
    public Camera getNativeCamera() {
        return cameraInternal.getNativeCamera();
    }

    /**
     * the final preview size, mostly this is not the same as the one set in {@link ReactiveXCameraConfig}
     *
     * @return
     */
    public Point getFinalPreviewSize() {
        return cameraInternal.getFinalPreviewSize();
    }

    public void installPreviewCallback(OnReactiveXCameraPreviewFrameCallback previewCallback) {
        this.cameraInternal.installPreviewCallback(previewCallback);
    }

    public void uninstallPreviewCallback(OnReactiveXCameraPreviewFrameCallback previewCallback) {
        this.cameraInternal.uninstallPreviewCallback(previewCallback);
    }

    public void installOneShotPreviewCallback(OnReactiveXCameraPreviewFrameCallback previewFrameCallback) {
        this.cameraInternal.installOneShotPreviewCallback(previewFrameCallback);
    }

    public void uninstallOneShotPreviewCallback(OnReactiveXCameraPreviewFrameCallback previewFrameCallback) {
        this.cameraInternal.uninstallOneShotPreviewCallback(previewFrameCallback);
    }
}
