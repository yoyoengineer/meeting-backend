package com.meeting.binary.android.binarymeeting.taskbase;

/**
 * Created by meldi on 3/25/2018.
 */

public class TaskDbSchema {

    public static final class TaskTable {
        public static final String NAME = "tasks";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DONE = "done";
        }
    }

    public static final class ContactTable {
        public static final String NAME = "contacts";

        public static final class Cols{
            public static final String NAME = "name";
            public static final String USERNAME = "username";
            public static final String TOWN = "town";
            public static final String PHOTO = "photo";
        }
    }


    public static final class NotepadTable {
        public static final String NAME = "notes";

        public static final class Cols{
            public static final String UUID = "name";
            public static final String TITLE = "username";
            public static final String CONTENT = "town";
            public static final String DATE = "date";
        }
    }
}
