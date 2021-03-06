package com.practice.android.moments.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practice.android.moments.Models.Profile_model_class;
import com.practice.android.moments.R;


@SuppressLint("NewApi")
public class GetProfile extends Fragment {

    private static final String TAG = "GET profile";
    TextView getname;
    TextView getemail;
    TextView getphone;
    TextView getAbout;
    TextView getDate;
    TextView getrealtion_ship;
    Button get;
    FirebaseUser firebaseUser;
    String user_id;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_get_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        user_id = firebaseUser.getUid();

        getname = (TextView) v.findViewById(R.id.TextViewname);
        getemail = (TextView) v.findViewById(R.id.TextViewemail);
        getphone = (TextView) v.findViewById(R.id.TextViewphone);
        getAbout = (TextView) v.findViewById(R.id.TextViewabout);
        getDate = (TextView) v.findViewById(R.id.TextViewdate);
        getrealtion_ship = (TextView) v.findViewById(R.id.relationship);

        get = (Button) v.findViewById(R.id.sub);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(user_id).child("User Info").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile_model_class user = dataSnapshot.getValue(Profile_model_class.class);

                        assert user != null;
                        Log.e(TAG, "User name: " + user.getName() + ", email " + user.getEmail() + "    " + user.getRelationship() + "    " + user.getAbout());
                        getname.setText(user.getName());
                        getemail.setText(user.getEmail());
                        getphone.setText(user.getPhone());
                        getAbout.setText(user.getAbout());
                        getDate.setText(user.getDate_of_birth());
                        getrealtion_ship.setText(user.getRelationship());
                            Log.e(TAG, "\n" + user.getPhoto() + "        " + user.getGender() + "    " + user.getRelationship() + "    " + user.getAbout());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        return v;
    }


}
