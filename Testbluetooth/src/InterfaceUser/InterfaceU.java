package InterfaceUser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.bluetooth.BluetoothStateException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Backtest.BlueConnection;

public class InterfaceU extends JFrame {
    private JButton button1 = new JButton("Envoyer");
    private JButton button2 = new JButton("Start");
    private JButton button3 = new JButton("Go");
    private JButton button4 = new JButton("Back");
    private JButton button5 = new JButton("Stab");
    private JButton button6 = new JButton("Off");
    private JButton closeButton = new JButton("X");
    private JTextField messageToSend = new JTextField(20);
    private BlueConnection bconnection;
    private JLabel label= new JLabel("Drone Médicale");

    public InterfaceU() {
        ConnectionListener connect = new ConnectionListener();
        button1.addActionListener(connect);
        button2.addActionListener(connect);
        button3.addActionListener(connect);
        button4.addActionListener(connect);
        button5.addActionListener(connect);
        button6.addActionListener(connect);
        try {
            bconnection = new BlueConnection();
        } catch (BluetoothStateException | InterruptedException e) {
            e.printStackTrace();
        }

        initInterface();
    }

    private void initInterface() {
        // Enlève les bordures de la fenêtre
        setUndecorated(true);
        
        setTitle("Amélioration de l'Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                // Charge l'ima	ge de fond depuis un fichier (assurez-vous d'avoir l'image dans le même répertoire)
                ImageIcon background = new ImageIcon("src/InterfaceUser/fond2.jpg");
                // Dessine l'image de fond qui couvre toute la fenêtre
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BorderLayout());
        label.setFont(new Font("Arial", Font.BOLD, 14)); 
        Font customFont = new Font("Arial", Font.BOLD, 12); // Personnalisation de la police
        
        button1.setFont(customFont);
        button2.setFont(customFont);
        button3.setFont(customFont);
        button4.setFont(customFont);
        button5.setFont(customFont);
        button6.setFont(customFont);
        messageToSend.setFont(customFont);

        // Personnalisation des boutons
        customizeButton(button1);
        customizeButton(button2);
        customizeButton(button3);
        customizeButton(button4);
        customizeButton(button5);
        customizeButton(button6);
        closeButton.setFont(customFont);
        closeButton.setBackground(new Color(220, 220, 220));
        closeButton.setForeground(Color.red);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel panel1 = new JPanel();
        panel1.add(messageToSend);
        panel1.add(button1);

        JPanel panel2 = new JPanel();
        panel2.add(button2);
        panel2.add(button3);
        panel2.add(button4);
        panel2.add(button5);
        panel2.add(button6);
        
        JPanel panel4= new JPanel();
        JPanel panel5= new JPanel();
        

        messageToSend.setPreferredSize(new Dimension(100, 30));
        messageToSend.setBackground(new Color(240, 240, 240)); 
        messageToSend.setForeground(Color.BLACK);
        
        panel4.setLayout(new BorderLayout());
        panel4.add(closeButton,BorderLayout.EAST);
        panel4.add(label,BorderLayout.CENTER);
        
        
        panel5.setLayout(new BorderLayout());
        panel5.add(panel4,BorderLayout.NORTH);
        panel5.add(panel1,BorderLayout.SOUTH);
        
        panel.add(panel5, BorderLayout.NORTH);
        panel.add(panel2, BorderLayout.SOUTH);
        
        setJMenuBar(new JMenuBar());
        setContentPane(panel);
        setSize(350, 280);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void customizeButton(JButton button) {
        button.setBackground(new Color(100, 100, 100)); // Couleur de fond du bouton
        button.setForeground(Color.WHITE); // Couleur du texte du bouton
        button.setFocusPainted(false); // Enlève la bordure autour du texte
        button.setBorderPainted(false); // Enlève la bordure du bouton
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
    }

    class ConnectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (arg0.getSource() == button1)
                bconnection.sendMess(messageToSend.getText());
            if (arg0.getSource() == button2)
                bconnection.sendMess("on0");
            if (arg0.getSource() == button3)
                bconnection.sendMess("decollage0");
            if (arg0.getSource() == button4)
                bconnection.sendMess("ater0");
            if (arg0.getSource() == button5)
                bconnection.sendMess("stab0");
            if (arg0.getSource() == button6)
                bconnection.sendMess("off0");
        }
    }
}
