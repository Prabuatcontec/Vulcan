package com.example.vulcan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SerialListAdapter extends ArrayAdapter<String> {
    Context context;
    String[] key;
    String[] value;

    public SerialListAdapter(Context context, String[] key, String[] value){
        super(context,R.layout.single_item, R.id.listDetailScanLabel,key);
        this.context = context;
        this.key = key;
        this.value = value;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singleItem = convertView;
        SerialListHolder holder = null;

        if(singleItem == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_item, parent, false);
            holder = new SerialListHolder(singleItem);
            singleItem.setTag(holder);
        } else {
            holder = (SerialListHolder) singleItem.getTag();
        }

        holder.key.setText(key[position]);
        holder.value.setText(value[position]);

        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return singleItem;
    }
}
