package com.itcs.aihome.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.itcs.aihome.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanning extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.scanqr, container, false);
        init(view);
        startScanning();
        return view;
    }

    private void init(View view) {
        scannerView = view.findViewById(R.id.scanner_view);
    }

    private void startScanning() {
        if(!permitted()) {
            scannerView.setResultHandler(this);
            scannerView.setAutoFocus(true);
            scannerView.requestFocus();
            scannerView.startCamera();
        } else {
            requestPermission();
        }
    }

    @Override
    public void handleResult(Result result) {

    }

    private void requestPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA},1);
        }
    }

    private boolean permitted() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}
