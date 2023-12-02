package io.github.surajkumar.client;

import io.github.surajkumar.server.HostCodec;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;

public class LaunchScreen extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaunchScreen.class);

    private JFrame frame;
    private final String uuid;

    public LaunchScreen(String uuid) {
        this.uuid = uuid;
        initializeFrame();
        addComponents();
    }

    private void initializeFrame() {
        frame = new JFrame("Remote Control");
        frame.setSize(410, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating a toolbar
        JToolBar toolBar = new JToolBar();
        JButton githubButton = new JButton("GitHub");
        githubButton.addActionListener(event -> openGitHub());

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(event -> System.exit(0));

        toolBar.add(githubButton);
        toolBar.addSeparator();
        toolBar.add(exitButton);

        this.setBackground(Color.WHITE);

        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
    }

    private void addComponents() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel titleLabel = new JLabel("Remote Control");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField uuidLabel = new JTextField("Your UUID is: " + "sample");
        uuidLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        uuidLabel.setEditable(false);
        uuidLabel.setBorder(null);

        JTextField status;

        {
            Host decoded = HostCodec.decode(uuid);
            if(pingServer("localhost", decoded.port())) {
                status = new JTextField("You are accepting incoming connections!");
                status.setForeground(Color.DARK_GRAY);
            } else {
                status = new JTextField("You are not able to accept incoming connections!");
                status.setForeground(Color.RED);
            }
            status.setFont(new Font("Arial", Font.BOLD, 17));
            status.setEditable(false);
            status.setBorder(null);
        }

        JLabel subtitleLabel = new JLabel("Enter remote's unique identifier");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField input = new JTextField(20);
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(event -> {
            String host = input.getText();
            Host decoded = HostCodec.decode(host);
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new RemoteClient(decoded.host(), decoded.port()));
            hideWindow();
        });

        add(titleLabel);
        add(subtitleLabel);
        add(input);
        add(connectButton);
        add(uuidLabel);
        add(status);
    }


    private void openGitHub() {
        String url = "https://github.com/surajkumar/remote-control-tool";
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                } else {
                    LOGGER.warn("Browse action not supported");
                }
            } else {
                LOGGER.error("Desktop API is not supported on your system");
            }
        } catch (Exception e) {
            LOGGER.error("Error while launching web page: {}", e.getMessage(), e);
        }
    }

    public void hideWindow() {
        frame.setVisible(false);
    }

    public void showWindow() {
        frame.setVisible(true);
    }

    private static boolean pingServer(String host, Integer port) {
        Socket socket;
        try {
            socket = new Socket(host, port);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
