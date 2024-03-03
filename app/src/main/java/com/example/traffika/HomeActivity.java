package com.example.traffika;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.Manifest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.traffika.db.BackgroundWorker;
import com.example.traffika.search.SearchActivity;

import java.time.OffsetTime;


public class HomeActivity extends AppCompatActivity {
    private TextView textInput;
    EditText etviolation, etnumberplate;
    String username;
    private Uri selectedImageUri;
    ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123; // Define your own request code





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = getIntent().getStringExtra("username");

        //textInput = findViewById(R.id.text_input);
        Button submitButton = findViewById(R.id.submit_button);
        ImageButton viewButton = findViewById(R.id.button_view);
        ImageButton sanctionsButton = findViewById(R.id.button_sanctions);
        etviolation=findViewById(R.id.etViolation);
        etnumberplate=findViewById(R.id.etNumberPlate);

        Button currentUser = findViewById(R.id.loggedin_user);
        currentUser.setText(username);


        Toast.makeText(this,"Welcome back "+username,Toast.LENGTH_LONG).show();

    }
    public void submitReport(View view) {
        String type="report";
        String numberplate=etnumberplate.getText().toString();
        String violation=etviolation.getText().toString();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String time= String.valueOf(OffsetTime.now());

            BackgroundWorker backgroundWorker=new BackgroundWorker(this, null);
            backgroundWorker.execute(type,violation, username, numberplate,time);
            etviolation.setText("");
            etnumberplate.setText("");
        }
    }
    public void onViewHistory(View view){

    }
    public void onAddImage(View view){
        showPopupWindow();
    }
    public void onViewViolations(View view){

    }
    public void onManageSanctions(View view){

    }
    public void search(View view){
        Intent intent=new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
    public void goHome(View view){

    }
    public void goToAccount(View view){

    }
    private void showPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.upload_image_popup, null);

        int width = 340;
        int height = 340;
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // Handle button clicks inside the popup
        imageView=popupView.findViewById(R.id.uploadedImage);
        Button selectImageBtn = popupView.findViewById(R.id.button_select_image);
        Button uploadBtn = popupView.findViewById(R.id.button_upload);

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    // Request the permission
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    // Permission has already been granted
                    // Open file picker
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    uploadImage(selectedImageUri);
                } else {
                    Toast.makeText(HomeActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            selectedImageUri = data.getData();


            imageView.setImageURI(selectedImageUri);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle selecting an image
                    uploadImage(selectedImageUri);
                }
            });
        }
    }
    private void uploadImage(Uri imageUri) {
        // Get the file path from the URI
        String path = getRealPathFromURI(imageUri);

        // Check if file path is valid
        if (path != null) {
            // Create an instance of BackgroundWorker
            BackgroundWorker backgroundWorker = new BackgroundWorker(this, username);

            backgroundWorker.execute("upload", path);
                Toast.makeText(this, "The path is: "+path, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to get file path", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String path = null;
        String documentId = DocumentsContract.getDocumentId(uri);
        String[] split = documentId.split(":");
        String type = split[0];
        String id = split[1];

        if ("image".equals(type)) {
            // Handle image document type
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media._ID + " = ?",
                    new String[]{id},
                    null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    }
                } finally {
                    cursor.close();
                }
            }
        } else if ("video".equals(type)) {
            // Handle video document type
            // Add your logic here for handling videos
        } else if ("audio".equals(type)) {
            // Handle audio document type
            // Add your logic here for handling audio files
        }
        // Add more else-if blocks for handling other document types if needed

        return path;
    }





}