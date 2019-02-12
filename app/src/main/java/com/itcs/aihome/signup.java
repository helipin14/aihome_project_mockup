package com.itcs.aihome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import io.socket.client.Socket;

public class signup extends AppCompatActivity {

    private Button signup;
    private TextView login;
    private MaterialEditText name, email, pass, cpass;
    private Boolean isOK, isSame = false;
    private String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private SocketListener listener;

    @Override
    protected void onStart() {
        super.onStart();
        startSocket();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        init();
        main();
    }

    private void init() {
        login = findViewById(R.id.goto_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
            }
        });
        signup = findViewById(R.id.signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this, VerifyEmail.class);
                startActivity(intent);
            }
        });
        name = findViewById(R.id.s_nama);
        email = findViewById(R.id.s_email);
        pass = findViewById(R.id.s_pass);
        cpass = findViewById(R.id.s_cpass);
    }

    private void main() {
        sendData();
        if(!isPasswordSame()) {
            Toast.makeText(this, "Password and confirmed password isn't same", Toast.LENGTH_LONG).show();
        }
    }

    private void sendData() {
        if(!isDataEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject("data");
                jsonObject.put("name", name.getText().toString().trim());
                jsonObject.put("password", pass.getText().toString().trim());
                jsonObject.put("email", email.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidEmailId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private boolean isDataEmpty() {
        if(!name.getText().toString().isEmpty()
          && !email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()
                && !cpass.getText().toString().isEmpty() && isValidEmailId(email.getText().toString()) && isPasswordSame()) {
            isOK = false;
        } else if(name.getText().toString().isEmpty()
                || email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()
                || cpass.getText().toString().isEmpty()) {
            isOK = true;
        } else {
            isOK = true;
        }
        return isOK;
    }

    private boolean isPasswordSame() {
        if(pass.getText().toString().equals(cpass.getText().toString())) {
            isSame = false;
        } else {
            isSame = true;
        }
        return isSame;
    }

    // create folder for image profile users
    private void createFolderProfile() {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "/Image Profile");
        boolean success = true;
        if(!folder.exists()) {
            success = folder.mkdir();
        }
        if(success) {
            Log.e(TAG, "Berhasil membuat folder");
        } else {
            Log.e(TAG, "Gagal membuat folder");
        }
    }

    private void startSocket() {
        listener = new SocketListener();
        socket = listener.getSocket();
        socket.connect();
        Log.e(TAG, "Socket IO Running");
        socket.emit("vEmail", "Hello");
    }
}
