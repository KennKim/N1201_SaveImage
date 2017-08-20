package com.example.conscious.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri imageUri;
    private Uri albumUri;
    private TextView tvURI;
    private ImageView iv_UserPhoto;
    private ImageView iv_AlbumImg;
    private int id_view;

//    private DB_Manger dbmanger;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        dbmanger = new DB_Manger();

        tvURI = (TextView) findViewById(R.id.tvURI);
        iv_UserPhoto = (ImageView) this.findViewById(R.id.user_image);
        iv_AlbumImg = (ImageView) this.findViewById(R.id.ivAlbumImg);
        Button btn_UploadPicture = (Button) this.findViewById(R.id.btn_UploadPicture);
        Button btn = (Button) findViewById(R.id.buttonTest);
        Button btnTestFile = (Button) findViewById(R.id.btn_test_file_activity);


        btn_UploadPicture.setOnClickListener(this);
        btn.setOnClickListener(this);
    }


    /**
     * 카메라에서 사진 촬영
     */
    public void openCamera() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    public void openGallery() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_LONG).show();
//            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                imageUri = data.getData();
                Log.d("SmartWheel", imageUri.getPath().toString());

            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageUri, "image/*");

//                CROP할 이미지를 200*200 크기로 저장
//                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
//                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동
                break;
            }
            case CROP_FROM_iMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle b = data.getExtras();

                if (b != null) {
                    Bitmap bitmap = b.getParcelable("data"); // CROP된 BITMAP
                    saveCropImage(bitmap); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    break;
                }

                // 임시 파일 삭제
                File f = new File(imageUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }


    }

    @Override
    public void onClick(View v) {
        id_view = v.getId();
//        if(v.getId() == R.id.btn_signupfinish) {
//            /** SharedPreference 환경 변수 사용 **/
//            SharedPreferences prefs = getSharedPreferences("login", 0);
//            /** prefs.getString() return값이 null이라면 2번째 함수를 대입한다. **/
//            String login = prefs.getString("USER_LOGIN", "LOGOUT");
//            String facebook_login = prefs.getString("FACEBOOK_LOGIN", "LOGOUT");
//            String user_id = prefs.getString("USER_ID","");
//            String user_name = prefs.getString("USER_NAME", "");
//            String user_password = prefs.getString("USER_PASSWORD", "");
//            String user_phone = prefs.getString("USER_PHONE", "");
//            String user_email = prefs.getString("USER_EMAIL", "");
////            dbmanger.select(user_id,user_name,user_password, user_phone, user_email);
////            dbmanger.selectPhoto(user_name, imageUri, absoultePath);
//
////            Intent mainIntent = new Intent(SignUpPhotoActivity.this, LoginActivity.class);
////            SignUpPhotoActivity.this.startActivity(mainIntent);
////            SignUpPhotoActivity.this.finish();
////            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
//
//        }else

        if (v.getId() == R.id.btn_UploadPicture) {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openCamera();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openGallery();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();

        } else if (v.getId() == R.id.buttonTest) {
            Toast.makeText(this, "buttonTest", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv_AlbumImg.setImageBitmap(bitmap);
        } else if(v.getId() == R.id.btn_test_file_activity){
            startActivity(new Intent(MainActivity.this,TestFile.class));
        }
    }

    /*
     * Bitmap을 저장하는 부분
     */
    private void saveCropImage(Bitmap bitmap) {

        iv_UserPhoto.setImageBitmap(bitmap); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌

        // CROP된 이미지를 저장하기 위한 FILE 경로
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/SmartWheel/" + System.currentTimeMillis() + ".jpg";

        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File fileDir = new File(dirPath);

        if (!fileDir.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            fileDir.mkdir();

        File file = new File(filePath);
        BufferedOutputStream out = null;

        try {

            file.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        albumUri = Uri.fromFile(new File(filePath));

    }
//
//    // Bitmap to File
////bitmap에는 비트맵, strFilePath에 는 파일을 저장할 경로, dirPath 에는 파일 이름을 할당해주면 됩니다.
//    public static void saveThumb(Bitmap bitmap, String dirPath, String filename) {
//
//        String thumbFileName = "thumb_"+filename;
//        File file = new File(dirPath);
//
//        // If no folders
//        if (!file.exists()) {
//            file.mkdirs();
//            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//        }
//        File fileCacheItem = new File(dirPath + thumbFileName);
//        OutputStream out = null;
//
//
//        try {
//
//            int height = bitmap.getHeight();
//            int width = bitmap.getWidth();
//
//
//            fileCacheItem.createNewFile();
//            out = new FileOutputStream(fileCacheItem);
////160 부분을 자신이 원하는 크기로 변경할 수 있습니다.
//            bitmap = Bitmap.createScaledBitmap(bitmap, 80, height / (width / 160), true);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                out.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}

