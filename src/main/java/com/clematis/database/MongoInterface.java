package com.clematis.database;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

public class MongoInterface{
	public static DB db;
	public static Set<String> colls;
	public static MongoClient mongoClient;
	
	public MongoInterface(){
		try{
			mongoClient = new MongoClient( "localhost" , 27017 );
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
	

	public static Double newSessionDocument(String userName, String URL){
		
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
		       //BasicDBObject sess = new BasicDBObject("userName", userName).append("sessionNumber", sessionNumber);
		       //db.getCollection("user").insert(sess); 
		       
		       
		       //add to the array in the user's document - user cursor
		       BasicDBObject carrier = new BasicDBObject();
		       BasicDBObject queryObject = new BasicDBObject();
		       queryObject.put("YOUR_QUERY_STRING", query);

		       BasicDBObject set = new BasicDBObject("$set", carrier);
		       carrier.put("sessionIDs", numSessions);     
		       db.getCollection("users").update(query, set);
		       
		       //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		       DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d yyyy HH:mm:ss");
		       Date date = new Date();
		       //System.out.println(dateFormat.format(date));
		       
		       //create session information document
		       BasicDBObject sessionInformation = new BasicDBObject("username", userName).append("sessionNumber", sessionNumber)
		    		   .append("sessionName", "Session " + sessionNumber).append("Date",dateFormat.format(date))
		    		   .append("URL", URL);
		     
		       db.getCollection("sessionInfo").insert(sessionInformation);
		   }
		} finally{
		   cursor.close(); 
		   
		}
		return sessionNumber;
		
	}
	
	public static void newTracer(String userName, Double sessionNum){
		//JSONArray points = new JSONArray();
		//String p = points.toString();
		DBCollection coll = db.getCollection("tracers");
		BasicDBObject tracer = new BasicDBObject("username", userName).append("sessionNumber", sessionNum).append("points", "{}");
	    coll.insert(tracer);
	}
	
	public static void updateTracerOutput(String userName, Double sessionNum, String output){
		BasicDBObject query = new BasicDBObject("username", userName).append("sessionNumber", sessionNum);
		
		BasicDBObject carrier = new BasicDBObject();
	    BasicDBObject queryObject = new BasicDBObject();
	    queryObject.put("YOUR_QUERY_STRING", query);

	    BasicDBObject set = new BasicDBObject("$set", carrier);
	    carrier.put("outputString", output);     
	    db.getCollection("tracers").update(query, set);
	}
	
	public static void updateTracerCounter(String userName, Double sessionNum, int counter){
		BasicDBObject query = new BasicDBObject("username", userName).append("sessionNumber", sessionNum);
		
		BasicDBObject carrier = new BasicDBObject();
	    BasicDBObject queryObject = new BasicDBObject();
	    queryObject.put("YOUR_QUERY_STRING", query);

	    BasicDBObject set = new BasicDBObject("$set", carrier);
	    carrier.put("counter", counter);     
	    db.getCollection("tracers").update(query, set);
	}
	
	public static void updateTracerPoints(String userName, Double sessionNum, String points){
		BasicDBObject query = new BasicDBObject("username", userName).append("sessionNumber", sessionNum);
		
		BasicDBObject carrier = new BasicDBObject();
	    BasicDBObject queryObject = new BasicDBObject();
	    queryObject.put("YOUR_QUERY_STRING", query);

	    BasicDBObject set = new BasicDBObject("$set", carrier);
	    carrier.put("points", points);     
	    db.getCollection("tracers").update(query, set);
	}
	
