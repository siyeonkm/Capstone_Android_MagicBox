package com.example.capstoneblackbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstoneblackbox.databinding.ActivityHomeBinding;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    public static Context hcontext;
    private long backKeyPressedTime = 0;
    private Toast toast;

    private static final int PICK_FROM_ALBUM = 1;

    ImageButton btnvideo;
    ImageButton btncrop;
    ImageButton btnsetting;

    String date = "2021-01-01 12:30:01";
    String size = "1000";
    String path = "/uploads/test.mp4";
    int user_id = 2;
    String videopath;


    static final int REQUEST_VIDEO_CAPTURE = 1;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        hcontext = this;

        btnvideo = binding.videoButton;
        btncrop = binding.cropButton;
        btnsetting = binding.buttonSetting;

        tedPermission();
        goToAlbum();

        btnvideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                ((MainActivity)MainActivity.mcontext).connectServerPost
                        .requestPost("http://b049b8cfa4d4.ngrok.io/api/full", videopath, path, size, date, user_id);

                //기본 카메라 연결
                /*Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                   startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }*/

                //Intent intent = new Intent(HomeActivity.this, RecordActivity.class );
                //startActivity(intent);
            }
        });

        btncrop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PopupActivity.class );
                startActivity(intent);
            }
        });

        btnsetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {

            Uri uri2 = data.getData();

            if (uri2.toString().contains("video")) {
                UriToPath uri2path = new UriToPath();
                videopath = uri2path.getPath(hcontext, uri2);
            }

        }
    }
}