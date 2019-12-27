package com.example.gisyproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    public static View root;
    public static Spinner statespin;
    public static EditText city;
    public static EditText address;
    public static TextView username;
    public static TextView useremail;
    public static ImageView userphoto;
    public static Button UPDATE;
    public static ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        TextView resetPass=(TextView)root.findViewById(R.id.homeResetPassword);

        statespin=(Spinner)root.findViewById(R.id.spinnerState);
        city=(EditText) root.findViewById(R.id.CityName);
        address=(EditText)root.findViewById(R.id.FullAddress);
        username=root.findViewById(R.id.dashUserName);
        useremail=root.findViewById(R.id.dashUserEmail);
        userphoto=root.findViewById(R.id.dashUserProfileImage);
        UPDATE=root.findViewById(R.id.updateBtn);
        city.setText("");
        address.setText("");
        statespin.setPrompt("select state");


        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(getContext());
                progressDialog.setTitle("Reset Password");
                progressDialog.show();

                mAuth.sendPasswordResetEmail(useremail.getText().toString())

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
            }
        });


        root.findViewById(R.id.EditButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city.setEnabled(true);
                address.setEnabled(true);
                statespin.setEnabled(true);
                UPDATE.setEnabled(true);

                //
                city.setText("");
                address.setText("");
                statespin.setPrompt("select Degree");



            }
        });
        UPDATE=root.findViewById(R.id.updateBtn);
        UPDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploader();
            }
        });
        UpdateUi();

        return root;
    }
    public void Uploader(){
//        Spinner statespin=(Spinner)root.findViewById(R.id.spinnerState);
//        EditText city=(EditText) root.findViewById(R.id.CityName);
//        EditText address=(EditText)root.findViewById(R.id.FullAddress);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Update");
        progressDialog.show();
        progressDialog.setMessage("Updating user details");


        final String state=String.valueOf(statespin.getSelectedItem());
        final String cityname=city.getText().toString();
        final String fulladress=address.getText().toString();

        UserData userData=new UserData(
                state,
                cityname,
                fulladress
        );
        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("College")
                .setValue(cityname);
        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Degree")
                .setValue(state);
        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Address")
                .setValue(fulladress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Successfully updated!", Toast.LENGTH_SHORT).show();

                    UpdateUi();
                    progressDialog.dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Error occured!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
    public void disableInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        editText.setTextIsSelectable(false);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true;
            }


        });
    }
    public void UpdateUi(){
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.show();
        progressDialog.setMessage("Fetching user details..");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);



                city.setText("");
                address.setText("");
                statespin.setPrompt("select Degree");
                statespin.setPrompt(dataSnapshot.child("Degree").getValue().toString());
                city.setText(dataSnapshot.child("College").getValue().toString());
                address.setText(dataSnapshot.child("Address").getValue().toString());

                username.setText(dataSnapshot.child("Username").getValue().toString());
                useremail.setText(dataSnapshot.child("Email").getValue().toString());
                Picasso.get().load(dataSnapshot.child("ProfileImageuRL").getValue().toString()).into(userphoto);
                //Log.d(TAG, "Value is: " + value);
                city.setEnabled(false);
                address.setEnabled(false);
                statespin.setEnabled(false);
                UPDATE.setEnabled(false);
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}