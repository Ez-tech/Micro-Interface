/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.protocol.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aoki-kun
 */
public abstract class SerialInterface {

    private static byte[] toByteArray(int... numbers) {
        byte[] buffer = new byte[numbers.length * Integer.BYTES];
        for (int i = 0; i < numbers.length; i++) {
            byte[] bytes = ByteBuffer.allocate(Integer.BYTES).putInt(numbers[i]).array();
            System.arraycopy(bytes, 0, buffer, i * Integer.BYTES, bytes.length);
        }
        return buffer;
    }

    protected OutputStream out;
    protected InputStream in;
    protected boolean connected = false, busy = false;
    private MicroHandler microHandler;

    ArrayList<Message> slaveMessages = new ArrayList<>();

    public abstract void connectToPort(SerialPortParamters params);

    public abstract List<String> getAvailablePorts();

    public void sendMessage(Message header, int... body) {
        sendMessage(header, toByteArray(body));
    }

    public void sendMessage(Message header, byte... body) {
        header.body = body;
        sendMessage(header);
    }

    public void sendMessage(Message msg) {
        if (msg.bodyLength > 0 && msg.body != null) {
            if (msg.bodyLength == msg.body.length) {
                send(msg.header);
                for (byte c : msg.body) {
                    send(c);
                }
            } else {
                System.err.println("Message body is not enough");
            }
        } else {
            send(msg.header);
        }
    }

    private void send(byte... message) {
        try {
            while (busy) {
                Thread.sleep(1);
            }
            busy = true;
            out.write(message);
            Thread.sleep(10);
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
        }
        busy = false;
    }

    public void serialEventHandler() {
        List<Byte> buffer = new ArrayList<>();
        try {
            while (in.available() > 0) {
                byte b = (byte) in.read();
                buffer.add(b);

                for (Message msg : slaveMessages) {
                    if (msg.header == b) {
                        msg.body = null;
                        if (msg.bodyLength > 0) {
                            msg.body = new byte[msg.bodyLength];
                            while (in.available() < msg.body.length);
                            for (int i = 0; i < msg.body.length; i++) {
                                msg.body[i] = (byte) in.read();
                            }
                        }
                        microHandler.processMicroMessage(msg);
                        break;
                    }
                }
                Thread.sleep(1);
            }
            byte[] bufferArray = new byte[buffer.size()];
            for (int i = 0; i < bufferArray.length; i++) {
                bufferArray[i] = buffer.get(i);
            }
            System.out.println(new String(bufferArray));
        } catch (IOException ex) {
            Logger.getLogger(RxTxISerial.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SerialInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected int read() throws IOException {
        in.available();
        return in.read();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setMicroHandler(MicroHandler micro) {
        this.microHandler = micro;
    }

    public ArrayList<Message> getSlaveMessages() {
        return slaveMessages;
    }

    public abstract SerialPortParamters getConfigrations();

}
