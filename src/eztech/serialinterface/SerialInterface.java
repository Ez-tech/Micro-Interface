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
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Aoki-kun
 */
public abstract class SerialInterface {

    public static final byte ACKNOWLEDGE = 0x55;

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
    BuffredTransmitter buffredTransmitter;
    HashMap<Byte, Byte> slaveMessages = new HashMap<>();
    Logger logger;
    protected boolean msgBuffered, doubleBuffred, msgbundelled;

    public SerialInterface() {
        buffredTransmitter = new BuffredTransmitter(this);
        try {
            logger = Logger.getLogger(this.getClass().getName());
            FileHandler fh = new FileHandler("logs/" + this.getClass().getName() + ".log", true);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.SEVERE);
            logger.setUseParentHandlers(false);
        } catch (IOException | SecurityException ex) {
            System.err.println("Logging init Error" + ex.getMessage());
        }
    }

    public abstract void connectToPort(SerialPortParamters params);

    public abstract List<String> getAvailablePorts();

    public abstract void disconnect();

    public void sendMessage(Message msg) {
        send(msg.toByteArray());
    }

    public void sendMessages(List<Message> msgList) {
        msgList.forEach(msg -> send(msg.toByteArray()));
    }

    public void sendBundle(List<Message> msgList) {
        int totalBundleSize = msgList.stream()
                .mapToInt((msg) -> msg.getBodyLength() + 1).sum();
        ByteBuffer buffer = ByteBuffer.allocate(totalBundleSize);
        msgList.forEach(msg -> buffer.put(msg.toByteArray()));
        send(buffer.array());
    }

    public synchronized void directSend(byte... message) {
        if (connected && out != null) {
            try {
                while (busy) {
                    Thread.sleep(1);
                }
                busy = true;
                if (msgbundelled) {
                    out.write(message);
                } else {
                    for (byte b : message) {
                        out.write(b);
                    }
                }
            } catch (InterruptedException | IOException e) {
                System.err.println(e.getMessage());
            }
            busy = false;
        } else {
            System.err.println("Error: Not Connected");
        }
    }

    private void buffredSend(byte... message) {
        buffredTransmitter.send(message);
    }

    private void send(byte... message) {
        if (msgBuffered) {
            buffredSend(message);
        } else {
            directSend(message);
        }
    }

    public void serialEventHandler() {
        StringBuilder msgBuffer = new StringBuilder();
        try {
            while (in.available() > 0) {
                byte header = (byte) in.read();
                if (header == ACKNOWLEDGE) {
                    byte b = (byte) in.read();
                    buffredTransmitter.setAcknowledge(!(b == 0));
                } else if (slaveMessages.containsKey(header)) {
                    Message msg = new Message(header, slaveMessages.get(header));
                    if (msg.hasBody()) {
                        int[] body = new int[msg.getBodyLength()];
                        while (in.available() < msg.getBodyLength());
                        for (int i = 0; i < msg.getBodyLength(); i++) {
                            body[i] = in.read();
                        }
                        msg.setBody(body);
                    }
                    microHandler.processMicroMessage(msg);
                } else {
                    msgBuffer.append((char) header);
                }
                Thread.sleep(1);
            }
            System.out.print(msgBuffer.toString());
        } catch (IOException | InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setMicroHandler(MicroHandler micro) {
        this.microHandler = micro;
    }

    public void addSlaveMessage(Message msg) {
        if (msg.getHeader() == ACKNOWLEDGE) {
            System.err.printf("%x, header is reserved", ACKNOWLEDGE);
            return;
        }
        slaveMessages.put(msg.getHeader(), (byte) msg.getBodyLength());
    }

    public void addSlaveMessages(List<Message> messages) {
        messages.forEach(msg -> addSlaveMessage(msg));
    }

    public abstract SerialPortParamters getConfigrations();

}
