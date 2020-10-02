package com.pysdbi.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    private static final int CAMERA_REQUEST = 0;

    TextView textview;
    ImageView imgView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView) findViewById(R.id.logger);
        imgView = (ImageView) findViewById(R.id.imgview);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            Bitmap thumbnailBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imgView.setImageBitmap(thumbnailBitmap);

            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                    .build();
            if (!barcodeDetector.isOperational())
            {
                textview.setText("Не удалось считать QR CODE");
            }

            try
            {
                assert thumbnailBitmap != null;
                Frame frame = new Frame.Builder().setBitmap(thumbnailBitmap).build();
                SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

                Barcode thisCode = barcodes.valueAt(0);
                textview.setText(thisCode.rawValue);

            }
            catch (Exception ex)
            {
                textview.setText("Не удалось считать QR CODE");
            }
        }
    }
}