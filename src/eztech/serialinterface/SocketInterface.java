/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.serialinterface.exceptions.ConnectionFailedException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author yami
 */
public class SocketInterface extends SerialInterface {

    Socket socket;
    static final int MACHINE_SERVER_PORT = 8080;
    static final String MACHINE_SERVER_IP = "192.168.0.101";

    public SocketInterface() {
        super(true, true);
        buffredTransmitter.setChecksum(false);
    }

    @Override
    public void connectToPort(ConnectionParamters params) throws ConnectionFailedException {
        try {
            socket = new Socket(MACHINE_SERVER_IP, MACHINE_SERVER_PORT);
            socket.setKeepAlive(true);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            socket.setOOBInline(true);
            socket.setPerformancePreferences(1, 2, 0);
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
            }, "Socket Listener").start();
            connected = true;
        } catch (IOException ex) {
            throw new ConnectionFailedException(ex);
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
}
