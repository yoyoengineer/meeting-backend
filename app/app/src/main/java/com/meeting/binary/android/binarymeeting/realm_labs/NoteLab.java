package com.meeting.binary.android.binarymeeting.realm_labs;

import android.content.Context;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.model.Note;
import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class NoteLab {

    private static final String TAG = "todolab_tag";
    private Realm myRealm;

    private static NoteLab sLab;
    private Context mContext;

    private NoteLab(Context context){
        mContext = context.getApplicationContext();
    }

    public static NoteLab get(Context context){
        if (sLab == null){
            sLab = new NoteLab(context);
        } else {

        }
        return sLab;
    }


    /**
     * return a list of note Items
     * @return
     */
    public List<Note> getItems(Realm realm){
        List<Note> items = new ArrayList<>();
        myRealm = realm;
        try {
            RealmResults<Note> userList = myRealm.where(Note.class).findAll();
            Log.d(TAG, "getItems: " + userList.size());
            //userList.sort("title", Sort.ASCENDING);
            for (Note item : userList){
                items.add(0, item);
            }
            return items;
        }catch(Exception e){
            return null;
        }
    }



    /**
     * return a single item to do with a specify id
     * @param id
     * @return
     */
    public Note getItem(String id, Realm realm){
        myRealm = realm;
        try {
            RealmResults<Note> realmResults = myRealm.where(Note.class)
                    .contains("id", id)
                    .findAll();
            Log.d(TAG, "getItem: " + realmResults.get(0).getId());
            return realmResults.get(0);
        } catch(Exception e) {
            Log.d(TAG, "getItem: " + e.getMessage());
            Log.d(TAG, "getItem: error in finding items");
            return null;
        }
    }



    /**
     * insert list item to do into the database
     */
    public void addItem(String id, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Note note = myRealm.createObject(Note.class, id);
                    note.setTitle(" ");
                    note.setContent(" ");
                    note.setDate(new Date().getTime());
                    Log.d(TAG, "execute: succeed to add");
                    RealmResults<Note> realmResultNoteItem = myRealm.where(Note.class).findAll();
                    Log.d(TAG, "add list Item");
                    Log.d(TAG, "execute: items inside " + realmResultNoteItem.get(0).getId() + " id inside");
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "addItem: failed to add" + e.getMessage());
        }
    }



    /**
     * @param notes
     * @param realm
     */
    public void updateOrInsertNote(final List<Note> notes, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(notes);
                    Log.d(TAG, "execute: executed");
                    RealmResults<Note> realmResultTodoItem = myRealm.where(Note.class).findAll();
                    Log.d(TAG, "add list Item");
                    Log.d(TAG, "execute: items inside " + realmResultTodoItem.get(0).getTitle());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "add list Item: " + e.getMessage());
        }
    }


    public void update(String id, String title, String content, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Note note = myRealm.where(Note.class)
                            .contains("id", id)
                            .findFirst();
                    note.setTitle(title);
                    note.setContent(content);
                    note.setDate(new Date().getTime());
                    //realm.insertOrUpdate(note);
                    Log.d(TAG, "execute: executed");
                    RealmResults<Note> realmResultTodoItem = myRealm.where(Note.class).findAll();
                    Log.i(TAG, "onCreateView: check the title " + title);
                    Log.d(TAG, "add list Item");
                    Log.d(TAG, "execute: items inside " + realmResultTodoItem.get(0).getTitle());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "add list Item: " + e.getMessage());
        }
    }



    public void deleteNote(final String id, Realm realm){
        myRealm = realm;
        try {
            Note realmResult = myRealm.where(Note.class)
                    .contains("id", id)
                    .findFirst();
            realmResult.deleteFromRealm();
            Log.d(TAG, "deleteNote: " + realmResult.getId());

        } catch(Exception e) {
            Log.e(TAG, "getItem: " + e.getMessage());
            Log.e(TAG, "getItem: error in finding items");

        }
    }
}
