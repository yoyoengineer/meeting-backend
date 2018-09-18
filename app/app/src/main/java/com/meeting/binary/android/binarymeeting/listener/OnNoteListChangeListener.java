package com.meeting.binary.android.binarymeeting.listener;


import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.List;

public interface OnNoteListChangeListener {

    void onNoteListChanged(List<TodoItem> items);
}
