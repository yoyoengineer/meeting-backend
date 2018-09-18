package com.meeting.binary.android.binarymeeting.taskbase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by meldi on 3/25/2018.
 */

public class TaskBasedHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public TaskBasedHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " +
                TaskDbSchema.TaskTable.NAME +
                " (" + " _id integer primary key autoincrement, " +
                TaskDbSchema.TaskTable.Cols.UUID + ", " +
                TaskDbSchema.TaskTable.Cols.TITLE + ", " +
                TaskDbSchema.TaskTable.Cols.DATE + ", " +
                TaskDbSchema.TaskTable.Cols.DONE +
                ")"
        );

        sqLiteDatabase.execSQL("create table " +
                TaskDbSchema.ContactTable.NAME +
                " (" + " _id integer primary key autoincrement, " +
                TaskDbSchema.ContactTable.Cols.NAME + ", " +
                TaskDbSchema.ContactTable.Cols.USERNAME + ", " +
                TaskDbSchema.ContactTable.Cols.TOWN + ", " +
                TaskDbSchema.ContactTable.Cols.PHOTO +
                ")"
        );


        sqLiteDatabase.execSQL("create table " +
                TaskDbSchema.NotepadTable.NAME +
                " (" + " _id integer primary key autoincrement, " +
                TaskDbSchema.NotepadTable.Cols.UUID + ", " +
                TaskDbSchema.NotepadTable.Cols.TITLE + ", " +
                TaskDbSchema.NotepadTable.Cols.CONTENT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
