/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yami
 */
public class SocketInterface extends SerialInterface {

    Socket socket;
    static final int MACHINE_SERVER_PORT = 8080;
    static final String MACHINE_SERVER_IP = "192.168.1.101";

    public SocketInterface() {
        msgbundelled = true;
        buffredTransmitter.setChecksum(false);
        msgBuffered = true;
    }

    @Override
    public void connectToPort(SerialPortParamters params) {
        try {
            socket = new Socket(MACHINE_SERVER_IP, MACHINE_SERVER_PORT);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            new Thread(() -> {
                while (socket.isConnected()) {
                    try {
                        if (in.available() > 0) {
                            serialEventHandler();
                        } else {
                            Thread.sleep(10);
                        }
                    } catch (Exception ex) {
                    }
                }
            },"Socket Listener").start();
            connected = true;
        } catch (IOException ex) {
            Logger.getLogger(SocketInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> getAvailablePorts() {
        return null;
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Error Close");
        }
    }

    @Override
    public SerialPortParamters getConfigrations() {
        return null;
    }

}
