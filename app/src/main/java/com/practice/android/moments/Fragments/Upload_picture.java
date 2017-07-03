package com.practice.android.moments.Fragments;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.practice.android.moments.Activities.Timeline;
import com.practice.android.moments.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class Upload_picture extends Fragment {
    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_REQUEST = 0;
    private static final String TAG = "Upload picture";
    ImageView Uploadimage;
    EditText tittle, description;
    Button choose, upload;
    StorageReference mstorageReference;
    FirebaseUser firebaseuser;
    Uri selectedImage;
    Uri uri;
    Uri download_uri;
    String user_id;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload_picture, container, false);

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mstorageReference = FirebaseStorage.getInstance().getReference();

        user_id = firebaseuser.getUid();


        Uploadimage = (ImageView) v.findViewById(R.id.imageView2);
        tittle = (EditText) v.findViewById(R.id.image_title);
        description = (EditText) v.findViewById(R.id.image_description);
        choose = (Button) v.findViewById(R.id.button_choose);
        upload = (Button) v.findViewById(R.id.button_upload);


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fn_Choose_Image();
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, selectedImage.toString());

                uploadFile();

            }
        });


        return v;
    }

    public void fn_Choose_Image() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");
        myAlertDialog.setPositiveButton("Gallery", (arg0, arg1) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_PICTURE);
        });

        myAlertDialog.setNegativeButton("Camera", (arg0, arg1) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        });

        myAlertDialog.show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String picturePath;

        Bitmap thumbnail;
        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {
       selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
            assert c != null;
            c.moveToFirst();

            int columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);

            Uploadimage.setImageURI(data.getData());

            c.close();

            try {
                thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.e("gallery.***********692." + thumbnail, picturePath);
                uri = Uri.fromFile(new File(picturePath));
            } catch (Exception e) {
                Log.e("gallery***********692.", "Exception==========Exception==============Exception");
                e.printStackTrace();
            }



            Log.i(TAG, selectedImage.toString());
//
//            uploadFile();


        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            selectedImage  = data.getData();
            picturePath = getRealPathFromURI( selectedImage );
            uploadFile();
            try {
                thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.e("gallery.***********692." + thumbnail, picturePath);
                uri = Uri.fromFile(new File(picturePath));
                Uploadimage.setImageURI(selectedImage);
//                uploadFile();
            } catch (Exception e) {
                Log.e("gallery***********692.", "Exception==========Exception==============Exception");
                e.printStackTrace();
            }
            Log.i(TAG, selectedImage.toString());


        }
    }

    private void uploadFile() {
        //if there is a file to upload
        if (selectedImage != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            StorageReference riversRef = mstorageReference.child("Photos")
                    .child(firebaseuser.getUid()).child("User Photo")
                    .child(selectedImage.getLastPathSegment());
            riversRef.putFile(selectedImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        //if the upload is successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();


                        download_uri = taskSnapshot.getDownloadUrl();
                        String user_id = firebaseuser.getUid();
                        String picture = String.valueOf(download_uri);
                        DatabaseReference currentuser_db = databaseReference.child(user_id).child("User Pictures");
                        currentuser_db.child(selectedImage.getLastPathSegment());
                        DatabaseReference currentuser = currentuser_db.child(selectedImage.getLastPathSegment());
                        currentuser.child("like").setValue("0");
                        currentuser.child("pic").setValue(picture);
                        currentuser.child("comments");
                        currentuser.child("tittle").setValue(tittle.getText().toString());
                        currentuser.child("description").setValue(description.getText().toString());

                        DatabaseReference current =   currentuser.child("Comments");
                        current.child("user_id").setValue("1");


                        //and displaying a success toast
                        Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(getActivity(), Timeline.class));
                    })
                    .addOnFailureListener(exception -> {
                        //if the upload is not successful
                        //hiding the progress dialog
                        progressDialog.dismiss();

                        //and displaying error message
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(getActivity(), "File Upload Failed", Toast.LENGTH_SHORT).show();
        }
    }
}