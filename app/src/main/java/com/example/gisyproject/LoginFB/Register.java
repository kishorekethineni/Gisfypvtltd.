package com.example.gisyproject.LoginFB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gisyproject.MainActivity;
import com.example.gisyproject.R;
import com.example.gisyproject.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Register extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST =234 ;
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";
    Button regBtn;
    RadioButton Malebtn,Femalebtn;
    Spinner spinner1;
    EditText Email,name,Password;
    String gender="";
    ImageView userImage;
    String generatedImagePath="";
    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //regBtn=(Button)findViewById(R.id.regbutton);
        Email=(EditText)findViewById((R.id.regEmail));
        Password=(EditText)findViewById(R.id.regPassword);
        name=(EditText)findViewById(R.id.UserName);
        regBtn=(Button)findViewById(R.id.Regbtn);
        spinner1=(Spinner)findViewById(R.id.spinnerState);
        Malebtn=(RadioButton)findViewById(R.id.Male);
        Femalebtn=(RadioButton)findViewById(R.id.Female);

        userImage=(ImageView)findViewById(R.id.profilepic);
        TextView back=(TextView)findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Register.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
//        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference=FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth=FirebaseAuth.getInstance();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username=name.getText().toString();
                final String email=Email.getText().toString();
                final String password=Password.getText().toString();
                final String state=String.valueOf(spinner1.getSelectedItem());
                //getting gender bro
                //gender taken
                if(Malebtn.isChecked()){
                    gender="male";
                }
                if(Femalebtn.isChecked()){
                    gender="female";
                }
                // check for empty values !
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(Register.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(state)){
                    Toast.makeText(Register.this, "Please select your state", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(state)){
                    Toast.makeText(Register.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    return;

                }
                if(filePath==null){
                    Toast.makeText(Register.this, "Please select profile photo", Toast.LENGTH_SHORT).show();
                    return;
                }




                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if(filePath!=null) {
                                        Uploader(filePath, FirebaseAuth.getInstance().getCurrentUser(), username, email, password, gender, state);
                                    }
                                    else{
                                        Toast.makeText(Register.this, "Select profile picture", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(Register.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });



            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }


    //method i declare for getting the gender value .
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }




    //my uploader for user image
    private void Uploader(Uri pickedImgUri, final FirebaseUser currentUser, final String username, final String email, final String password, final String gender, final String degree)
    {

        // first we need to upload user photo to firebase storage and get url
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Register");
        progressDialog.show();

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded succesfully
                // now we can get our image url



                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println(uri.toString());

                        User information=new User(
                                username,
                                email,
                                password,
                                gender,
                                degree,
                                "",
                                "",
                                uri.toString()

                        );



                        FirebaseDatabase.getInstance().getReference("User")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                //Toast.makeText(RegisterAcitivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //Toast.makeText(RegisterAcitivity.this, "Link "+uri.toString(), Toast.LENGTH_SHORT).show();
                        // uri contain user image url


                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                //.setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            Toast.makeText(Register.this, "You are successfully registered!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Intent i=new Intent(Register.this,MainActivity.class);
                                            startActivity(i);
                                            finish();
                                        }

                                    }
                                });

                    }
                });





            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //displaying the upload progress

                progressDialog.setMessage("Registering user ");
            }
        });






    }
}
