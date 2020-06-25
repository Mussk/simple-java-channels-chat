package zad1;

import javax.swing.*;
import java.awt.*;


public class GUI_Client extends JFrame {

   private String message = "";

   private JTextArea textArea;

   private String nickname = "";


    public String loadLoginWindow(){

         nickname = JOptionPane.showInputDialog(this,"Enter your nickname","Login",JOptionPane.YES_NO_OPTION);

        return nickname;
    }

    public void loadChatWindow(){

        JPanel panel = new JPanel(new BorderLayout());

        JPanel grid_panel = new JPanel(new GridLayout());

        panel.setPreferredSize(new Dimension(640,480));

        textArea = new JTextArea(5, 20);

        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setEditable(false);

        JTextField textField = new JTextField();

        JButton send_button = new JButton("Send!");

        send_button.addActionListener((o) ->{

            System.out.println("Button is pressed");

            if(o.getActionCommand().equals("Send!")) message = nickname + ": " + textField.getText();

        });

        grid_panel.add(textField,0);

        grid_panel.add(send_button,1);

        panel.add(textArea,BorderLayout.CENTER);

        panel.add(grid_panel,BorderLayout.PAGE_END);

        setTitle("Chat");

        add(panel);

        pack();

        setVisible(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
