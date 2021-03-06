package com.face4j.facebook;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;

import com.face4j.facebook.criteria.ConnectionColumnCriteria;
import com.face4j.facebook.entity.Page;
import com.face4j.facebook.entity.Post;
import com.face4j.facebook.entity.User;
import com.face4j.facebook.entity.paging.Paging;
import com.face4j.facebook.enums.*;
import com.face4j.facebook.exception.FacebookException;
import com.face4j.facebook.fql.FqlConnection;
import com.face4j.facebook.fql.FqlPage;
import com.face4j.facebook.fql.FqlPost;
import com.face4j.facebook.fql.FqlUser;
import com.face4j.facebook.http.APICallerFactory;
import com.face4j.facebook.http.APICallerInterface;
import com.face4j.facebook.util.Constants;
import com.face4j.facebook.util.JSONToObjectTransformer;
import com.face4j.facebook.wrapper.FqlPageColumnCriteria;
import com.face4j.facebook.wrapper.FqlUserColumnCriteria;
import com.face4j.facebook.wrapper.StreamColumnCriteria;
import com.google.gson.reflect.TypeToken;

/**
 * This is the main facebook class that will have methods which return facebook data as well as
 * publish data to facebook.
 * 
 * @author Nischal Shetty - nischalshetty85@gmail.com
 */
public class Facebook implements Serializable {

	private static final long serialVersionUID = 350726728289608542L;

	Logger logger = Logger.getLogger(Facebook.class.getName());

	private OAuthAccessToken authAccessToken;

	private APICallerInterface caller = null;

	/**
	 * If only the access token is passed, then the Apache Http Client library is used for making http
	 * requests
	 * 
	 * @param authAccessToken
	 */
	public Facebook(OAuthAccessToken authAccessToken) {
		// apache http client is the default client type
		this(authAccessToken, HttpClientType.APACHE_HTTP_CLIENT);
	}

	public Facebook(OAuthAccessToken authAccessToken, HttpClientType clientType) {
		this.authAccessToken = authAccessToken;
		caller = APICallerFactory.getAPICallerInstance(clientType);
	}

	/**
	 * Returns the current user (for whom the client has been set).
	 * 
	 * @return
	 * @throws Exception
	 */
	public User getCurrentUser() throws FacebookException {
		return getUser(Constants.ME);
	}

