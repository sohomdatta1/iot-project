package com.example.wifies.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wifies.R;
import com.example.wifies.datamodel.WifiModal;

import java.util.ArrayList;

public class WifiAdapter extends ArrayAdapter<WifiModal> {

    public WifiAdapter(@NonNull Context context, ArrayList<WifiModal> wifiModalArrayList) {
        super(context, 0, wifiModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_card, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        WifiModal wifiModal = getItem(position);

        TextView wifi_ssid = (TextView)listitemView.findViewById(R.id.wifi_ssid);
        TextView wifi_ect = (TextView)listitemView.findViewById(R.id.wifi_ect);
        TextView wifi_krack = (TextView)listitemView.findViewById(R.id.wifi_krack);
        TextView wifi_pw = (TextView)listitemView.findViewById(R.id.wifi_password);
        TextView wifi_uuid = (TextView)listitemView.findViewById(R.id.wifi_uuid);
        TextView user_id = (TextView)listitemView.findViewById(R.id.user_id);

        wifi_ssid.setText(wifiModal.getSsid());
        wifi_uuid.setText(wifiModal.getUuid());
        user_id.setText(wifiModal.getUser_id());
        wifi_ect.setText(wifiModal.getEct());
        wifi_krack.setText(wifiModal.getKrack());
        wifi_pw.setText(wifiModal.getPassword());

        return listitemView;
    }
}
