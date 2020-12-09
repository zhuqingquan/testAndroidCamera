package com.example.myapplication;

import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        checkCameraHardware(this.getApplicationContext());
        createPreviewSurfaceView();
        //openCamera();
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


    private boolean checkCameraHardware(Context context)
    {
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    private void initCamera(int width,int height){
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        //根据设置的宽高 和手机支持的分辨率对比计算出合适的宽高算法
        Camera.Size size = CameraUtils.calculatePerfectSize(parameters.getSupportedPreviewSizes(),
                1280, 720);
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
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                if(mCamera!=null)
                {
                    initCamera(mSurfaceView.getWidth(),mSurfaceView.getHeight());
                    mCamera.setPreviewDisplay(holder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    SurfaceHolder mHolder;
    SurfaceView mSurfaceView;
    private void createPreviewSurfaceView()
    {
        mSurfaceView = findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(new SurfaceCallback());
    }

}
