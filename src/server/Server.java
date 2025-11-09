package server;
import java.net.*;
import java.sql.*;

public class Server {

    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;
        int port = 8000;
        String address = "127.0.0.1";
        String db_name = Var_Env.DB_NAME;
        String db_pass = Var_Env.DB_PASSWORD;
        String db_user = Var_Env.DB_USER;
        int db_port = Var_Env.DB_port;
        
        String url = "jdbc:mysql://localhost:" + db_port + "/" + db_name;
        
        try{
            server = new ServerSocket();
            server.bind(
              new InetSocketAddress(address, port)
            );
            System.out.println("Server bind : "+ address + ":"+port);
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, db_user, db_pass);
            
            System.out.println("Connection Success !");
            
            while(true){
                socket = server.accept();
                System.out.println("Client Connected Success !");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
