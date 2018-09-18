package com.meeting.binary.android.binarymeeting.realm_labs;

import android.content.Context;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.model.Contact;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ContactLab {


    private Realm myRealm;
    private static String TAG = "contactlab_tag";


    private static ContactLab sLab;
    private Context mContext;

    private ContactLab(Context context){ mContext = context;}

    public static ContactLab get(Context context){
        if (sLab == null){
            sLab = new ContactLab(context);
        }
        return sLab;
    }


    /**
     * return a list of Items to do
     * @return
     */
    public RealmList<Contact> getItems(Realm realm){
        RealmList<Contact> contacts = new RealmList<>();
        //List<Contact> items = new ArrayList<>();
        myRealm = realm;
        try {
            RealmResults<Contact> userList = myRealm.where(Contact.class).findAll();
            Log.d(TAG, "getItems: " + userList.size());
            //userList.sort("title", Sort.ASCENDING);
            for (Contact item : userList){
                contacts.add(0, item);
            }
            return contacts;
        }catch(Exception e){
            return null;
        }
    }


    /**
     * return a single item to do with a specify id
     * @param id
     * @return
     */
    public Contact getItem(String id, Realm realm){
        myRealm = realm;
        try {
            RealmResults<Contact> realmResults = myRealm.where(Contact.class)
                    .contains("id", id)
                    .findAll();
            Log.d(TAG, "getItem: " + realmResults.get(0).getUsername());
            return realmResults.get(0);
        } catch(Exception e) {
            Log.d(TAG, "getItem: " + e.getMessage());
            Log.d(TAG, "getItem: error in finding items");
            return null;
        }
    }


    /**
     * insert item to do into the database
     */
    public void addItem(final String id, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Contact todoItem = realm.createObject(Contact.class, id);
                    todoItem.setName("");
                    todoItem.setUsername("");
                    todoItem.setLastModified(new Date().getTime());
                    Log.d(TAG, "execute: executed");
                    RealmResults<Contact> realmResultTodoItem = myRealm.where(Contact.class).findAll();
                    Log.d(TAG, "execute: item inside " + realmResultTodoItem.get(0).getName());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "addItem: " + e.getMessage());
        }
    }



    /**
     * insert list item to do into the database
     */
    public void addItem(final List<Contact> contacts, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insert(contacts);
                    Log.d(TAG, "execute: executed");
                    RealmResults<Contact> realmResultTodoItem = myRealm.where(Contact.class).findAll();
                    Log.d(TAG, "add list Item");
                    Log.d(TAG, "execute: items inside " + realmResultTodoItem.get(0).getName());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "add list Item: " + e.getMessage());
        }
    }



    /**
     * update a specific row in the database
     * @param id
     */
    public void updateContact(final String id, final String name, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Contact todoItem = realm.createObject(Contact.class, id);
                    todoItem.setName("");
                    todoItem.setUsername("");
                    todoItem.setLastModified(new Date().getTime());
                    Log.d(TAG, "execute: executed");
                    RealmResults<Contact> realmResultTodoItem = myRealm.where(Contact.class).findAll();
                    Log.d(TAG, "execute: item inside " + realmResultTodoItem.get(0).getName());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "addItem: " + e.getMessage());
        }
    }


    /**
     *
     * @param contacts
     * @param realm
     */
    public void updateOrInsertContact(final List<Contact> contacts, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(contacts);
                    Log.d(TAG, "execute: executed");
                    RealmResults<Contact> realmResultTodoItem = myRealm.where(Contact.class).findAll();
                    Log.d(TAG, "add list Item");
                    Log.d(TAG, "execute: items inside " + realmResultTodoItem.get(0).getName());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "add list Item: " + e.getMessage());
        }
    }
}
