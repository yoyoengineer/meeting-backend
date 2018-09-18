package com.meeting.binary.android.binarymeeting.event.upload_download;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class UploadPhotoFragmentBar extends Fragment {

    private static final String TAG = "errortag";
    private static final String EXTRA_ID_MES = "upload_message";

    private static final int MY_PERMISSION_REQUEST = 100;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;

    Button btnUpload, btnPickImage;
    String mediaPath;
    ImageView imgView;
    ProgressDialog progressDialog;
    String[] mediaColumns = { MediaStore.Video.Media._ID };

    private LinearLayout uploaded;
    private ProgressBar progress_loaded;
    private TextView uploaded_text;
    private ImageView uploaded_view;

    private Uri photoUri;

    private String eventId;

    public static UploadPhotoFragmentBar newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ID_MES, id);
        UploadPhotoFragmentBar fragment = new UploadPhotoFragmentBar();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            eventId = getArguments().getString(EXTRA_ID_MES);
            if (eventId != null){
                Log.d(TAG, "onCreate: eventId is not null");
            } else {
                Log.d(TAG, "onCreate: event id is null");
            }
        } else {
            Log.i(TAG, "onCreate: argument null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_photo_fragment_bar, container, false);



        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading...");


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        }

        btnUpload = view.findViewById(R.id.upload_photo);
        imgView = view.findViewById(R.id.photo_view);
        btnPickImage = view.findViewById(R.id.pick_photo);

        uploaded = view.findViewById(R.id.uploaded);
        progress_loaded = view.findViewById(R.id.loading_spinner);
        uploaded_text = view.findViewById(R.id.uploaded_text);
        uploaded_view = view.findViewById(R.id.upload_icon);


        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);

            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoUri==null){
                    Toast.makeText(getActivity(), "please first pick a file then upload it", Toast.LENGTH_SHORT).show();
                } else {
                    progress_loaded.setVisibility(View.VISIBLE);
                    uploadUi(photoUri);
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //if request is canceled the result array are empty
        switch (requestCode){
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission was granted

                } else {
                    //permission denied
                    //disable functionality that depends on this permission
                }
                return;
        }
    }



    // Providing Thumbnail For Selected Image
    public Bitmap getThumbnailPathForLocalFile(Activity context, Uri fileUri) {
        long fileId = getFileId(context, fileUri);
        return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
    }


    // Getting Selected File ID
    public long getFileId(Activity context, Uri fileUri) {
        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            return cursor.getInt(columnIndex);
        }
        return 0;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST ||requestCode == 0  && resultCode == RESULT_OK && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                if (selectedImage != null){
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    photoUri = selectedImage;
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);
                    // Set the Image in ImageView for Previewing the Media
                    imgView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                    cursor.close();
                } else {
                    Toast.makeText(getActivity(), "pick a file", Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    public void uploadUi(Uri fileUri){
        RequestWebServiceInterface client = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        MultipartBody.Part part = prepareFilePart("file", fileUri);
        if(part ==null)
            return;
        Call<ResponseBody> responseBodyCall = client.uploadPhoto(part, eventId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
//                    Toast.makeText(getActivity(), "file uploaded", Toast.LENGTH_SHORT).show();
                    progress_loaded.setVisibility(View.GONE);
                    uploaded_text.setText("photo Uploaded");
                    uploaded_view.setImageResource(R.drawable.ok_c);
                    uploaded.setVisibility(View.VISIBLE);
                } else {
//                    Toast.makeText(getActivity(), "response failed " + response.toString(), Toast.LENGTH_SHORT).show();
                    progress_loaded.setVisibility(View.GONE);
                    uploaded_text.setText("Upload failed");
                    uploaded_view.setImageResource(R.drawable.exclamation_circle);
                    uploaded.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "failed upload, " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    //    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri){
        File file = FileUtils.getFile(getActivity(), fileUri);
        String convertedFileName = getActivity().getContentResolver().getType(fileUri);
        if(convertedFileName==null || convertedFileName.isEmpty())
            return null;
        RequestBody requestFile = RequestBody.create(MediaType.parse(convertedFileName), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
