package com.clematis.database;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoInterface{
	public static DB db;
	public static Set<String> colls;
	
	public MongoInterface(){
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			db = mongoClient.getDB( "ClematisUsers" );
			
			colls = db.getCollectionNames();
			/*System.out.println("Collections:");
			for (String s : colls) {
			    System.out.println(s);
			}*/
			
		} catch(UnknownHostException e){
			System.out.println("Could not connect to MongoDB");
		}
	}

	public static Double newSessionDocument(String userName){
		
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("userName", userName);
		DBCursor cursor = coll.find(query);
		Double sessionNumber = 1.0;

		try {
		   while(cursor.hasNext()) {
			   DBObject user = cursor.next();
		       System.out.println(user);
		       
		       BasicDBList numSessions = (BasicDBList) user.get("sessionIDs");
		       //System.out.println(numSessions);
		       
		       //just to see individual contents of array
		       /*for(int index = 0; index < numSessions.size(); index++){
		    		   Object a = numSessions.get(index);
		    		   System.out.println(a);
		       }*/
		       
		       Object last;
		       //last element in array
		       if(numSessions.size() < 1){
		    	   last = 0.0;
		       }
		       else {
		    	   last = numSessions.get(numSessions.size()-1);
		    	   
		       }
		       Double lastElem = (Double) last;
		       
		       sessionNumber = lastElem + 1;
		       numSessions.add(numSessions.size(), sessionNumber);
		       System.out.println("SESSION NUMBER: " + sessionNumber);
		       
		       
		       //create new session document
		       BasicDBObject sess = new BasicDBObject("userName", userName).append("sessionNumber", sessionNumber);
		       db.getCollection("user").insert(sess); 
		       
		       
		       //add to the array in the user's document - user cursor
		       BasicDBObject carrier = new BasicDBObject();
		       BasicDBObject queryObject = new BasicDBObject();
		       queryObject.put("YOUR_QUERY_STRING", query);

		       BasicDBObject set = new BasicDBObject("$set", carrier);
		       carrier.put("sessionIDs", numSessions);     
		       db.getCollection("users").update(query, set);
		     
		   }
		} finally{
		   cursor.close(); 
		   
		}
		return sessionNumber;
		
	}
	
	public static void checkUser(String userName){
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("userName", userName);
		DBCursor cursor = coll.find(query);
		
		if (!cursor.hasNext()){
			System.out.println("new user");
			//CHANGE HARD CODED PASSWORD - MONGO REALM
			BasicDBList sessionArray = new BasicDBList(); 
			BasicDBObject doc = new BasicDBObject("userName", userName).append("password", "password").append("sessionIDs", sessionArray);
        	MongoInterface.db.getCollection("users").insert(doc);
		}
		
	}
	
	public static String getStoryAsString(String userName, Double sessionNum){
		//MongoInterface.db.getCollection("story").findOne().get("story").toString()
		
		DBCollection coll = db.getCollection("story");
		BasicDBObject query = new BasicDBObject("userName", userName).append("sessionNumber", sessionNum);
		DBCursor cursor = coll.find(query);
		
		try {
			   if(cursor.hasNext()) {
				   DBObject storyObject = cursor.next();
				   String storyString = (String) storyObject.get("story");
				   //System.out.println("STORY "+ storyObject); 
				   return storyString;
			   }
			   
		}finally{
			cursor.close();
		}
		
		return null;
	}
	
	public static Double getLastSessionNumber(String userName){
		
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("userName", userName);
		DBCursor cursor = coll.find(query);
		Double lastElem = 1.0;
		
		try {
			   while(cursor.hasNext()) {
				   DBObject user = cursor.next();   
			       BasicDBList numSessions = (BasicDBList) user.get("sessionIDs");

			       Object last = numSessions.get(numSessions.size()-1);
			       lastElem = (Double) last;

			       //System.out.println("LAST SESSION NUMBER: " + lastElem);
			   }
		} finally{
			cursor.close();
		}
		
		return lastElem;
	}
	
	//returns true if a document already exists for the session and user
	public static boolean checkNumDocumentExistsForSession(String userName, String collection){
		DBCollection coll = db.getCollection(collection);
		BasicDBObject query = new BasicDBObject("userName", userName).append("sessionNumber", getLastSessionNumber(userName));
		DBCursor cursor = coll.find(query);
		
		try {
			   if(cursor.hasNext()) {
				   return true;
			   }
		} finally{
			cursor.close();
		}
		
		
		return false;
	}
	
	public static String getAllEpisodesAsString(String userName, Double sessionNum){
		DBCollection coll = db.getCollection("allEpisodes");
		BasicDBObject query = new BasicDBObject("userName", userName).append("sessionNumber", sessionNum);
		DBCursor cursor = coll.find(query);
		
		try {
			   if(cursor.hasNext()) {
				   DBObject storyObject = cursor.next();
				   String episodeString = (String) storyObject.get("allEpisodes");
				   //System.out.println("STORY "+ storyObject);
				   return episodeString;
			   }
			   
		}finally{
			cursor.close();
		}
		
		return null;

	}
	
}