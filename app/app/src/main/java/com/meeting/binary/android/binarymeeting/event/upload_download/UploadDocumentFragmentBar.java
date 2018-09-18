package com.meeting.binary.android.binarymeeting.event.upload_download;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class UploadDocumentFragmentBar extends Fragment {

    private static final String TAG = "MainActivity_file";
    private static final int REQUEST_CODE = 6384; // onActivityResult request code
    private static final String EXTRA_ID_MES = "upload_message";

    private String eventId;

    private EditText mTextView;
    private Button pickFile;
    private Button uploadFile;


    private LinearLayout uploaded;
    private ProgressBar progress_loaded;
    private TextView uploaded_text;
    private ImageView uploaded_view;


    private Uri fileUri;

    public static UploadDocumentFragmentBar newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ID_MES, id);
        UploadDocumentFragmentBar fragment = new UploadDocumentFragmentBar();
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        View view = inflater.inflate(R.layout.upload_document_layout_bar, container, false);



        mTextView = view.findViewById(R.id.file_name);
        pickFile = view.findViewById(R.id.pick_file);
        uploadFile = view.findViewById(R.id.upload_file);
        mTextView.setEnabled(false);


        uploaded = view.findViewById(R.id.uploaded);
        progress_loaded = view.findViewById(R.id.loading_spinner);
        uploaded_text = view.findViewById(R.id.uploaded_text);
        uploaded_view = view.findViewById(R.id.upload_icon);


        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooser();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri!= null) {
                    Log.i("special_uri", "onClick: " + fileUri.toString());
                    progress_loaded.setVisibility(View.VISIBLE);
                    uploadUi(fileUri);
                }
                else {
                    Log.i("special_uri", "onClick: uri is null");
                    Toast.makeText(getActivity(), "Please pick up a file ", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }






    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }







    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        target.addCategory(Intent.CATEGORY_OPENABLE);
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        if (uri!= null)
                            Log.i("special_uri", "onActivityResult: " + uri.toString());
                        else
                            Log.i("special_uri", "onActivityResult: uri is null");
                        fileUri = uri;
                        File originalFile = FileUtils.getFile(getActivity(), uri);
                        String filename = originalFile.getName();
                        Log.i("special_uri", "onActivityResult: " + filename);

                        if (filename != null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(filename);
                                }
                            });
                        }

                        Log.i(TAG, "file name " + filename);
                        Log.i(TAG, "Uri = " + uri.toString());
                        Log.i(TAG, "file uri is : " + fileUri.toString());
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(getActivity(), uri);
//                            Toast.makeText(getActivity(),"File Selected: " + path, Toast.LENGTH_LONG).show();
                            Log.i(TAG, "path = " + path);
                        } catch (Exception e) {
                            Log.e("FileSelectorTestActivit", "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }






    public void uploadUi(Uri fileUri){
        if(fileUri==null)
            return;
        RequestWebServiceInterface client = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        MultipartBody.Part part = prepareFilePart("file", fileUri);
        if(part == null)
            return;
        Call<ResponseBody> responseBodyCall = client.uploadPhoto(part, eventId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    // Toast.makeText(getActivity(), "file uploaded", Toast.LENGTH_SHORT).show();
                    progress_loaded.setVisibility(View.GONE);
                    uploaded_text.setText("file Uploaded");
                    uploaded_view.setImageResource(R.drawable.ok_c);
                    uploaded.setVisibility(View.VISIBLE);
                    Log.i(TAG, "response succeed, " + response.toString());
                    mTextView.setText("");
                } else {
                    // Toast.makeText(getActivity(), "response failed " + response.toString(), Toast.LENGTH_SHORT).show();
                    progress_loaded.setVisibility(View.GONE);
                    uploaded_text.setText("Upload failed");
                    uploaded_view.setImageResource(R.drawable.exclamation_circle);
                    uploaded.setVisibility(View.VISIBLE);
                    Log.i(TAG, "response failed, " + response.toString());
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

        File file = FileUtils.getFile(getContext(), fileUri);
        String convertedFileName = getActivity().getContentResolver().getType(fileUri);
        if(convertedFileName==null || convertedFileName.isEmpty())
            return null;
        Log.d(TAG, "prepareFilePart: " + fileUri.toString());
        RequestBody requestFile = RequestBody.create(MediaType.parse(convertedFileName), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        return body;
    }
}
