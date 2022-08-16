package com.example.myapplication;

import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FrameLayout mContainer = null;
    EGLFragment meglFragment = null;
    CameraFragment mCamFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FrameLayout fb_container = findViewById(R.id.fl_container);
        mContainer = fb_container;
        meglFragment = new EGLFragment();
        mCamFragment = new CameraFragment();
        RadioButton rb_cam = findViewById(R.id.rb_camera);
        rb_cam.setOnClickListener(this);
        RadioButton rb_egl = findViewById(R.id.rb_egl);
        rb_egl.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, mCamFragment, "FragmentCam").commit();

        int cameraCount = CameraUtils.GetCameraCount();

        // Android 6.0相机动态权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            //initView();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0);
        }

        int orientation = this.getResources().getConfiguration().orientation;
        mIsLanscape = orientation== Configuration.ORIENTATION_LANDSCAPE;

        createPreviewSurfaceView();
        //openCamera();
    }

    @Override
    public void onClick(View var1)
    {
        if(var1.getId()==R.id.rb_camera)
        {
            if(!mCamFragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container, mCamFragment, "FragmentCam").commit();
            else {
                getSupportFragmentManager().beginTransaction().hide(meglFragment).commit();
                getSupportFragmentManager().beginTransaction().show(mCamFragment).commit();
            }
        }
        else if(var1.getId()==R.id.rb_egl)
        {
            if(!meglFragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container, meglFragment, "FragmentEGL").commit();
            else {
                getSupportFragmentManager().beginTransaction().hide(mCamFragment).commit();
                getSupportFragmentManager().beginTransaction().show(meglFragment).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Camera mCamera;
    private int mCamerId = 0;

    private boolean openCamera()
    {
        try {
            Camera camera = Camera.open(mCamerId);
            mCamera = camera;
            return true;
        }catch (Exception e){
            Log.e("main", "摄像头被占用");
        }
        return false;
    }

    private void initCamera(int width,int height){
        Camera.CameraInfo info = new Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCamerId, info);

        int rotation = getDisplayRotationDegree(this);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + rotation) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - rotation + 360) % 360;
        }

//        mCamera.setDisplayOrientation((rotation + 90) % 360);
        mCamera.setDisplayOrientation(result);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        //根据设置的宽高 和手机支持的分辨率对比计算出合适的宽高算法
        Camera.Size size = CameraUtils.calculatePerfectSize(parameters.getSupportedPreviewSizes(),
                width, height);
        //Camera.Size optionSize = new ;
        //CameraUtil
        //        .getInstance(mCamera, mCamerId)
        //        .getOptimalPreviewSize(width, height);
        parameters.setPreviewSize(size.width, size.height);
        //parameters.setPreviewSize(width, height);
        //设置照片尺寸
        parameters.setPictureSize(size.width, size.height);
        //parameters.setPictureSize(width, height);
        //设置实时对焦 部分手机不支持会crash

        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(parameters);
        //开启预览
        mCamera.startPreview();
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

//            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                if(mCamera!=null)
                {
                    initCamera(mSurfaceView.getWidth(),mSurfaceView.getHeight());
                    mCamera.setPreviewDisplay(holder);
                    mSurface = holder.getSurface();
                    if(null!=mSurface)
                    {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    Surface mSurface;
    SurfaceHolder mHolder;
    SurfaceView mSurfaceView;
    private void createPreviewSurfaceView()
    {
//        mSurfaceView = findViewById(R.id.surfaceView);
//        mHolder = mSurfaceView.getHolder();
//        mHolder.addCallback(new SurfaceCallback());
    }

    boolean mIsLanscape = false;

    private int getDisplayRotationDegree(Activity activity)
    {
        Display display = this.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int rotation_dgr = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                rotation_dgr = 0;
                break;
            case Surface.ROTATION_90:
                rotation_dgr = 90;
                break;
            case Surface.ROTATION_180:
                rotation_dgr = 180;
                break;
            case Surface.ROTATION_270:
                rotation_dgr = 270;
                break;
            default:
                break;
        }
        return rotation_dgr;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mIsLanscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;


        //DisplayMetrics metrics = new DisplayMetrics();
        //display.getMetrics(metrics);
        //Log.i("Display", "Rotation="+rotation_dgr+" w*h = "+metrics.widthPixels + "*" + metrics.heightPixels);
    }
}
