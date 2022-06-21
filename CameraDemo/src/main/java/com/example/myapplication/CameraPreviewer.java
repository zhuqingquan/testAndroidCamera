package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreviewer extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    Surface mSurface;
    private Camera mCamera;

    public CameraPreviewer(Context context) {
        super(context);
        if(!checkCameraHardware(context))
            throw new RuntimeException("Current System Not surpport Camera Feature");
        initSurfaceHolder();
    }

    public CameraPreviewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!checkCameraHardware(context))
            throw new RuntimeException("Current System Not surpport Camera Feature");
        initSurfaceHolder();
    }

    public CameraPreviewer(Context context, Camera camera) {
        super(context);
        if(!checkCameraHardware(context))
            throw new RuntimeException("Current System Not surpport Camera Feature");
        mCamera = camera;
        initSurfaceHolder();
    }

    private boolean checkCameraHardware(Context context)
    {
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    void initSurfaceHolder()
    {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        CameraUtils.openFrontCamera(30);
//        CameraUtils.openCamera(0, 30);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        try {
            mSurface = holder.getSurface();
            CameraUtils.setPreviewDisplay(holder);
            startPreview(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        CameraUtils.stopPreview();
        CameraUtils.releaseCamera();
        mSurface = null;
    }

    private void startPreview(int width,int height){
        //根据设置的宽高 和手机支持的分辨率对比计算出合适的宽高算法
        CameraUtils.setPreviewSize(width, height);
        WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
        if(wm==null)
            return;
        int displayRotation = wm.getDefaultDisplay().getRotation();
        int cameraRotation = CameraUtils.calculateCameraPreviewOrientation(displayRotation);
        CameraUtils.setPreviewOrientation(cameraRotation);

//        Camera.CameraInfo info = new Camera.CameraInfo();
//        android.hardware.Camera.getCameraInfo(mCamerId, info);
//        int rotation = getDisplayRotationDegree();
//
//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + rotation) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - rotation + 360) % 360;
//        }
//        mCamera.setDisplayOrientation(result);



//        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.setPreviewFormat(ImageFormat.NV21);
//        //根据设置的宽高 和手机支持的分辨率对比计算出合适的宽高算法
//        Camera.Size size = CameraUtils.calculatePerfectSize(parameters.getSupportedPreviewSizes(),
//                width, height);
//
//        parameters.setPreviewSize(size.width, size.height);
        //设置照片尺寸
        CameraUtils.setPictureSize(1920, 1080);
        //设置实时对焦 部分手机不支持会crash

        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        mCamera.setParameters(parameters);
        //开启预览
        CameraUtils.startPreview();
    }
}
