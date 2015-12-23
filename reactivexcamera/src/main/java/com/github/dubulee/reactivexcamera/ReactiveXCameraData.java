package com.github.dubulee.reactivexcamera;

import android.graphics.Matrix;

/**
 * Created by dubulee on 15/12/23.
 * the preview frame data
 */
public class ReactiveXCameraData {

    /**
     * the raw preview frame, the format is in YUV if you not set the
     * preview format in the config
     */
    public byte[] cameraData;

    /**
     * a matrix help you rotate the camera data in portrait mode
     */
    public Matrix rotateMatrix;
}
