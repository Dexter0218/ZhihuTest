package com.dexter0218.zhihu.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.dexter0218.zhihu.BuildConfig;
import com.dexter0218.zhihu.R;

import hugo.weaving.DebugLog;

public class BaseActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private CoordinatorLayout mCoordinatorLayout;
    protected int layoutResID = R.layout.activity_base;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        if (BuildConfig.DEBUG) {
            // do something you only want to do in debug builds (logging, ...)
            riseAndShine(this);
        }
    }


    /**
     * Show the activity over the lockscreen and wake up the device. If you launched the app manually
     * both of these conditions are already true. If you deployed from the IDE, however, this will
     * save you from hundreds of power button presses and pattern swiping per day!
     */
    @DebugLog
    public static void riseAndShine(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        PowerManager power = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock lock =
                power.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakeup!");

        lock.acquire();
        lock.release();
    }

    public void showSnackbar(int resId) {
        Snackbar.make(mCoordinatorLayout, resId, Snackbar.LENGTH_SHORT).show();
    }
}
