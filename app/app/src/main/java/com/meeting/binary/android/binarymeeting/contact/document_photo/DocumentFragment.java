package com.meeting.binary.android.binarymeeting.contact.document_photo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.upload_download.DownloadService;
import com.meeting.binary.android.binarymeeting.model.FileModel;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.utils.DateUtils;
import com.meeting.binary.android.binarymeeting.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.BIND_AUTO_CREATE;

public class DocumentFragment extends Fragment {

    private List<FileModel> mDocumentModels = new ArrayList<>();

    private RecyclerView mDocumentRecycler;
    private FileAdapter mFileAdapter;

    private static final String EXTRA_MESSAGE_FILE = "eventId";
    private static final String TAG = "viewphoto";

    private String eventId;




    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    public static DocumentFragment newInstance(String eventId) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE_FILE, eventId);
        DocumentFragment fragment = new DocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            eventId = getArguments().getString(EXTRA_MESSAGE_FILE);
            if (eventId != null){
                Log.i(TAG, "onCreate: the event id is not null " + eventId);
            } else {
                Log.i(TAG, "onCreate: id is null");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_file_layout, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("CampusEvent");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDocumentRecycler = view.findViewById(R.id.recycler_id);
        mDocumentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestFile();

        Intent intent = new Intent(getContext(), DownloadService.class);
        getActivity().startService(intent); // 启动服务
        getActivity().bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(connection);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }


    private void requestFile(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<FileModel>> call = requestWebServiceInterface.getOther(eventId);
        call.enqueue(new Callback<List<FileModel>>() {
            @Override
            public void onResponse(Call<List<FileModel>> call, Response<List<FileModel>> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: response succeed");
                    List<FileModel> mFileModels = response.body();
                    if (mFileModels != null){
                        addTofile(mFileModels);
                        updateUi();
                    } else {
                        Toast.makeText(getActivity(), "list of photos is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "onResponse: response failed");
                }
            }

            @Override
            public void onFailure(Call<List<FileModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to connect");
            }
        });
    }

    private void addTofile(List<FileModel> fileModels){
        for (FileModel fileModel : fileModels){
            if (fileModel.getFileName() != null && !TextUtils.isEmpty(fileModel.getFileName()) && fileModel.getFileName().length() > 0){
                mDocumentModels.add(fileModel);
            }
        }
    }




    private void updateUi(){
        if (mFileAdapter == null){
            mFileAdapter = new FileAdapter(mDocumentModels);
            mDocumentRecycler.setAdapter(mFileAdapter);
        } else {
            mFileAdapter.setModels(mDocumentModels);
            mFileAdapter.notifyDataSetChanged();
        }
    }



    /**
     * =========================================
     * holder
     */
    private class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        FileModel mFileModel;

        private ImageView mImageType;
        private ImageView mDowmload;
        private TextView mFileName;
        private TextView mDateUploaded;
        private TextView mFileSize;



        public FileHolder(View itemView) {
            super(itemView);
        }

        public FileHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_view_file, parent, false));
            itemView.setOnClickListener(this);
            mDowmload = itemView.findViewById(R.id.download);
            mFileSize = itemView.findViewById(R.id.file_size);
            mDateUploaded = itemView.findViewById(R.id.date_upload);
            mFileName = itemView.findViewById(R.id.file_name);
            mImageType = itemView.findViewById(R.id.file_type);

        }

        public void bind(FileModel fileModel){
            mFileModel = fileModel;
            mFileName.setText(mFileModel.getFileName());
            mDateUploaded.setText(DateUtils.getReadableModifyDate(mFileModel.getTime()));
            mFileSize.setText(mFileModel.getSize().toString());
            checkExtension(mFileModel);

            mDowmload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Downloading....", Toast.LENGTH_SHORT).show();
                    if (downloadBinder != null) {
                        Toast.makeText(getActivity(), "Downloading....", Toast.LENGTH_LONG).show();
                        String url = BaseUrlGenerator.BINARY_BASE_URL + "/event/file/download/" + eventId + "/" + mFileModel.getFileId();
                        Log.i(TAG, "onClick: " + mFileModel.getFileName());
                        downloadBinder.startDownload(url, mFileModel.getFileName());
                    }
                }
            });
        }

        private void checkExtension(FileModel fileModel){
            if (fileModel.getFileName() != null && !TextUtils.isEmpty(fileModel.getFileName()) && fileModel.getFileName().length() > 0){
                Log.d(TAG, "checkExtension: " + fileModel.getFileName());
                String extension = FileUtils.getExtension(fileModel.getFileName());
                Log.d(TAG, "This is the file extension: " + extension);
                if (extension.equalsIgnoreCase(".Doc") || extension.equalsIgnoreCase(".Docx") ||
                        extension.equalsIgnoreCase(".Docs") || extension.equalsIgnoreCase(".doc")){
                    mImageType.setImageDrawable(getResources().getDrawable(R.drawable.word));
                } else if (extension.equalsIgnoreCase(".pdf")){
                    mImageType.setImageDrawable(getResources().getDrawable(R.drawable.pdfextension));
                } else if (extension.equalsIgnoreCase(".xls")){
                    mImageType.setImageDrawable(getResources().getDrawable(R.drawable.excel));
                } else if (extension.equalsIgnoreCase(".txt")){
                    mImageType.setImageDrawable(getResources().getDrawable(R.drawable.document));
                }
            }
        }

        @Override
        public void onClick(View view) {
            Log.i("clicked", "onClick: clicked");
            if (downloadBinder != null && mFileModel.getFileId() != null) {
                Toast.makeText(getActivity(), "Downloading....", Toast.LENGTH_LONG).show();
                String url = BaseUrlGenerator.BINARY_BASE_URL + "/event/file/download/" + eventId + "/" + mFileModel.getFileId();
                downloadBinder.startDownload(url, mFileModel.getFileName());
            }
        }
    }


    /**
     * =========================================
     * adapter
     */
    private class FileAdapter extends RecyclerView.Adapter<FileHolder>{

        List<FileModel> mModels;

        public FileAdapter(List<FileModel> models) {
            mModels = models;
        }

        @Override
        public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new FileHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(FileHolder holder, int position) {
            holder.bind(mModels.get(position));
        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }

        public void setModels(List<FileModel> models) {
            mModels = models;
        }
    }
}
