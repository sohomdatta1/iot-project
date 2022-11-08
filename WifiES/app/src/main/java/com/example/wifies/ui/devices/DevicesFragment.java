package com.example.wifies.ui.devices;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wifies.R;
import com.example.wifies.WifiESActivity;
import com.example.wifies.databinding.FragmentDevicesBinding;
import com.example.wifies.datamodel.DeviceModal;
import com.example.wifies.helpers.DeviceAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DevicesFragment extends Fragment {

    private FragmentDevicesBinding binding;
    public static String TAG = "LoginFragment";
    String cloud;
    RequestQueue queue;
    ListView deviceLV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DevicesViewModel devicesViewModel =
                new ViewModelProvider(this).get(DevicesViewModel.class);

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cloud = "http://172.22.0.2:8000";
        queue = Volley.newRequestQueue(getActivity());

        deviceLV = binding.deviceLV;

        loadDevices();
        final TextView textView = binding.textHome;
        devicesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void loadDevices() {
        SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        String username = sp.getString("username","");
        String user_id = sp.getString("user_id","");
        StringRequest req = new StringRequest(Request.Method.GET, cloud+"/devices_api",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            String msg = res.get("msg").toString();
                            if(msg.equals(getResources().getString(R.string.device_list_ok)))
                            {
                                String devicelist = res.get(getResources().getString(R.string.devicelist)).toString();
                                JSONObject devices = new JSONObject(devicelist);
                                Iterator<String> keys = devices.keys();

                                ArrayList<DeviceModal> deviceModalArrayList = new ArrayList<>();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    if (devices.get(key) instanceof JSONObject) {
                                        JSONObject device = new JSONObject(devices.get(key).toString());
                                        String uuid = device.get("uuid").toString();
                                        String name = device.get("name").toString();
                                        String user_id = device.get("user_id").toString();
                                        DeviceModal d = new DeviceModal(uuid,name,user_id);
                                        deviceModalArrayList.add(d);
                                    }
                                }
                                DeviceAdapter adapter = new DeviceAdapter(getActivity(), deviceModalArrayList);
                                deviceLV.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG,error.toString());
            }
        }) {

            @Override
            public Map getHeaders() throws AuthFailureError
            {
                HashMap headers = new HashMap();
                headers.put("Cookie", getResources().getString(R.string.user_id_cookie)+"="+user_id);
                return headers;
            }

        };

        queue.add(req);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}