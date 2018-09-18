package com.meeting.binary.android.binarymeeting.other.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.listener.ItemTouchHelperAdapter;
import com.meeting.binary.android.binarymeeting.listener.ItemTouchHelperViewHolder;
import com.meeting.binary.android.binarymeeting.listener.OnStartDragListener;
import com.meeting.binary.android.binarymeeting.listener.OnTodoListChangedListener;
import com.meeting.binary.android.binarymeeting.model.Note;
import com.meeting.binary.android.binarymeeting.model.TodoItem;
import com.meeting.binary.android.binarymeeting.other.Constants;
import com.meeting.binary.android.binarymeeting.other.SimpleTouchHelperCallback;
import com.meeting.binary.android.binarymeeting.realm_labs.NoteLab;
import com.meeting.binary.android.binarymeeting.realm_labs.TodoLab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by meldi on 4/8/2018.
 */

public class ListTaskFragment extends Fragment implements OnStartDragListener {

    private RecyclerView mTaskRecycler;
    private TodoAdapter mTodoAdapter;

    private static final String TAG = "list_task_todo";

    private boolean mSubtitleVisible;
    private static final String SAVE_SUBTITLE_VISIBILITY = "subtitle";

    private RealmResults<TodoItem> items;
    private Realm myRealm;

    List<TodoItem> listTaskItems;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private ItemTouchHelper mItemTouchHelper;


    //variable call each time a changed has been made in the background
    RealmChangeListener<RealmResults<TodoItem>> userListListener = new RealmChangeListener<RealmResults<TodoItem>>() {
        @Override
        public void onChange(RealmResults<TodoItem> users) {
            items.sort("taskDate", Sort.DESCENDING);
        }
    };

