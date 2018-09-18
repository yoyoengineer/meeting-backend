package com.meeting.binary.android.binarymeeting.listener;






import com.meeting.binary.android.binarymeeting.model.TodoItem;

import java.util.List;

/**
 * Created by daryl on 12/7/2017.
 */

public interface OnTodoListItemChangedListener {
    void onTodoListItemListenerChanged(List<TodoItem> todoItems);
}