	/**
	 * Returns a Facebook user's available info.
	 * 
	 * @param fbId
	 * @return
	 * @throws FacebookException
	 */
	public User getUser(String fbId) throws FacebookException {
		NameValuePair[] nameValuePairs = { new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()) };
		return pullData(Constants.FACEBOOK_GRAPH_URL + "/" + fbId, User.class, nameValuePairs);
	}
	
	
	/**
	 * Returns an array of facebook users for all the fb user ids passed
	 * @param fbIds
	 * @return
	 * @throws FacebookException
	 */
	public User[] getUsers(String[] fbIds) throws FacebookException {
		
		String concatenatedFbIds = StringUtils.join(fbIds, ",");
		NameValuePair[] nameValuePairs = { new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()), 
				new NameValuePair("ids",concatenatedFbIds) };
		User[] users = null;
		
		Type type = new TypeToken<Map<String, User>>(){}.getType();
		Map<String, User> userMap = pullData(Constants.FACEBOOK_GRAPH_URL+"/", type, nameValuePairs);
		
		if(userMap != null){
			users = new User[fbIds.length];
			
			int i=0;
			for(Iterator<String> iterator = userMap.keySet().iterator();iterator.hasNext();){
					users[i++] = userMap.get(iterator.next());
			}
		}
		
		return users;
	}
	
	/**
	 * Returns a facebook page's available info.
	 * 
	 * @param fbId
	 * @return
	 * @throws FacebookException
	 */
	public Page getPage(String fbId) throws FacebookException {
		NameValuePair[] nameValuePairs = { new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()) };
		return pullData(Constants.FACEBOOK_GRAPH_URL + "/" + fbId, Page.class, nameValuePairs);
	}
	
	
	/**
	 * Returns an array of facebook pages for all the fb page ids passed
	 * @param fbIds
	 * @return
	 * @throws FacebookException
	 */
	public Page[] getPages(String[] fbIds) throws FacebookException {
		
		String concatenatedFbIds = StringUtils.join(fbIds, ",");
		NameValuePair[] nameValuePairs = { new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()), 
				new NameValuePair("ids",concatenatedFbIds) };
		Page[] pages = null;
		
		Type type = new TypeToken<Map<String, Page>>(){}.getType();
		Map<String, Page> userMap = pullData(Constants.FACEBOOK_GRAPH_URL+"/", type, nameValuePairs);
		
		if(userMap != null){
			pages = new Page[fbIds.length];
			
			int i=0;
			for(Iterator<String> iterator = userMap.keySet().iterator();iterator.hasNext();){
					pages[i++] = userMap.get(iterator.next());
			}
		}
		
		return pages;
	}
	

	/**
	 * Deprecated: Use {@link #link(List)} instead
	 * Posts a link on the user's/page's wall <br>
	 * Requires the {@link Permission#PUBLISH_STREAM} permission
	 * 
	 * @param link The URL to share 
	 * @param name The name of the link (optional)
	 * @param caption The caption of the link, appears beneath the link name (optional)
	 * @param description A description of the link, appears beneath the link caption (optional)
	 * @param message The message from the user about this link(optional)
	 * @param icon A URL to the link icon that Facebook displays in the news feed (optional)
	 * @param picture A URL to the thumbnail image used in the link post (optional)
	 * @param privacy 
	 * @throws FacebookException
	 */
	/*@Deprecated
	public void postLink(String link, String name, String caption, String description, String message, String icon, String picture, Value privacy)
			throws FacebookException {
		
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		namesValues.add(new NameValuePair(Constants.LINK, link));

		if (name != null) {
			namesValues.add(new NameValuePair(Constants.NAME, name));
		}
		if (caption != null) {
			namesValues.add(new NameValuePair(Constants.CAPTION, caption));
		}
		if (description != null) {
			namesValues.add(new NameValuePair(Constants.DESCRIPTION, description));
		}
		if (message != null) {
			namesValues.add(new NameValuePair(Constants.MESSAGE, message));
		}
		if (icon != null) {
			namesValues.add(new NameValuePair(Constants.ICON, icon));
		}
		if (picture != null) {
			namesValues.add(new NameValuePair(Constants.PICTURE, picture));
		}
		if(privacy != null){
			namesValues.add(new NameValuePair(Constants.PRIVACY,"{\"value\":\""+privacy.toString()+"\"}"));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_LINK.replaceFirst("PROFILE_ID", "me"), nameValuePairs);
	}*/

	/**
	 * Posts the given url on facebook <br>
	 * Internally calls {@link Facebook#postLink(String, String, String, String, String)} with other
	 * params null. If you want more control call that method
	 * 
	 * @param link The URL to share 
	 * @throws FacebookException
	 */
/*	@Deprecated
	public void postLink(String link) throws FacebookException {
		postLink(link, null, null, null, null, null, null,Value.EVERYONE);
	}*/
	
	/**
	 * Data would be posted to the logged in users wall. Requires the publish_stream permission.
	 * 
	 * @param wallPost
	 *          The data to be posted to the logged in users wall
	 * @throws FacebookException
	 */
