package com.ogma.dealshaiapp.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.MyAccount;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

public class BasicInfoEditView extends Dialog implements View.OnClickListener {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String dob;
    private TextInputEditText et_name;
    private TextInputEditText et_email;
    private TextInputEditText et_phone;
    private AppCompatTextView et_dob;
    private RadioButton rdb_male;
    private RadioButton rdb_female;

    private Activity activity;
    private String date;
    Calendar c = Calendar.getInstance();
    final int year = c.get(Calendar.YEAR);
    final int mon = c.get(Calendar.MONTH);
    final int day = c.get(Calendar.DAY_OF_MONTH);
    String path;
    private String encode;
    private File image;
    private CardView parentPanel;

    public BasicInfoEditView(MyAccount myAccount, String userId, String name, String email, String phone, String gender, String dob, String path) {
        super(myAccount);
        this.activity = myAccount;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dob = dob;
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_info_edit_view);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Session session = new Session(getContext());
        HashMap<String, String> imagePath = session.getKeyImagePath();
        path = imagePath.get(Session.KEY_IMAGE_PATH);

        parentPanel = findViewById(R.id.parentPanel);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_dob = findViewById(R.id.et_dob);

        rdb_male = findViewById(R.id.rdb_male);
        rdb_female = findViewById(R.id.rdb_female);

        TextView tv_btn_save = findViewById(R.id.tv_btn_save);
        TextView tv_btn_cancel = findViewById(R.id.tv_btn_cancel);

        rdb_male.setChecked(true);

        if (name != null) {
            et_name.setText(name);
        }

        if (email != null) {
            et_email.setText(email);
        }

        if (phone != null) {
            et_phone.setText(phone);
        }

        if (gender != null) {
            switch (gender) {
                case "Male":
                    rdb_male.setChecked(true);
                    break;
                case "Female":
                    rdb_female.setChecked(true);
                    break;
            }
        }

        if (dob != null) {
            et_dob.setText(dob);
        }

        session = new Session(getContext());
        HashMap<String, String> user = session.getUserDetails();

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                et_dob.setText("" + String.valueOf(selectedDay) + "-" + String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedYear) + "");
                date = et_dob.getText().toString();
            }
        };

        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog dialog = new DatePickerDialog(getContext(), datePickerListener, year, mon, day);
                dialog.show();
            }
        });


        tv_btn_save.setOnClickListener(this);
        tv_btn_cancel.setOnClickListener(this);
    }

    private void saveUserDetails(final String userId, String name, String email, String phone, String date, String gender, File image) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int isError = jsonObject.getInt("err");
                    if (isError == 0) {
                        Snackbar.make(parentPanel, "Information Saved Successfully.", Snackbar.LENGTH_LONG).show();
                        JSONObject user = jsonObject.getJSONObject("userData");
                        Session session = new Session(getContext());
                        session.saveUserLoginSession(
                                user.getString("id"),
                                user.getString("name"),
                                user.getString("phone"),
                                user.getString("email"),
                                user.getString("dob"),
                                user.getString("gander"),
                                user.getString("img"));
                    } else
                        Snackbar.make(parentPanel, "An internal error occur.", Snackbar.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });

            }
        };
        webServiceHandler.saveUserDetails(userId, name, email, phone, date, gender, image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_save:
                if (isValid()) {
                    email = et_email.getText().toString();
                    phone = et_phone.getText().toString();
                }
                name = et_name.getText().toString();
                date = et_dob.getText().toString();
                gender = rdb_male.isChecked() ? "Male" : "Female";

                if (path != null) {
                    image = new File(path);
                } else {
                    image = null;
                }
                saveUserDetails(userId, name, email, phone, date, gender, image);
                break;

            case R.id.tv_btn_cancel:
                dismiss();
                break;
        }
    }

    private boolean isValid() {
        if (et_email.getText().toString().trim().equals("")) {
            et_email.setError("Please provide a valid email id.");
            return false;
        }
        if (et_phone.getText().toString().trim().equals("")) {
            et_phone.setError("Please provide a valid phone number.");
            return false;
        }
        return true;
    }
}
