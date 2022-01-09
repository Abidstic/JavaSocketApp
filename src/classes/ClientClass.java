/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author User
 */
public class ClientClass {
    private Socket socket;
    public final int port= 2222;
    public void startClient() throws IOException{
        socket = new Socket("localhost",port);
        new ClientHandler(socket,"Client-2").start();
    }
    
    public static void main(String[] args) throws IOException{
        new ClientClass().startClient();
    }
}
