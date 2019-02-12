package com.itcs.aihome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VerifyEmail extends AppCompatActivity {

    Button confirm;
    TextView resend;
    LinearLayout skip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_email);
        init();
    }

    private void init() {
        skip = findViewById(R.id.skip);
        confirm = findViewById(R.id.confirm);
        resend = findViewById(R.id.resend);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyEmail.this, Stepper.class);
                startActivity(intent);
            }
        });
    }
}
