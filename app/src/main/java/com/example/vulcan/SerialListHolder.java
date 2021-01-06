package com.example.vulcan;

import android.view.View;
import android.widget.TextView;

public class SerialListHolder {

    TextView key;
    TextView value;

    SerialListHolder(View v){
        key = v.findViewById(R.id.listDetailScanLabel);
        value = v.findViewById(R.id.listDetailScanValue);
    }
}
