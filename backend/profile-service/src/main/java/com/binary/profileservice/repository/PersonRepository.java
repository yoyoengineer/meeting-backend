package com.binary.profileservice.repository;

import com.binary.profileservice.model.neo4j.Person;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PersonRepository extends GraphRepository<Person> {

    @Query("MATCH (a:Person{username:{0}}),(b:Person{username:{1}})" +
            "WHERE NOT (a)-[:FRIEND_WITH]-(b) " +
            "MERGE (a)-[r:FRIEND_WITH]->(b) RETURN a.username as username")
    Person beFriend(String user0, String user1);

    @Query("MATCH (a:Person{username:{username}}), (c:Conference{eventId:{eventId}}) " +
            "MERGE (a)-[:ATTENDED]->(c)")
    void attend(@Param("username")String username, @Param("eventId")String eventId);


    @Query("MATCH (a:Person{username:{username}}), (c:Conference{eventId:{eventId}}) " +
            "MERGE (a)-[:PARTICIPATES]->(c)")
    void participate(@Param("username")String username, @Param("eventId")String eventId);


    @Query("MATCH (a:Person{username:{username}}), (c:Conference{eventId:{eventId}}) " +
            "MERGE (a)-[:LIKES]->(c)")
    void likes(@Param("username")String username, @Param("eventId")String eventId);

    @Query("MATCH (a:Person{username:{username}})-[r:LIKES]->(c:Conference{eventId:{eventId}}) " +
            "RETURN count(r) > 0 as liked")
    List<Map<String,Object>> liked(@Param("username")String username, @Param("eventId")String eventId);

    @Query("MATCH (a:Person)-[r:ATTENDED]->(c:Conference{eventId:{eventId}}) " +
            "RETURN count(r) > 0 as liked")
    List<Map<String,Object>> attendeed( @Param("eventId")String eventId);

    @Query("MATCH (:Person{username:{0}})-[r:WORKS_AT]-() DELETE r")
    void deleteCompany(String username);

    @Query("MATCH (:Person{username:{0}})-[r:HOME_TOWN]-() DELETE r")
    void deleteHomeTown(String username);

    @Query("MATCH (:Person{username:{0}})-[r:CURRENT_TOWN]-() DELETE r")
    void deleteCurrentTown(String username);
    // THE TWO LAST ARE GOOD

    @Query("MATCH (:Person{username:{0}})-[r:STUDIED_AT]-() DELETE r")
    void deleteSchool(String username);

    @Query("MATCH(:Person{username:{me}})-[r:FRIEND_WITH]-(p:Person{username:{username}}) " +
            "WITH count(p) > 0 as areFriend " +
            // The part bellow is the original query
            "MATCH (n:Person{username:{username}}) " +
            "OPTIONAL MATCH (n)-[:WORKS_AT]-(c:Company) " +
            "OPTIONAL MATCH (n)-[:STUDIED_AT]-(s:School) " +
            "OPTIONAL MATCH (n)-[:HOME_TOWN]-(h:City) " +
            "OPTIONAL MATCH (n)-[:CURRENT_TOWN]-(ct:City) " +
            "RETURN n.username as username, n.name as name, " +
            "n.aboutMe as aboutMe, " +
            "s.name as schoolName, " +
            "c.name as companyName, " +
            "n.birthday as birthday, " +
            "n.photo as photo, " +
            "h.name as homeTown, ct.name as currentTown, areFriend")
    Person findByUsername(@Param("username") String username,@Param("me") String me);


    Person findByUsername(String username, @Depth int depth);


    @Query("MATCH (me:Person{username:{me}})-[:FRIEND_WITH]-(:Person)-[:FRIEND_WITH]-(fof:Person) " +
            "WHERE me <> fof AND NOT (me)-[:FRIEND_WITH]-(fof) " +
            "WITH fof, count(*) AS frequency " +
            "ORDER BY frequency DESC " +
            "OPTIONAL MATCH (fof)-[:CURRENT_TOWN]->(ci:City) " +
            "WITH fof, frequency, ci " +
            "RETURN fof.username AS username, fof.name AS name, " +
            "       ci.name as currentTown, frequency ")
    List<Map<String,Object>> recommendFriendFromMutualFriends(@Param("me")String me);

    @Query("MATCH (me:Person{username:{me}})-[:ATTENDED|LIKES]->(:Conference)<-[:ORGANIZED]-(p:Person) " +
            "            WHERE me <> p AND NOT (me)-[:FRIEND_WITH]-(p) " +
            "            WITH p, count(*) AS frequency " +
            "            ORDER BY frequency DESC " +
            "            OPTIONAL MATCH (p)-[:CURRENT_TOWN]->(ci:City) " +
            "            WITH p, frequency, ci " +
            "            RETURN p.username AS username, p.name AS name,      ci.name as currentTown, frequency")
    List<Map<String,Object>> recommendFriendFromConferenceOrganizer(@Param("me")String me);

    @Query("MATCH (me:Person{username:{me}})-[:ATTENDED]->(:Conference)<-[:ATTENDED]-(p:Person) " +
            "WHERE me <> p AND NOT (me)-[:FRIEND_WITH]-(p) " +
            "WITH p, count(*) AS frequency " +
            "ORDER BY frequency DESC " +
            "OPTIONAL MATCH (p)-[:CURRENT_TOWN]->(ci:City) " +
            "WITH p, frequency, ci " +
            "RETURN p.username AS username, p.name AS name,      ci.name as currentTown, frequency")
    List<Map<String,Object>> recommendFriendFromConferencesAttendedTogether(@Param("me")String me);

    @Query("MATCH (me:Person{username:{me}})-[:HOME_TOWN]->(:City)<-[:HOME_TOWN]-(p:Person) " +
            "WHERE me <> p AND NOT (me)-[:FRIEND_WITH]-(p) " +
            "OPTIONAL MATCH (p)-[:CURRENT_TOWN]->(ci:City) " +
            "RETURN p.username AS username, p.name AS name, ci.name as currentTown")
    List<Map<String,Object>> recommendFriendBySameHomeTown(@Param("me")String me);

    @Query("MATCH (me:Person{username:{me}})-[:CURRENT_TOWN]->(ci:City)<-[:CURRENT_TOWN]-(p:Person) " +
            "WHERE me <> p AND NOT (me)-[:FRIEND_WITH]-(p) " +
            "RETURN p.username AS username, p.name AS name, ci.name as currentTown")
    List<Map<String,Object>> recommendFriendBySameCurrentTown(@Param("me")String me);

    @Query("MATCH (:Person{username:{me}})-[:FRIEND_WITH]-(f:Person) " +
            "OPTIONAL MATCH (f)-[:CURRENT_TOWN]->(ci:City) " +
            "RETURN f.username AS username, f.name AS name, f.photo as photo, ci.name as town " +
            "ORDER BY name ASC")
    List<Map<String,Object>> myFriends(@Param("me")String me);

}
