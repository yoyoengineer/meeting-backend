package com.meeting.binary.android.binarymeeting.other;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;

public class SimpleItemTouchHelperCallback extends Callback {


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
