package cn.tiancaifan.enjoy_music.ui.welcome;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;


import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.ui.MainActivity;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;

public class WelcomeActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    private LottieAnimationView animationView;


    //需要检查的权限
    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //获取电话状态
            Manifest.permission.RECORD_AUDIO,
    };
    private static final int PERMISSIONS_CODE = 1;
    private boolean flag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        StatusBarUtil.setTransparentForWindow(this);


        animationView = findViewById(R.id.la_aw);


        animationView.setScale(0.45f);
        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                if (valueAnimator.getAnimatedFraction() == 1f) {
                    requestPermission();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag){
            requestPermission();
            flag = false;
        }
    }


    //申请权限
    private void requestPermission() {
        // 当API大于 23 时，才动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(mPermissionList,PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 授予权限
                    //处理授权之后逻辑
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.anim_out,R.anim.anim_in);
                    finish();
                } else {
                    // Permission Denied 权限被拒绝
                    showDialog();
                }

                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    //弹出提示框
    private void showDialog(){
        flag = false;
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("需要录音和互联网权限，是否去设置？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag=true;
                        dialog.dismiss();
                        goToAppSetting();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag=true;

                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}