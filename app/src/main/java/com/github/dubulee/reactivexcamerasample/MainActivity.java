package com.github.dubulee.reactivexcamerasample;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dubu.runtimepermissionshelper.rxver.RxPermissions;
import com.github.dubulee.reactivexcamera.ReactiveXCamera;
import com.github.dubulee.reactivexcamera.ReactiveXCameraData;
import com.github.dubulee.reactivexcamera.config.ReactiveXCameraConfig;
import com.github.dubulee.reactivexcamera.config.ReactiveXCameraConfigChooser;
import com.github.dubulee.reactivexcamera.request.Func;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextureView textureView;
    private Button openCameraBtn;
    private Button closeCameraBtn;
    private TextView logTextView;
    private ScrollView logArea;

    private ReactiveXCamera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textureView = (TextureView) findViewById(R.id.preview_surface);
        openCameraBtn = (Button) findViewById(R.id.open_camera);
        closeCameraBtn = (Button) findViewById(R.id.close_camera);
        logTextView = (TextView) findViewById(R.id.log_textview);
        logArea = (ScrollView) findViewById(R.id.log_area);

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RxPermissions.getInstance(MainActivity.this)
                        .request(Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                                showLog("RxPermissions: Complete");
                            }

                            @Override
                            public void onError(Throwable e) {
                                showLog("RxPermissions: Error: " + e.toString());
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                showLog("RxPermissions: Next: is?: " + aBoolean);
                                if (aBoolean) {
                                    openCamera();
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Permission denied, can't enable the camera ",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        closeCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.closeCameraWithResult().subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            showLog("close camera finished, success: " + aBoolean);
                        }
                    });
                }
            }
        });
    }

    private void openCamera() {
        ReactiveXCameraConfig config = ReactiveXCameraConfigChooser.obtain().
                useBackCamera().
                setAutoFocus(true).
                setPreferPreviewFrameRate(15, 30).
                setPreferPreviewSize(new Point(640, 480)).
                setHandleSurfaceEvent(true).
                get();
        Log.d(TAG, "config: " + config);
        ReactiveXCamera.open(this, config).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
            @Override
            public Observable<ReactiveXCamera> call(ReactiveXCamera reactiveXCamera) {
                showLog("isopen: " + reactiveXCamera.isOpenCamera() + ", thread: " + Thread.currentThread());
                camera = reactiveXCamera;
                return reactiveXCamera.bindTexture(textureView);
            }
        }).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
            @Override
            public Observable<ReactiveXCamera> call(ReactiveXCamera reactiveXCamera) {
                showLog("isbindsurface: " + reactiveXCamera.isBindSurface() + ", thread: " + Thread.currentThread());
                return reactiveXCamera.startPreview();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ReactiveXCamera>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showLog("open camera error: " + e.getMessage());
            }

            @Override
            public void onNext(ReactiveXCamera reactiveXCamera) {
                camera = reactiveXCamera;
                showLog("open camera success: " + camera);
            }
        });


    }

    private void showLog(String s) {
        Log.d(TAG, s);
        logTextView.append(s + "\n");
        logTextView.post(new Runnable() {
            @Override
            public void run() {
                logArea.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.closeCamera();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_log:
                toggleLogArea();
                break;
            case R.id.action_successive_data:
                requestSuccessiveData();
                break;
            case R.id.action_periodic_data:
                requestPeriodicData();
                break;
            case R.id.action_one_shot:
                requestOneShot();
                break;
            case R.id.action_take_picture:
                requestTakePicture();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleLogArea() {
        if (logArea.getVisibility() == View.VISIBLE) {
            logArea.setVisibility(View.GONE);
        } else {
            logArea.setVisibility(View.VISIBLE);
        }
    }

    private void requestSuccessiveData() {
        if (!checkCamera()) {
            return;
        }
        camera.request().successiveDataRequest().subscribe(new Action1<ReactiveXCameraData>() {
            @Override
            public void call(ReactiveXCameraData reactiveXCameraData) {
                showLog("successiveData, cameraData.length: " + reactiveXCameraData.cameraData.length);
            }
        });
    }

    private void requestOneShot() {
        if (!checkCamera()) {
            return;
        }
        camera.request().oneShotRequest().subscribe(new Action1<ReactiveXCameraData>() {
            @Override
            public void call(ReactiveXCameraData reactiveXCameraData) {
                showLog("one shot request, cameraData.length: " + reactiveXCameraData.cameraData.length);
            }
        });
    }

    private void requestPeriodicData() {
        if (!checkCamera()) {
            return;
        }
        camera.request().periodicDataRequest(1000).subscribe(new Action1<ReactiveXCameraData>() {
            @Override
            public void call(ReactiveXCameraData reactiveXCameraData) {
                showLog("periodic request, cameraData.length: " + reactiveXCameraData.cameraData.length);
            }
        });
    }

    private void requestTakePicture() {
        if (!checkCamera()) {
            return;
        }
        camera.request().takePictureRequest(true, new Func() {
            @Override
            public void call() {
                showLog("Captured!");
            }
        }).subscribe(new Action1<ReactiveXCameraData>() {
            @Override
            public void call(ReactiveXCameraData reactiveXCameraData) {
                String path = Environment.getExternalStorageDirectory() + "/test.jpg";
                File file = new File(path);
                Bitmap bitmap = BitmapFactory.decodeByteArray(reactiveXCameraData.cameraData, 0, reactiveXCameraData.cameraData.length);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        reactiveXCameraData.rotateMatrix, false);
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showLog("Save file on " + path);
            }
        });
    }

    private boolean checkCamera() {
        if (camera == null || !camera.isOpenCamera()) {
            return false;
        }
        return true;
    }

}
