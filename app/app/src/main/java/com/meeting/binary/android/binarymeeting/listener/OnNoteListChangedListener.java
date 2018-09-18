package com.meeting.binary.android.binarymeeting.listener;

import com.meeting.binary.android.binarymeeting.model.Note;

import java.util.List;

public interface OnNoteListChangedListener {

    void onNoteListChanged(List<Note> items);
}