    public static ListTaskFragment newInstance() {
        Bundle args = new Bundle();
        ListTaskFragment fragment = new ListTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRealm = Realm.getDefaultInstance();
        setHasOptionsMenu(true);
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_to_do, container, false);

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBILITY);
        }

        mPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.title_note_list);

        mTaskRecycler = view.findViewById(R.id.todo_recycler_view);
        mTaskRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //add a new item
        FloatingActionButton mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = UUID.randomUUID().toString();
                TodoLab todoLab = TodoLab.get(getContext());
                todoLab.addItem(id, myRealm);

                Intent intent = AddTaskActivity.newIntent(getActivity(), id);
                startActivity(intent);
            }
        });




        final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });


        //handle the position of the item after the drag and the drop
        new GetTodoItemsFromDatabaseAsync().execute();

        updateUI();
        mTodoAdapter.setChangedListener(new OnTodoListChangedListener() {
            @Override
            public void onTodoListChanged(List<TodoItem> items) {
                List<String> ids = new ArrayList<>();
                for (TodoItem item : items){
                    ids.add(item.getId());
                }
                Gson gson = new Gson();
                String jsonListOfIds = gson.toJson(ids);
                mEditor.putString(Constants.LIST_OF_TODO_ID, jsonListOfIds);
            }
        });

        return view;
    }





    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }

    /**
     * sets up TaskListFragment’s UI
     */
    private void updateUI(){
        TodoLab lab = TodoLab.get(getActivity());
        listTaskItems = lab.getItems(myRealm);
        Log.d(TAG, "updateUI: the list has " + listTaskItems.size());
        if (mTodoAdapter == null){
            mTodoAdapter = new TodoAdapter(listTaskItems, this);
            mTaskRecycler.setAdapter(mTodoAdapter);
        } else{
            mTodoAdapter.setTodoItems(listTaskItems);
            ItemTouchHelper.Callback callback = new SimpleTouchHelperCallback(mTodoAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(mTaskRecycler);
            mTodoAdapter.notifyDataSetChanged();
        }

        updateSubtitle();

    }


//==============================================================================
    //==========================================================================
    //==========================================================================
    /**
     * handle the operation in the menu toolbar
     * @param menu
     * @param inflater
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_to_do, menu);

        /**
         * retrieve the MenuItem in charge of the subtitle
         * keep track of the subtitle visibility across rotation
         */
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_subtitle :{
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            }

            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * update the subtitle of the toolbar
     */
    private void updateSubtitle(){
        TodoLab lab = TodoLab.get(getActivity());
        List<TodoItem> listTaskItems = lab.getItems(myRealm);
        int count = listTaskItems.size();
        String subTitle = getResources()
                .getQuantityString(R.plurals.todo_plural, count, count);
        // if the subtitle on the action bar is not visible
        //set its value to null
        if (!mSubtitleVisible){
            subTitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subTitle);
    }

//===============================================================================


    /**
     * handle the long press to delete or update items
     * @param itemId
     * @param title
     * @param state
     */
    private void handleOnTodoItemClick(String itemId, String title, boolean state){
        final String[] options = {getString(R.string.label_delete), getString(R.string.label_edit), getString(R.string.label_check), getString(R.string.label_web_search)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog_view = inflater.inflate(R.layout.dialog_list_todo_option, null);
        builder.setView(dialog_view);

        View header_view = inflater.inflate(R.layout.custom_dialog_header, null);
        builder.setCustomTitle(header_view);

        ListView dialogListView = (ListView) dialog_view.findViewById(R.id.dialog_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options);
        dialogListView.setAdapter(adapter);

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        //capture the dialog
        final Dialog dialog = builder.create();
        dialog.show();

        dialogListView .setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //delete
                        askForConfirm(itemId, title);
                        dialog.dismiss();
                        break;

                    case 1:
                        //edit
                        dialog.dismiss();
                        Intent intent = AddTaskActivity.newIntent(getActivity(), itemId);
                        startActivity(intent);
                        break;

                    case 2:
                        //checck
                        dialog.dismiss();
                        boolean isChecked = state ? false : true;
                        TodoLab.get(getActivity()).updateTaskTodo(itemId, title, myRealm, isChecked);
                        updateUI();
                        break;
                    case 3:
                        //web
                        dialog.dismiss();
                        Uri uri = Uri.parse("https://www.bing.com/search?q=" + itemId);
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent2);
                        break;
                }
            }
        });

    }



    private void askForConfirm(String id, String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Delete " + title + "?");
        alertDialog.setMessage("are you sure you want to delete " + title + "?");

        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            TodoItem item = TodoLab.get(getActivity()).getItem(id, myRealm);
                            item.deleteFromRealm();

                            Log.i(TAG, "execute: item deleted");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "ntask deleted", Toast.LENGTH_SHORT).show();
                                }
                            }, 250);
                        }
                    });
                } catch (Exception e){
                    Log.e(TAG, "onOptionsItemSelected: failled to delete item" +  id + " ", e);
                    Log.e(TAG, "onOptionsItemSelected: " + e.getMessage());
                }
                updateUI();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_SUBTITLE_VISIBILITY, mSubtitleVisible);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    /**============================================================================================*/


    /**
     * ======================================================
     * holder for the item list to be inflated in the adapter
     * ======================================================
     */
    private class TodoHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private OnStartDragListener mDragListener;
        private TodoItem mTodoItem;

        private AppCompatCheckBox mCheckBox;
        private TextView mTaskDate;
        private ImageView handleView;

        public TodoHolder(View itemView) {
            super(itemView);
        }

        public TodoHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_task_todo, parent, false));

            mCheckBox = itemView.findViewById(R.id.title_task_todo);
            mTaskDate= itemView.findViewById(R.id.date_task_todo);
            handleView = itemView.findViewById(R.id.handle);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(TodoItem item , OnStartDragListener dragListener, final int position){
            mDragListener = dragListener;
            mTodoItem = item;
            mTaskDate.setText(mTodoItem.getReadableModifyDate(mTodoItem.getTaskDate()));
            mCheckBox.setText(mTodoItem.getTitle());
            if (mTodoItem.isDone()){
                mCheckBox.setChecked(true);
                mCheckBox.setPaintFlags(mCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }


            handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN){
                        mDragListener.onStartDrag(TodoHolder.this);
                    }
                    return false;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleOnTodoItemClick(mTodoItem.getId(), mTodoItem.getTitle(), mTodoItem.isDone());
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.GRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }



    /**
     * =================================================
     * adapter for the item list store into the database
     * =================================================
     */
    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder> implements ItemTouchHelperAdapter {

        private OnTodoListChangedListener mChangedListener;
        private OnStartDragListener mDragListener;

        List<TodoItem> mTodoItems;

        public TodoAdapter(List<TodoItem> todoItems, OnStartDragListener dragListener) {
            mTodoItems = todoItems;
            mDragListener = dragListener;
        }

        @Override
        public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TodoHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TodoHolder holder, int position) {
            holder.bind(mTodoItems.get(position), mDragListener, position);
            mDragListener.onStartDrag(holder);
        }

        @Override
        public int getItemCount() {
            return mTodoItems.size();
        }

        public void setTodoItems(List<TodoItem> todoItems) {
            mTodoItems = todoItems;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mTodoItems, fromPosition, toPosition);
            mChangedListener.onTodoListChanged(mTodoItems);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {

        }

        public void setChangedListener(OnTodoListChangedListener changedListener) {
            mChangedListener = changedListener;
        }
    }


    /**
     * ============================================================
     * AsyncTask class to remember the position of the item dragged
     * ============================================================
     */
    private class GetTodoItemsFromDatabaseAsync extends AsyncTask<Void, Void, List<TodoItem>> {

        List<TodoItem> todoItemList = new ArrayList<>();

        @Override
        protected List<TodoItem> doInBackground(Void... lists) {

            todoItemList = TodoLab.get(getActivity()).getItems(myRealm);

            //create an empty array to hold the list of empty todoItem
            List<TodoItem> sortedTodoItems = new ArrayList<>();

            //get the list of id saved in the shared preferences
            String jsonListOfId = mPreferences.getString(Constants.LIST_OF_TODO_ID, "");

            //make sure that it is not null
            if (!jsonListOfId.isEmpty()){
                //convert the json Stirng to List<String>
                Gson gson = new Gson();
                List<String> listOfSortedTodoItemsId = gson.fromJson(jsonListOfId, new TypeToken<List<String>>(){}.getType());

                if (listOfSortedTodoItemsId != null && listOfSortedTodoItemsId.size() > 0){
                    for (String id : listOfSortedTodoItemsId){
                        for (TodoItem todoItem : todoItemList){

                            if (todoItem.getId().equals(id)){
                                sortedTodoItems.add(todoItem);
                                todoItemList.remove(todoItem);
                                break;
                            }
                        }
                    }
                }


                if (todoItemList.size() >0){
                    sortedTodoItems.addAll(todoItemList);
                }
            }
            return sortedTodoItems.size() > 0 ? sortedTodoItems : todoItemList;
        }

        @Override
        protected void onPostExecute(List<TodoItem>  items) {
            super.onPostExecute(items);
            if (items != null){
                for (TodoItem note : items){
                    todoItemList.add(note);
                    mTodoAdapter.notifyItemInserted(todoItemList.size() - 1);
                }
            }

        }
    }

}
