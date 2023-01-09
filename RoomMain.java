import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomMain extends JFrame implements ActionListener {
    private JPanel[] roomPanels = new JPanel[8];
    private JLabel[] labels = new JLabel[8];
    private JButton[] buttons = new JButton[8];
   
    public RoomMain() {
        setLayout(null);
        for (int i = 0; i < 8; i++) {
            roomPanels[i] = createPanel();
            
            buttons[i] = new JButton("입장하기");
           
    		buttons[i] = ButtonUI.normalColoredButton("입장하기 가기",new Color(255, 159, 67), Color.white,14);
            buttons[i].addActionListener(this);
            
            labels[i] = new JLabel("               "+String.valueOf(i+1)+"번 방");
            labels[i].setFont(Fonts.boldFont(14));

            roomPanels[i].add(labels[i], BorderLayout.CENTER);
            roomPanels[i].add(buttons[i], BorderLayout.SOUTH);
            roomPanels[i].setBackground(Color.WHITE);
            
            roomPanels[i].setBorder(new LineBorder(new Color(254, 202, 87),4));
            
            if (i > 3) {
                roomPanels[i].setBounds(30 + (i - 4) * 250, 400, 200, 150);
                add(roomPanels[i]);
            } else {
                roomPanels[i].setBounds(30 + i * 250, 180, 200, 150);
                add(roomPanels[i]);
            }
        }
        JLabel label = new JLabel("원하는 방에 입장해주세요.");
        label.setFont(Fonts.boldFont(25));
        add(label);
        label.setBounds(370,50,400,60);
        		
        setSize(1000, 700);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createPanel() {
        JPanel jPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Dimension arcs = new Dimension(15, 15);
                int width = getWidth();
                int height = getHeight();
                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setColor(Color.white);
                graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);// 배경색 흰색으로
                graphics.setColor(Color.white);
                graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);// border 흰색으로
            }
        };
        jPanel.setLayout(new BorderLayout(20, 10));
        return jPanel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
		int num = 0;
		for (int i = 0; i < 8; i++) {
			if (e.getSource() == buttons[i]) {
				int roomNumber = i+1;
				try {
				   new ChatClient(StartMain.userNickname, roomNumber);
				   //new Thread(new ChatClient(StartMain.userNickname, roomNumber)).start();	
				} catch (Exception ex) {
				} finally {
					dispose();
				}
			}
		}
    }
}
