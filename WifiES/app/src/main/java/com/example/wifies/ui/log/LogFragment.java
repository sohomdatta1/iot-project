package com.example.wifies.ui.log;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.wifies.databinding.FragmentLogBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogFragment extends Fragment {

    private FragmentLogBinding binding;
    public static String TAG = "addDeviceFragment";
    String cloud;
    RequestQueue queue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        cloud = "http://172.22.0.2:8000";
        queue = Volley.newRequestQueue(getActivity());

        binding = FragmentLogBinding.inflate(inflater, container, false);

        Button add = binding.addWifi;

        add.setOnClickListener(v -> {
            EditText wifi_ssid = binding.wifiSsid;
            EditText wifi_password = binding.wifiPassword;
            String ssid = wifi_ssid.getText().toString();
            String password = wifi_password.getText().toString();
            SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
            String username = sp.getString("username","");
            String user_id = sp.getString("user_id","");

            StringRequest req = new StringRequest(Request.Method.POST, cloud+"/add_wifi_deets",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res = new JSONObject(response);
                                String msg = res.get("msg").toString();
                                if(msg.equals(getResources().getString(R.string.add_wifi_msg)))
                                {
                                    Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                                    wifi_password.setText("");
                                    wifi_ssid.setText("");
                                }
                                else{
                                    Toast.makeText(getActivity(),"something went wrong",Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,msg);
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
                protected Map getParams()
                {
                    Map params = new HashMap();
                    params.put("ssid", ssid);
                    params.put("password", password);

                    return params;
                }

                @Override
                public Map getHeaders() throws AuthFailureError
                {
                    HashMap headers = new HashMap();
                    headers.put("Cookie", getResources().getString(R.string.user_id_cookie)+"="+user_id);
                    return headers;
                }

            };

            queue.add(req);
        });

        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}