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

    HashMap<Byte, Byte> slaveMessages = new HashMap<>();
    Logger logger;

    public SerialInterface() {
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

    public void sendMessage(Message header, int... body) {
        sendMessage(header, toByteArray(body));
    }

    public void sendMessage(Message header, byte... body) {
        header.setBody(body);
        sendMessage(header);
    }

    public void sendMessage(Message msg) {
        if (msg.hasBody()) {
            send(msg.getHeader());
            for (byte c : msg.getBody()) {
                send(c);
            }
        } else {
            send(msg.getHeader());
        }
    }

    protected synchronized void send(byte... message) {
        if (connected && out != null) {
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
        } else {
            Logger.getLogger(this.getClass().getName()).severe("Error: Not Connected");
        }
    }

    public void serialEventHandler() {
        List<Byte> buffer = new ArrayList<>();
        try {
            while (in.available() > 0) {
                byte header = (byte) in.read();
                buffer.add(header);
                if (slaveMessages.containsKey(header)) {
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
                }
                Thread.sleep(1);
            }
            byte[] bufferArray = new byte[buffer.size()];
            for (int i = 0; i < bufferArray.length; i++) {
                bufferArray[i] = buffer.get(i);
            }
            System.out.print(new String(bufferArray));
            // logger.info(new String(bufferArray));
        } catch (IOException | InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
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

    public void addSlaveMessage(Message msg) {
        slaveMessages.put(msg.getHeader(), (byte) msg.getBodyLength());
    }

    public void addSlaveMessages(List<Message> messages) {
        messages.forEach(msg -> addSlaveMessage(msg));
    }

    public abstract SerialPortParamters getConfigrations();

}
