/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.protocol.Message;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Aoki-kun
 */
public class SerialInterfaceTest {

    SerialInterface serialInterface;
    ArrayList<Byte> inBuffer;
    ArrayList<Byte> outBuffer;

    @Before
    public void setUp() {

        inBuffer = new ArrayList<>();
        outBuffer = new ArrayList<>();
        serialInterface = new SerialInterfaceImpl(inBuffer, outBuffer);
        serialInterface.connectToPort(null);
        serialInterface.buffredTransmitter = new BuffredTransmitter(serialInterface) {

            @Override
            synchronized void send(byte... message) {
                serialInterface.directSend(message);
            }

        };
    }

    /**
     * Test of sendMessage method, of class SerialInterface.
     */
    @Test
    public void testSendMessage_Message() {
        System.out.println("sendMessage");
        Message header = new Message((byte) 10, (byte) 3);
        header.setBody(2, 4, 8);
        serialInterface.sendMessage(header);
        // TODO review the generated test code and remove the default call to fail.
        assertArrayEquals(outBuffer.toArray(new Byte[]{}), new Byte[]{10, 2, 4, 8});
    }

    /**
     * Test of serialEventHandler method, of class SerialInterface.
     */
    @Test
    public void testSerialEventHandler() {
        System.out.println("serialEventHandler");
        serialInterface.setMicroHandler((Message msg) -> {
            assertEquals(6, (int) msg.getHeader());
            assertArrayEquals(new byte[]{1, 2, 3}, msg.getBody());
        });
        inBuffer.add((byte) 6);
        inBuffer.add((byte) 1);
        inBuffer.add((byte) 2);
        inBuffer.add((byte) 3);
        serialInterface.slaveMessages.put((byte) 6, (byte) 3);
        serialInterface.serialEventHandler();
    }

    /**
     * Test of isConnected method, of class SerialInterface.
     */
    @Test
    public void testIsConnected() {
        serialInterface.connectToPort(null);
        assertEquals(serialInterface.isConnected(), true);
    }

}
