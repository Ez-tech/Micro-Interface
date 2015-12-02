/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yami
 */
public class BuffredTransmitter extends Thread {

    static int BUFFER_MAX_SIZE = 250;
    static int BUFFER_ACTUAL_MAX_SIZE = BUFFER_MAX_SIZE - 10;
    final static byte MESSAGE_START = '@';
    final SerialInterface si;
    final ArrayList<byte[]> dataBuffer = new ArrayList<>();
    boolean checksum;
    volatile Boolean acknowledged = false;

    public BuffredTransmitter(SerialInterface si) {
        this(si, false);
    }

    public BuffredTransmitter(SerialInterface si, boolean checksum) {
        super("Buffred Transmitter");
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

    void routine() throws InterruptedException {
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
                acknowledged = false;
                while (!acknowledged) {
                    si.directSend(msgBuffer.array());
                    wait();
                }
                for (int i = 0; i < messages; i++) {
                    dataBuffer.remove(0);
                }
            }
        }
        if(dataBuffer.isEmpty()){
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
