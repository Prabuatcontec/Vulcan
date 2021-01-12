package com.example.vulcan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.barcode.Barcode;
import com.example.vulcan.BarcodeReaderActivity;
import com.example.vulcan.BarcodeReaderFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity1 extends AppCompatActivity implements View.OnClickListener, BarcodeReaderFragment.BarcodeReaderListener {
    private static final int BARCODE_READER_ACTIVITY_REQUEST = 1208;
    private TextView mTvResult;
    private TextView mTvResultHeader;
    Map<Integer, String> serials = new HashMap<Integer, String>();
    Map<Integer, String> sorted = new TreeMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_main);
//        findViewById(R.id.btn_fragment).setOnClickListener(this);
        addBarcodeReaderFragment();
        mTvResult = findViewById(R.id.tv_result);
        //mTvResultHeader = findViewById(R.id.tv_result_head);
    }

    private void addBarcodeReaderFragment() {
        BarcodeReaderFragment readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
        readerFragment.setListener(this);
        FragmentManager supportFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fm_container, readerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_fragment:
//                addBarcodeReaderFragment();
//                break;
//            case R.id.btn_activity:
//                FragmentManager supportFragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
//                Fragment fragmentById = supportFragmentManager.findFragmentById(R.id.fm_container);
//                if (fragmentById != null) {
//                    fragmentTransaction.remove(fragmentById);
//                }
//                fragmentTransaction.commitAllowingStateLoss();
//                launchBarCodeActivity();
//                break;
//        }
    }


    private void launchBarCodeActivity() {
        Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
        startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "error in  scanning", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);
            Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show();
            //mTvResultHeader.setText("On Activity Result");
            mTvResult.setText(barcode.rawValue);

            Log.e("BARCODE-SCANNER", "Retrieving barcode data");
            String dbarcodes = data.getStringExtra(BarcodeReaderActivity.KEY_CAPTURED_RAW_BARCODES);
            mTvResult.setText(dbarcodes);
        }


    }

    @Override
    public void onScanned(Barcode barcode) {

        if(serials.containsValue(barcode.rawValue.replaceAll("[-+^]*", "")) == false)
        {
            serials.put(barcode.cornerPoints[0].y, barcode.rawValue.replaceAll("[-+^]*", "")
            );
            Log.d("Barcode123", serials.toString().replace(",",","));

        }

        Map<Integer, String> treeMap = new TreeMap<Integer, String>(serials);

        mTvResult.setText(treeMap.toString().replace(",",","));
    }




    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
}
