package com.example.wifies.ui.scan;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.example.wifies.databinding.FragmentScanBinding;
import com.example.wifies.datamodel.DeviceModal;
import com.example.wifies.datamodel.WifiModal;
import com.example.wifies.helpers.DeviceAdapter;
import com.example.wifies.helpers.WifiAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    public static String TAG = "LoginFragment";
    String cloud;
    RequestQueue queue;
    ListView wifiLV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel scanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);

        cloud = "http://172.22.0.2:8000";
        queue = Volley.newRequestQueue(getActivity());

        wifiLV = binding.wifiLV;

        loadWifi();
        View root = binding.getRoot();
        return root;
    }

    private void loadWifi() {
        SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        String username = sp.getString("username","");
        String user_id = sp.getString("user_id","");
        StringRequest req = new StringRequest(Request.Method.GET, cloud+"/get_wifi_api",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            String msg = res.get("msg").toString();
                            if(msg.equals(getResources().getString(R.string.device_list_ok)))
                            {
                                String wifilist = res.get(getResources().getString(R.string.wifilist)).toString();
                                JSONObject wifi = new JSONObject(wifilist);
                                Iterator<String> keys = wifi.keys();

                                ArrayList<WifiModal> wifiModalArrayList = new ArrayList<>();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    if (wifi.get(key) instanceof JSONObject) {
                                        JSONObject device = new JSONObject(wifi.get(key).toString());
                                        String ssid = device.get("ssid").toString();
                                        String uuid = device.get("uuid").toString();
                                        String user_id = device.get("user_id").toString();
                                        String ect = device.get("ect").toString();
                                        String krack = device.get("krack").toString();
                                        String password = device.get("password").toString();
                                        WifiModal w = new WifiModal(ssid,uuid,user_id,password,ect,krack);
                                        wifiModalArrayList.add(w);
                                    }
                                }
                                WifiAdapter adapter = new WifiAdapter(getActivity(), wifiModalArrayList);
                                wifiLV.setAdapter(adapter);
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