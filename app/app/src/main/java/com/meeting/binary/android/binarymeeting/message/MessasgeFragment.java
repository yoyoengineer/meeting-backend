package com.meeting.binary.android.binarymeeting.message;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Message;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.service.websocket.WebSocketStomptConfig;
import com.meeting.binary.android.binarymeeting.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class MessasgeFragment extends Fragment {

    private static final String EXTRA_ID_MES = "upload_message";

    private static final int MY_PERMISSION_REQUEST = 100;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;

    private List<Message> mMessages = new ArrayList<>();
    private static final String TAG = "stomp_tag";

    private static final String EXTRA_MESSAGE = "extra_message";

    private Message message;

    ProgressDialog progressDialog;
    String[] mediaColumns = { MediaStore.Video.Media._ID };
    String mediaPath;
    private Uri photoUri;

    private Map<String, String> fileInfo = new HashMap<>();

    private static final String DIALOG_PICK_FILE = "DialogLikeEvent";

    public static final int REQUEST_PICK_LOAD_ACTION = 3;
    private int mLastItem = -1;
    private String destination;
    private String me;

    private EditText inputText;
    private Button send;
    private StompClient mStompClient;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;




    public static MessasgeFragment newInstance(Message message) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MESSAGE, message);
        MessasgeFragment fragment = new MessasgeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mStompClient = WebSocketStomptConfig.stompConnect(getContext());

        me = CookiePreferences.getStoredName(getActivity());

        if (getArguments() != null){
            message = (Message) getArguments().getSerializable(EXTRA_MESSAGE);
//            Log.d(TAG, "onCreate: " + message.getId());

            destination = message.getToUser();
            if (destination.equalsIgnoreCase(me))
                destination = message.getFromUser();
        } else {
            Log.i(TAG, "onCreate: argument null");
        }
        message.setId(me+destination);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_activity_main, container, false);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Campus Event");


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        }

        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            view.setBackgroundResource (R.drawable.chatbackground);
        } else {
            view.setBackgroundResource (R.drawable.chatbackground);
        }

        inputText = (EditText) view.findViewById(R.id.input_text);
        send = view.findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) view.findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        msgRecyclerView.setLayoutManager(layoutManager);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                Message msg = new Message();
                msg.setToUser(destination);
                msg.setText(content);
                sendMessage(msg);
                inputText.setText("");
