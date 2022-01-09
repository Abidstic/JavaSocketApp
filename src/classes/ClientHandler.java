/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import static java.io.FileDescriptor.out;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sun.misc.MessageUtils.out;


/**
 *
 * @author User
 */
public class ClientHandler extends Thread {
   public String username;
   public final Socket socket;
   private final DataInputStream dis;
   private final DataOutputStream dos;
   private final BufferedReader bufReader;
   public String path;
     
   public ClientHandler(Socket socket,String username) throws IOException{
       this.socket = socket;
       dis = new DataInputStream(socket.getInputStream());
       dos = new DataOutputStream(socket.getOutputStream());
       bufReader = new BufferedReader(new InputStreamReader(System.in));
       this.username = username;
   }
   
   private boolean makeDirectory() {
        String userName = System.getProperty("user.name");
        String os = System.getProperty("os.name");
        String[] oss = os.split(" ", 2);
        if ("Windows".equals(oss[0])) {
            path = "C:\\Users\\" + userName + "\\Downloads\\SharedFiles\\" + username + "\\"; //folderPath to save incoming files
        } else {
            path = "/home/" + userName + "/SharedFiles/" + username + "/"; //folderPath to save incoming files
        }
        File directory = new File(path);
        if (directory.exists()) {
            return false;
        }
        return directory.mkdirs();
    }
   
   public void sendingMessage() throws IOException{
       String messageToSend = bufReader.readLine();
       messageToSend = username + ": " + messageToSend;
       String[] messageSplit = messageToSend.split(" ",3);
       
       if("/send".equals(messageSplit[1])&&messageSplit.length==3){
           sendingFile(messageSplit);
       }
       else{
           System.out.println(messageToSend);
           dos.writeUTF(messageToSend);    
       }
       dos.flush();
   }
   
   public void sendingFile(String[] args) throws IOException{
       String filePath = args[2];
       File file = new File(filePath);
       if(file.exists()){
           System.out.println("Sent " + file.getPath());
           String messageToSend = username + ": "+"/send "+file.getName();
           dos.writeUTF(messageToSend);
           FileInputStream fileInputStream = new FileInputStream(file);
           byte[] byteBuffer = new byte[(int)file.length()];
           fileInputStream.read(byteBuffer);
           dos.writeInt(byteBuffer.length);
           dos.write(byteBuffer);
       }
       else{
           System.out.println("Requested file does not exist in the directory!!!");
       }
   }
   
   public void receiveMessage() throws IOException{
       String messageReceived = dis.readUTF();
       String[] messageSplit = messageReceived.split(" ",3);
       if("/send".equals(messageSplit[1])&& messageSplit.length == 3){
           receieveFile(messageSplit);
           messageReceived = messageSplit[0]+" "+messageSplit[2];
       }
       System.out.println(messageReceived);
   }
   
   public void receieveFile(String args[]) throws IOException{
       File file = new File(path + args[2]);
       int fileLength = dis.readInt();
       if(fileLength>0){
           byte[] byteBuffer = new byte[fileLength];
           dis.readFully(byteBuffer, 0, fileLength);
           FileOutputStream fileOutputStream = new FileOutputStream(file);
           fileOutputStream.write(byteBuffer, 0, fileLength);
           fileOutputStream.close();
           System.out.println("Recieved a new file...");
           System.out.println("File saved in "+ file.getAbsolutePath());
       }
   }
   
   public void run() {
       
        makeDirectory();
        new Thread(() -> {
            while (true) {
                try {
                    sendingMessage();
                } catch (IOException ex) {
                    Logger.getLogger(ServerClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    receiveMessage();
                } catch (IOException ex) {
                    Logger.getLogger(ServerClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}