package com.clematis.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoRealm extends AuthorizingRealm {

    private MongoClient mongoClient = null; 
    public DB db;

    public MongoRealm(){
    	MongoInterface mongo = new MongoInterface();
    	this.mongoClient = mongo.mongoClient;
    	this.db = mongo.db;
    	//System.out.println("db : " + db.getName());
    }
    
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo( AuthenticationToken authcToken ) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String username = token.getUsername();
		
		//System.out.println("doGetAuthenticationInfo username: " + username);
		
		//check that user exists in database?
		if (username == null) {
			System.out.println("Username is null.");
			return null;
		}
		
		PasswdSalt passwdSalt = getPasswordForUser(username);
		
		if (passwdSalt == null) {
			System.out.println("No account found for user [" + username + "]");
			return null;
		}
		
		// return salted credentials
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, passwdSalt.password, getName());
		//info.setCredentialsSalt(new SimpleByteSource(passwdSalt.salt));
		
		return info;
	}
	
	private PasswdSalt getPasswordForUser(String username) {
		
		DBCollection coll = db.getCollection("users");	
		BasicDBObject query = new BasicDBObject("userName", username);
		DBCursor cursor = coll.find(query);
		String password = null;
		
		try {
			if (cursor.hasNext()){
				DBObject sessionInfo = cursor.next();
				List<String> userList = new ArrayList<String>();
				   
				userList.add((String) sessionInfo.get("userName"));
				userList.add((String) sessionInfo.get("password"));
				password = userList.get(1);
			}
			else{
				System.out.println("does not hasAccount");
				return null;
			}

			if (cursor.hasNext()) {
				throw new AuthenticationException("More than one user row found for user [" + username + "]. Usernames must be unique.");
			}
				
		}finally{
			cursor.close();
		}
			
		String salt = null;
		//System.out.println("password: " + password);
		//if (resultSet.getMetaData().getColumnCount() > 1)
		//salt = resultSet.getString(2);

		return new PasswdSalt(password, salt);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		// TODO Auto-generated method stub
		return null;
	} 

}

class PasswdSalt {

	public String password;
	public String salt;

	public PasswdSalt(String password, String salt) {
		super();
		this.password = password;
		this.salt = salt;
	}

}