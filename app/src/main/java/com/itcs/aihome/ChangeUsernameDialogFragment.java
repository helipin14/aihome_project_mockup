package com.itcs.aihome;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUsernameDialogFragment extends DialogFragment {

    public static final String TAG = "change_username_dialog";
    private Toolbar toolbar;
    private Button save;
    private EditText username, password;
    private Snackbar snackbar;
    private String iduser;
    private int flag = 0;
    private ImageButton showpass;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    public static ChangeUsernameDialogFragment display(FragmentManager fm) {
        ChangeUsernameDialogFragment fragment = new ChangeUsernameDialogFragment();
        fragment.show(fm, TAG);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fullscreen_dialog_username, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        save = view.findViewById(R.id.save_username);
        username = view.findViewById(R.id.username_baru);
        password = view.findViewById(R.id.askpass);
        showpass = view.findViewById(R.id.showpass);
        getDataUser();
        saveData(view);
        showPassword();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        toolbar.setTitle("Change Username");
    }

    private void saveData(final View itemView) {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(username.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(getContext(), "Please fill empty field!", Toast.LENGTH_LONG).show();
                } else {
                    sendData(itemView);
                }
            }
        });
    }

    private void sendData(final View view) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "http://dataaihome2.itcs.co.id/changeName.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from change name : " + response);
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getString("status").equals("success")) {
                        JSONObject jsonObject = new JSONObject(getDefaults("data", getContext()));
                        JSONObject user = jsonObject.getJSONObject("user");
                        user.put("name", username.getText().toString());
                        setDefaults("data", jsonObject.toString(), getContext());
                        showSnack("Success to change your username!", R.color.colorGreen, view);
                    } else {
                        showSnack("Failed to change your username! Please try agian!", R.color.colorRed, view);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", iduser);
                params.put("password", password.getText().toString().trim());
                params.put("name", username.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void showSnack(String message, int color, View view) {
        snackbar = Snackbar.make(view.findViewById(R.id.parent_dialog_username), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(color);
        snackbar.show();
    }

    private void showPassword() {
        showpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 0) {
                    flag = 1;
                    showpass.setImageResource(R.drawable.eye_slash);
                    password.setTransformationMethod(null);
                } else {
                    flag = 0;
                    showpass.setImageResource(R.drawable.ic_remove_red_eye_black_48dp);
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }

    private void getDataUser() {
        String data = getDefaults("data_user", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                iduser = jsonObject.getString("iduser");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
