package com.example.wifies.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wifies.R;
import com.example.wifies.datamodel.DeviceModal;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DeviceAdapter extends ArrayAdapter<DeviceModal> {

    public DeviceAdapter(@NonNull Context context, ArrayList<DeviceModal> deviceModalArrayList) {
        super(context, 0, deviceModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.device_card, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        DeviceModal deviceModal = getItem(position);

        TextView device_uuid = (TextView)listitemView.findViewById(R.id.device_uuid);
        TextView device_name = (TextView)listitemView.findViewById(R.id.device_name);
        TextView user_id = (TextView)listitemView.findViewById(R.id.user_id);

        device_name.setText(deviceModal.getName());
        device_uuid.setText(deviceModal.getUuid());
        user_id.setText(deviceModal.getUser_id());


        return listitemView;
    }
}
