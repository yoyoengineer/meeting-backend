package com.meeting.binary.android.binarymeeting.taskbase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.Cols.DONE;
import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.Cols.TITLE;
import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.Cols.UUID;
import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.NAME;

/**
 * Created by meldi on 3/25/2018.
 */

public class TodoItemLab {

    private static TodoItemLab sLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private TodoItemLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TaskBasedHelper(mContext).getWritableDatabase();
    }

    public static TodoItemLab get(Context context){
        if (sLab == null){
            sLab = new TodoItemLab(context);
        }
        return sLab;
    }




    /**
     * return a list of crime
     * @return
     */
    public List<TodoItem> getItems(){
        List<TodoItem> ite = new ArrayList<>();

        TaskCursorWrapper cursorWrapper = queryCrimes(null, null);

        try{
            cursorWrapper.moveToFirst();
            while(!cursorWrapper.isAfterLast()){
                ite.add(cursorWrapper.getItemTodo());
                cursorWrapper.moveToNext();
            }
        } finally{
            cursorWrapper.close();
        }
        return ite;
    }


    /**
     * return a single crime with a specify id
     * @param id
     * @return
     */
    public TodoItem getItem(java.util.UUID id){
        TaskCursorWrapper cursorWrapper = queryCrimes(TaskDbSchema.TaskTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        try{
            if (cursorWrapper.getCount() == 0){
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getItemTodo();
        } finally {
            cursorWrapper.close();
        }
    }



    /**
     * write date in the database using a key pair method
     * @param item
     * @return
     */
    private static ContentValues getContentValues(TodoItem item){
        ContentValues values = new ContentValues();
        values.put(UUID, item.getId().toString());
        values.put(TITLE, item.getTitle());
        values.put(DONE, item.isDone());


        return values;
    }


    /**
     * insert crime into the database
     * @param item
     */
    public void addCrime(TodoItem item){
        ContentValues values = getContentValues(item);
        mDatabase.insert(NAME, null, values);
    }

    /**
     * update a specific row in the database
     * @param item
     */
    public void updateTaskTodo(TodoItem item){
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);
        mDatabase.update(NAME, values, UUID + " = ?", new String[]{uuidString});
    }


    /**
     * using a cursor wrapper to query the database
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private TaskCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(NAME,
                null, // column - null means selects all the columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new TaskCursorWrapper(cursor);
    }
}
