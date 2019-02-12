package com.itcs.aihome;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class scanqr extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private String TAG = scanqr.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        requestPermission();
    }

    private void init() {
        setContentView(R.layout.scanqr);
        scannerView = findViewById(R.id.scanner_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.setAutoFocus(true);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        if(!TextUtils.isEmpty(result.getText())) {
            Intent intent = new Intent(scanqr.this, Homepage.class);
            startActivity(intent);
        }
        scannerView.resumeCameraPreview(this);
    }

    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }
    }
}
