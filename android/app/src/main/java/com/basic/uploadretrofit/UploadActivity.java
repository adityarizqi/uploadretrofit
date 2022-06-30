package com.basic.uploadretrofit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.basic.uploadretrofit.api.Server;
import com.basic.uploadretrofit.config.ApiClient;
import com.basic.uploadretrofit.utils.PathUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private final int CHOOSE_IMAGE = 30;
    private final int TAKE_IMAGE = 40;
    private final int PERMISSION_REQUEST = 1 << 3;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private MultipartBody.Part imagePart;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        
        server = ApiClient.getRetrofitInstance().create(Server.class);

        findViewById(R.id.chooseImageBtn).setOnClickListener(view -> {
            if(!checkForPermission()){
                requestPermission();
            }else{
                takeImage();
//                 selectImage();
            }
        });
    }

    private boolean checkForPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        return granted;
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
    }

    private void takeImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Akses izin diberikan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Akses izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_IMAGE:
                try {
                    String filePath= PathUtil.getPath(this,data.getData());
                    assert filePath != null;
                    File file = new File(filePath);
                    parsingImage(file);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case TAKE_IMAGE:
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = PathUtil.getImageUri(this, photo);
                File file = new File(PathUtil.getRealPathFromURI(this, tempUri));
                parsingImage(file);
                break;
        }
    }

    private void parsingImage(File file){
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        imagePart = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        Toast.makeText(this, "Foto berhasil ditambahkan", Toast.LENGTH_SHORT).show();

        findViewById(R.id.uploadImageBtn).setEnabled(true);
        findViewById(R.id.uploadImageBtn).setOnClickListener(view -> uploadImage());
    }

    private void uploadImage() {
        Call<String> call = server.uploadImage(imagePart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    if(response.body() != null){
                        JSONObject object = new JSONObject(response.body());
                        Toast.makeText(UploadActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }
}