package Server;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Server extends ServerSocket{
	public static int nClientNum = 0;
	public static final int PORT = 9999;
	public static ArrayList<Client> clientThread = new ArrayList<Client>();
	public static ArrayList<Integer> array = new ArrayList<Integer>();
	
	public Server() throws Exception{
	       super(PORT);
	       newGame(60,15);
	       try {
	           while(true){
	        	   Socket socketServer = accept();
	        	   nClientNum ++;
	        	   new Client(socketServer,nClientNum);
	        	   Thread.sleep(10);
	           }
	       }
	       finally{
	    	   close();
	       }
	}
	@SuppressWarnings("resource")
	public static void main(String arg[]) throws Exception{
        JFrame jframe = new JFrame("连连看");
        jframe.setSize(400, 400);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel contentpane = (JPanel) jframe.getContentPane();
        contentpane.setLayout(new GridLayout(2, 1));
        JLabel jlabelName = new JLabel("连连看服务器", JLabel.CENTER);
        JLabel jlabelIP = new JLabel("IP: " + InetAddress.getLocalHost() + ":" + PORT, JLabel.CENTER);
//        JLabel jlabelClient1 = new JLabel("Waiting for Player 1...", JLabel.CENTER);
//        JLabel jlabelClient2 = new JLabel("Waiting for Player 2...", JLabel.CENTER);
        contentpane.add(jlabelName);
        contentpane.add(jlabelIP);
//        contentpane.add(jlabelClient1);
//        contentpane.add(jlabelClient2);
        jframe.setVisible(true);  
        new Server();
	}
	
	class Client extends Thread {
		private BufferedReader brInFromClient;
		private DataOutputStream  dosOutToClient;
		private String input; 
		private Socket socket;
		private int clientNum;
	    
	    public Client(Socket s, int num) throws Exception{
	        socket = s;
	        clientNum = num;
	        brInFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
            dosOutToClient = new DataOutputStream(socket.getOutputStream());
            dosOutToClient.writeBytes(clientNum + "\n");
            for (int i=0;i<60;i++){
            	dosOutToClient.writeBytes(array.get(i) + "\n");
            }
            
            start();
	    }
	    public void run() {
	        try{
	        	clientThread.add(this);
	        	do{
	        		input = brInFromClient.readLine();
	        		if (input.equals("newgame")){
	        			newGame(60,15);
	        			for(Client client:clientThread){
		        			client.dosOutToClient.writeBytes("newgame" + "\n");
		        		}
	        			sleep(10);
	                    for (int i=0;i<60;i++){
	                    	for(Client client:clientThread){
	                    		client.dosOutToClient.writeBytes(array.get(i) + "\n");
	                    	}
	                    }
	        		}
	        		else{
	        			if (input.equals("refresh")){
		        			int totalnum = Integer.parseInt(brInFromClient.readLine());
		        			newGame(totalnum,15);
		        			for(Client client:clientThread){
			        			client.dosOutToClient.writeBytes("refresh" + "\n");
			        		}
		                    for (int i=0;i<totalnum;i++){
		                    	for(Client client:clientThread){
		                    		client.dosOutToClient.writeBytes(array.get(i) + "\n");
		                    	}
		                    }
		        		}
		        		else{
		        			for(Client client:clientThread){
			        			client.dosOutToClient.writeBytes(clientNum + ":" + input + "\n");
			        		}
		        		}
	        		}
	        		
	            }while(!input.equals("no"));
	            socket.close();
	        }
	        catch(Exception e){}
	    }
	}
	
	
	public void newGame(int totalnum,int catagory){
			int x,y,t;
			Random random = new Random();
			array.clear();
			for (int i=0;i<totalnum;i++){
				if (i<totalnum/2){
					array.add(random.nextInt(catagory));
				}
				else{
					array.add(array.get(totalnum-i-1));
				}
			}
			for (int i=0;i<totalnum*totalnum;i++){
				x=random.nextInt(totalnum);
				y=random.nextInt(totalnum);
				t=array.get(x);
				array.set(x, array.get(y));
				array.set(y, t);
			}
		}
	}




