import javax.swing.*;
import java.awt.*;

public class MessageBlock extends JPanel {

    public MessageBlock(String userMessage) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel("User Sent: " + userMessage);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(messageLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        inputPanel.add(new InputRow("Input 1:", "Save 1"));
        inputPanel.add(new InputRow("Input 2:", "Save 2"));
        inputPanel.add(new InputRow("Input 3:", "Save 3"));

        add(inputPanel, BorderLayout.CENTER);
    }
}