/*	@Deprecated
	public void post(WallPost wallPost) throws FacebookException {
		post(wallPost, Constants.ME);	
	}*/

	/**
	 * Requires the publish_stream permission.
	 * 
	 * To publish a wall post, POST the message and optional attachment to the feed/wall of the user,
	 * page or group, i.e., http://graph.facebook.com/PROFILE_ID/feed. When publishing to a Page, to
	 * target a post to users in a specific location or language, use the appropriate values from the
	 * following files: all cities (CSV) {@link http://developers.facebook.com/attachment/all_cities_final.csv}, 
	 * major cities (CSV) {@link http://developers.facebook.com/attachment/major_cities_final.csv}, 
	 * locales (CSV) {@link http://developers.facebook.com/attachment/locales_final.csv}, 
	 * cities and locales {@link http://developers.facebook.com/attachment/targeting_ids.json} 
	 * 
	 * @param wallPost
	 * @param profileId
	 * @throws FacebookException
	 */
	/*@Deprecated
	public void post(WallPost wallPost, String profileId) throws FacebookException{
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();
		Gson gson = new Gson();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		if(wallPost.getActions()!=null){
			namesValues.add(new NameValuePair(Constants.ACTIONS,gson.toJson(wallPost.getActions())));
		}
		if(wallPost.getCaption()!=null){
			namesValues.add(new NameValuePair(Constants.CAPTION,wallPost.getCaption()));
		}
		if(wallPost.getDescription()!=null){
			namesValues.add(new NameValuePair(Constants.DESCRIPTION,wallPost.getDescription()));
		}
		if(wallPost.getLink()!=null ){
			namesValues.add(new NameValuePair(Constants.LINK,wallPost.getLink()));
		}
		if(wallPost.getMessage()!=null){
			namesValues.add(new NameValuePair(Constants.MESSAGE, wallPost.getMessage()));
		}
		if(wallPost.getName()!=null){
			namesValues.add(new NameValuePair(Constants.NAME,wallPost.getName()));
		}
		if(wallPost.getPicture()!=null){
			namesValues.add(new NameValuePair(Constants.PICTURE,wallPost.getPicture()));
		}
		if(wallPost.getPrivacy()!=null){
			namesValues.add(new NameValuePair(Constants.PRIVACY,gson.toJson(wallPost.getPrivacy())));
		}
		if(wallPost.getSource()!=null){
			namesValues.add(new NameValuePair(Constants.SOURCE,wallPost.getSource()));
		}
		if(wallPost.getTargeting()!=null){
			namesValues.add(new NameValuePair(Constants.TARGETING,gson.toJson(wallPost.getPrivacy())));
		}

		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_LINK.replaceFirst("PROFILE_ID", profileId), nameValuePairs);
	}*/

	public OAuthAccessToken getAuthAccessToken() {
		return authAccessToken;
	}

	private NameValuePair getNameValuePairAccessToken() {
		return new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken());
	}
	
	/**
	 * Publish a new post on the given profile's feed/wall
	 * 
	 * @param message
	 *          (required)
	 * @param picture
	 * @param link
	 * @param name
	 * @param caption
	 * @param description
	 * @param source
	 * @param profileId
	 *          The user id of the user on whose wall the message needs to be posted, if null then the
	 *          post would be posted on the authenticated users wall. If it's a page then page id.
	 * @throws FacebookException 
	 */
	public void wallPost(String message, String picture, String link, String name, String caption, String description, String source, String profileId) throws FacebookException{

		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));

		if(profileId == null){
			profileId = Constants.ME;
		}
		
		if(message !=null){
			namesValues.add(new NameValuePair(Constants.MESSAGE,message));
		}
		
		if(picture != null){
			namesValues.add(new NameValuePair(Constants.PICTURE,picture));
		}
		
		if(link != null){
			namesValues.add(new NameValuePair(Constants.LINK,link));
		}
		
		if(name != null){
			namesValues.add(new NameValuePair(Constants.NAME,name));
		}
		
		if(caption != null){
			namesValues.add(new NameValuePair(Constants.CAPTION,caption));
		}
		
		if(description != null){
			namesValues.add(new NameValuePair(Constants.DESCRIPTION,description));
		}
		
		if(source != null){
			namesValues.add(new NameValuePair(Constants.SOURCE,source));
		}

		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_FEED.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	
	}
	
	/**
	 * Publish a link on the given profile
	 * @param link (mandatory)
	 * @param message
	 * @param picture
	 * @param name
	 * @param caption
	 * @param description
	 * @param profileId user id or page id
	 * @throws FacebookException
	 */
	public void shareLink(String link, String message, String picture, String name, String caption, String description, String profileId) throws FacebookException{

		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));

		if(profileId == null){
			profileId = Constants.ME;
		}
		
		if(message !=null){
			namesValues.add(new NameValuePair(Constants.MESSAGE,message));
		}
		
		if(picture != null){
			namesValues.add(new NameValuePair(Constants.PICTURE,picture));
		}
		
		if(link != null){
			namesValues.add(new NameValuePair(Constants.LINK,link));
		}
		
		if(name != null){
			namesValues.add(new NameValuePair(Constants.NAME,name));
		}
		
		if(caption != null){
			namesValues.add(new NameValuePair(Constants.CAPTION,caption));
		}
		
		if(description != null){
			namesValues.add(new NameValuePair(Constants.DESCRIPTION,description));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_LINK.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	
	}
	
	/**
	 * Comment on the given object (if it has a /comments connection)
	 * @param message
	 * @param objectId
	 * 
	 * @return The new ID of the comment
	 * 
	 * @throws FacebookException
	 */
	public CommonReturnObject comment(String message, String objectId) throws FacebookException{
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		namesValues.add(new NameValuePair(Constants.MESSAGE,message));
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		String response =  caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_COMMENTS.replaceFirst(Constants.REPLACE_OBJECT_ID, objectId), nameValuePairs);
		
		return JSONToObjectTransformer.getObject(response, CommonReturnObject.class);
	}
	
	/**
	 * 
	 * @param objectId
	 * @throws FacebookException
	 */
	public void like(String objectId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_LIKES.replaceFirst(Constants.REPLACE_OBJECT_ID, objectId), nameValuePairs);
	}
	
	/**
	 * Publish a note on the given profile
	 * @param message
	 * @param subject
	 * @param profileId
	 * @throws FacebookException
	 */
	public void createNote(String message, String subject, String profileId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		if(message != null){
			namesValues.add(new NameValuePair(Constants.MESSAGE,message));
		}
		
		if(subject != null){
			namesValues.add(new NameValuePair(Constants.SUBJECT, subject));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_NOTES.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	}
	
	/**
	 * Create an event
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @param profileId If for a user then the users id, if for a page then the page id
	 * @throws FacebookException
	 */
	public void createEvent(String name, String startTime, String endTime, String profileId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		if(profileId == null){
			profileId = Constants.ME;
		}
		
		if(name != null){
			namesValues.add(new NameValuePair(Constants.NAME,name));
		}
		
		if(startTime != null){
			namesValues.add(new NameValuePair(Constants.START_TIME, startTime));
		}
		
		if(endTime != null){
			namesValues.add(new NameValuePair(Constants.END_TIME, endTime));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_EVENTS.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	}
	
	/**
	 * RSVP "attending" to the given event
	 * @param eventId
	 * @throws FacebookException
	 */
	public void eventAttending(String eventId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_ATTENDING.replaceFirst(Constants.REPLACE_EVENT_ID, eventId), nameValuePairs);
	}
	
	/**
	 * RSVP "maybe" to the given event
	 * @param eventId
	 * @throws FacebookException
	 */
	public void eventMaybe(String eventId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_MAYBE.replaceFirst(Constants.REPLACE_EVENT_ID, eventId), nameValuePairs);
	}
	
	/**
	 * RSVP "declined" to the given event
	 * @param eventId
	 * @throws FacebookException
	 */
	public void eventDeclined(String eventId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_DECLINED.replaceFirst(Constants.REPLACE_EVENT_ID, eventId), nameValuePairs);
	}
	
	/**
	 * Create an album
	 * @param name
	 * @param message
	 * @param profileId
	 * @throws FacebookException
	 */
	public void createAlbum(String name, String message, String profileId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		if(profileId == null){
			profileId = Constants.ME;
		}
		
		if(name != null){
			namesValues.add(new NameValuePair(Constants.NAME,name));
		}
		
		if(message != null){
			namesValues.add(new NameValuePair(Constants.MESSAGE, message));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_ALBUMS.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	}
	
	//TODO: Upload photo
	/*public void photos(String name, String message, String profileId) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		if(profileId == null){
			profileId = Constants.ME;
		}
		
		if(name != null){
			namesValues.add(new NameValuePair(Constants.NAME,name));
		}
		
		if(message != null){
			namesValues.add(new NameValuePair(Constants.MESSAGE, message));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_EVENTS.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	}*/
	
	//TODO: Publish Checkins
	/*public void checkins(String coordinates, String place, String message, String tags) throws FacebookException {
		List<NameValuePair> namesValues = new ArrayList<NameValuePair>();

		namesValues.add(new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()));
		
		if(profileId == null){
			profileId = Constants.ME;
		}
		
		if(name != null){
			namesValues.add(new NameValuePair(Constants.NAME,name));
		}
		
		if(message != null){
			namesValues.add(new NameValuePair(Constants.MESSAGE, message));
		}
		
		NameValuePair[] nameValuePairs = new NameValuePair[namesValues.size()]; 
		namesValues.toArray(nameValuePairs);
		caller.postData(Constants.FACEBOOK_GRAPH_URL + Constants.POST_CHECKINS.replaceFirst(Constants.REPLACE_PROFILE_ID, profileId), nameValuePairs);
	}*/

	public FqlPost[] newsFeed() throws FacebookException {

		List<StreamColumn> columnNames = new ArrayList<StreamColumn>();

		columnNames.add(StreamColumn.POST_ID);
		columnNames.add(StreamColumn.ACTOR_ID);
		columnNames.add(StreamColumn.TARGET_ID);
		columnNames.add(StreamColumn.VIEWER_ID);
		columnNames.add(StreamColumn.SOURCE_ID);
		columnNames.add(StreamColumn.MESSAGE);
		columnNames.add(StreamColumn.ATTACHMENT);
		columnNames.add(StreamColumn.UPDATED_TIME);
		columnNames.add(StreamColumn.CREATED_TIME);
		columnNames.add(StreamColumn.ATTRIBUTION);
		columnNames.add(StreamColumn.COMMENTS);
		columnNames.add(StreamColumn.LIKES);
		columnNames.add(StreamColumn.PERMALINK);

		return newsFeed(columnNames, null);
	}

	public FqlPost[] newsFeed(List<StreamColumn> columnNames, StreamColumnCriteria columnCriteria)
			throws FacebookException {

		//StringBuilder criteria = constructCriteria(columnCriteria);
		StringBuilder columnName = appendColumns(columnNames);

		String fqlQuery = "SELECT "
			+ columnName.toString()
			+ " FROM stream WHERE filter_key in (SELECT filter_key FROM stream_filter WHERE uid=me() AND type='newsfeed') ";
		
		if(columnCriteria!=null){
			fqlQuery += columnCriteria.toString();
		}
		
		NameValuePair[] nameValuePairs = { getNameValuePairAccessToken(), new NameValuePair("query", fqlQuery),
				new NameValuePair("format", "JSON") };

		String jsonResponse = caller.getData("https://api.facebook.com/method/fql.query", nameValuePairs);

		// fql currently sends empty arrays with {} but we need []
		jsonResponse = jsonResponse.replaceAll("\\{\\}", "[]");

		FqlPost[] fqlPosts = JSONToObjectTransformer.getObject(jsonResponse, FqlPost[].class);

		return fqlPosts;
	}
	
	/**
	 * Retrieve an array of users with the fields that you specify. Uses the Fql API
	 * @param columnNames
	 * @param columnCriteria
	 * @return
	 * @throws FacebookException
	 */
	public FqlUser[] fqlUsers(List<FqlUserColumn> columnNames, FqlUserColumnCriteria columnCriteria) throws FacebookException {
		
		StringBuilder columnName = appendColumns(columnNames);
		
		String fqlQuery = "SELECT "
			+ columnName.toString()
			+ " FROM user WHERE "
			+ columnCriteria.toString();
		
		NameValuePair[] nameValuePairs = { getNameValuePairAccessToken(), new NameValuePair("query", fqlQuery),
				new NameValuePair("format", "JSON") };
		
		String jsonResponse = caller.getData("https://api.facebook.com/method/fql.query", nameValuePairs);
		
		// fql currently sends empty arrays with {} but we need []
		jsonResponse = jsonResponse.replaceAll("\\{\\}", "[]");
		
		FqlUser[] fqlUsers = JSONToObjectTransformer.getObject(jsonResponse, FqlUser[].class);
		
		return fqlUsers;
	}

	public FqlPage[] fqlPages(List<FqlPageColumn> columnNames, FqlPageColumnCriteria columnCriteria) throws FacebookException {
	
		StringBuilder columnName = appendColumns(columnNames);
		
		String fqlQuery = "SELECT "
			+ columnName.toString()
			+ " FROM page WHERE "
			+ columnCriteria.toString();
		
		NameValuePair[] nameValuePairs = { getNameValuePairAccessToken(), new NameValuePair("query", fqlQuery),
				new NameValuePair("format", "JSON") };
		
		String jsonResponse = caller.getData("https://api.facebook.com/method/fql.query", nameValuePairs);
		
		// fql currently sends empty arrays with {} but we need []
		jsonResponse = jsonResponse.replaceAll("\\{\\}", "[]");
		
		FqlPage[] fqlUsers = JSONToObjectTransformer.getObject(jsonResponse, FqlPage[].class);
		return fqlUsers;
		
	}

	private <E> StringBuilder appendColumns(List<E> columnNames) {
		StringBuilder columnName = null;
		for (E column : columnNames) {
			if (columnName != null) {
				columnName.append(", " + column.toString());
			} else {
				columnName = new StringBuilder();
				columnName.append(column.toString());
			}
		}
		return columnName;
	}

	/*private StringBuilder constructCriteria(StreamColumnCriteria columnCriteria) {

		StringBuilder criteria = new StringBuilder();

		if (columnCriteria != null) {

			if (columnCriteria.isDefaultXid()) {
				criteria.append(" AND " + StreamColumn.XID.toString() + " = 'default'");
			} else if (columnCriteria.getXid() != null) {
				criteria.append(" AND " + StreamColumn.XID.toString() + " = " + columnCriteria.getXid());
			}

			if (columnCriteria.getActorId() != null) {
				criteria.append(" AND " + StreamColumn.ACTOR_ID.toString() + " = '" + columnCriteria.getActorId() + "'");
			}

			if (columnCriteria.getAppId() != null) {
				criteria.append(" AND " + StreamColumn.APP_ID.toString() + " = " + columnCriteria.getAppId());
			}

			if (columnCriteria.getAttribution() != null) {
				criteria.append(" AND " + StreamColumn.ATTRIBUTION.toString() + " = '" + columnCriteria.getAttribution() + "'");
			}

			if (columnCriteria.getCreatedTimeGreaterThan() != null) {
				criteria.append(" AND " + StreamColumn.CREATED_TIME.toString() + " > "
						+ columnCriteria.getCreatedTimeGreaterThan());
			}

			if (columnCriteria.getCreatedTimeLessThan() != null) {
				criteria.append(" AND " + StreamColumn.CREATED_TIME.toString() + " < "
						+ columnCriteria.getCreatedTimeLessThan());
			}

			if (columnCriteria.getFilterKey() != null) {
				criteria.append(" AND " + StreamColumn.FILTER_KEY.toString() + " = '" + columnCriteria.getFilterKey() + "'");
			}

			if (columnCriteria.getPostId() != null) {
				criteria.append(" AND " + StreamColumn.POST_ID.toString() + " = '" + columnCriteria.getPostId() + "'");
			}

			if (columnCriteria.getSourceId() != null) {
				criteria.append(" AND " + StreamColumn.SOURCE_ID.toString() + " = " + columnCriteria.getSourceId());
			}

			if (columnCriteria.getTargetId() != null) {
				criteria.append(" AND " + StreamColumn.TARGET_ID.toString() + " = '" + columnCriteria.getTargetId() + "'");
			}

			if (columnCriteria.getUpdatedTimeGreaterThan() != null) {
				criteria.append(" AND " + StreamColumn.UPDATED_TIME.toString() + " > "
						+ columnCriteria.getUpdatedTimeGreaterThan());
			}

			if (columnCriteria.getUpdatedTimeLessThan() != null) {
				criteria.append(" AND " + StreamColumn.UPDATED_TIME.toString() + " < "
						+ columnCriteria.getUpdatedTimeLessThan());
			}

			if (columnCriteria.getViewerId() != null) {
				criteria.append(" AND " + StreamColumn.VIEWER_ID.toString() + " = " + columnCriteria.getViewerId());
			}

			// We will always be passing this param
			// TODO: Do we need to always set this?
			if (columnCriteria.isShowHidden()) {
				criteria.append(" AND " + isHidden + " = 0 ");
			} else {
				// criteria.append(" AND " + isHidden + " = 1 ");
			}

			// This should be in the end
			if (columnCriteria.getLimit() != null) {
				criteria.append(" LIMIT " + columnCriteria.getLimit());
			}

		}

		return criteria;
	}*/
	
	public FqlConnection[] getConnection(List<ConnectionColumn> columnNames, ConnectionColumnCriteria columnCriteria)throws FacebookException {

		StringBuilder criteria = constructCriteria(columnCriteria);
		StringBuilder columnName = appendConnectionColumns(columnNames);

		String fqlQuery = "SELECT "
				+ columnName.toString()
				+ " FROM connection WHERE source_id = me() AND "
				+ criteria.toString();

		NameValuePair[] nameValuePairs = { getNameValuePairAccessToken(), new NameValuePair("query", fqlQuery),
				new NameValuePair("format", "JSON") };

		String jsonResponse = caller.getData("https://api.facebook.com/method/fql.query", nameValuePairs);

		// fql currently sends empty arrays with {} but we need []
		jsonResponse = jsonResponse.replaceAll("\\{\\}", "[]");

		FqlConnection[] fqlConnection = JSONToObjectTransformer.getObject(jsonResponse, FqlConnection[].class);

		return fqlConnection;
	}
	
	private StringBuilder constructCriteria(ConnectionColumnCriteria columnCriteria) {
		StringBuilder criteria = new StringBuilder();

		if (columnCriteria != null) {
			if (columnCriteria.getTargetType() != null) {
				criteria.append(ConnectionColumn.TARGET_TYPE.toString() + " = '" + columnCriteria.getTargetType() + "'");
			}

			if (columnCriteria.getLimit() != null) {
				criteria.append(" LIMIT " + columnCriteria.getLimit());
			}
			
			if (columnCriteria.getOffset() != null){
				criteria.append(" OFFSET " + columnCriteria.getOffset());
			}
		}
		return criteria;
	}
	
	private StringBuilder appendConnectionColumns(List<ConnectionColumn> columnNames) {
		StringBuilder columnName = null;
		for (ConnectionColumn column : columnNames) {
			if (columnName != null) {
				columnName.append(", " + column.toString());
			} else {
				columnName = new StringBuilder();
				columnName.append(column.toString());
			}
		}
		return columnName;
	}

	/**
	 * Returns a Post object containing all the details of a post. <br>
	 * <b>Post</b> = An individual entry in a profile's feed. The read_stream extended permission is
	 * required to access any information in a profile's feed that is not shared with everyone.
	 * 
	 * @param postId
	 * @return
	 * @throws FacebookException
	 */
	public Post getPost(String postId) throws FacebookException{
		NameValuePair[] nameValuePairs = { new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()) };
		return pullData(Constants.FACEBOOK_GRAPH_URL + "/" + postId, Post.class, nameValuePairs);
	}

	/**
	 * Returns the appropriate object for the given connection type. Facebook objects, apart from
	 * their fields have connections such as likes, comments etc. This method will return the
	 * connections corresponding to the type that you specify
	 * 
	 * @param <E>
	 * @param id The facebook object id
	 * @param connectionType Enum, send in what the type of the connection is
	 * @param e
	 * @param pagingCriteria
	 * @return
	 * @throws FacebookException
	 */
	public <E> E getConnections(String id, ConnectionType connectionType, Class<E> e, Paging paging) throws FacebookException {

		NameValuePair[] nameValuePairs = constructNameValuePairs(paging);
		
		E t = pullData(Constants.FACEBOOK_GRAPH_URL + "/" + id + "/" + connectionType.getType(), e, nameValuePairs);
		touchPaging(t);
		
		return t;
	}
	
	public boolean delete(String id) throws FacebookException {
		NameValuePair[] nameValuePairs = constructNameValuePairs(null);
		return Boolean.parseBoolean(caller.deleteData(Constants.FACEBOOK_GRAPH_URL + "/" + id, nameValuePairs));
	}
	
	public boolean unlike(String id) throws FacebookException {
		NameValuePair[] nameValuePairs = constructNameValuePairs(null);
		return Boolean.parseBoolean(caller.deleteData(Constants.FACEBOOK_GRAPH_URL + "/" + id +"/likes", nameValuePairs));
	}
	
	/**
	 * This has been done so that we touch the paging object inside the connection. Paging object contains private fields which need to be modfied.
	 * The private fields get modified on calling any of the getters. This is needed because gson directly calls private fields. It does not call the
	 * getters. Since the private fields depend on the getters, we are touching the getter so that private fields get initialized accordingly.
	 * 
	 * @param <E>
	 * @param e
	 */
	private <E> void touchPaging(E e) {
		try {
			Class thisClass = Class.forName(e.getClass().getName());

			Method method = thisClass.getDeclaredMethod("getPaging");

			if (method != null) {
				Object paging = method.invoke(e);

				if (paging != null) {
					Class pagingClass = Class.forName(Paging.class.getName());
					Method pagingMethod = pagingClass.getDeclaredMethod("getLimit");
					if(pagingMethod != null){
						pagingMethod.invoke(paging);
					}
				}
			}
		} catch (SecurityException e1) {
		} catch (IllegalArgumentException e1) {
		} catch (ClassNotFoundException e1) {
		} catch (NoSuchMethodException e1) {
		} catch (IllegalAccessException e1) {
		} catch (InvocationTargetException e1) {
		} catch (Exception e1) {
		}

	}
	

	private NameValuePair[] constructNameValuePairs(Paging paging) {
		int i = 1;
		NameValuePair[] nameValuePairs = null;
		
		if (paging != null) {
			i += Paging.pagingElementCount(paging);
			nameValuePairs = new NameValuePair[i];
			Paging.addNameValuePairs(paging, nameValuePairs);
		} else {
			nameValuePairs = new NameValuePair[i];
		}

		nameValuePairs[i - 1] = new NameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken());
		return nameValuePairs;
	}
	

	/**
	 * Raw API method to pull any data in json form and transform it into the right object <br>
	 * An HTTP GET method is used here
	 * 
	 * @param <E>
	 * @param url
	 * @param e The class into which the json object returned by the url fetch needs to be cast
	 * @param nameValuePairs Pass parameters that need to accompany the call
	 * @return
	 * @throws FacebookException
	 */
	public <E> E pullData(String url, Class<E> e, NameValuePair[] nameValuePairs) throws FacebookException {
		// APICaller would retrieve the json string object from facebook by making a https call
		// Once the json string object is obtaind, it is passed to obj transformer and the right object
		// is retrieved
		return JSONToObjectTransformer.getObject(caller.getData(url, nameValuePairs), e);
	}
	
	/**
	 * This method is useful when your json contains maps (key value pairs). Send in parameterized maps.<br>
	 * Example: Type type = new TypeToken<Map<String, User>>(){}.getType();
	 * @param <E>
	 * @param url
	 * @param type
	 * @param nameValuePairs
	 * @return
	 * @throws FacebookException
	 */
	public <E> E pullData(String url, Type type, NameValuePair[] nameValuePairs) throws FacebookException {
		// APICaller would retrieve the json string object from facebook by making a https call
		// Once the json string object is obtaind, it is passed to obj transformer and the right object
		// is retrieved
		return JSONToObjectTransformer.<E>getObject(caller.getData(url, nameValuePairs), type);
	}
	

}