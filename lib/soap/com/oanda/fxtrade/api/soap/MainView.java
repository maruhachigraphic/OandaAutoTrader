/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.oanda.fxtrade.api.soap.Logger.LogStream;
import com.oanda.fxtrade.api.soap.MulticlientConnectionServer.CountListener;

public class MainView extends JFrame implements ActionListener{
	private static final long serialVersionUID = -3578030389685129134L;

	public class ControlPanel extends JPanel implements CountListener {
		private static final long serialVersionUID = 7106678547846615945L;
		private JButton   stopButton        = new JButton("Stop");
		private JLabel    proxyVersionLabel = new JLabel("Proxy version : ");
		private JLabel    connectionsLabel  = new JLabel("Connections   : 0");
		//private JComboBox clientsComboBox   = new JComboBox();

        public ControlPanel() {
            setLayout(new BorderLayout());

            JPanel propertiesPanel = new JPanel();
            propertiesPanel.setBorder(new EmptyBorder(0, 0, 0, 5));

            GridLayout gridLayout = new GridLayout(0, 1);
            propertiesPanel.setLayout(gridLayout);

            proxyVersionLabel.setFont(new Font("Serif", Font.PLAIN, 12));
            proxyVersionLabel.setHorizontalTextPosition(JLabel.LEFT);
            propertiesPanel.add(proxyVersionLabel);

            connectionsLabel.setFont(new Font("Serif", Font.PLAIN, 12));
            connectionsLabel.setHorizontalTextPosition(JLabel.LEFT);
            propertiesPanel.add(connectionsLabel);

            add(propertiesPanel, BorderLayout.WEST);


            //clientsComboBox.setBorder(new EmptyBorder(0, 0, 0, 5));
            //add(clientsComboBox, BorderLayout.CENTER);

            stopButton.setFont(new Font("Serif", Font.PLAIN, 12));
            stopButton.setPreferredSize(new Dimension(100, 25));
            stopButton.setActionCommand("stop");

            add(stopButton, BorderLayout.EAST);
        }

        public void setButtonListener(ActionListener actionListener) {
        	stopButton.addActionListener(actionListener);
        }

        public void setConnectionCount(int count) {
        	connectionsLabel.setText("Connections : " + count);
        }

		public void inform(int connectionCount) {
			setConnectionCount(connectionCount);
		}

		public void setProxyVersion(String proxyVersion) {
			proxyVersionLabel.setText("Proxy version : " + proxyVersion);
		}

		public void addClientHost(String clientIP) {
			//clientsComboBox.addItem(clientIP);
		}
    }

	public class LogPanel extends JPanel implements LogStream {
		private static final long serialVersionUID   = -6562115732080608287L;
		private static final int MAX_LOG_LINES_COUNT = 1000;
		private JTextArea logText = new JTextArea();
        public LogPanel() {
            setLayout(new BorderLayout());

            logText.setFont(new Font("Serif", Font.PLAIN, 12));
            logText.setEditable(false);

            add(new JScrollPane(logText), BorderLayout.CENTER);
        }

		public void print(String text) {
			if(logText.getLineCount() > MAX_LOG_LINES_COUNT) {
				logText.replaceRange("", 0, 200);
			}
			logText.append(text + "\r\n");
			logText.setCaretPosition(logText.getText().length());
		}
    }

	private ControlPanel controlPanel = new ControlPanel();
	private LogPanel logPanel = new LogPanel();

	public MainView(SoapServer soapServer) {
		this.setTitle("FXTrade SOAP Server Monitor");
		this.setSize(450, 300);

		controlPanel.setButtonListener(this);
		controlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(controlPanel, BorderLayout.NORTH);

		logPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(logPanel, BorderLayout.CENTER);

		centerOnScreen();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLogger(soapServer.getLogger());
		setConnectionCountPublisher(soapServer.getConnectionServer());
		setProxyVersion(com.oanda.fxtrade.api.Configuration.getProxyReleaseVersion());

		setVisible(true);
	}

	private void setProxyVersion(String proxyVersion) {
		controlPanel.setProxyVersion(proxyVersion);
	}

	private void setConnectionCountPublisher(MulticlientConnectionServer connectionServer) {
		connectionServer.setCountListener(controlPanel);
	}

	private void setLogger(Logger logger) {
		logger.setOutputStream(logPanel);
	}

	private void centerOnScreen() {
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;

        // Move the window
        setLocation(x, y);
    }

	public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("stop")) {
            System.exit(0);
        }
	}
}
