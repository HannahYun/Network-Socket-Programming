import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class ChatClient  extends JFrame implements ActionListener, Runnable {
	
	String userNickname = null;
	int roomId;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	
	JTextField textField,whisperField;
	JTextArea textArea,whisperArea;
	JScrollPane scrollPane, participantPane, whisperPane;
	JPopupMenu popupMenu;
	
	JMenuItem whisper;
	JLabel infoLabel,whisperLabel;
	JButton exitBtn,whisperBtn;
	String msg,sendMessage;
	
	JList<String> participantJList;
	Vector<String> participantNicknames;
	
	public ChatClient(String userNickname, int roomId){
		this.roomId = roomId;
		this.userNickname = userNickname;
	
        setLayout(null);
        setComponent();
        
        add(infoLabel);
        add(textField);
        add(scrollPane);
        add(exitBtn);
        add(participantPane);
        add(whisperBtn);
        add(whisperField);
        add(whisperPane);
        add(whisperLabel);
       
        setSize(900, 700);
        setVisible(true); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		try {
			//clientSocket = new Socket("127.0.0.1", 7790); //로컬 Host
			//clientSocket = new Socket("10.101.47.15", 7790);
			clientSocket = new Socket("10.101.36.18", 7790); //컴퓨터 IP주소 사용함.
			
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			out.print("entering_user_nickname"+userNickname);
			out.print(" entering_room_id"+roomId);
			out.flush();
			
		} catch (Exception e) {
            System.out.println("error [ChatClient] run" + e);
		}
	
		Thread thraead = new Thread(this);
		thraead.start();
		
		addEvent();
	}
	private void addEvent() {
        participantJList.addMouseListener(
        		new MouseAdapter() {
        			@Override
        			public void mouseClicked(MouseEvent event) {
        				showWhisperField(event);
        			}
    			}
		);
		
		whisperBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage = whisperField.getText();
				if (sendMessage.length()>0) {
			    out.print("whisper_msg");
			    String toNickname = whisperLabel.getText().split(" ")[0];
			    out.print(toNickname+"-");
		        out.print(userNickname+" >> "+sendMessage);
		        out.flush();
		        
		    	whisperBtn.setVisible(false);
				whisperField.setVisible(false);
		        whisperField.setText("");
		        whisperLabel.setVisible(false);
				}
			}
		});
	}

	
	private void setComponent() {

        infoLabel = new JLabel("채팅");
        infoLabel.setBounds(235,10,90,30);
        
        
		textField = new JTextField();
        textField.setBounds(40,465,420,40);
        textField.addActionListener(this);
        textField.requestFocus();

		textArea = new JTextArea();
        textArea.setEditable(false);
        
		scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setBounds(40,40,420,400);
		
        exitBtn =  ButtonUI.normalColoredButton("나가기",new Color(220, 221, 225), Color.BLACK, 14);
        
        exitBtn.addActionListener(this);
        exitBtn.setBounds(40,610,420,40);
        
        whisperArea = new JTextArea(); 
        whisperArea.setEditable(false);
		whisperArea.append("이곳은 개인 쪽지함입니다.\n"); // 안 그려진다: 이유: add 해서 그랬음(지웠더니 잘 동작)

        whisperField = new JTextField();
        whisperField.requestFocus();
        whisperField.addActionListener(this);
        whisperField.setBounds(500,300,210,40);
        whisperField.setVisible(false);

        whisperBtn = ButtonUI.normalColoredButton("보내기",new Color(30, 144, 255), Color.WHITE, 10);
        whisperBtn.setBounds(720,300,100,40);
        whisperBtn.setVisible(false);
        
        whisperLabel = new JLabel("");
        whisperLabel.setVisible(false);
        whisperLabel.setFont(Fonts.normoalFont(13));
        whisperLabel.setBounds(500,270,210,40);

        participantJList = new JList<String>();
        participantJList.setFixedCellHeight(20);
        participantJList.setBorder(new LineBorder(Color.DARK_GRAY,2));
        participantPane = new JScrollPane(participantJList,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        participantPane.setBounds(500,40,350,200);
        
        participantNicknames = new Vector<String>();
      
    
        whisperPane = new JScrollPane(whisperArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        whisperPane.setBounds(500,350,350,300);
       
	}
	
	private void showWhisperField(MouseEvent event) {
		String clickedNickname = (String) participantJList.getSelectedValue();
		if (!clickedNickname.equals(userNickname)) { //자신이 자신에게 쪽지를 보내면 안 된다.
			personalMessageDialog(clickedNickname);	
		}
	}
	
	private synchronized void updateParticipant(Vector<String> participantsName) {
		participantJList.setListData(participantsName);
		participantJList.validate();
	}
	
	private void personalMessageDialog(String toNickname) {
		whisperLabel.setText(null);
		whisperLabel.setText(toNickname + " 님에게 쪽지를 보냅니다.");
		
		whisperLabel.setVisible(true);
		whisperField.setVisible(true);
		whisperBtn.setVisible(true);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				while ((msg = in.readLine()) != null) {
					if (msg.startsWith("participant_list")) {
						
						participantNicknames = new Vector<String>();
						msg = msg.replaceAll("participant_list ", "");
						String[] nicknameList = msg.split(" ");
						for (final String value: nicknameList) {
							participantNicknames.add(value);
						}
						updateParticipant(participantNicknames);
						continue;
					} 
					else if (msg.startsWith("whisper_msg_arrived")) {
						msg = msg.replaceAll("whisper_msg_arrived", "");
						whisperArea.append(msg + "\n"); // 안 그려진다: 이유: add 해서 그랬음(지웠더니 잘 동작)

					}
					else if(msg.startsWith(userNickname)) {
						// 내가 보낸 메세지라면 닉네임 대신 나: 이렇게 보내기
						msg = msg.replaceAll(userNickname, "");
						msg = "me"+msg;
						textArea.append(msg + "\n");
					}
					else {
						textArea.append(msg + "\n");	
					}
            	}
			} catch (Exception e) {
    			System.out.println("[ChatGUI] run method failed");
    			e.printStackTrace();
            }
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == exitBtn) { // 방을 나간다면
	          exitRoom();
	          dispose();
	          new RoomMain();
	     }
	    else if (e.getSource()== textField) { // 채팅방에 메시지를 보내는 경우
		    sendMessage = textField.getText();
		    textField.setText("");
		    if (sendMessage.length()>0) {
		        out.print(userNickname+": ");
		        out.println(sendMessage);
		        out.flush();
		    }	        
	    } 
	    else if (e.getSource()==whisperField) {  // 귓속 메시지를 보내는 경우
	    	sendMessage = whisperField.getText();
		    out.print("whisper_msg");
		    String toNickname = whisperLabel.getText().split(" ")[0];
		    out.print(toNickname+"-");
	        out.print(userNickname+" >> "+sendMessage);
	        out.flush();
	        
	    	whisperBtn.setVisible(false);
			whisperField.setVisible(false);
			whisperLabel.setVisible(false);
	        whisperField.setText("");
	    }
	}
	
	public void exitRoom() { // 방을 나갈 경우
		// 서버에게 특정 유저가 방을 나간다는 것을 알린다 : 방에 남아있는 사람에게 알리기 위해서
	   out.print("exit_room"+userNickname+" ");
	   out.print("exit_room_id"+roomId);
       out.flush();
	}
	
}
