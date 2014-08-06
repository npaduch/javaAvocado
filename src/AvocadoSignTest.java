import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AvocadoSignTest {
	static final String API_URL_BASE = "https://avocado.io/api/";
	static final String API_URL_LOGIN = API_URL_BASE + "authentication/login";
	static final String API_URL_COUPLE = API_URL_BASE + "couple";
	static final String API_URL_CONVERSATION = API_URL_BASE + "conversation";
	static final String API_URL_SEND = API_URL_BASE + "conversation";
	static final String COOKIE_NAME = "user_email";
	static final String USER_AGENT = "Avocado Test Api Client v.1.0";
	static final String ERROR_MSG = "\nFAILED.  Signature was tested and failed. " +
			"Try again and check the auth information.";


	public AvocadoSignTest() {}

	public class AvocadoAPI {
		private final AuthClient authClient;
		private String coupleData = null;
		private Map<String, String> messages = new HashMap<String, String>();
		private String user = null;
		private String other = null;
		private String userId = null;
		private String otherId = null;
		

		public AvocadoAPI(AuthClient authClient) {
			this.authClient = authClient;
		}

		public void updateFromCommandLine() {
			this.authClient.updateFromCommandLine();

			this.authClient.updateSignature();
			if (this.authClient.getSignature() == null) {
				System.out.println(ERROR_MSG);
				return;
			}

			this.updateCouple();
			if (this.coupleData == null) {
				System.out.println(ERROR_MSG);
			} else {
				System.out.println("\nBelow is your Avocado API signature:");
				System.out.println(this.authClient.getSignature());
			}
		}

		public boolean updateCouple() {
			URL url = null;
			try {
				url = new URL(API_URL_COUPLE);
			} catch(MalformedURLException e) {
				System.out.println(e.toString());
				return false;
			} catch(IOException e) {
				System.out.println(e.toString());
				return false;
			}

			boolean isValid = false;

			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Cookie", COOKIE_NAME + "=" + this.authClient.getCookie());
				connection.setRequestProperty("X-AvoSig", this.authClient.getSignature());
				connection.setRequestProperty("User-Agent", USER_AGENT);
				connection.connect();

				int responseCode = connection.getResponseCode();
				if (responseCode == 200) isValid = true;


				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String strLine = "";
				String total = "";
				while((strLine = in.readLine()) != null){
					total+= strLine;
				}
				
				JSONTokener jTokener = new JSONTokener(total);
				JSONObject jObject = (JSONObject)jTokener.nextValue();
				this.user = (String) ((JSONObject)jObject.get("currentUser")).get("firstName");
				this.other = (String) ((JSONObject)jObject.get("otherUser")).get("firstName");
				this.userId = (String) ((JSONObject)jObject.get("currentUser")).get("id");
				this.otherId = (String) ((JSONObject)jObject.get("otherUser")).get("id");
				System.out.println(this.user);
				System.out.println(this.other);
				
			} catch(IOException e) {
				System.out.println(e.toString());

			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				if (isValid) {
					this.coupleData = "{}"; // TODO: Extract the response body for realz.
				}
				return isValid;
			}
		}

		public String getMessages() {
			URL url = null;
			try {
				url = new URL(API_URL_CONVERSATION);
			} catch(MalformedURLException e) {
				System.out.println(e.toString());
				return e.toString();
			} catch(IOException e) {
				System.out.println(e.toString());
				return e.toString();
			}

			boolean isValid = false;
			String updated = "";

			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Cookie", COOKIE_NAME + "=" + this.authClient.getCookie());
				connection.setRequestProperty("X-AvoSig", this.authClient.getSignature());
				connection.setRequestProperty("User-Agent", USER_AGENT);
				connection.connect();

				int responseCode = connection.getResponseCode();
				if (responseCode == 200) isValid = true;
				
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String strLine = "";
				String total = "";
				while((strLine = in.readLine()) != null){
					total+= strLine;
				}
				JSONTokener jTokener = new JSONTokener(total);
				JSONArray jArray = new JSONArray(jTokener);
				for(int i=0;i<jArray.length();i++){
					if(!messages.containsKey(((JSONObject) (jArray.get(i))).get("id"))){
						if(getName((String)((JSONObject) (jArray.get(i))).get("userId")).equals(this.user)){
							String tempString = "<b><font color = \"#00CCFF\">"+
									getName((String)((JSONObject) (jArray.get(i))).get("userId"))+" ("+
									getDate((Long)(((JSONObject) (jArray.get(i))).get("timeCreated")))+
									"):</b></font>\t"+
									"<font color = white>"+
									(String)((JSONObject) ((JSONObject) (jArray.get(i))).get("data")).get("text")+
									"</font>";
							messages.put((String)((JSONObject) (jArray.get(i))).get("id"),tempString);
							updated+="<br>"+tempString;
						} 
						else{
							String tempString = "<b><font color = \"#00CC00\">"+
									getName((String)((JSONObject) (jArray.get(i))).get("userId"))+" ("+
									getDate((Long)(((JSONObject) (jArray.get(i))).get("timeCreated")))+
									"):</b></font>\t"+
									"<font color = white>"+
									(String)((JSONObject) ((JSONObject) (jArray.get(i))).get("data")).get("text")+
									"</font>";
							messages.put((String)((JSONObject) (jArray.get(i))).get("id"), tempString);	
							updated+="<br>"+tempString;
						}
					}
				}
				
				//JSONArray messageList = json.getJSONArray(key)
				
				
			} catch(IOException e) {
				System.out.println(e.toString());

			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				if (isValid) {
					return updated;
					//return this.messages;
				}

				return updated;
			}
			
		}
		
		public boolean sendMessage(String msg) {
			URL url = null;
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			try {
				url = new URL(API_URL_SEND);
			} catch(MalformedURLException e) {
				System.out.println(e.toString());
				return false;
			} catch(IOException e) {
				System.out.println(e.toString());
				return false;
			}

			boolean isValid = false;

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(API_URL_SEND);
			httpPost.addHeader("Cookie", COOKIE_NAME + "=" + this.authClient.getCookie());
			httpPost.addHeader("X-AvoSig", this.authClient.getSignature());
			httpPost.addHeader("User-Agent", USER_AGENT);
			params.add(new BasicNameValuePair("message",msg));
			HttpResponse httpResponse = null;
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				
				//int responseCode = httpResponse.;
				//if (responseCode == 200) isValid = true;
				
				BufferedReader in = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
				String strLine = "";
				String total = "";
				while((strLine = in.readLine()) != null){
					total+= strLine;
				}
				System.out.println(total);
/*
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String strLine = "";
				String total = "";
				while((strLine = in.readLine()) != null){
					total+= strLine;
				}
				
				JSONTokener jTokener = new JSONTokener(total);
				JSONObject jObject = (JSONObject)jTokener.nextValue();
				this.user = (String) ((JSONObject)jObject.get("currentUser")).get("firstName");
				this.other = (String) ((JSONObject)jObject.get("otherUser")).get("firstName");
				this.userId = (String) ((JSONObject)jObject.get("currentUser")).get("id");
				this.otherId = (String) ((JSONObject)jObject.get("otherUser")).get("id");
				System.out.println(this.user);
				System.out.println(this.other);
*/		
			} catch(IOException e) {
				System.out.println(e.toString());

			}/* finally {
				if (httpResponse != null) {
					httpClient.;
				}
				if (isValid) {
					//this.coupleData = "{}"; // TODO: Extract the response body for realz.
					System.out.println("Send successful");
				}
				return isValid;
			}*/
			return true;
		}
		
		private String getName(String id){
			if(id.equals(userId))
				return user;
			else if(id.equals(otherId))
				return other;
			else
				return "NULL";
		}
		
		private String getDate(long time){
			Date d = new Date((long)time);
			String[] parts = d.toString().split(" ");
			return parts[3];
		}
	}

	public class AuthClient {
		private String email = null;
		private char[] password = null;
		private int devId = 0;
		private String devKey = null;
		private String devSignature = null;
		private String cookie = null;

		public AuthClient() {
			this.devId = 81;
			this.devKey = "7UZiE6na8htEm1f2HhqimSQyX213pgmQjEvxiGnvpzccvE+GDiMrZ/1v9RlfPKFY";
		}

		public void setCredentials(String e, char[] p){
			this.email = e;
			this.password = p;
			System.out.println(e);
			System.out.println(p);
		}

		public String getCookie() {
			return this.cookie;
		}

		public String getSignature() {
			return this.devSignature;
		}

		public String getDevKey() {
			return this.devKey;
		}

		private void updateCookieFromLogin() {
			String data;
			try {
				data = "email=" + URLEncoder.encode(this.email, "UTF-8") +
						"&password=" + URLEncoder.encode(new String(this.password), "UTF-8");
			} catch(UnsupportedEncodingException e) {
				System.out.println(e.toString());
				return;
			}

			URL url = null;
			try {
				url = new URL(API_URL_LOGIN);
			} catch(MalformedURLException e) {
				System.out.println(e.toString());
				return;
			} catch(IOException e) {
				System.out.println(e.toString());
				return;
			}

			String cookieValue = null;

			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("User-Agent", USER_AGENT);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				DataOutputStream dataOut = new DataOutputStream(
						connection.getOutputStream());
				dataOut.writeBytes(data);
				dataOut.flush();
				dataOut.close();

				connection.connect();

				int responseCode = connection.getResponseCode();
				if (responseCode != 200) return;

				String headerName = null;
				for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
					if (headerName.equals("Set-Cookie")) {
						String cookie = connection.getHeaderField(i);
						cookie = cookie.substring(0, cookie.indexOf(";"));
						cookieValue = cookie.split("=", 2)[1];
					}
				}

			} catch(IOException e) {
				System.out.println(e.toString());

			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				this.cookie = cookieValue;
			}
		}

		private String generateHashedUserToken(String userToken) {
			MessageDigest hasher = null;
			try {
				hasher = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				// pass
			}

			byte[] bytes = userToken.getBytes();
			byte[] digest = hasher.digest(bytes);
			StringBuffer result = new StringBuffer();
			for (int i : digest) {
				String hex = Integer.toHexString(0xFF & i);
				if (hex.length() == 1) {
					result.append("0");
				}
				result.append(hex);
			}
			return result.toString();
		}

		public void updateFromCommandLine() {
			/*Console c = System.console();
      this.email = c.readLine("Email of an Avocado account: ");
      this.password = c.readPassword("Password: ");
      this.devId = Integer.parseInt(c.readLine("Developer ID: "));
      this.devKey = c.readLine("Developer key: ");
			 */
			//this.email = 
		}

		public void updateSignature() {
			this.updateCookieFromLogin();
			if (this.cookie == null) {
				System.out.println("The cookie is missing. Login must have failed.");
				return;
			}

			// Hash the user token.
			String hashedUserToken = generateHashedUserToken(
					this.cookie + this.devKey);

			// Get their signature.
			this.devSignature = this.devId + ":" + hashedUserToken;
		}
	}

} 