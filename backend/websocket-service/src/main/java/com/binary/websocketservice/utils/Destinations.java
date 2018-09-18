package com.binary.websocketservice.utils;

public class Destinations {

    public static class Chat{
        public static String privateMessages(){
            return "/queue/chat";
        }
    }

    public static class Conference{

    }

    public static class Notification{
        public static String notification(){
            return "/queue/notifications";
        }
    }

    public static class Event{
        public static String privateEvent(String eventId){return "/queue/event/"+eventId;}
        public static String publicEvent(String eventId){return "/topic/event/"+eventId;}

    }
}
