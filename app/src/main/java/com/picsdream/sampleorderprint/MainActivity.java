package com.picsdream.sampleorderprint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.picsdream.picsdreamsdk.util.AnalyticsTrackers;
import com.picsdream.picsdreamsdk.util.OrderPrint;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Integer REQUEST_CODE_CHOOSE = 200;
    private List<Uri> mSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        Tracker tracker = GoogleAnalytics.getInstance(getApplicationContext()).newTracker(R.xml.global_tracker);
        // Build and send an Event.
        tracker.send(new HitBuilders.EventBuilder().setCategory("TEST_CAT").setAction("TEST FROM INIT").setLabel("HELLO").build());



        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                initializeMatisse();
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void initializeMatisse() {
        Matisse.from(MainActivity.this)
                .choose(MimeType.allOf())
                .countable(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            initializePicsDream(mSelected.get(0));
            Log.d("Matisse", "mSelected: " + mSelected);
        }
    }

    private void initializePicsDream(Uri uri) {
        OrderPrint.getInstance()
                .with(this.getApplication())
                .returnBackActivity(MainActivity.class)
                .runInSandboxMode(false)
                .initialize("1510814073239977884");

        OrderPrint.getInstance()
                .ImageUri(uri).launch(this);
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
}
