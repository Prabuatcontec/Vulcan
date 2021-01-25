package com.example.vulcan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

public class BarcodeReaderActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {
    public static String KEY_CAPTURED_BARCODE = "key_captured_barcode";
    public static String KEY_CAPTURED_RAW_BARCODE = "key_captured_raw_barcode";
    public static String KEY_CAPTURED_RAW_BARCODES = "key_captured_raw_barcodes";
    private static final String KEY_AUTO_FOCUS = "key_auto_focus";
    private static final String KEY_USE_FLASH = "key_use_flash";
    private boolean autoFocus = false;
    private boolean useFlash = false;
    private BarcodeReaderFragment mBarcodeReaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

        final Intent intent = getIntent();
        if (intent != null) {
            autoFocus = intent.getBooleanExtra(KEY_AUTO_FOCUS, false);
            useFlash = intent.getBooleanExtra(KEY_USE_FLASH, false);
        }
        mBarcodeReaderFragment = attachBarcodeReaderFragment();
    }

    private BarcodeReaderFragment attachBarcodeReaderFragment() {
        final FragmentManager supportFragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        BarcodeReaderFragment fragment = BarcodeReaderFragment.newInstance(autoFocus, useFlash);
        fragment.setListener(this);
        fragmentTransaction.replace(R.id.fm_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    public static Intent getLaunchIntent(Context context, boolean autoFocus, boolean useFlash) {
        Intent intent = new Intent(context, BarcodeReaderActivity.class);
        intent.putExtra(KEY_AUTO_FOCUS, autoFocus);
        intent.putExtra(KEY_USE_FLASH, useFlash);
        return intent;
    }

    @Override
    public void onScanned(Barcode barcode) {
        if (mBarcodeReaderFragment != null) {
            mBarcodeReaderFragment.pauseScanning();
        }
        if (barcode != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_CAPTURED_BARCODE, barcode);
            intent.putExtra(KEY_CAPTURED_RAW_BARCODE, barcode.rawValue);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        Log.d("BARCODE-SCANNER", "MULTIPLE BARCODES");

        StringBuilder s = new StringBuilder(100);
        for(Barcode barcode : barcodes){
            s.append( barcode.rawValue + "\n");
        }
        String mbarcodes = s.toString();
        Intent intent = new Intent();
        intent.putExtra(KEY_CAPTURED_RAW_BARCODES, mbarcodes);

        setResult(RESULT_OK, intent);
        Log.d("BARCODE-SCANNER", "MULTIPLE BARCODES END");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
