# ReactiveXCamera
Android RxJava(ReactiveX Java), RxAndroid(ReactiveX Java Android) style API for Android Camera with Android (Runtime over 6.0) RxPermissions

----

Download
--------

Current version: [0.1.0]

Gradle:
```
repositories {
        jcenter()
}
dependencies {
	compile 'com.github.dubulee:reactivexcamera:0.1.0'
}
```

How to Use
-----------
1. set the camera parameter by choose a [ReactiveXCameraConfig]which created by [ReactiveXCameraConfigChooser]
	
	```Java
	ReactiveXCameraConfig config = ReactiveXCameraConfigChooser.obtain().
            useBackCamera().
            setAutoFocus(true).
            setPreferPreviewFrameRate(30, 30).
            setPreferPreviewSize(new Point(640, 480)).
            setHandleSurfaceEvent(true).
            get();
	```
	for all camera currently support, please see [ReactiveXCameraConfig]
	
2. open camera
	
	```Java
	ReactiveXCamera.open(context, config)
	```
	it return an ReactiveXJava Observable object, the type is ``Observable<ReactiveXCamera>``
	
3. bind a ``SurfaceView`` or ``TextureView`` and startPreview

	since ``ReactiveXCamera.open`` is return an Observable, so you can chain the call like this
	
	```Java
	ReactiveXCamera.open(this, config).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
          @Override
          public Observable<ReactiveXCamera> call(ReactiveXCamera rxCamera) {
              return rxCamera.bindTexture(textureView);
              // or bind a SurfaceView:
              // rxCamera.bindSurface(SurfaceView)
          }
    }).flatMap(new Func1<ReactiveXCamera, Observable<ReactiveXCamera>>() {
          @Override
          public Observable<ReactiveXCamera> call(ReactiveXCamera rxCamera) {
              return rxCamera.startPreview();
          }
    });
	```
	both ``ReactiveXCamera.bindTexture`` and ``ReactiveXCamera.startPreview`` will return an ``Observable<ReactiveXCamera>`` object
	
4. request camera data

	ReactiveXCamera support many styles of camera data requests:
	
	-  successiveDataRequest
		
		```Java
		camera.request().successiveDataRequest()
		```
		it will return the camera data infinitely
		
	- periodicDataRequest
		
		```Java
		camera.request().periodicDataRequest(1000)
		```
		as the name, it will return camera data periodic, pass the interval in millisecond
		
	- oneShotRequest
	
		```Java
		camera.request().oneShotRequest()
		```
		it will return the camera data **only once**
		
	- takePictureRequest
	
		```Java
		camera.request().takePictureRequest(boolean isContinuePreview, Func shutterAction)
		```
	all the data request will return an ``Observalbe<ReactiveXCameraData>``
	
	the ``ReactiveXCameraData`` contained two fields:
	
	- ``byte[] cameraData``, the raw data of camera, for the takePicture request, it will return the jpeg encode byte, other request just return raw camera preview data, if you don't set preview format, the default is YUV420SP
	- ``Matrix rotateMatrix``, this matrix help you rotate the camera data in portrait


Welcome the pull request
-------------------------

License
-------------------------
Copyright 2015 DUBULEE

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
