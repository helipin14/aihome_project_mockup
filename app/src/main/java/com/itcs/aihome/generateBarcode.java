package com.itcs.aihome;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class generateBarcode {
    String data;
    ImageView imageView;
    MultiFormatWriter multiFormatWriter;
    public generateBarcode(String data, ImageView imageView, MultiFormatWriter multiFormatWriter) {
       this.data = data;
       this.imageView = imageView;
       this.multiFormatWriter = multiFormatWriter;
    }

    public void generate() {
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 200,  200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
