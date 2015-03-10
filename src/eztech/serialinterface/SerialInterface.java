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

    protected OutputStream out;
    protected InputStream in;
    protected boolean connected = false, busy = false;
    protected MicroHandler micro;

    ArrayList<Message> slaveMessages = new ArrayList<>();

    public abstract void connectToPort(SerialPortParamters params);

    public abstract List<String> getAvailablePorts();

     public void sendMessage(Message header, int... body) {
         sendMessage(header,toByteArray(body));
     }
    public void sendMessage(Message header, byte... body) {
        if (header.bodyLength == body.length) {
            sendHeader(header.header);
            for (byte c : body) {
                send(new byte[]{c});
            }
        } else {
            System.err.println("Message body is not enough");
        }
    }

    public void sendMessage(Message msg) {
        send(new byte[]{msg.header});
        send(msg.body);
    }

    private void sendHeader(byte header) {
        send(new byte[]{header});
    }

    private void send(byte...message) {
        try {
            while (busy) {
                Thread.sleep(1);
            }
            busy = true;
            out.write(message);
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
        }
        busy = false;
    }

    private static byte[] toByteArray(int... numbers) {
        byte[] buffer = new byte[numbers.length * Integer.BYTES];
        for (int i = 0; i < numbers.length; i++) {
            byte[] bytes = ByteBuffer.allocate(Integer.BYTES).putInt(numbers[i]).array();
            System.arraycopy(bytes, 0, buffer, i * Integer.BYTES, bytes.length);
        }
        return buffer;
    }

    public void serialEventHandler() {
        List<Byte> buffer = new ArrayList<>();
        try {
            while (in.available() > 0) {
                byte b = (byte) in.read();
                buffer.add(b);
                
                for (Message msg : slaveMessages) {
                    if (msg.header == b) {
                        msg.body =null;
                        if (msg.bodyLength > 0) {
                            msg.body = new byte[msg.bodyLength];
                            while (in.available() < msg.body.length);
                            for (int i = 0; i < msg.body.length; i++) {
                                msg.body[i] = (byte) in.read();
                            }
                        }
                        micro.processMicroMessage(msg);
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
        this.micro = micro;
    }

    abstract public SerialPortParamters getConfigrations();
}
