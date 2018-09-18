package com.binary.profileservice.repository;

import com.binary.profileservice.model.neo4j.Conference;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface ConferenceRepository extends GraphRepository<Conference> {

    @Query("MATCH (c:Conference{eventId:{0}}) " +
            "WITH c " +
            "OPTIONAL MATCH (c)<-[:LIKES]-(p:Person) " +
            "WITH c, count(p.username) AS likes " +
            "OPTIONAL MATCH (c)-[r:LOCATED_IN]->(ci:City) " +
            "WITH c,likes,ci " +
            "RETURN c.eventId as eventId," +
            " c.topic as topic, c.description as description, c.time as time, likes, ci.name as town ")
    Conference findConferenceByEventId(String eventId);

    Conference findByEventId(String eventId, @Depth int depth);

    @Query("MATCH (:Person{username:{0}})-[:ORGANIZED]->(c:Conference) " +
            "WITH c " +
            "OPTIONAL MATCH (c)<-[:LIKES]-(p:Person) " +
            "WITH c, count(p.username) AS likes " +
            "OPTIONAL MATCH (c)-[r:LOCATED_IN]->(ci:City) " +
            "WITH" +
            " c.eventId as eventId," +
            " c.topic as topic, c.description as description, c.time as time, likes, ci.name as town " +
            "RETURN eventId as id, topic, description, time, likes, town " +
            "ORDER BY time DESC " +
            "SKIP {1} "
//            +
//            "LIMIT 7"
    )
    List<Map<String,Object>> findMyConferences(String username, int page);


    @Query("MATCH (:Person{username:{0}})-[:ATTENDED]->(c:Conference) " +
            "WITH c " +
            "OPTIONAL MATCH (c)<-[:LIKES]-(p:Person) " +
            "WITH c, count(p.username) AS likes " +
            "OPTIONAL MATCH (c)-[r:LOCATED_IN]->(ci:City) " +
            "WITH c,likes,ci " +
            "RETURN c.eventId as id," +
            " c.topic as topic, c.description as description, c.time as time, likes, ci.name as town " +
//            "RETURN eventId as id, topic, description, time, likes, town " +
            "ORDER BY time DESC " +
            "SKIP {1}"
//            +
//            "LIMIT 2"
    )
    List<Map<String,Object>> findAttendedConferences(String username,int page);


    @Query("MATCH (:Conference{eventId:{0}})-[r:LOCATED_IN]-() DELETE r")
    void deleteCity(String eventId);

    @Query("MATCH (p:Person{username:{0}})-[r:PARTICIPATES]->(c:Conference) " +
            "WHERE NOT (p)-[:ATTEND]->(c) " +
            "RETURN c.eventId as id ,c.topic as topic, c.time as time, c.description as description")
    List<Map<String,Object>> conferenceParticipate(String username);

    //// TO BE CHANGED LATTER
    @Query("MATCH (:Person{username:{0}})-[r:ATTEND]-(c:conference) " +
            "RETURN c.eventId as id ,c.topic as topic, c.time as time, c.description as description")
    List<Map<String,Object>> conferenceAttend(String username);

    @Query("MATCH (:Conference{eventId:{0}})<-[:LIKES]-(:Person) RETURN count(*)")
    Integer likesByEventId(String eventId);

    @Query("MATCH (me:Person{username:{me}})-[:ATTENDED|LIKES]->(c:Conference)<-[:ORGANIZED]-(o:Person)" +
            "WHERE me.username <> o.username " +
            "WITH o, count(*) AS frequency " +
            "ORDER BY frequency DESC " +
            "MATCH (co:Conference)<-[:ORGANIZED]-(o) , (p:Person{username:{me}}) " +
            "WHERE NOT (p)-[:PARTICIPATES]->(co) AND NOT (p)-[:ATTENDED]->(co) " +

            // Changes started here
            "WITH COLLECT(DISTINCT co) as cos " +
            "UNWIND cos as con " +
            "" +
            "OPTIONAL MATCH (:People)-[:LIKES]->(con) " +
            "WITH con, count(*) AS likes " +
            "OPTIONAL MATCH (con)-[:LOCATED_IN]->(ci:City) " +
            "WITH con, ci, likes  " +
            "RETURN con.eventId AS id, con.topic AS topic, con.description AS description, " +
            "       con.time as time, ci.name as town, likes " +
            "ORDER BY time DESC")
    List<Map<String,Object>> recommendFromMostAttendedOrganizer(@Param("me")String me);

    @Query("MATCH (p:Person)-[:LIKES]->(:Conference{eventId:{eventId}}) " +
            "OPTIONAL MATCH (p)-[:CURRENT_TOWN]->(ci:City) " +
            "RETURN p.username as username, p.name as name, ci.name as town, p.photo as photo")
    List<Map<String,Object>> conferenceLikedBy(@Param("eventId")String eventId);

    @Query("MATCH (p:Person)-[:ATTENDED]->(:Conference{eventId:{eventId}}) " +
            "OPTIONAL MATCH (p)-[:CURRENT_TOWN]->(ci:City) " +
            "RETURN p.username as username, p.name as name, ci.name as town, p.photo as photo")
    List<Map<String,Object>> conferenceAttendedBy(@Param("eventId")String eventId);

    @Query("MATCH (p:Person)-[:LIKES]->(:Conference{eventId:{eventId}}) " +
            "RETURN count(*)")
    Integer numberOfPeopleLiked(@Param("eventId") String eventId);

    @Query("MATCH (me:Person{username:{me}})-[:CURRENT_TOWN]->(ci:City)<-[:LOCATED_IN]-(c:Conference) " +
            "WHERE NOT (me)-[:ORGANIZED]->(c) " +
            "WITH c " +
            "OPTIONAL MATCH (c)<-[:LIKES]-(:Person) " +
            "WITH c, count(*) as likes " +
            "OPTIONAL MATCH (ci:City)<-[:LOCATED_IN]-(c) " +
            "RETURN c.eventId AS id, c.topic AS topic, c.description AS description, c.time AS time, likes, ci.name AS town " +
            "ORDER BY likes DESC ")
    List<Map<String,Object>> recommendFromMostPopular(@Param("me")String me);



    @Query("MATCH (me:Person{username:{me}}), (c:Conference) " +
            "WHERE NOT (me)-[:ORGANIZED|PARTICIPATES|ATTENDED]->(c)   " +
            "WITH c " +
            "OPTIONAL MATCH (c)<-[:LIKES]-(:Person) " +
            "WITH c, count(*) as likes " +
            "OPTIONAL MATCH (ci:City)<-[:LOCATED_IN]-(c) " +
            "RETURN c.eventId AS id, c.topic AS topic, c.description AS description, c.time AS time, likes, ci.name AS town " +
            "ORDER BY time DESC ")
    List<Map<String,Object>> recommendLatest(@Param("me")String me);


}
