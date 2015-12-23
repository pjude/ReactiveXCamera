package com.github.dubulee.reactivexcamera.config;

import android.graphics.Point;
import android.hardware.Camera;

/**
 * Created by dubulee on 15/12/23.
 * a config chooser for ReactiveXCamera, after finish choosing the config,
 * return a {@link ReactiveXCameraConfig} object
 */
public class ReactiveXCameraConfigChooser {

    private ReactiveXCameraConfigChooser() {
        configResult = new ReactiveXCameraConfig();
    }

    private ReactiveXCameraConfig configResult;

    public static ReactiveXCameraConfigChooser obtain() {
        return new ReactiveXCameraConfigChooser();
    }

    public ReactiveXCameraConfigChooser useFrontCamera() {
        configResult.isFaceCamera = true;
        configResult.currentCameraId = CameraUtil.getFrontCameraId();
        return this;
    }

    public ReactiveXCameraConfigChooser useBackCamera() {
        configResult.isFaceCamera = false;
        configResult.currentCameraId = CameraUtil.getBackCameraId();
        return this;
    }

    public ReactiveXCameraConfigChooser setPreferPreviewSize(Point size) {
        if (size == null) {
            return this;
        }
        configResult.preferPreviewSize = size;
        return this;
    }

    public ReactiveXCameraConfigChooser setPreferPreviewFrameRate(int minFrameRate, int maxFrameRate) {
        if (minFrameRate <= 0 || maxFrameRate <= 0 || maxFrameRate < minFrameRate) {
            return this;
        }
        configResult.minPreferPreviewFrameRate = minFrameRate;
        configResult.maxPreferPreviewFrameRate = maxFrameRate;
        return this;
    }

    public ReactiveXCameraConfigChooser setPreviewFormat(int previewFormat) {
        configResult.previewFormat = previewFormat;
        return this;
    }

    public ReactiveXCameraConfigChooser setDisplayOrientation(int displayOrientation) {
        configResult.displayOrientation = displayOrientation;
        return this;
    }

    public ReactiveXCameraConfigChooser setAutoFocus(boolean isAutoFocus) {
        configResult.isAutoFocus = isAutoFocus;
        return this;
    }

    public ReactiveXCameraConfigChooser setHandleSurfaceEvent(boolean isHandle) {
        configResult.isHandleSurfaceEvent = isHandle;
        return this;
    }

    public ReactiveXCameraConfigChooser setPreviewBufferSize(int size) {
        configResult.previewBufferSize = size;
        return this;
    }

    private ReactiveXCameraConfigChooser setProperConfigVal() {
        if (configResult.currentCameraId == -1) {
            if (configResult.isFaceCamera) {
                configResult.currentCameraId = CameraUtil.getFrontCameraId();
            } else {
                configResult.currentCameraId = CameraUtil.getBackCameraId();
            }
        }
        if (configResult.preferPreviewSize == null) {
            configResult.preferPreviewSize = ReactiveXCameraConfig.DEFAULT_PREFER_PREVIEW_SIZE;
        }

        Camera.CameraInfo cameraInfo = CameraUtil.getCameraInfo(configResult.currentCameraId);
        if (cameraInfo != null) {
            configResult.cameraOrien = cameraInfo.orientation;
        }
        return this;
    }


    public ReactiveXCameraConfig get() {
        setProperConfigVal();
        return configResult;
    }
}
