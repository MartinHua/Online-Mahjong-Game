package ClientPartner;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;


public class ClientPartner implements ActionListener {
	static boolean notEnd,musicplay;
	int playerNum,x,y,player,winner,playerbegin,playerend,xbegin,xend,ybegin,yend,middle;
	JFrame jFrame;
	JPanel contentPane,topPanel,buttonPanel,centerPanel,rightPanel,leftPanel;
	JButton[][] grid = new JButton[8][12];
	JButton refresh,newgame,stop;
	JLabel yourScores,otherScores,time;
	int[][] gridcleared = new int[8][12];
	String strServer,position,firstposition,secondposition;
	Socket socketClient;
	BufferedReader brInFromServer;
	BufferedReader brInFromUser;
    DataOutputStream dosOutToServer;
    int[] playerScores = new int[100];
    ArrayList<ImageIcon> icon = new ArrayList<ImageIcon>();
    ImageIcon refreshImage = new ImageIcon(ClientPartner.class.getResource("/refresh.png"));
    ImageIcon rawImage = new ImageIcon(ClientPartner.class.getResource("/raw.png"));
    ImageIcon colImage = new ImageIcon(ClientPartner.class.getResource("/col.png"));
    ImageIcon stopImage = new ImageIcon(ClientPartner.class.getResource("/stop.png"));
    ImageIcon continueImage = new ImageIcon(ClientPartner.class.getResource("/stop.png"));
    ImageIcon startImage = new ImageIcon(ClientPartner.class.getResource("/startstop.png"));
    ImageIcon backgroundImage = new ImageIcon(ClientPartner.class.getResource("/rabbit.jpg"));
    JLabel backgroundImageLabel = new JLabel(backgroundImage);
    JLabel rawImageLabel = new JLabel(rawImage);
    JLabel colImageLabel = new JLabel(colImage);
    MyTimerTask task;
    Timer timer;
    boolean timerwork=false;
    
    boolean looping = false; 
	File file1 = new File("C:\\Users\\Martin Hua\\java\\workspace\\JavaProject\\0.wav");
	AudioClip sound1;
	AudioClip chosenClip;

	JButton playButton = new JButton("播放"); 
	JButton loopButton = new JButton("循环播放");    
	JButton stopButton = new JButton("停止"); 
	JLabel status = new JLabel(""); 