//                if (!"".equals(content)) {
//                    Msg msg = new Msg(content, Msg.TYPE_SENT);
//                    msgList.add(msg);
//                    adapter.notifyItemInserted(msgList.size() - 1);
//                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
//
//                }
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        registerStompTopic();
        loadHistoric();
    }



    public void updateUi(){
        if (adapter == null){
            adapter = new MsgAdapter(mMessages);
            msgRecyclerView.setAdapter(adapter);
        } else {
            adapter.setMessageList(mMessages);
            adapter.notifyDataSetChanged();
//            if (mLastItem < 0){
//
//            } else{
//                adapter.notifyItemChanged(mLastItem);
//                mLastItem = -1;
//            }
        }
            if (mMessages.size() > 0) msgRecyclerView.scrollToPosition(mMessages.size() - 1);
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

    public void getTypeAction(String type){
        if (type.equalsIgnoreCase("PHOTO")){
            Toast.makeText(getActivity(), "inside the function", Toast.LENGTH_SHORT).show();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 0);

        }
    }

    private void sendMessage(Message message){
        RequestWebServiceInterface client = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> responseBodyCall = client.sendMessage(message);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "message sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "response failed " + response.toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "failed upload, " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void loadHistoric(){
        RequestWebServiceInterface client = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Message>> responseBodyCall = client.loadHistoricMessage(message.getId());
        responseBodyCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "message sent", Toast.LENGTH_SHORT).show();
                    List<Message> messages = response.body();
                    if (messages!= null){
                        mMessages.clear();
                        mMessages.addAll(messages);
                        updateUi();
                    }
                } else {
                    Toast.makeText(getActivity(), "response failed " + response.toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(getActivity(), "failed upload, " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.message_activity_main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                return true;
            case R.id.action_file:
                FragmentManager manager = getFragmentManager();
                PickFileActionDialogFragment dialogFragment = PickFileActionDialogFragment.newInstance();
                dialogFragment.setTargetFragment(MessasgeFragment.this, REQUEST_PICK_LOAD_ACTION);
                dialogFragment.show(manager, DIALOG_PICK_FILE);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        try {
            if (requestCode == REQUEST_PICK_LOAD_ACTION) {
                Toast.makeText(getActivity(), "test une deux", Toast.LENGTH_SHORT).show();
                if (data != null){
                    String type = data.getStringExtra(PickFileActionDialogFragment.EXTRA_TYPE);
                    Log.d(TAG, "onActivityResult() returned: " + type);
                    getTypeAction(type);
                }

            } else if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST || requestCode == 0){
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

                    //imgView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));

                    uploadUi(photoUri);
                    cursor.close();
            }
        }

    }catch (Exception e){

        }
    }


    public void uploadUi(Uri fileUri){

        RequestWebServiceInterface client = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        MultipartBody.Part part = prepareFilePart("file", fileUri);

        if(part ==null) return;

        Call<ResponseBody> responseBodyCall = client.uploadPrivatePhoto(part, destination);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "file uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "response failed " + response.toString(), Toast.LENGTH_SHORT).show();
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




    private void registerStompTopic(){
        mStompClient.topic("/user/queue/chat").subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.e(TAG, "call: " +stompMessage.getPayload() );
//                addToMessageList(stompMessage);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addToMessageList(stompMessage);
//                        loadHistoric();
                    }
                });

            }
        });

    }


    /**
     * fetch the json string into a object mapper and
     * transform it to get the object mapped to it
     * @param stompMessage
     */
    /**
     * update the ui on message receive from Stomp
     * @param stompMessage
     */
    private void addToMessageList(final StompMessage stompMessage){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Message message = objectMapper.readValue(stompMessage.getPayload(), Message.class);

            mMessages.add(message);
            Log.i("allelou", "fetchMessages: receive" + message.getText());
            adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(mMessages.size() - 1);
            msgRecyclerView.scrollToPosition(mMessages.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /**
     * ======================================================
     * holder that load message exchange ui between two users
     * ======================================================
     */
     abstract class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //private ImageView file;
        private TextView msgText;
        private TextView time;
        //private TextView fileSize;

        public MessageHolder(View itemView) {
            super(itemView);
        }

        public MessageHolder(LayoutInflater inflater, ViewGroup parent, int layout){
            super(inflater.inflate(layout, parent, false));

            //fileSize= itemView.findViewById(R.id.size);
            //file = itemView.findViewById(R.id.file);
            msgText = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.time);
        }

        public void bind(Message msg){
            String fileName;
            String size;
            fileInfo =  msg.getExtra();
            mLastItem = getAdapterPosition();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Log.i(TAG, "check value: " + msg.getText());
            msgText.setText(msg.getText());
            time.setText(dateFormat.format(new Date(msg.getTime())));

//            if (msg.getType().equalsIgnoreCase("FILE")){
//            file.setVisibility(View.VISIBLE);
//            fileSize.setVisibility(View.VISIBLE);
//            fileName = fileInfo.get("fileName");
//            size = fileInfo.get("size");
//            checkExtension(fileName);
//            fileSize.setText(size);
//        } else if (msg.getType().equalsIgnoreCase("TEXT")){
//            file.setVisibility(View.GONE);
//            fileSize.setVisibility(View.GONE);
//            msgText.setText(msg.getText());
//            time.setText(dateFormat.format(new Date(msg.getTime())));
//        }
    }


//        private void checkExtension(String filename){
//            Log.d(TAG, "This is the file filename: " + filename);
//            if (filename.equalsIgnoreCase(".Doc") || filename.equalsIgnoreCase(".Docx") ||
//                    filename.equalsIgnoreCase(".Docs") || filename.equalsIgnoreCase(".doc")){
//                file.setImageDrawable(getResources().getDrawable(R.drawable.word));
//            } else if (filename.equalsIgnoreCase(".pdf")){
//                file.setImageDrawable(getResources().getDrawable(R.drawable.pdfextension));
//            } else if (filename.equalsIgnoreCase(".xls")){
//                file.setImageDrawable(getResources().getDrawable(R.drawable.excel));
//            } else if (filename.equalsIgnoreCase(".txt")){
//                file.setImageDrawable(getResources().getDrawable(R.drawable.txt));
//            } else if (filename.equals(".png") || filename.equals(".PNG") || filename.equals(".jpg") || filename.equals(".JPG") ||
//                    filename.equals(".jpeg") || filename.equals(".JPEG") || filename.equals(".gif") ||
//                    filename.equals(".GIF")){
//                file.setImageDrawable(getResources().getDrawable(R.drawable.photos));
//            } else {
//                file.setImageDrawable(getResources().getDrawable(R.drawable.ic_file));
//            }
//        }

        @Override
        public void onClick(View view) {
            mLastItem = getAdapterPosition();
        }
    }




    class MessageHolderLeft extends MessageHolder{
        private LinearLayout layout;
        public MessageHolderLeft(LayoutInflater inflater, ViewGroup group) {
            super(inflater, group, R.layout.msg_item_left);
            layout = itemView.findViewById(R.id.l_layout);
        }

        @Override
        public void bind(Message msg) {
            super.bind(msg);
            layout.setVisibility(View.VISIBLE);
        }
    }



    class MessageHolderRigth extends MessageHolder{
        private LinearLayout layout;
        public MessageHolderRigth(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.msg_item);
            layout = itemView.findViewById(R.id.l_layout);
        }

        @Override
        public void bind(Message msg) {
            super.bind(msg);
            layout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * =================================================
     * Adapter to the message exchange between two users
     * =================================================
     */
    class MsgAdapter extends RecyclerView.Adapter<MessageHolder> {

        private List<Message> mMessageList;

        public MsgAdapter(List<Message> messageList) {
            mMessageList = messageList;
        }

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (viewType == 1){
                return new MessageHolderRigth(inflater, parent);
            } else {
                return new MessageHolderLeft(inflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            Message msg = mMessageList.get(position);
            holder.bind(msg);
        }

        public void setMessageList(List<Message> messageList) {
            mMessageList = messageList;
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (mMessageList.get(position).getFromUser().equalsIgnoreCase(me))
                return 1;
            else
                return 0;
        }
    }
}
