/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author User
 */
public class ServerClass {
     private ServerSocket serverSocket;
     private Socket socket;
     public final int port = 2222;
     public void startServer() throws IOException{
         serverSocket = new ServerSocket(port);
         socket = serverSocket.accept();
         new ClientHandler(socket,"Client-1").start();
     }
     public static void main(String[] args) throws IOException{
         new ServerClass().startServer();
     }
}
