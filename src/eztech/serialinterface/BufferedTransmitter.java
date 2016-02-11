/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yami
 */
public class BufferedTransmitter extends Thread {

    static int BUFFER_MAX_SIZE = 1400;
    static int BUFFER_ACTUAL_MAX_SIZE = BUFFER_MAX_SIZE - 10;
    final static byte MESSAGE_START = '@';
    final SerialInterface si;
    final ArrayList<byte[]> dataBuffer = new ArrayList<>();
    boolean checksum;
    volatile Boolean acknowledged = false;

    public BufferedTransmitter(SerialInterface si) {
        this(si, false);
    }

    public BufferedTransmitter(SerialInterface si, boolean checksum) {
        super("Buffered Transmitter");
        this.si = si;
        this.checksum = checksum;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                routine();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void routine() throws Exception {
        synchronized (this) {
            while (!dataBuffer.isEmpty()) {
                short dataLength = 0, messages = 0;
                while (messages < dataBuffer.size() && dataLength + dataBuffer.get(messages).length < BUFFER_ACTUAL_MAX_SIZE) {
                    dataLength += dataBuffer.get(messages).length;
                    messages++;
                }
                ByteBuffer msgBuffer = ByteBuffer.allocate(checksum ? dataLength + 5 : dataLength);
                List<byte[]> messagesToSend = dataBuffer.subList(0, messages);
                if (checksum) {
                    addCheckSumHeader(msgBuffer, messagesToSend);
                }
                messagesToSend.stream().forEach((msg) -> msgBuffer.put(msg));
                dataBuffer.removeAll(dataBuffer.subList(0, messages));
                acknowledged = false;
                if (!acknowledged) {
                    si.directSend(msgBuffer.array());
                    if (!dataBuffer.isEmpty()) {
                        wait();
                    }else{
                        wait(100);
                    }
                }
            }
        }
        if (dataBuffer.isEmpty()) {
            sleep(100);
        }
    }

    public synchronized void setAcknowledge(boolean acknowledged) {
        this.acknowledged = acknowledged;
        notify();
    }

    synchronized void send(byte... message) {
        dataBuffer.add(message);
    }

    public void setChecksum(boolean checksum) {
        this.checksum = checksum;
    }

    void addCheckSumHeader(ByteBuffer msgBuffer, List<byte[]> msgList) {
        short sum = 0, dataLength = 0;
        for (byte[] msg : msgList) {
            for (byte b : msg) {
                sum += Byte.toUnsignedInt(b);
                dataLength++;
            }
        }
        msgBuffer.put(MESSAGE_START);
        msgBuffer.putShort(dataLength);
        msgBuffer.putShort(sum);
    }
}
