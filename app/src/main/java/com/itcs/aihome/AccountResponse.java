package com.itcs.aihome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountResponse extends AppCompatActivity {

    private LinearLayout container1, container2;
    private ImageButton changeprofile, back;
    private CardView opendialog, opendialog2;
    private int GALLERY = 1;
    private Bitmap image = null;
    private static Bitmap rotateImage = null;
    private CircleImageView user_profile;
    private TextView username;
    private RecyclerView recyclerView;
    private ColorItemAdapter colorItemAdapter;
    private List<ColorItem> colors;
    private SearchableSpinner spinner;
    private String TAG = this.getClass().getSimpleName();
    private List<String> timezone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_response);
        init();
        getData();
        showDialogUsername();
        showDialogPassword();
        changeProfilePicture();
        getDataUser();
        setupColors();
        back();
        getDefaultTimeZone();
        setupSpinner();
    }

    private void init() {
        container1 = findViewById(R.id.container_layout1);
        container2 = findViewById(R.id.container_layout2);
        opendialog = findViewById(R.id.open_dialog_change_username);
        opendialog2 = findViewById(R.id.change_password);
        changeprofile = findViewById(R.id.change_profile_picture);
        user_profile = findViewById(R.id.image_profile);
        username = findViewById(R.id.username_detail);
        recyclerView = findViewById(R.id.color_list);
        back = findViewById(R.id.back);
        spinner = findViewById(R.id.timezone);
    }

    private void getData() {
        Intent intent = getIntent();
        String tipe = intent.getStringExtra("tipe");
        if(tipe.equals("account")) {
            showElement(container1);
            hideElement(container2);
        } else {
            hideElement(container1);
            showElement(container2);
        }
    }

    private void showElement(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void hideElement(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.GONE);
    }

    private void showDialogUsername() {
        opendialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeUsernameDialogFragment.display(getSupportFragmentManager());
            }
        });
    }

    private void showDialogPassword() {
        opendialog2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordDialogFragment.display(getSupportFragmentManager());
            }
        });
    }

    private void changeProfilePicture() {
        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == GALLERY && resultCode != 0) {
            Uri imageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if(getOrientation(getApplicationContext(), imageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getApplicationContext(), imageUri));
                    if(rotateImage != null) {
                        rotateImage.recycle();
                        rotateImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                        user_profile.setImageBitmap(rotateImage);
                    } else {
                        user_profile.setImageBitmap(image);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if(cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private void getDataUser() {
        try {
            JSONObject jsonObject = rootData();
            JSONObject jsonObject1 = jsonObject.getJSONObject("user");
            username.setText(jsonObject1.getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject rootData() {
        String data = getDefaults("data", getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        if(!TextUtils.isEmpty(data)) {
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void setupColors() {
        loadDataColor();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        colorItemAdapter = new ColorItemAdapter(colors, getApplicationContext());
        recyclerView.setAdapter(colorItemAdapter);
    }

    private void loadDataColor() {
        colors = new ArrayList<>();
        colors.add(new ColorItem(0, Color.parseColor("#f5af19")));
        colors.add(new ColorItem(0, Color.parseColor("#e74c3c")));
        colors.add(new ColorItem(0, Color.parseColor("#3498db")));
        colors.add(new ColorItem(0, Color.parseColor("#2ecc71")));
        colors.add(new ColorItem(0, Color.parseColor("#f1c40f")));
        colors.add(new ColorItem(0, Color.parseColor("#9b59b6")));
        colors.add(new ColorItem(0, Color.parseColor("#e67e22")));
        colors.add(new ColorItem(0, Color.parseColor("#e67e22")));
        colors.add(new ColorItem(0, Color.parseColor("#2c3e50")));
        Log.e(TAG, colors.toString());
    }

    private void back() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getDefaultTimeZone() {
        timezone = new ArrayList<>();
        String[] ids = TimeZone.getAvailableIDs();
        for (int i = 0; i < ids.length; i++) {
            TimeZone d = TimeZone.getTimeZone(ids[i]);
            if (!ids[i].matches(".*/.*")) {
                continue;
            }
            String region = ids[i].replaceAll(".*/", "").replaceAll("_", " ");
            int hours = Math.abs(d.getRawOffset()) / 3600000;
            int minutes = Math.abs(d.getRawOffset() / 60000) % 60;
            String sign = d.getRawOffset() >= 0 ? "+" : "-";
            String timeZonePretty = String.format(Locale.getDefault(), "(UTC %s %02d:%02d) %s", sign, hours, minutes, region);
            timezone.add(timeZonePretty);
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> listtimezone = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timezone);
        listtimezone.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setTitle("Select time zone");
        spinner.setPositiveButton("OK");
        spinner.setAdapter(listtimezone);
    }
}
