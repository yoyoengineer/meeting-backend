package com.meeting.binary.android.binarymeeting.service.request_interface;


import com.meeting.binary.android.binarymeeting.model.Agenda;
import com.meeting.binary.android.binarymeeting.model.Announcement;
import com.meeting.binary.android.binarymeeting.model.Bulletin;
import com.meeting.binary.android.binarymeeting.model.Choice;
import com.meeting.binary.android.binarymeeting.model.Comment;
import com.meeting.binary.android.binarymeeting.model.Conference;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.DanmakuMessage;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.model.FileModel;
import com.meeting.binary.android.binarymeeting.model.GenericResponse;
import com.meeting.binary.android.binarymeeting.model.Lottery;
import com.meeting.binary.android.binarymeeting.model.LotteryPlayer;
import com.meeting.binary.android.binarymeeting.model.LotteryResult;
import com.meeting.binary.android.binarymeeting.model.Message;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;
import com.meeting.binary.android.binarymeeting.model.UserDto;

import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RequestWebServiceInterface {

    /**
     * login feed and method to call
     */
    String LOGINFEED = "/signin";
    @POST(LOGINFEED)
    Call<Void> login();


    String SIGNUPFEED = "/signup";
    @POST(SIGNUPFEED)
//    @FormUrlEncoded
    Call<GenericResponse> register(@Body UserDto user);

    /**
     * createLottery feed and method to call
     */
    String CREATELOTTERY = "event/lottery";
    @POST(CREATELOTTERY)
    Call<Void> createLottery(@Body Lottery prizes);

    /**
     * createLottery feed and method to call
     */
    String GET_USER_LOTTERY_RESULT = "/event/lottery/{eventId}";
    @GET(GET_USER_LOTTERY_RESULT)
    Call<List<LotteryPlayer>> getUserLotteryResult(@Path("eventId") String eventId);

    /*******************************
     * start lottery
     */
    String START_LOTTERY = "/event/lottery/play/{eventId}";
    @POST(START_LOTTERY)
    Call<LotteryResult> getLotteryResult(@Path("eventId") String eventId);

    /**
     *
     */
    String POLL_ACTIVITY = "/event/vote";
    @POST(POLL_ACTIVITY)
    Call<ResponseBody> sendVotes(@Body Choice choice);


    /**
     * contact feed and method to call
     */
    String CONTACT_FEED = "/profile/myfriends";
    @GET(CONTACT_FEED)
    Call<List<Contact>> getContacts();



    /**
     * get profile name
     */
    String PROFILE_NAME_FEED = "/profile/myname";
    @GET(PROFILE_NAME_FEED)
    Call<Map<String, String>> getProfileName();


    /**
     * send comment event feed
     */
    String SEND_COMMENT_FEED = "/event/comment";
    @POST(SEND_COMMENT_FEED)
    Call<ResponseBody> sendComment(@Body Comment comment);


    /**
     * get people like
     */
    String CONTACT_LIKE_FEED = "/profile/conference/likes/people/{eventId}";
    @GET(CONTACT_LIKE_FEED)
    Call<List<Contact>> getContactsLike(@Path("eventId") String eventId);

    /**
     * get people like
     */
    String CHAT_LIST_HISTORY_FEED = "/private/history";
    @GET(CHAT_LIST_HISTORY_FEED)
    Call<List<Message>> getChatHistory();


    /**
     * get people like
     */
    String CONTACT_ATTENDEES_FEED = "/profile/conference/attendees/people/{eventId}";
    @GET(CONTACT_ATTENDEES_FEED)
    Call<List<Contact>> getContactsAttendes(@Path("eventId") String eventId);


    /**
     * send like to the server
     */
    String LIKE_FEED = "/profile/likes/{eventId}";
    @POST(LIKE_FEED)
    Call<ResponseBody> sendLike(@Path("eventId") String eventId);

    /**
     * contact profile & event
     */
    String CONTACT_PROFILE_FEED = "/profile/user/{username}";
    @GET(CONTACT_PROFILE_FEED)
    Call<MyContactPage> getContactProfile(@Path("username") String username);


    /**
     * send danmaku message
     * @param bullet
     * @param eventId
     * @return
     */
    @POST("/event/bullet/{eventId}")
    Call<ResponseBody> bullet(@Body DanmakuMessage bullet , @Path("eventId") String eventId);

    /**
     * all the events
     */
    String MY_EVENTS_FEED = "/event/myevents";
    @GET(MY_EVENTS_FEED)
    Call<List<Event>> getAllEvents();


    /**
     * get the response from the scan
     */
    String MY_SCAN_FEED = "/event/attend/{eventId}";
    @POST(MY_SCAN_FEED)
    Call<ResponseBody> getScanResponce(@Path("eventId") String eventId);


    /**
     * get the questions for the poll
     */
    String ASSERTION_FEED = "/event/bulletin/{eventId}";
    @GET(ASSERTION_FEED)
    Call<Bulletin> getAssertions(@Path("eventId") String eventId);


    /**
     * get the answer for A specific poll
     */
    String ANSWER_STAT_FEED = "/event/bulletin/{eventId}";
    @GET(ANSWER_STAT_FEED)
    Call<Bulletin> getAnswerStat(@Path("eventId") String eventId);



    /**
     * get the contact event
     */
    String CONTACT_EVENTS_FEED = "/profile/conferences/user/{username}";
    @GET(CONTACT_EVENTS_FEED)
    Call<List<Event>> getContactEvent(@Path("username") String username);

    /**
     * specific event
     */
    String EVENT_FEED = "/event/get/{id}";
    @GET(EVENT_FEED)
    Call<Event> getEvent(@Path("id") String id);


    /**
     * specific event announcement
     */
    String EVENT_ANNOUNCEMENT_FEED = "/event/announcements/{eventId}";
    @GET(EVENT_ANNOUNCEMENT_FEED)
    Call<List<Announcement>> getEventAnnouncemennt(@Path("eventId") String eventId);


    /**
     * send specific event announcement
     */
    String SEND_EVENT_ANNOUNCEMENT_FEED = "/event/announcement";
    @POST(SEND_EVENT_ANNOUNCEMENT_FEED)
    Call<ResponseBody> sendEventAnnouncement(@Body Announcement announcement);


    /**
     * specific event comment
     */
    String EVENT_COMMENT_FEED = "/event/comments/{eventId}";
    @GET(EVENT_COMMENT_FEED)
    Call<List<Comment>> getEventComment(@Path("eventId") String eventId);

    /**
     * get contact pagination
     */
    String CONTACT_PAGINATION_URI = "/profile/friends/{username}";
    @GET(CONTACT_PAGINATION_URI)
    Call<List<Contact>> getContactPagination(@Path("username") String username);

    /************************************
     * latest events
     */
    String LATEST_EVENTS_FEED = "/profile/conference/latest";
    @GET(LATEST_EVENTS_FEED)
    Call<List<Event>> getLatestEvents();

    /***********************************
     * most popular events
     */
    String MOST_POPULAR_EVENTS_FEED = "/profile/conference/mpcct";
    @GET(MOST_POPULAR_EVENTS_FEED)
    Call<List<Event>> getSameCityRecommededEvents();


    /*******************************
     * create meeting
     */
    String GET_AGENDAS = "/event/agenda/{eventId}";
    @GET(GET_AGENDAS)
    Call<List<Agenda>> getAgendas(@Path("eventId") String eventId);


    /*******************************
     * recommended events
     */
    String RECOMMEDED_EVENTS_FEED = "/profile/conference/mac";
    @GET(RECOMMEDED_EVENTS_FEED)
    Call<List<Event>> getMostAttentedEventsWithOrganizer();


    /*******************************
     * get participating events
     */
    String GET_PARTICIPATING_EVENTS = "/profile/conferences/participate";
    @GET(GET_PARTICIPATING_EVENTS)
    Call<List<Event>> getParticipatingEvents();

    /*****************************
     * get friends
     */
    String GET_FRIENDS = "/profile/myfriends";
    @GET(GET_FRIENDS)
    Call<List<Contact>> getFriends();


    /*******************************
     * create meeting
     */
    String CREATE_AGENDA = "/event/agenda";
    @POST(CREATE_AGENDA)
    Call<Void> createAgenda(@Body Agenda agenda);


    /*******************************
     * create meeting
     */
    String CREATE_MEETING = "/event/create";
    @POST(CREATE_MEETING)
    Call<Void> createMeeting(@Body Conference conference);



    /*****************************
     * event attended
     */
    String ATTENDED_EVENTS = "/profile/conferences/attended";
    @GET(ATTENDED_EVENTS)
    Call<List<Event>> getAttendedEvents();


    /**this is the upload file*/
    @Multipart
    @POST("/event/file/{eventId}")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part file, @Path("eventId") String eventId);



    /**this is the upload file*/
    String PRIVATE_FILE = "/private/file/{toUser}";
    @Multipart
    @POST(PRIVATE_FILE)
    Call<ResponseBody> uploadPrivatePhoto(@Part MultipartBody.Part file, @Path("toUser") String toUser);


    /**this is the upload file*/
    String PRIVATE_MESSAGE = "/private/message";
    @POST(PRIVATE_MESSAGE)
    Call<ResponseBody> sendMessage(@Body Message message);


    /**this is the upload file*/
    String PRIVATE_MESSAGE_HISTORIC = "/private/messages/{historicId}";
    @GET(PRIVATE_MESSAGE_HISTORIC)
    Call<List<Message>> loadHistoricMessage(@Path("historicId") String historicId);


    /**this is the upload file*/
    @Multipart
    @POST("/file/file/{eventId}")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part file);

    @GET("/event/votes/{eventId}")
    Call<List<Bulletin>> getPoll(@Path("eventId") String eventId);


    @POST("/event/vote/create")
    Call<ResponseBody> createPoll(@Body Bulletin bulletin);

    @GET("/event/files/{eventId}")
    Call<List<FileModel>> getFiles(@Path("eventId")String eventId);

    @GET("/event/mobile/photo/{eventId}")
    Call<List<FileModel>> getPhotos(@Path("eventId")String eventId);

    @GET("/event/mobile/files/{eventId}")
    Call<List<FileModel>> getOther(@Path("eventId")String eventId);

    String LOGOUT = "/logout";
    @POST(LOGOUT)
    Call<ResponseBody> logout();

    String DOWNLOAD = "/event/file/download/{eventId}/{fileId}";
    @POST(DOWNLOAD)
    Call<ResponseBody> download(@Path("eventId") String eventId, @Path("fileId") String fileId);

}
