package com.meeting.binary.android.binarymeeting.listener;

/**
 * Created by daryl on 12/5/2017.
 */

public interface ItemTouchHelperViewHolder {

    /**
     * Implementations should update the item view to indicate it's active state
     */
    void onItemSelected();

    /**
     * state should be cleared
     */
    void onItemClear();
}
