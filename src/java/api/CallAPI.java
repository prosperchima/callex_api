package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author USER
 */
@Path("API")
public class CallAPI {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CallAPI
     */
    public CallAPI() {
    }
    
    @Path("call")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String call(@FormParam("isActive") String isActive, @FormParam("sessionId") String sessionId,
            @FormParam("callerNumber") String phoneNumber, @FormParam("callStartTime") String callStartTime,
            @FormParam("recordingUrl") String recordingUrl, @FormParam("durationInSeconds") int durationInSeconds,
            @FormParam("amount") double amount, @FormParam("destinationNumber") String dNumber,
            @FormParam("dtmfDigits") String inputnumber) {
        String response;
        
        switch(isActive){
            case "1":
                response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<Response>"
                            + "<GetDigits timeout=\"20\" finishOnKey=\"#\" callbackUrl=\"http://142.93.42.123:8080/CallexAPI/API/route\">"
                                + "<Say>Welcome to Pro Gadget store, Your number one gadget store. We are available twenty 4 seven "
                                + " to serve you in the best way"
                                + "To know the goods that are available press 1."
                                + "To make a complain press 2."
                                + "To talk to a customer care agent press 3.</Say>"
                            + "</GetDigits>"
                        + "</Response>";
                
                
                return response;
                
            default:
                return "";
        }
        
        
    }
    @Path("route")
    @POST
    @Produces (MediaType.TEXT_PLAIN)
    public String route (@FormParam("isActive") String isActive, @FormParam("sessionId") String sessionId,
            @FormParam("callerNumber") String phoneNumber, @FormParam("callStartTime") String callStartTime,
            @FormParam("recordingUrl") String recordingUrl, @FormParam("durationInSeconds") int durationInSeconds,
            @FormParam("amount") double amount, @FormParam("destinationNumber") String dNumber,
            @FormParam("dtmfDigits") String inputnumber) throws ClassNotFoundException {
        String response;
            switch(isActive){
            case "1":
                switch(inputnumber){
                                    case "1":
                                            response  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                                + "<Response>"
                                                    + "<GetDigits timeout=\"20\" finishOnKey=\"#\" callbackUrl=\"http://142.93.42.123:8080/CallexAPI/API/menu\">"
                                                        + "<Say>"                  
                                                            + "New age power bank of 20,000 MAH is available at 10,000 naira."
                                                            + "i phone 12 pro is available at 270,000 naira."
                                                            + "Samsung S 21 is available at 300,000 naira."
                                                            + "To Listen to the menu again Press 5."
                                                        + "</Say>"
                                                    + "</GetDigits>"
                                                + "</Response>";
                                               return response;
                                    case "2":
                                            response  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                                + "<Response>"
                                                    + "<Record finishOnKey=\"#\" maxLength=\"20\" trimSilence=\"true\" playBeep=\"true\">"
                                                        + "<Say>"
                                                            + "Please tell us what the situation is, after the beep."
                                                        + "</Say>"
                                                    + "</Record>"
                                                + "</Response>";
                                            return response;
                                    case "3":
                                              String b_phone = getphone(inputnumber);
                                              response  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                                + "<Response>"
                                                      + "<Say>" 
                                                        + "<Dial phoneNumbers=\""+"+234"+b_phone.substring(1)+"\" "
                                                        + " ringbackTone=\"http://sthannah.com.ng/clearday.mp3\" " 
                                                        + "record=\"true\"  maxDuration = \"10\" sequential = \"true\" >"
                                                      + "</Say>"
                                                   + "</Dial>"
                                                + "</Response>";
                                    default:
                                                response  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                                    + "<Response>"
                                                        + "<Say>Your response is not valid, please call back.</Say>"
                                                    + "</Response>";
                                                return response;
                                }
            default:
                try(Connection conn = dbConnect()){
                    String sql = "UPDATE call_log SET `callerNumber` = ?, `callStartTime` = ?, `recordingUrl` = ?, `durationInSeconds` = ?"
                            + " WHERE sId = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, phoneNumber);
                    stmt.setString(2, callStartTime);
                    stmt.setString(3, recordingUrl);
                    stmt.setInt(4, durationInSeconds);
                    stmt.setString(5, sessionId);
                    stmt.execute();
                }catch(SQLException e){
                    Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, e);
                }
                return "I routed.";
        }
        
        
    }

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText() {
       return "E dey work o";
    }
                    public static Connection dbConnect() throws ClassNotFoundException, 
                          SQLException{
                      String URL = "jdbc:mysql://localhost:3306/callex?useSSL=false&serverTimezone=Africa/Lagos";
                      String USERNAME = "root";
                      String PASSWORD = "password";
                      Class.forName("com.mysql.cj.jdbc.Driver");
                      return DriverManager.getConnection(URL, USERNAME, PASSWORD);
                  }

                  private String getphone(String inputnumber) {
                      String phone= "";
                      try(Connection conn = dbConnect()){
                          String query = "SELECT * FROM users WHERE inputnumber = ?" ;
                          PreparedStatement stmt = conn.prepareStatement(query);
                          stmt.setString(1, inputnumber);
                          ResultSet rs = stmt.executeQuery();

                          if(rs.next()){
                              phone = rs.getString("phone");
                          }

                      } catch (SQLException | ClassNotFoundException ex) {
                          Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      return phone;
                  }




}







