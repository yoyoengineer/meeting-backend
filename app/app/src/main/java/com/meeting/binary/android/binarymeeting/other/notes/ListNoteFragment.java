package com.meeting.binary.android.binarymeeting.other.notes;

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
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.listener.ItemTouchHelperAdapter;
import com.meeting.binary.android.binarymeeting.listener.ItemTouchHelperViewHolder;
import com.meeting.binary.android.binarymeeting.listener.OnNoteListChangedListener;
import com.meeting.binary.android.binarymeeting.listener.OnStartDragListener;
import com.meeting.binary.android.binarymeeting.listener.OnTodoListChangedListener;
import com.meeting.binary.android.binarymeeting.model.Note;
import com.meeting.binary.android.binarymeeting.model.TodoItem;
import com.meeting.binary.android.binarymeeting.other.Constants;
import com.meeting.binary.android.binarymeeting.other.SimpleTouchHelperCallback;
import com.meeting.binary.android.binarymeeting.other.todo.AddTaskActivity;
import com.meeting.binary.android.binarymeeting.realm_labs.NoteLab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListNoteFragment extends Fragment implements OnStartDragListener {


    private RecyclerView mNoteRecycler;
    private NoteAdapter mNoteAdapter;

    private static final String TAG = "list_note";

    private boolean mSubtitleVisible;
    private static final String SAVE_SUBTITLE_VISIBILITY = "subtitle";

    private RealmResults<TodoItem> items;
    private Realm myRealm;

    List<Note> mNoteList;

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


    public static ListNoteFragment newInstance() {
        Bundle args = new Bundle();
        ListNoteFragment fragment = new ListNoteFragment();
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
        View view = inflater.inflate(R.layout.fragment_note_to_take, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Campus Event");

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBILITY);
        }


        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.bm);

        mPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        mNoteRecycler = view.findViewById(R.id.note_recycler_view);
        mNoteRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //add a new item
        FloatingActionButton mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = UUID.randomUUID().toString();
                NoteLab lab = NoteLab.get(getActivity());
                lab.addItem(id, myRealm);

                Intent intent = CreateNoteActivity.newIntent(getActivity(), id);
                startActivity(intent);
            }
        });

        updateUI();
        mNoteAdapter.setChangedListener(new OnNoteListChangedListener() {
            @Override
            public void onNoteListChanged(List<Note> items) {
                List<String> ids = new ArrayList<>();
                for (Note item : items){
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
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

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
        NoteLab lab = NoteLab.get(getActivity());
        mNoteList = lab.getItems(myRealm);
        Log.d(TAG, "updateUI: the list has " + mNoteList.size());
        if (mNoteAdapter == null){
            mNoteAdapter = new NoteAdapter(this, mNoteList);
            mNoteRecycler.setAdapter(mNoteAdapter);
        } else{
            mNoteAdapter.setNotes(mNoteList);
            ItemTouchHelper.Callback callback = new SimpleTouchHelperCallback(mNoteAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(mNoteRecycler);
            mNoteAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }


    /**
     * handle the operation in the menu toolbar
     * @param menu
     * @param inflater
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_to_take, menu);

        /**
         * retrieve the MenuItem in charge of the subtitle
         * keep track of the subtitle visibility across rotation
         */
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_notes);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                getActivity().onBackPressed();
                return true;
            }
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
        NoteLab lab = NoteLab.get(getActivity());
        List<Note> noteList = lab.getItems(myRealm);
        int count = noteList.size();
        String subTitle = getResources()
                .getQuantityString(R.plurals.notes_plural, count, count);
        // if the subtitle on the action bar is not visible
        //set its value to null
        if (!mSubtitleVisible){
            subTitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subTitle);
    }



    /**
     * handle the long press to delete or update items
     * @param item
     */
    private void handleOnTodoItemClick(String item, String title){
        final String[] options = {getString(R.string.label_edit), getString(R.string.label_delete), getString(R.string.label_web_search)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog_view = inflater.inflate(R.layout.dialog_list_note_option, null);
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
                        //edit
                        dialog.dismiss();
                        Intent intent = CreateNoteActivity.newIntent(getActivity(), item);
                        startActivity(intent);
                        break;

                    case 1:
                        //delete
                        dialog.dismiss();
                        askForConfirm(item, title);
                        break;

                    case 2:
                        //web
                        dialog.dismiss();
                        Uri uri = Uri.parse("https://www.bing.com/search?q=" + title);
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
                            Note note = NoteLab.get(getActivity()).getItem(id, myRealm);
                            note.deleteFromRealm();

                            Log.i(TAG, "execute: item deleted");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "note deleted", Toast.LENGTH_SHORT).show();
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




    /**============================================================================================*/


    /**
     * ======================================================
     * holder for the item list to be inflated in the adapter
     * ======================================================
     */
    private class NoteHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private OnStartDragListener mDragListener;
        private Note mNoteItem;

        private TextView title;
        private TextView mNoteDate;
        private ImageView handleView;

        public NoteHolder(View itemView) {
            super(itemView);
        }

        public NoteHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_note_item, parent, false));

            title = itemView.findViewById(R.id.title_note);
            mNoteDate= itemView.findViewById(R.id.date_note);
            handleView = itemView.findViewById(R.id.handle);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(Note item , OnStartDragListener dragListener, final int position){
            mDragListener = dragListener;
            mNoteItem = item;
            mNoteDate.setText(mNoteItem.getReadableModifyDate(mNoteItem.getDate()));

            title.setText(mNoteItem.getTitle());

            handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN){
                        mDragListener.onStartDrag(NoteHolder.this);
                    }
                    return false;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleOnTodoItemClick(mNoteItem.getId(), mNoteItem.getTitle());
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
    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> implements ItemTouchHelperAdapter {

        private OnNoteListChangedListener mChangedListener;
        private OnStartDragListener mDragListener;

        List<Note> mNotes;

        public NoteAdapter(OnStartDragListener dragListener, List<Note> notes) {
            mDragListener = dragListener;
            mNotes = notes;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NoteHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            holder.bind(mNotes.get(position), mDragListener, position);
            mDragListener.onStartDrag(holder);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public void setNotes(List<Note> notes) {
            mNotes = notes;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mNotes, fromPosition, toPosition);
            mChangedListener.onNoteListChanged(mNotes);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {

        }

        public void setChangedListener(OnNoteListChangedListener changedListener) {
            mChangedListener = changedListener;
        }
    }



}
