package com.ogma.dealshaiapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.dialog.BasicInfoEditView;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;
import com.ogma.dealshaiapp.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class MyAccount extends AppCompatActivity implements View.OnClickListener {

    private ImageView profile_image;
    private LinearLayout parentPanel;
    private static final int REQUEST_STORAGE = 2;
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String userChoosenTask;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String dob;
    private String imageUrl;

    private TextView et_profile_name;
    private TextView contact_number;
    private TextView et_email;
    private TextView et_dob;
    private TextView et_gender;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String path;
    private File image;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        parentPanel = findViewById(R.id.parentPanel);
        profile_image = findViewById(R.id.profile_image);
        ImageView fab_img_edit = findViewById(R.id.fab_img_edit);
        ImageView iv_info_edit = findViewById(R.id.iv_info_edit);

        et_profile_name = findViewById(R.id.et_profile_name);
        contact_number = findViewById(R.id.contact_number);
        et_email = findViewById(R.id.et_email);
        et_dob = findViewById(R.id.et_dob);
        et_gender = findViewById(R.id.et_gender);


        fab_img_edit.setOnClickListener(this);
        iv_info_edit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session session = new Session(MyAccount.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);
        name = user.get(Session.KEY_NAME);
        email = user.get(Session.KEY_EMAIL);
        phone = user.get(Session.KEY_CONTACT_NUMBER);
        gender = user.get(Session.KEY_GENDER);
        dob = user.get(Session.KEY_DOB);
        imageUrl = user.get(Session.KEY_IMAGE);

        if (name != null)
            et_profile_name.setText(name);
        else
            name = "";

        if (email != null)
            et_email.setText(email);

        if (phone != null) {
            contact_number.setText(phone);
        }

        if (gender != null)
            et_gender.setText(gender);
        else
            gender = "";

        if (dob != null)
            et_dob.setText(dob);
        else
            dob = "";

        switch (gender) {
            case "Male":
                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.pf_male)
                        .showImageForEmptyUri(R.drawable.pf_male)
                        .showImageOnFail(R.drawable.pf_male)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
                break;
            case "Female":
                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.pf_female)
                        .showImageForEmptyUri(R.drawable.pf_female)
                        .showImageOnFail(R.drawable.pf_female)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
                break;
            default:
                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.pf_male)
                        .showImageForEmptyUri(R.drawable.pf_male)
                        .showImageOnFail(R.drawable.pf_male)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
        }

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(MyAccount.this));
        }
        imageLoader.displayImage(imageUrl, profile_image, options);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyAccount.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MyAccount.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                    Snackbar.make(parentPanel, "You can't use this without this permissions.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                path = onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                path = onCaptureImageResult(data);

            if (path != null) {
                Session session = new Session(MyAccount.this);
                session.setKeyImagePath(path);
                image = new File(path);
            } else
                image = null;
            saveUserDetails(userId, name, email, phone, dob, gender, image);
        }
    }

    private void saveUserDetails(String userId, String name, String email, String phone, String dob, String gender, File image) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(MyAccount.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int isError = jsonObject.getInt("err");
                    if (isError == 0) {
                        Snackbar.make(parentPanel, "Information Saved Successfully.", Snackbar.LENGTH_LONG).show();
                        JSONObject user = jsonObject.getJSONObject("userData");
                        final String imgUri = user.getString("img");
                        Session session = new Session(MyAccount.this);
                        session.saveUserLoginSession(
                                user.getString("id"),
                                user.getString("name"),
                                user.getString("phone"),
                                user.getString("email"),
                                user.getString("dob"),
                                user.getString("gander"),
                                user.getString("img"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageLoader.displayImage(imgUri, profile_image, options);
                            }
                        });


                    } else
                        Snackbar.make(parentPanel, "An internal error occur.", Snackbar.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.saveUserDetails(userId, name, email, phone, dob, gender, image);
    }

    private String onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profile_image.setImageBitmap(thumbnail);
        return destination.getAbsolutePath();
    }

    @SuppressWarnings("deprecation")
    private String onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        FileOutputStream fo;
        File destination = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profile_image.setImageBitmap(bm);
        if (destination != null) {
            return destination.getAbsolutePath();
        }
        return null;
    }

    private void requestCameraAndStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(parentPanel, "Camera & Storage permission required.", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MyAccount.this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
                    ActivityCompat.requestPermissions(MyAccount.this, PERMISSIONS_STORAGE, REQUEST_STORAGE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_img_edit:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestCameraAndStoragePermissions();
                } else {
                    selectImage();
                }

                break;

            case R.id.iv_info_edit:
                BasicInfoEditView basicInfoEditView =
                        new BasicInfoEditView(MyAccount.this, userId, name, email, phone, gender, dob, path);
                basicInfoEditView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onResume();
                    }
                });
                basicInfoEditView.show();
//                count++;
//                if (count % 2 == 0) {
//
//                    String name = et_profile_name.getText().toString();
//                    String phone = contact_number.getText().toString();
//                    String email = et_email.getText().toString();
//                    String dob = et_dob.getText().toString();
//
//                    if (rdb_female.isChecked()) {
//                        gender = "Female";
//                    } else {
//                        gender = "Male";
//                    }
//
//                    saveUserDetails(name, phone, email, gender, dob);
//
//                    iv_info_edit.setImageResource(R.drawable.ic_edit);
//                    et_profile_name.setEnabled(false);
//                    contact_number.setEnabled(false);
//                    et_email.setEnabled(false);
//
//                } else {
//                    iv_info_edit.setImageResource(R.drawable.ic_save);
//                    et_profile_name.setEnabled(true);
//                    et_profile_name.setCursorVisible(true);
//                    contact_number.setEnabled(true);
//                    et_email.setEnabled(true);
//                }
                break;
        }
    }
}