    ClientPartner() throws Exception {
    	try {
            InetAddress address = InetAddress.getLocalHost();
            String strIPPort = JOptionPane.showInputDialog("Type the IP and port of Server\n", address.getHostAddress() + ":9999");
            String[] strList = strIPPort.split(":");
            address = InetAddress.getByName(strList[0]);
            int port = Integer.parseInt(strList[1]);

            socketClient = new Socket(address, port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the server.", "", JOptionPane.ERROR_MESSAGE);
        }
    	brInFromServer = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        dosOutToServer = new DataOutputStream(socketClient.getOutputStream());
        
        strServer = brInFromServer.readLine();
        playerNum = strServer.charAt(0) - '0';
    }
    public static void main(String[] args) throws Exception {
    	ClientPartner Game = new ClientPartner();
    	Game.go();
        while (notEnd) {
        	Game.waitServer();
//        	Thread.sleep(10);
        }
    }
    
    void go() throws Exception{
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/0.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/1.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/2.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/3.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/4.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/5.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/6.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/7.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/8.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/9.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/10.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/11.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/12.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/13.png")));
    	icon.add(new ImageIcon(ClientPartner.class.getResource("/14.png")));
    	
    	jFrame = new JFrame("Game - Player " + playerNum);
    	jFrame.setSize(backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
    	jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	jFrame.getLayeredPane().add(backgroundImageLabel, new Integer(Integer.MIN_VALUE));
    	backgroundImageLabel.setBounds(0,0,backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
//    	jFrame.setLayeredPane(new MyLayeredPanePanel());

        contentPane = (JPanel) jFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());      
        topPanel = getTopPanel();
        contentPane.add(topPanel, "North");
        centerPanel = getCenterPanel(); 
        contentPane.add(centerPanel, "Center");
        leftPanel = getWestPanel(); 
        contentPane.add(leftPanel, "West");
        rightPanel = getEastPanel(); 
        contentPane.add(rightPanel, "East");
        buttonPanel = getButtonPanel(); 
        contentPane.add(buttonPanel, "South");
        contentPane.setOpaque(false);
        
        
        for (int i=0;i<60;i++){
        	strServer = brInFromServer.readLine();
        	grid[i/10+1][i%10+1].setIcon(icon.get(Integer.parseInt(strServer)));
        	gridcleared[i/10+1][i%10+1]=Integer.parseInt(strServer);
        	grid[i/10+1][i%10+1].setContentAreaFilled(false);
        	grid[i/10+1][i%10+1].setRolloverEnabled(false);
        }
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        JOptionPane.showMessageDialog(null, "Your order is " + playerNum + ".");
        firstposition="";
        secondposition="";
        winner = 0;
        notEnd = true;
        musicplay = true;
		System.out.println(grid[1][1].getWidth() + " " + grid[1][1].getHeight());
    }
    
//    class MyLayeredPanePanel extends JLayeredPane{
//    	int x1,x2,y1,y2;
//    	boolean cleanLines = false;
//        public MyLayeredPanePanel(){
//        	this.cleanLines = false;
//        	this.add(backgroundImageLabel, new Integer(Integer.MIN_VALUE));
//        	backgroundImageLabel.setBounds(0,0,backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
//        }
//    	public MyLayeredPanePanel(int x1,int y1,int x2,int y2){
//    		this.x1=x1;
//    		this.x2=x2;
//    		this.y1=y1;
//    		this.y2=y2;
//    		this.cleanLines = true;
//    		this.add(backgroundImageLabel, new Integer(Integer.MIN_VALUE));
//        	backgroundImageLabel.setBounds(0,0,backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
//    	}
////    	public void paint(Graphics g) {
////            g.setColor(Color.GREEN);
////            g.fillOval(20, 20, 100, 100);
////        }
//    }
    private JPanel getWestPanel() {
    	JPanel jPanel = new JPanel();
    	jPanel.setLayout(new GridLayout(8, 4));
    	for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
            	JButton emptyButton = new JButton();
            	emptyButton.setContentAreaFilled(false);
            	emptyButton.setBorderPainted(false);
            	jPanel.add(emptyButton);
            }
        }
        jPanel.setOpaque(false);
		return jPanel;
	}
    private JPanel getEastPanel() {
    	JPanel jPanel = new JPanel();
    	jPanel.setLayout(new GridLayout(8, 4));
    	for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
            	JButton emptyButton = new JButton();
            	emptyButton.setContentAreaFilled(false);
            	emptyButton.setBorderPainted(false);
            	jPanel.add(emptyButton);
            }
        }
        jPanel.setOpaque(false);
		return jPanel;
	}
    private JPanel getTopPanel() {
    	JPanel jPanel = new JPanel();
//    	jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
    	
    	time = new JLabel();
    	time.setFont(new Font("华文琥珀", Font.PLAIN, 25));
    	time.setForeground(Color.red);
    	time.setText(" TIME 01:00   ");
    	jPanel.add(time);
    	
    	yourScores = new JLabel();
    	yourScores.setFont(new Font("华文琥珀", Font.PLAIN, 25));
    	yourScores.setForeground(Color.red);
    	yourScores.setText("Your Scores:00   ");
    	jPanel.add(yourScores);
    	
    	otherScores = new JLabel();
    	otherScores.setFont(new Font("华文琥珀", Font.PLAIN, 25));
    	otherScores.setForeground(Color.red);
    	otherScores.setText("Highest Scores:00   ");
    	jPanel.add(otherScores);
    	
    	refresh = new JButton("重新布置");
    	refresh.setName("refresh");
    	refresh.setFont(new Font("华文琥珀", Font.PLAIN, 25));
//    	refresh.setForeground(Color.white);
//    	refresh.setContentAreaFilled(false);
//    	refresh.setBorderPainted(false);
//    	refresh.setBackground(Color.WHITE);
//    	refresh.setIcon(refreshImage);	
//    	refresh.setBorderPainted(false);
    	jPanel.add(refresh);
    	
    	stop = new JButton("暂停/继续");
    	stop.setName("stop");
    	stop.setFont(new Font("华文琥珀", Font.PLAIN, 25));
//    	stop.setBackground(Color.WHITE);
//    	stop.setIcon(stopImage);	
//    	stop.setBorderPainted(false);   	
    	jPanel.add(stop);
    	
    	newgame = new JButton("新游戏");
    	newgame.setName("newgame");
    	newgame.setFont(new Font("华文琥珀", Font.PLAIN, 25));
//    	newgame.setBackground(Color.WHITE);
//    	newgame.setIcon(startImage);	
//    	newgame.setBorderPainted(false);
    	newgame.addActionListener(this);
    	jPanel.add(newgame);

	   	jPanel.setOpaque(false);
		return jPanel;
	}  
	@SuppressWarnings("deprecation")
	private JPanel getButtonPanel() {
		
    	JPanel jPanel = new JPanel();
    	status.setFont(new Font("华文琥珀", Font.PLAIN, 25));
    	status.setForeground(Color.red);
    	
    	playButton.addActionListener(this);
    	playButton.setName("playButton");
    	playButton.setFont(new Font("华文琥珀", Font.PLAIN, 25));
		loopButton.addActionListener(this);
		loopButton.setName("loopButton");
		loopButton.setFont(new Font("华文琥珀", Font.PLAIN, 25));
		stopButton.addActionListener(this);
		stopButton.setName("stopButton");
		stopButton.setFont(new Font("华文琥珀", Font.PLAIN, 25));
		stopButton.setEnabled(false); 

		jPanel.add(playButton);
		jPanel.add(loopButton);
		jPanel.add(stopButton);
		jPanel.add(status);
    	
    	try {
			sound1 = Applet.newAudioClip(file1.toURL());
			chosenClip = sound1;
		} catch(OutOfMemoryError e){
			System.out.println("内存溢出");
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

	   	jPanel.setOpaque(false);
		return jPanel;
	}
	private JPanel getCenterPanel() {
    	JPanel jPanel = new JPanel();
    	jPanel.setLayout(new GridLayout(8, 12));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 12; j++) {
            	grid[i][j] = new JButton();
                grid[i][j].setName("(" + i + "," + j + ")");
                grid[i][j].setOpaque(true);
                grid[i][j].setBorderPainted(false);
            	if (i==0 || i==7 || j==0 || j==11){
            		gridcleared[i][j] = -1;
            		grid[i][j].setContentAreaFilled(false);
            	}
            	else{        		
            		grid[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
            	}
            	jPanel.add(grid[i][j]);
            }
        }
        jPanel.setOpaque(false);
		return jPanel;
	}
	private void waitServer() throws Exception{
    	strServer = brInFromServer.readLine();
    	if (strServer.equals("newgame")){
    		newgame();
    	}
    	else{
    		if (strServer.equals("refresh")){
    			int count=0;
    			for (int i=0;i<8;i++)
    				for (int j=0;j<12;j++){
    					if (gridcleared[i][j]!=-1) count++;
    			}
        		refresh(count);
        	}
    		else{
    			playerbegin = -1;
    	    	playerend = strServer.indexOf(':');
    	    	player = Integer.parseInt(strServer.substring(playerbegin+1,playerend));
    	    	strServer = strServer.substring(playerend+1);
    			if (strServer.equals("stop")){
    				stopgame(player);
    	    	}
    			else{
    				if (strServer.equals("continue")){
    					continuegame(player);
    				}
    				else{
    					
            	    	middle = strServer.indexOf('-');
            	    	Continue(player,strServer.substring(0, middle),strServer.substring(middle+1));
    				}
        		}
    		}
    	}
    }
	
	private void continuegame(int player) {
		JOptionPane.showMessageDialog(null, "Player " + player+ " continued the game.");
		stop.setName("stop");
//    	stop.setIcon(stopImage);
    	synchronized (task) {
            task.condition = true;
            task.notify();
        }
	}
	private void stopgame(int player) throws Exception {
		synchronized (task) {
            task.condition = false;
        }
		JOptionPane.showMessageDialog(null, "Player " + player + " stopped the game.");
		stop.setName("continue");
//    	stop.setIcon(continueImage);
	}
	private void newgame() throws Exception {
		if (timerwork){
			timer.cancel();
			timerwork=false;
		}
		for (int i=1;i<7;i++)
			for (int j=1;j<11;j++){
				grid[i][j].removeActionListener(this);
			}
    	time.setText(" TIME 01:00   ");
    	yourScores.setText("Your Scores:00   ");
    	otherScores.setText("Highest Scores:00   ");
		for (int i=0;i<100;i++){
			playerScores[i]=0;
		}
		for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 12; j++) {
            	if (i==0 || i==7 || j==0 || j==11){
            		gridcleared[i][j] = -1;
            		grid[i][j].setContentAreaFilled(false);
            	}
            	else{
            		gridcleared[i][j] = 0;
            		grid[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
            	}
            }
        }
		
		refresh(60);
		for (int i=1;i<7;i++)
			for (int j=1;j<11;j++){
				grid[i][j].addActionListener(this);
			}
		refresh.addActionListener(this);
		stop.addActionListener(this);
		task = new MyTimerTask();
        timer = new Timer();
        timer.schedule(task, 0, 1000);
        timerwork=true;
        System.out.println(grid[0][11].getX());
	}
    
	class MyTimerTask extends TimerTask{
	    public boolean condition = true;
	    int secondCount=0;
	    String text;
	    public void run() {
	        synchronized (this) {
	            while(!condition) {
	                try {
	                    wait();
	                } catch (InterruptedException e) {
	                    Thread.interrupted();
	                }
	            }
	        }
	        secondCount++;
			if (secondCount>50)
				text = " TIME 00:0" + (60-secondCount) + "   ";
			else
				text = " TIME 00:" + (60-secondCount) + "   ";
			time.setText(text);
			if (secondCount==60){
				try {
					over();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	    }
	}

	private void over() throws Exception {
		timer.cancel();
		timerwork=false;
		for (int i=0;i<8;i++)
			for (int j=0;j<12;j++){
				grid[i][j].removeActionListener(this);
			}
		stop.removeActionListener(this);
		refresh.removeActionListener(this);
		if (clear())
			JOptionPane.showMessageDialog(null, "You win!");
		else
			JOptionPane.showMessageDialog(null, "Timeout!");
	}
	
	private void refresh(int count) throws Exception {
    	int x=0,y=0;
    	for (int i=0;i<count;i++){
        	strServer = brInFromServer.readLine();
        	while(gridcleared[x][y]==-1){
        		y++;
        		if (y==12){
        			x++;
        			y=0;
        		}
        	}
        	grid[x][y].setIcon(icon.get(Integer.parseInt(strServer)));
        	gridcleared[x][y]=Integer.parseInt(strServer);
        	y++;
    		if (y==12){
    			x++;
    			y=0;
    		}
        }
    	firstposition="";
        secondposition="";
        winner = 0;
        notEnd = true;
	}
	public void actionPerformed(ActionEvent e) {
    	JButton clickButton = (JButton) e.getSource();
        position = clickButton.getName();

		if (position.equals("playButton")) {
			stopButton.setEnabled(true); 
			loopButton.setEnabled(true); 
			chosenClip.play();
			status.setText("正在播放");
			return;
		}

		if (position.equals("loopButton")) {
			looping = true;
			chosenClip.loop(); 
			loopButton.setEnabled(false); 
			stopButton.setEnabled(true); 
			status.setText("正在循环播放"); 
			return;
		}
		if (position.equals("stopButton")) {
			if (looping) {
				looping = false;
				chosenClip.stop(); 
				loopButton.setEnabled(true);
			} else {
				chosenClip.stop();
			}
			stopButton.setEnabled(false); 
			status.setText("停止播放");
			return;
		}
        if (position.equals("newgame")){
        	try {
				dosOutToServer.writeBytes("newgame"+"\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	return;
        }
        if (position.equals("refresh")){
        	int count=0;
			for (int i=0;i<8;i++)
				for (int j=0;j<12;j++){
					if (gridcleared[i][j]!=-1) count++;
			}
			try {
				dosOutToServer.writeBytes("refresh"+"\n");
				dosOutToServer.writeBytes(count+"\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	return;
        }
        if (position.equals("stop")){
        	try {
				dosOutToServer.writeBytes("stop"+"\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	return;
        }
        if (position.equals("continue")){
        	try {
				dosOutToServer.writeBytes("continue"+"\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	return;
        }
        if (firstposition.equals("")){
        	firstposition=position;
        	highlight(firstposition);
        }
        else{
        	if (position.equals(firstposition)){
        		dehighlight(firstposition);
        		firstposition="";
        	}
        	else{
        		secondposition=position;
        		if (Feasible(firstposition,secondposition)){
        			try {
						dosOutToServer.writeBytes(firstposition + "-" + secondposition+ '\n');
					} catch (IOException e1) {
						e1.printStackTrace();
					}
        		}
        		else{
        			dehighlight(firstposition);
        			dehighlight(secondposition);
        		}
        		firstposition="";
        		secondposition="";
        	}
        } 
    }
    
    private void highlight(String position) {
    	xbegin = position.indexOf('(');
    	xend = position.indexOf(',');
    	x = Integer.parseInt(position.substring(xbegin+1,xend));
    	
    	ybegin = position.indexOf(',');
    	yend = position.indexOf(')');
    	y = Integer.parseInt(position.substring(ybegin+1,yend));
    	
    	grid[x][y].setContentAreaFilled(true);
    	grid[x][y].setBackground(Color.RED);
    	grid[x][y].setBorder(BorderFactory.createRaisedBevelBorder());
    	grid[x][y].setBorderPainted(true);
	}
    
    private void dehighlight(String position) {
    	xbegin = position.indexOf('(');
    	xend = position.indexOf(',');
    	x = Integer.parseInt(position.substring(xbegin+1,xend));
    	
    	ybegin = position.indexOf(',');
    	yend = position.indexOf(')');
    	y = Integer.parseInt(position.substring(ybegin+1,yend));
    	
    	grid[x][y].setContentAreaFilled(false);
    	grid[x][y].setBackground(Color.WHITE);
    	grid[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
    	grid[x][y].setBorderPainted(false);
	}
	
    private boolean FeasibleLine(int x1,int y1,int x2,int y2){
    	if (x1!=x2 && y1!=y2){
    		return false;
    	}
    	if (x1==x2){
    		for (int i=y1+(y2-y1)/Math.abs(y2-y1);i!=y2;i=i+(y2-y1)/Math.abs(y2-y1)){
    			if (gridcleared[x1][i]!=-1){
    				return false;
    			}
    		}
    		
    	}
    	if (y1==y2){
    		for (int i=x1+(x2-x1)/Math.abs(x2-x1);i!=x2;i=i+(x2-x1)/Math.abs(x2-x1)){
    			if (gridcleared[i][y1]!=-1){
    				return false;
    			}
    		}
    	}
    	return true;
    }
    private boolean Feasible(String position1,String position2) {
    	xbegin = position1.indexOf('(');
    	xend = position1.indexOf(',');
    	int x1 = Integer.parseInt(position1.substring(xbegin+1,xend));
    	
    	ybegin = position1.indexOf(',');
    	yend = position1.indexOf(')');
    	int y1 = Integer.parseInt(position1.substring(ybegin+1,yend));
    	
    	xbegin = position2.indexOf('(');
    	xend = position2.indexOf(',');
    	int x2 = Integer.parseInt(position2.substring(xbegin+1,xend));
    	
    	ybegin = position2.indexOf(',');
    	yend = position2.indexOf(')');
    	int y2 = Integer.parseInt(position2.substring(ybegin+1,yend));
    	
    	if (gridcleared[x1][y1]!=gridcleared[x2][y2]){
    		return false;
    	}
    	if (FeasibleLine(x1,y1,x2,y2)){
    		return true;
    	}
    	
    	if (x1!=x2 && y1!=y2){
    		if (gridcleared[x1][y2]==-1 && FeasibleLine(x1,y1,x1,y2) && FeasibleLine(x1,y2,x2,y2)){
    			return true;
    		}
    		if (gridcleared[x2][y1]==-1 && FeasibleLine(x1,y1,x2,y1) && FeasibleLine(x2,y1,x2,y2)){
    			return true;
    		}
    	}
    	if (y1!=y2){
    		for (int i=0;i<8;i++){
        		if (x1!=i && x2!=i){
        			if (gridcleared[i][y1]==-1 && gridcleared[i][y2]==-1 &&
        					FeasibleLine(x1,y1,i,y1) && 
        					FeasibleLine(i,y1,i,y2) &&
        					FeasibleLine(i,y2,x2,y2)){
        				return true;
        			}
        		}
        	}
    	}
    	if (x1!=x2){
    		for (int i=0;i<12;i++){
        		if (y1!=i && y2!=i){
        			if (gridcleared[x1][i]==-1 && gridcleared[x2][i]==-1 &&
        					FeasibleLine(x1,y1,x1,i) && 
        					FeasibleLine(x1,i,x2,i) &&
        					FeasibleLine(x2,i,x2,y2)){
        				return true;
        			}
        		}
        	}
    	}
		return false;
	}
	
    private void Continue(int player,String position1,String position2) throws Exception {
    	playerScores[player]++;
    	if (player==playerNum){
    		if (playerScores[playerNum]<10)
    			yourScores.setText("Your Scores:0"+playerScores[playerNum]+"   ");
    		else
    			yourScores.setText("Your Scores:"+playerScores[playerNum]+"   ");
    	}
    	int max=0;
    	for (int i=0;i<100;i++){
    		if (max<playerScores[i])
    			max=playerScores[i];
    	}
    	if (max<10)
			otherScores.setText("Highest Scores:0"+max+"   ");
    	else
    		otherScores.setText("Highest Scores:"+max+"   ");
    	xbegin = position1.indexOf('(');
    	xend = position1.indexOf(',');
    	int x1 = Integer.parseInt(position1.substring(xbegin+1,xend));   	
    	ybegin = position1.indexOf(',');
    	yend = position1.indexOf(')');
    	int y1 = Integer.parseInt(position1.substring(ybegin+1,yend));
    	xbegin = position2.indexOf('(');
    	xend = position2.indexOf(',');
    	int x2 = Integer.parseInt(position2.substring(xbegin+1,xend));
    	ybegin = position2.indexOf(',');
    	yend = position2.indexOf(')');
    	int y2 = Integer.parseInt(position2.substring(ybegin+1,yend));
    	
    	int count=cleanLine(x1,y1,x2,y2);
    	Thread.sleep(200);
    	for (int i=0;i<count;i++){
    		jFrame.getLayeredPane().remove(0);
    	}
    	jFrame.getLayeredPane().repaint();
    	
    	gridcleared[x1][y1] = -1;
    	grid[x1][y1].removeActionListener(this);
    	grid[x1][y1].setIcon(null);
    	grid[x1][y1].setBorderPainted(false);
    	grid[x1][y1].setContentAreaFilled(false); 
    	gridcleared[x2][y2] = -1;
    	grid[x2][y2].removeActionListener(this);
    	grid[x2][y2].setIcon(null);
    	grid[x2][y2].setBorderPainted(false);
    	grid[x2][y2].setContentAreaFilled(false);
    	
    	if (clear()){
//    		notEnd = false;
    		over();
    	}
    }
    
    private int cleanLine(int x1, int y1, int x2, int y2){
    	if (FeasibleLine(x1,y1,x2,y2)){
    		addline(x1,y1,x2,y2);
    		return Math.abs(x1-x2)+Math.abs(y1-y2);
    	}
    	
    	if (x1!=x2 && y1!=y2){
    		if (gridcleared[x1][y2]==-1 && FeasibleLine(x1,y1,x1,y2) && FeasibleLine(x1,y2,x2,y2)){
    			addline(x1,y1,x1,y2);
    			addline(x1,y2,x2,y2);
    			return Math.abs(x1-x2)+Math.abs(y1-y2);
    		}
    		if (gridcleared[x2][y1]==-1 && FeasibleLine(x1,y1,x2,y1) && FeasibleLine(x2,y1,x2,y2)){
    			addline(x1,y1,x2,y1);
    			addline(x2,y1,x2,y2);
    			return Math.abs(x1-x2)+Math.abs(y1-y2);
    		}
    	}
    	if (y1!=y2){
    		for (int i=0;i<8;i++){
        		if (x1!=i && x2!=i){
        			if (gridcleared[i][y1]==-1 && gridcleared[i][y2]==-1 &&
        					FeasibleLine(x1,y1,i,y1) && 
        					FeasibleLine(i,y1,i,y2) &&
        					FeasibleLine(i,y2,x2,y2)){
        				addline(x1,y1,i,y1);
        				addline(i,y1,i,y2);
            			addline(i,y2,x2,y2);
            			return Math.abs(x1-i)+Math.abs(y1-y2)+Math.abs(i-x2);
        			}
        		}
        	}
    	}
    	if (x1!=x2){
    		for (int i=0;i<12;i++){
        		if (y1!=i && y2!=i){
        			if (gridcleared[x1][i]==-1 && gridcleared[x2][i]==-1 &&
        					FeasibleLine(x1,y1,x1,i) && 
        					FeasibleLine(x1,i,x2,i) &&
        					FeasibleLine(x2,i,x2,y2)){
        				addline(x1,y1,x1,i);
        				addline(x1,i,x2,i);
            			addline(x2,i,x2,y2);
            			return Math.abs(y1-i)+Math.abs(x1-x2)+Math.abs(i-y2);
        			}
        		}
        	}
    	}
		return 0;    	
    }
	private void addline(int x1, int y1, int x2, int y2) {
		if (y1==y2){
			if (x1>x2){
				int t=x1;
				x1=x2;
				x2=t;
			}
			for (int i=x1;i!=x2;i++){
				JLabel label = new JLabel(colImage);
				jFrame.getLayeredPane().add(label, new Integer(0));
				label.setBounds(grid[i][y1].getX()+165,grid[i][y1].getY()+75,colImage.getIconWidth(),colImage.getIconHeight());
			}
		}
		if (x1==x2){
			if (y1>y2){
				int t=y1;
				y1=y2;
				y2=t;
			}
			for (int i=y1;i!=y2;i++){
				JLabel label = new JLabel(rawImage);
				jFrame.getLayeredPane().add(label, new Integer(0));
				label.setBounds(grid[x1][i].getX()+165,grid[x1][i].getY()+75,rawImage.getIconWidth(),rawImage.getIconHeight());
			}
		}

	}
	boolean clear(){
    	for (int i=0;i<8;i++)
    		for (int j=0;j<12;j++){
    			if (gridcleared[i][j]!=-1)
    				return false;
    		}
		return(true);
	}
     
}

