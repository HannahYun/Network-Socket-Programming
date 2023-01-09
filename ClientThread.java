import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.Vector;

public class ClientThread implements Runnable {
	Socket socket;
	String[] values = null;
	String nickname;
	
	int roomId;
	InputStream in;
	OutputStream out;
	byte[] buffer;
	StringBuilder sb;
	
	public ClientThread(Socket socket) {
		this.socket = socket;
	}
	
	public String getNickname() {
		return nickname;
	}

	@Override
	public void run() {
		try {
			 while(true) {
				 in = socket.getInputStream();
				 buffer = new byte[512];
				 int length = in.read(buffer);
				 while (length == -1) 
					 throw new IOException();
				 
				 if (length > 0) {
					 // 도착한 메시지가 있다면
					 String message = new String(buffer,0,length,"UTF-8");
					
					 // 특정 유저가 방을 나간 경우
					 if (message.startsWith("exit_room")) {
						 values = message.split(" ");
						 nickname = values[0].replaceAll("exit_room", "");
						 roomId = Integer.valueOf(values[1].replaceAll("exit_room_id", ""));
						 sendToRoom(nickname+"님이 방을 나갔습니다.\n");
				
						 // 해당 유저가 방을 나갔다는 메시지를 보낸 후에, 나간 유저 소켓 정리
						 removeClientSocket(roomId,nickname);
					 }
					 
					 // 특정 유저가 방에 입장한 경우 , 해당 유저의 소켓을 이동시킨다.
					 else if (message.startsWith("entering_user_nickname")){
						 values = message.split(" ");
						 nickname = values[0].replaceAll("entering_user_nickname", "");
						 roomId = Integer.valueOf(values[1].replaceAll("entering_room_id", ""));
						 changeRoom(roomId);
						 
					 }
					 
					 // 특정 유저가 귓속말 메시지를 보낸 경우
					 else if (message.startsWith("whisper_msg")) {
						 values = message.split("-");
						 
						 String toNickname = values[0].replaceAll("whisper_msg", "");
						 String whisperMessage = values[1];
						 whisperMessage(toNickname, whisperMessage);
					 }
					 // 특정 유저가 채팅창에 메시지를 보낸 경우
					 else {
						 sendToRoom(message);
					 }
				 }
			 }
		 } catch(Exception e) {}
	}
	
	private void removeClientSocket(Integer roodId, String nickname) {
		Vector<ClientThread> toClients = Server.roomClients.get(roodId);
		
		for (ClientThread client: toClients) {
			if (client.getNickname().equals(nickname)) {
				try {
					client.socket.close();
					Server.roomClients.get(roodId).remove(client);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		sendParticipantList(roodId);
	}
	
	//해당 방에 입장하고 있는 모든 사람의 닉네임을 보내준다.
	private synchronized void sendParticipantList(Integer roomId) {
		
		Vector<ClientThread> toClients = Server.roomClients.get(roomId);
		
		sb = new StringBuilder(); 
		sb.append("participant_list ");
		
		for (ClientThread thread: toClients) {
			sb.append(thread.getNickname()).append(" ");
		}
		
		sb.append("\n");
		
		for (ClientThread thread: toClients) {
			thread.send(sb.toString());
		}
	}
	
	// 메시지를 보낸 유저가 참여하고 있는 방에만 메시지를 보내기
	private synchronized void sendToRoom(String message) {
		Integer toRoomId = this.roomId;
		Vector<ClientThread> toClients = Server.roomClients.get(toRoomId);
		for (ClientThread thread: toClients) {
			thread.send(message);
		}
	}
	private synchronized void whisperMessage(String toNickname, String msg) {
		Vector<ClientThread> toClients = Server.roomClients.get(this.roomId);
		for (ClientThread client: toClients) {
			if (client.getNickname().equals(toNickname)) {
				client.send("whisper_msg_arrived");
				client.send(msg+"\n");
			}
		}
	}
	public synchronized void send(String message) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch(Exception e) {
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		new Thread(thread).start();
	}
	
	private synchronized void changeRoom(Integer roomId) {
        Server.roomClients.putIfAbsent(roomId, new Vector<ClientThread>());
        Server.roomClients.get(roomId).add(this);
        
        sendWelcomeMessage(this.nickname);
	}
	
	private synchronized void sendWelcomeMessage(String newUsername) {

		Integer toRoomId = this.roomId;
		Vector<ClientThread> toClients = Server.roomClients.get(toRoomId);
		
		for (ClientThread thread: toClients) {
			if (!thread.equals(this)) {
				thread.send(newUsername+"님이 입장했습니다.\n");
				sendParticipantList(toRoomId);
			}
			else {
				thread.send("방에 입장했습니다!\n");
				sendParticipantList(toRoomId);
			}
		}
	}
}
