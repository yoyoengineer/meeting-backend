package com.meeting.binary.android.binarymeeting.other.notes;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Note;
import com.meeting.binary.android.binarymeeting.realm_labs.NoteLab;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class CreateNoteFragment extends Fragment {

    private static final String EXTRA_STRING_CREATE_NOTE = "create_new_note";
    private static final String EXTRA_ID_MESSAGE = "extra_id_message";
    private static final String TAG = "addTask";

    private Note mNote;

    private Realm myRealm;

    private EditText title;
    private EditText content;

    public static CreateNoteFragment newInstance(String noteId) {
        Bundle args = new Bundle();
        args.putString(EXTRA_STRING_CREATE_NOTE, noteId);
        CreateNoteFragment fragment = new CreateNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        myRealm = Realm.getDefaultInstance();
        String id = getArguments().getString(EXTRA_STRING_CREATE_NOTE);
        if (id != null){
            mNote = NoteLab.get(getActivity()).getItem(id, myRealm);
            Log.d(TAG, "onCreate: the note has been found" + id);
        } else{
            Log.d(TAG, "onCreate: id is null");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_note, container, false);

        title = view.findViewById(R.id.note_title);
        content = view.findViewById(R.id.note_content);


        /**get the note*/
        title.setText(mNote.getTitle());
        content.setText(mNote.getContent());

        NoteLab.get(getActivity()).update(mNote.getId(), title.getText().toString(), content.getText().toString(), myRealm);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_create_note, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_delete:
                try {
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mNote.deleteFromRealm();
                            Toast.makeText(getActivity(), "note deleted", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "execute: item deleted");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //TodoLab.get(getContext()).updateTaskTodo();
                                    getActivity().onBackPressed();
                                }
                            }, 1000);
                        }
                    });
                } catch (Exception e){
                    Log.e(TAG, "onOptionsItemSelected: failled to delete item" +  mNote.getId() + " ", e);
                    Log.e(TAG, "onOptionsItemSelected: " + e.getMessage());
                }
                break;

            case R.id.action_save:
                NoteLab.get(getActivity()).update(mNote.getId(), title.getText().toString(), content.getText().toString(), myRealm);
                Toast.makeText(getActivity(), "note saved", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
