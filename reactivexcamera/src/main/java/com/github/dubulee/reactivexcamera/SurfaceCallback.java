package com.github.dubulee.reactivexcamera;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.TextureView;

/**
 * Created by mugku on 15/12/23.
 */
class SurfaceCallback implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    public interface SurfaceListener {
        void onAvailable();

        void onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        onSurfaceAvailable();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onSurfaceDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        onSurfaceAvailable();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        onSurfaceDestroy();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onSurfaceAvailable() {
        if (listener != null) {
            listener.onAvailable();
        }
    }

    public void onSurfaceDestroy() {
        if (listener != null) {
            listener.onDestroy();
        }
    }

    private SurfaceListener listener;

    public void setSurfaceListener(SurfaceListener l) {
        this.listener = l;
    }
}
