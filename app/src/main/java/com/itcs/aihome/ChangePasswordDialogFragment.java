package com.itcs.aihome;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class ChangePasswordDialogFragment extends DialogFragment {

    public static final String TAG = "change_password_dialog";
    public MaterialEditText oldpass, newpass, cnewpass;
    public Button change;
    public android.support.v7.widget.Toolbar toolbar;
    public CheckBox showpass;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static ChangePasswordDialogFragment display(FragmentManager fm) {
        ChangePasswordDialogFragment passwordDialogFragment = new ChangePasswordDialogFragment();
        passwordDialogFragment.show(fm, TAG);
        return passwordDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.change_password_fullscreen_dialog, container, false);
        init(view);
        showPassword();
        changePassword();
        validator();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    private void init(View view) {
        toolbar = view.findViewById(R.id.password_toolbar);
        oldpass = view.findViewById(R.id.password_lama);
        newpass = view.findViewById(R.id.password_baru);
        cnewpass = view.findViewById(R.id.cpass_baru);
        change = view.findViewById(R.id.change_password);
        showpass = view.findViewById(R.id.showpass_changepass);
    }

    private void showPassword() {
        showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    oldpass.setTransformationMethod(null);
                    newpass.setTransformationMethod(null);
                    cnewpass.setTransformationMethod(null);
                } else {
                    oldpass.setTransformationMethod(new PasswordTransformationMethod());
                    newpass.setTransformationMethod(new PasswordTransformationMethod());
                    cnewpass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }

    private void changePassword() {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConfirmed() && isNull()) {
                    Toast.makeText(getContext(), "OKE diterima", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Tidak diterima", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isConfirmed() {
        boolean confirmed = false;
        String pass = newpass.getText().toString().trim();
        String cpass = cnewpass.getText().toString().trim();
        if(pass.equals(cpass)) {
            confirmed = true;
        }
        return confirmed;
    }

    private boolean isNull() {
        boolean isnull = false;
        String old_pass = oldpass.getText().toString().trim();
        String new_pass = newpass.getText().toString().trim();
        String c_new_pass = cnewpass.getText().toString().trim();
        if(!old_pass.isEmpty() && !new_pass.isEmpty() && !c_new_pass.isEmpty()) {
            isnull = true;
        }
        return isnull;
    }

    private void validator() {
        oldpass.addValidator(new RegexpValidator("Please input valid password!", "^([1-9][0-9]{0,2})?(\\\\.[0-9]?)?$"));
        newpass.addValidator(new RegexpValidator("Please input valid password!", "^([1-9][0-9]{0,2})?(\\\\.[0-9]?)?$"));
        cnewpass.addValidator(new RegexpValidator("Please input valid password!", "^([1-9][0-9]{0,2})?(\\\\.[0-9]?)?$"));
    }

    private boolean isValid() {
        boolean valid = false;
        return valid;
    }
}
