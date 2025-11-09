package server;
import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    
    private static BufferedReader reader;
    private static BufferedWriter writer;
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        Socket socket = null;
        int port = 8020;
        String address = "127.0.0.1";
        String db_name = Var_Env.DB_NAME;
        String db_pass = Var_Env.DB_PASSWORD;
        String db_user = Var_Env.DB_USER;
        int db_port = Var_Env.DB_port;
        
        String url = "jdbc:mysql://localhost:" + db_port + "/" + db_name;
        
        try{
            //Create Server Socket and bind The Server 
            server = new ServerSocket();
            server.bind(
              new InetSocketAddress(address, port)
            );
            System.out.println("Server bind : "+ address + ":"+port);
           
            //Create Connection with database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, db_user, db_pass);
           
                socket = server.accept();
                
                 //Create objects for send and recieve Messeges
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                
                sendMessage("Hello , Please Login");
                
                sendMessage("Enter Your Email: ");
                String email = receiveMessage();
                System.out.println(email);
                
                sendMessage("Enter Your Password:");
                String password = receiveMessage();
                System.out.println(password);
                
                //Check Email Exist 
                String email_check =  "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(email_check);
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                
                
                if (rs.next()){
                     int userId = rs.getInt("id");
                     sendMessage("Login successful! " + userId);
                     
                }else{
                    sendMessage("Invalid email or password!");
                }
                
               
        }catch(Exception e){
            System.out.println(e);
        }finally{
            //Close Ressources
            
            if (server != null) server.close();
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        }
    }
    
    public static void sendMessage(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }
    
    public static String receiveMessage() throws IOException {
        return reader.readLine();
    }
}
