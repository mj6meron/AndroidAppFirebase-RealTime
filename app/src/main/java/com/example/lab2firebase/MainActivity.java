package com.example.lab2firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import com.example.lab2firebase.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // instance variables
    ActivityMainBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRef;

    ProgressDialog progressDialog;

    // Create onCreate method and initialize objects (binding, database, myRef()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("lab2");


        //Initialize the View
        initView();
    }

    // calling initView method from onCreate to set the view and initialize the event listener for switches
    private void initView() {

      /*  binding.clParent.setVisibility(View.INVISIBLE);
        binding.pb.setVisibility(View.VISIBLE);*/

        progressDialog = new ProgressDialog(this);


        // ProgressDialog shows a loading indicator while the data is being fetched from the Firebase database
        progressDialog.setMessage("Loading Data..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // getValue() method is called on it to get the data as an instance of the "Home" class
        myRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                // getValue method is called on it to get the data as an instance of the "Home" class
                Home home = dataSnapshot.getValue(Home.class);
                if (home != null) {
                    if (home.obj1) {

                        binding.swObj1.setChecked(true);
                        binding.img1.setImageDrawable(getDrawable(R.drawable.lightbulbon));
                        binding.tvObj1State.setText(getString(R.string.object1on));

                    }
                    if (home.obj2) {

                        binding.swObj2.setChecked(true);
                        binding.img2.setImageDrawable(getDrawable(R.drawable.dooropen));
                        binding.tvObj2State.setText(getString(R.string.object2on));

                    }
                    if (home.obj3) {

                        binding.swObj3.setChecked(true);
                        binding.img3.setImageDrawable(getDrawable(R.drawable.windowopen));
                        binding.tvObj3State.setText(getString(R.string.object3on));

                    }
                }

                progressDialog.dismiss();

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

            }
        });


        binding.swObj1.setOnCheckedChangeListener((buttonView, isChecked) -> {


            switchObject(1, isChecked,false);

        });

        binding.swObj2.setOnCheckedChangeListener((buttonView, isChecked) -> {

            switchObject(2, isChecked,false);

        });

        binding.swObj3.setOnCheckedChangeListener((buttonView, isChecked) -> {

            switchObject(3, isChecked,false);

        });

        binding.btnListen.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I'm Listening...");
                startActivityForResult(intent, 100);

            }
        });


    }

    /* onActivityResult() method is called when a voice command is recognized. It extracts the recognized
    speech text from the result intent and checks it against some pre-defined keywords to determine which
    object should be switched and whether it should be turned on or off.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0).trim();

            Log.d("Lab2 Debug", result);

            if (result.contains("turn on lamp") || result.contains("turn on the lamp")) {

                switchObject(1,true,true);

            } else if (result.contains("turn off lamp") || result.contains("turn off the lamp")) {

                switchObject(1,false,true);

            } else if (result.contains("close the door") || result.contains("close door")) {

                switchObject(2,false,true);

            } else if (result.contains("open the door") || result.contains("open door")) {

                switchObject(2,true,true);

            } else if (result.contains("open the window") || result.contains("open window")) {

                switchObject(3,true,true);

            } else if (result.contains("close the window") || result.contains("close window")) {

                switchObject(3,false,true);

            }

        }

    }

    /*The switchObject() method is called when a switch is toggled or a voice command is received.
     It takes three parameters: objectCode, isChecked, and isVoice. The objectCode parameter indicates
     which object is being switched, the isChecked parameter indicates whether the object is being
     turned on/off, and the isVoice parameter indicates whether the method was called by a voice command.*/
    private void switchObject(int objectCode, boolean isChecked,boolean isVoice) {

        Log.d("Lab2 debug","Method Called");
        if (objectCode == 1) {

            if (isVoice){
                binding.swObj1.setChecked(isChecked);
            }
            if (isChecked) {
                binding.img1.setImageDrawable(getDrawable(R.drawable.lightbulbon));
                binding.tvObj1State.setText(getString(R.string.object1on));

            } else {
                binding.img1.setImageDrawable(getDrawable(R.drawable.lightbulboff));
                binding.tvObj1State.setText(getString(R.string.object1off));
            }
            myRef.child("obj1").setValue(isChecked);

        } else if (objectCode == 2) {

            if (isVoice){
                binding.swObj2.setChecked(isChecked);
            }
            if (isChecked) {
                binding.img2.setImageDrawable(getDrawable(R.drawable.dooropen));
                binding.tvObj2State.setText(getString(R.string.object2on));
            } else {
                binding.img2.setImageDrawable(getDrawable(R.drawable.doorclose));
                binding.tvObj2State.setText(getString(R.string.object2off));
            }

            myRef.child("obj2").setValue(isChecked);

        } else if (objectCode == 3) {

            if (isVoice){
                binding.swObj3.setChecked(isChecked);
            }
            if (isChecked) {
                binding.img3.setImageDrawable(getDrawable(R.drawable.windowopen));
                binding.tvObj3State.setText(getString(R.string.object3on));
            } else {
                binding.img3.setImageDrawable(getDrawable(R.drawable.windowclose));
                binding.tvObj3State.setText(getString(R.string.object3off));
            }

            myRef.child("obj3").setValue(isChecked);

        }

    }
}