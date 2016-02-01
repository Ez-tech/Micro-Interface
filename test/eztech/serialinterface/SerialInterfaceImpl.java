/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.serialinterface.SerialInterface;
import eztech.serialinterface.ConnectionParamters;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yami
 */
public class SerialInterfaceImpl extends SerialInterface {

    ArrayList<Byte> inb;
    ArrayList<Byte> outb;

    public SerialInterfaceImpl(ArrayList<Byte> inBuffer, ArrayList<Byte> outBuffer) {
        this.inb = inBuffer;
        this.outb = outBuffer;
    }

    @Override
    public void connectToPort(ConnectionParamters params) {
        connected = true;
        in = new InputStream() {

            @Override
            public int available() throws IOException {
                return inb.size();
            }

            @Override
            public int read() {
                byte b = inb.get(0);
                inb.remove(0);
                return b;
            }

        };

        out = new OutputStream() {

            @Override
            public void write(int b) {
                outb.add((byte) b);
            }
        };
    }

    @Override
    public List<String> getAvailablePorts() {
        ArrayList<String> portList = new ArrayList<>();
        return portList;
    }

    @Override
    public ConnectionParamters getConfigrations() {
        return null;
    }

    @Override
    public void disconnect() {
    }

    @Override
    public synchronized void directSend(byte... message) {
        super.directSend(message);
        notifyAll();
    }
}
