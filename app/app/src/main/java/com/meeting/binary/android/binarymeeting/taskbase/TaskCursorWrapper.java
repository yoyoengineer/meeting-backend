package com.meeting.binary.android.binarymeeting.taskbase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.Note;
import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.Date;

import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.Cols.DATE;
import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.Cols.DONE;
import static com.meeting.binary.android.binarymeeting.taskbase.TaskDbSchema.TaskTable.Cols.TITLE;

/**
 * Created by meldi on 3/25/2018.
 */

public class TaskCursorWrapper extends CursorWrapper {

    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TodoItem getItemTodo(){
        String uuidString = getString(getColumnIndex(TaskDbSchema.TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TITLE));
        int isDone = getInt(getColumnIndex(DONE));
        long date = getLong(getColumnIndex(DATE));

        TodoItem item = new TodoItem(uuidString);
        item.setTitle(title);
        //item.setTaskDate(new Date(date));
        item.setDone(isDone != 0);
        return item;
    }




    public Contact getContact(){
        String name = getString(getColumnIndex(TaskDbSchema.ContactTable.Cols.NAME));
        String username = getString(getColumnIndex(TaskDbSchema.ContactTable.Cols.USERNAME));
        String town = getString(getColumnIndex(TaskDbSchema.ContactTable.Cols.TOWN));
        String photo = getString(getColumnIndex(TaskDbSchema.ContactTable.Cols.PHOTO));


        Contact contact = new Contact();
        contact.setName(name);
        contact.setUsername(username);
        contact.setTown(town);
        contact.setPhoto(photo);
        return contact;
    }




    public Note getNote(){
        String uuidString = getString(getColumnIndex(TaskDbSchema.NotepadTable.Cols.UUID));
        String content = getString(getColumnIndex(TaskDbSchema.NotepadTable.Cols.CONTENT));
        String title = getString(getColumnIndex(TaskDbSchema.NotepadTable.Cols.TITLE));
        long date = getLong(getColumnIndex(DATE));

        Note note = new Note(uuidString);
        note.setTitle(title);
        note.setContent(content);
        note.setDate(date);
        return note;
    }

}
