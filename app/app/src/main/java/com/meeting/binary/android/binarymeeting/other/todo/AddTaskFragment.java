package com.meeting.binary.android.binarymeeting.other.todo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.admin.ListAttendeesActivity;
import com.meeting.binary.android.binarymeeting.model.TodoItem;
import com.meeting.binary.android.binarymeeting.realm_labs.TodoLab;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

/**
 * Created by meldi on 4/8/2018.
 */

public class AddTaskFragment extends Fragment {

    private static final String EXTRA_ID_MESSAGE = "extra_id_message";
    private static final String TAG = "addTask";

    private EditText mEditTask;
    private Button mSaveTask;

    private TodoItem mTodoItem;

    private Realm myRealm;
    private RealmAsyncTask realmAsyncTask;
    RealmResults<TodoItem> userList;


    public static AddTaskFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ID_MESSAGE, id);
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        myRealm = Realm.getDefaultInstance();
        String id = getArguments().getString(EXTRA_ID_MESSAGE);
        Log.d(TAG, "onCreate: " + id);
        mTodoItem = TodoLab.get(getActivity()).getItem(id, myRealm);
        //Log.d(TAG, "onCreate: " + TodoLab.get(getActivity(), myRealm).getItem(id, myRealm));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_task_todo, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Campus Event");

        mEditTask = view.findViewById(R.id.task_editText);
        mSaveTask = view.findViewById(R.id.save_task);

        mEditTask.setText(mTodoItem.getTitle());
        Log.d(TAG, "onCreateView: " + mTodoItem.getId());
        Log.d(TAG, "onCreateView: check title "  + mEditTask.getText().toString());

        mSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodoLab.get(getActivity()).updateTaskTodo(mTodoItem.getId(), mEditTask.getText().toString(), myRealm);
                getActivity().onBackPressed();
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete_task_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                try {
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mTodoItem.deleteFromRealm();
                            Toast.makeText(getActivity(), "item deleted", Toast.LENGTH_SHORT).show();
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
                    Log.e(TAG, "onOptionsItemSelected: failled to delete item" +  mTodoItem.getId() + " ", e);
                    Log.e(TAG, "onOptionsItemSelected: " + e.getMessage());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
