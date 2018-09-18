package com.meeting.binary.android.binarymeeting.listener;


import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.List;

public interface OnTodoListChangedListener {

    void onTodoListChanged(List<TodoItem> items);
}
