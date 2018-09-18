package com.meeting.binary.android.binarymeeting.realm_labs;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by meldi on 4/7/2018.
 */

public class TodoLab {

    private static final String TAG = "todolab_tag";
    private Realm myRealm;

    private static TodoLab sLab;
    private Context mContext;

    private static int order = 0;

    public static int getOrder() {
        return order++;
    }


    private TodoLab(Context context){
        mContext = context.getApplicationContext();
    }

    public static TodoLab get(Context context){
        if (sLab == null){
            sLab = new TodoLab(context);
        } else {

        }
        return sLab;
    }


    /**
     * return a list of Items to do
     * @return
     */
    public List<TodoItem> getItems(Realm realm){
        List<TodoItem> items = new ArrayList<>();
        myRealm = realm;
        try {
            RealmResults<TodoItem> userList = myRealm.where(TodoItem.class).findAll();
            Log.d(TAG, "getItems: " + userList.size());
            //userList.sort("title", Sort.ASCENDING);
            for (TodoItem item : userList){
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
    public TodoItem getItem(String id, Realm realm){
        myRealm = realm;
        try {
            RealmResults<TodoItem> realmResults = myRealm.where(TodoItem.class)
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
     * insert item to do into the database
     */
    public void addItem(final String id, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TodoItem todoItem = myRealm.createObject(TodoItem.class, id);
                    todoItem.setTitle("");
                    todoItem.setTaskDate(new Date().getTime());
                    todoItem.setDone(false);
                    Log.d(TAG, "execute: executed");
                    RealmResults<TodoItem> realmResultTodoItem = myRealm.where(TodoItem.class).findAll();
                    Log.d(TAG, "execute: items inside " + realmResultTodoItem.get(0).getId());
                }
            });
        }  catch (Exception e){
            Log.d(TAG, "addItem: " + e.getMessage());
        }

    }




    /**
     * update a specific row in the database
     * @param id
     */
    public void updateTaskTodo(final String id, final String title, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d(TAG, "execute: " + title);
                    TodoItem realmResult = myRealm.where(TodoItem.class)
                            .contains("id", id)
                            .findFirst();
                    realmResult.setTitle(title);
                    realmResult.setTaskDate(new Date().getTime());
                    Toast.makeText(mContext, "task saved " + realmResult.getTitle(), Toast.LENGTH_LONG).show();
                    RealmResults<TodoItem> userList = myRealm.where(TodoItem.class).findAll();
                    userList.sort("title", Sort.ASCENDING);
                }
            });

        } catch (Exception e){
            Log.i(TAG, "updateTaskTodo: error occure " + e.getMessage());
        }
    }



    /**
     * update a specific row in the database
     * @param id
     */
    public void updateTaskTodo(final String id, final String title, Realm realm, final boolean isChecked){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d(TAG, "execute: " + title);
                    TodoItem realmResult = myRealm.where(TodoItem.class)
                            .contains("id", id)
                            .findFirst();
                    realmResult.setTitle(title);
                    realmResult.setTaskDate(new Date().getTime());
                    realmResult.setDone(isChecked);
                    //Toast.makeText(mContext, "task saved " + realmResult.getTitle(), Toast.LENGTH_LONG).show();
                    RealmResults<TodoItem> userList = myRealm.where(TodoItem.class).findAll();
                    userList.sort("title", Sort.ASCENDING);
                }
            });

        } catch (Exception e){
            Log.e(TAG, "updateTaskTodo: error occure " + e.getMessage());
        }
    }


    public void deleteTaskTodo(final String id, Realm realm){
        myRealm = realm;
        try {
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TodoItem realmResult = myRealm.where(TodoItem.class)
                            .contains("id", id)
                            .findFirst();
                    realmResult.deleteFromRealm();

                    Log.d(TAG, "deleteTaskTodo: " + realmResult.getTitle() + " deleted");
                }
            });
        }catch (Exception e){
            Log.e(TAG, "deleteTaskTodo: the item could not be deleted " + e.getMessage());
        }
    }
}
