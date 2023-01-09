import javax.swing.*;
import java.awt.*;

public class ButtonUI {
    public static JButton makeLabelButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        return button;
    }
    public static JButton normalColoredButton(String text, Color backColor, Color foreColor, int fontSize) {
        JButton button = new JButton(text);
        button.setForeground(foreColor);
        button.setBackground(backColor);
        button.setFont(new Font(null, Font.BOLD, fontSize));
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }
}
