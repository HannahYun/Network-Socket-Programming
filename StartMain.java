
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMain extends JFrame implements ActionListener {
    static String userNickname = null;
    private JPanel nicknamePanel;
    private JLabel nicknameLabel;
    private TextField nicknameField;
    private JButton enterBtn;
    
    public StartMain() {
        // 컴퍼넌트 생성
        nicknamePanel = new JPanel();
        setLayout();
        nicknamePanel.setLayout(null);
        add(nicknamePanel);
        setSize(500, 600);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void setLayout() {
        Font font = Fonts.normoalFont(20);
        nicknameLabel = new JLabel("닉네임을 입력해주세요");
        nicknameLabel.setFont(font);

        nicknameField = new TextField();
        nicknameField.setFont(font);
        enterBtn = ButtonUI.normalColoredButton("입장하기",Color.BLUE, Color.white,20);

        nicknameLabel.setBounds(150, 60, 200, 50);
        nicknameField.setBounds(90, 160, 300, 50);
        enterBtn.setBounds(170, 330, 140, 40);

        nicknamePanel.add(nicknameLabel);
        nicknamePanel.add(nicknameField);
        nicknamePanel.add(enterBtn);

        enterBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterBtn) {
            StartMain.userNickname = nicknameField.getText();
            new RoomMain();
            dispose();
        }
    }
}
