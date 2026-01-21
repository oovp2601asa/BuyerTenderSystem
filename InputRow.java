import javax.swing.*;
import java.awt.*;

public class InputRow extends JPanel {

    private JTextField textField;
    private JButton saveButton;

    public InputRow(String labelText, String buttonText) {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel label = new JLabel(labelText);
        textField = new JTextField(15);
        saveButton = new JButton(buttonText);

        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Saved value: " + textField.getText()
            );
        });

        add(label);
        add(textField);
        add(saveButton);
    }
}
