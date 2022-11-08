package com.example.wifies.ui.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wifies.LoginActivity;
import com.example.wifies.R;
import com.example.wifies.WifiESActivity;
import com.example.wifies.databinding.FragmentLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    public static String TAG = "LoginFragment";
    String cloud;
    RequestQueue queue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cloud = "http://172.22.0.2:8000";
        queue = Volley.newRequestQueue(getActivity());

        Button loginBtn = binding.login;
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username= binding.unm;
                EditText password= binding.pd;
                login(username.getText().toString(),password.getText().toString());
            }
        });

        Button registerBtn = binding.register;
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username= binding.unm;
                EditText password= binding.pd;
                register(username.getText().toString(),password.getText().toString());
            }
        });

        Button switchPageBtn = binding.switchPage;
        switchPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginBtn.getVisibility()==View.GONE)
                {
                    loginBtn.setVisibility(View.VISIBLE);
                    registerBtn.setVisibility(View.GONE);
                    switchPageBtn.setText(R.string.switch_state_register);
                }
                else {
                    registerBtn.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.GONE);
                    switchPageBtn.setText(R.string.switch_state_login);
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void register(String username, String password){
        StringRequest req = new StringRequest(Request.Method.POST, cloud+"/register_api",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            String msg = res.get("msg").toString();
                            if(msg.equals(getResources().getString(R.string.register_msg)))
                            {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor Ed=sp.edit();
                                Ed.putString("username",username);
                                Ed.commit();
                                Intent intent = new Intent(getActivity(), WifiESActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        queue.add(req);
    }

    private void login(String username, String password){
        StringRequest req = new StringRequest(Request.Method.POST, cloud+"/login_api",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            String msg = res.get("msg").toString();
                            if(msg.equals(getResources().getString(R.string.login_msg)))
                            {
                                SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor Ed=sp.edit();
                                Ed.putString("username",username);
                                Ed.commit();
                                Intent intent = new Intent(getActivity(), WifiESActivity.class);
                                startActivity(intent);
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
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        queue.add(req);
    }
}