	public static int getTracerCounter(String userName, Double sessionNum){
		DBCollection coll = db.getCollection("tracers");
		BasicDBObject query = new BasicDBObject("username", userName).append("sessionNumber", sessionNum);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject counterObject = cursor.next();
			   Integer counter = (Integer) counterObject.get("counter");
			   //System.out.println("STORY "+ storyObject); 
			   int count = (int) counter;
			   return count;
		   }
			
		}finally{
			cursor.close();
		}
		return 0;
	}
	
	public static String getTracerOutput(String userName, Double sessionNum){
		DBCollection coll = db.getCollection("tracers");
		BasicDBObject query = new BasicDBObject("username", userName).append("sessionNumber", sessionNum);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject outputObject = cursor.next();
			   String output = (String) outputObject.get("outputString");
			   //System.out.println("STORY "+ storyObject); 
			   
			   return output;
		   }
			
		}finally{
			cursor.close();
		}
		return null;
	}
	
	public static String getTracerPoints(String userName, Double sessionNum){
		DBCollection coll = db.getCollection("tracers");
		BasicDBObject query = new BasicDBObject("username", userName).append("sessionNumber", sessionNum);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject pointsObject = cursor.next();
			   String points = (String) pointsObject.get("points");
			   //System.out.println("POINTS "+ points); 
			   
			   return points;
		   }
			
		}finally{
			cursor.close();
		}
		return null;
	}
	
	public static void newUser(String userName, String password){
		
		DBCollection coll = db.getCollection("users");
		Double[] array = new Double[0];
		BasicDBObject user = new BasicDBObject("userName", userName).append("password", password).append("lastURL", "").append("sessionIDs",array).append("toolbarPosition", null).append("areWeRecording", false);
	    db.getCollection("users").insert(user); 
	    
	}
	
	public static void newGuestUser(Double guestNum){

		Double[] array = new Double[0];
		BasicDBObject user = new BasicDBObject("userName", "guest"+guestNum).append("guestNum", guestNum).append("password", "1234").append("sessionIDs",array).append("toolbarPosition", null).append("areWeRecording", false);
	    db.getCollection("users").insert(user); 
	    
	}
	
	public static void changelastURL(String userName, String url){
		
		BasicDBObject query = new BasicDBObject("userName", userName);
		
		BasicDBObject carrier = new BasicDBObject();
	    BasicDBObject queryObject = new BasicDBObject();
	    queryObject.put("YOUR_QUERY_STRING", query);

	    BasicDBObject set = new BasicDBObject("$set", carrier);
	    carrier.put("lastURL", url);     
	    db.getCollection("users").update(query, set);
	}
	
	public static String getLastURL(String userName){
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("userName", userName);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject urlObject = cursor.next();
			   String url = (String) urlObject.get("lastURL");
			   //System.out.println("POINTS "+ points); 
			   
			   return url;
		   }
			
		}finally{
			cursor.close();
		}
		return null;
	}
	
	public static Double getLastGuestUser(){
		DBCollection coll = db.getCollection("users");
		
		BasicDBObject regexQuery = new BasicDBObject();
		regexQuery.put("userName", 
			new BasicDBObject("$regex", "Guest.*")
			.append("$options", "i"));
	 
		System.out.println(regexQuery.toString());
	 
		DBCursor cursor = coll.find(regexQuery).sort(new BasicDBObject("guestNum",-1)).limit(1);

		try {
			while(cursor.hasNext()) {
			   DBObject guestObject = cursor.next();
			   String guest = (String) guestObject.get("userName");
			   Double number = (Double) guestObject.get("guestNum");
			   System.out.println("GUEST: " + guest + " Number: " + number); 
			   
			   return number;
		   }
			
		}finally{
			cursor.close();
		}
		return null;
	}
	
	public static void changeToolbarPosition(String userName, JSONObject toolbarPosition){
		
		String toolPos = toolbarPosition.toString();
		
		BasicDBObject query = new BasicDBObject("userName", userName);
		
		BasicDBObject carrier = new BasicDBObject();
	    BasicDBObject queryObject = new BasicDBObject();
	    queryObject.put("YOUR_QUERY_STRING", query);

	    BasicDBObject set = new BasicDBObject("$set", carrier);
	    carrier.put("toolbarPosition", toolPos);     
	    db.getCollection("users").update(query, set);
	}
	
	public static String getToolbarPosition(String userName){
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("userName", userName);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject toolbarObject = cursor.next();
			   String toolbarPosition = (String) toolbarObject.get("toolbarPosition");
			   //System.out.println("POINTS "+ points); 
			   
			   return toolbarPosition;
		   }
			
		}finally{
			cursor.close();
		}
		return null;
	}
	
	public static void updateRecordingState(String userName, Boolean recordingState){
		
		BasicDBObject query = new BasicDBObject("userName", userName);
		
		BasicDBObject carrier = new BasicDBObject();
	    BasicDBObject queryObject = new BasicDBObject();
	    queryObject.put("YOUR_QUERY_STRING", query);
	    
	    BasicDBObject set = new BasicDBObject("$set", carrier);
	    carrier.put("areWeRecording", recordingState);     
	    db.getCollection("users").update(query, set);
	}
	
	public static Boolean getRecordingState(String userName){
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("userName", userName);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject recordingObject = cursor.next();
			   Boolean recordingState = (Boolean) recordingObject.get("areWeRecording");
			   //System.out.println("POINTS "+ points); 
			   
			   return recordingState;
		   }
			
		}finally{
			cursor.close();
		}
		return null;
	}
	
	public static List<String> getSessInfo(String username, Double sessionNumber){
		DBCollection coll = db.getCollection("sessionInfo");
		BasicDBObject query = new BasicDBObject("username", username).append("sessionNumber", sessionNumber);
		DBCursor cursor = coll.find(query);
				
		try {
			while(cursor.hasNext()) {
			   DBObject sessionInfo = cursor.next();
			   List<String> infoList = new ArrayList<String>();
			   
			   //System.out.println("session name " + sessionInfo.get("sessionName"));
			   infoList.add((String) sessionInfo.get("sessionName"));
			   //System.out.println(sessionInfo.get("Date"));
			   infoList.add((String) sessionInfo.get("Date"));
			   //System.out.println(sessionInfo.get("URL"));
			   infoList.add((String) sessionInfo.get("URL"));
			   
			   return infoList;
		   }
			
		}finally{
			cursor.close();
		}
		
		return null;
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
			       Object last;
			       if(numSessions.size() > 0){
			    	   last = numSessions.get(numSessions.size()-1);
			       }
			       else{
			    	   last = (Double) 0.0;
			       }
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