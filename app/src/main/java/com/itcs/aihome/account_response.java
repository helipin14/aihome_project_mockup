package com.itcs.aihome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class account_response extends AppCompatActivity {

    private LinearLayout container_all, container_layout1, container_layout2, container_askpass, container_kwh;
    private String tipe, iduser, username_str, TAG;
    private ImageButton back, showpass;
    private Button username, save_username, cancel_username, save_passbaru, change_kwh, changekwhbtn, cancelkwhbtn;
    private EditText username_detail, askpass, username_baru, passbaru, kpassbaru, passlama, harga_kwh;
    private int flag = 0;
    private View parent;
    private Snackbar snackbar;
    private CheckBox showpass2;
    private DecimalFormat decimalFormat;
    private Double harga;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_response);

        // initialization
        container_all =  findViewById(R.id.container_all);
        container_layout1 =  findViewById(R.id.container_layout1);
        container_layout2 =  findViewById(R.id.container_layout2);
        container_askpass =  findViewById(R.id.container_askpass);
        back =  findViewById(R.id.back);
        username =  findViewById(R.id.change_username);
        username_detail =  findViewById(R.id.username_detail);
        askpass =  findViewById(R.id.askpass);
        username_baru =  findViewById(R.id.username_baru);
        save_username =  findViewById(R.id.save_username);
        cancel_username =  findViewById(R.id.cancel_username);
        passbaru =  findViewById(R.id.passwordbaru);
        kpassbaru =  findViewById(R.id.konfirmasi_passbaru);
        passlama = findViewById(R.id.passwordlama);
        save_passbaru =  findViewById(R.id.save_passbaru);
        showpass =  findViewById(R.id.showpass);
        showpass2 =  findViewById(R.id.showpass2);
        container_kwh = findViewById(R.id.container_kwh_settings);
        decimalFormat = new DecimalFormat("#.###");
        harga_kwh = findViewById(R.id.total_biaya_edittext);
        change_kwh = findViewById(R.id.change_kwh_price);
        changekwhbtn = findViewById(R.id.showchangekwh);
        cancelkwhbtn = findViewById(R.id.cancel_kwh);
        TAG = account_response.class.getSimpleName();

        // methods
        hidingElement();
        getData();
        getDataUser();
        showElement();
        backToPage();
        change();
        changeUsername();
        changePassword();
        cancel();
        showPassword();
        ambilDataHarga();
        showKwhChangeButton();
        changeKWHPrice();
    }

    private void backToPage() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showPassword() {
        showpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 0) {
                    flag = 1;
                    showpass.setImageResource(R.drawable.eye_slash);
                    askpass.setTransformationMethod(null);
                } else {
                    flag = 0;
                    showpass.setImageResource(R.drawable.eye);
                    askpass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        showpass2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    passbaru.setTransformationMethod(null);
                    kpassbaru.setTransformationMethod(null);
                    passlama.setTransformationMethod(null);
                } else {
                    passbaru.setTransformationMethod(new PasswordTransformationMethod());
                    kpassbaru.setTransformationMethod(new PasswordTransformationMethod());
                    passlama.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }

    private void change() {
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.container_askpass).setVisibility(View.VISIBLE);
                username.setVisibility(View.GONE);
            }
        });
    }

    private void changePassword() {
        save_passbaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passlama.getText().toString().isEmpty() && passbaru.getText().toString().isEmpty() && kpassbaru.getText().toString().isEmpty()) {
                    Toast.makeText(account_response.this, "Please fill the empty field!", Toast.LENGTH_LONG).show();
                } else if(passlama.getText().toString().isEmpty() || passbaru.getText().toString().isEmpty() || kpassbaru.getText().toString().isEmpty()){
                    Toast.makeText(account_response.this, "Please fill the empty field!", Toast.LENGTH_LONG).show();
                } else if(!kpassbaru.getText().toString().trim().equals(passbaru.getText().toString().trim())) {
                    Toast.makeText(account_response.this, "Confirmation password isn't the same as password", Toast.LENGTH_LONG).show();
                } else {
                    sendDataPassword();
                }
            }
        });
    }

    private void changeUsername() {
        save_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(askpass.getText().toString().trim().isEmpty() && username_baru.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill the empty field!", Toast.LENGTH_LONG).show();
                } else {
                    sendData();
                }
            }
        });
    }

    private void cancel() {
        cancel_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCancel();
            }
        });
    }

    private void dialogCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(account_response.this);
        builder.setMessage("Are you sure want to cancel?");
        builder.setTitle("Confirmation cancel");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username.setVisibility(View.VISIBLE);
                container_askpass.setVisibility(View.GONE);
                username_baru.setText("");
                askpass.setText("");
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setIcon(R.drawable.exclamation).show();
    }

    private void hidingElement() {
        container_layout1.setVisibility(View.GONE);
        container_layout2.setVisibility(View.GONE);
    }

    private void showElement() {
        if(tipe.equals("username")) {
            container_layout1.setVisibility(View.VISIBLE);
        } else if(tipe.equals("password")){
            container_layout2.setVisibility(View.VISIBLE);
        } else {
            container_kwh.setVisibility(View.VISIBLE);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        tipe = intent.getStringExtra("tipe");
    }

    private void getDataUser() {
        String data_user = getDefaults("data_user", getApplicationContext());
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data) && !TextUtils.isEmpty(data_user)) {
            try {
                // iduser
                JSONObject jsonObject = new JSONObject(data_user);
                iduser = jsonObject.getString("iduser");
                // username
                JSONObject jsonObject1 = new JSONObject(data);
                JSONObject user = jsonObject1.getJSONObject("user");
                username_str = user.getString("name");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        username_detail.setText(username_str, TextView.BufferType.NORMAL);
        username_detail.setEnabled(false);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private void sendData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://dataaihome.itcs.co.id/changeName.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from change name : " + response);
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getString("status").equals("success")) {
                        JSONObject jsonObject = new JSONObject(getDefaults("data", getApplicationContext()));
                        JSONObject user = jsonObject.getJSONObject("user");
                        user.put("name", username_baru.getText().toString());
                        setDefaults("data", jsonObject.toString(), getApplicationContext());
                        successUsername();
                    } else {
                        showSnack("Failed to change your name! Please try again!", R.color.colorRed);
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
                params.put("password", askpass.getText().toString().trim());
                params.put("name", username_baru.getText().toString().trim());
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

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void successUsername() {
        parent = findViewById(R.id.parent_container);
        username_detail.setText(username_baru.getText().toString(),  TextView.BufferType.NORMAL);
        askpass.setText("");
        username_baru.setText("");
        container_askpass.setVisibility(View.GONE);
        snackbar = Snackbar.make(parent, "Success to change your name!", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(R.color.colorGreen);
        snackbar.show();
    }

    private void successPassword() {
        parent = findViewById(R.id.parent_container);
        passlama.setText("");
        passbaru.setText("");
        kpassbaru.setText("");
        snackbar = Snackbar.make(parent, "Success to change your password!", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(R.color.colorGreen);
        snackbar.show();
    }

    private void sendDataPassword() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://dataaihome.itcs.co.id/changePassword.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from change name : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")) {
                        successPassword();
                    } else {
                        Toast.makeText(account_response.this, "Failed to change password! Please look at your old password", Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
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
                params.put("old_password", passlama.getText().toString().trim());
                params.put("new_password", passbaru.getText().toString().trim());
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

    private void showKwhChangeButton() {
        changekwhbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                harga_kwh.setEnabled(true);
                changekwhbtn.setVisibility(View.GONE);
                findViewById(R.id.container_change_kwh_button).setVisibility(View.VISIBLE);
            }
        });
        cancelkwhbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelKWH();
            }
        });
    }

    private void cancelKWH() {
        AlertDialog.Builder builder = new AlertDialog.Builder(account_response.this);
        builder.setMessage("Are you sure want to cancel?");
        builder.setTitle("Confirmation cancel");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                harga_kwh.setText(decimalFormat.format(harga));
                harga_kwh.setEnabled(false);
                findViewById(R.id.container_change_kwh_button).setVisibility(View.GONE);
                changekwhbtn.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setIcon(R.drawable.exclamation).show();
    }

    private void changeKWHPrice() {
        change_kwh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!harga_kwh.getText().toString().isEmpty()) {
                    harga = Double.parseDouble(harga_kwh.getText().toString());
                    ubahHargaKWH();
                } else {
                    Toast.makeText(account_response.this, "Please fill the empty field!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void ubahHargaKWH() {
        String url = "http://dataaihome.itcs.co.id/change_kwhPrice.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response dari change kwh price : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")) {
                        successKWH();
                        changeKwhPriceInside(harga.toString());
                    } else {
                        failedKWH();
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
                Map<String, String> params = new HashMap<>();
                params.put("iduser", iduser);
                params.put("harga_kwh", String.valueOf(harga));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void successKWH() {
        findViewById(R.id.container_change_kwh_button).setVisibility(View.GONE);
        changekwhbtn.setVisibility(View.VISIBLE);
        harga_kwh.setEnabled(false);
        harga_kwh.setText(decimalFormat.format(harga));
        String message = "Success to change KWH price!";
        int color = R.color.colorGreen;
        showSnack(message, color);
    }

    private void failedKWH() {
        String message = "Failed to change KWH price! Try again!";
        int color = R.color.colorRed;
        showSnack(message, color);
    }

    private void ambilDataHarga() {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                harga = Double.parseDouble(jsonObject.getString("harga_kwh"));
                harga_kwh.setText(decimalFormat.format(harga));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeKwhPriceInside(String harga) {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                harga = harga.replace(".0", "");
                jsonObject.put("harga_kwh", harga);
                Log.e(TAG, "Hasil dari ubah harga kwh : " + jsonObject.toString());
                setDefaults("data", jsonObject.toString(), getApplicationContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void inputFilter() {
        harga_kwh.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return false;
            }
        });
    }

    private void showSnack(String message, int color) {
        snackbar = Snackbar.make(findViewById(R.id.parent_container), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(color);
        snackbar.show();
    }
}
