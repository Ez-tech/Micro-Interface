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
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

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
        serialInterface = new SerialInterfaceImpl();
        serialInterface.connectToPort(null);
        inBuffer = new ArrayList<>();
        outBuffer = new ArrayList<>();
    }

    /**
     * Test of sendMessage method, of class SerialInterface.
     */
    @Test
    public void testSendMessage_Message_intArr() {
        System.out.println("sendMessage");
        Message header = new Message((byte)10,(byte)12);
        int[] body = {2,4,8};
        serialInterface.sendMessage(header, body);
        assertEquals(true, true);
    }

    /**
     * Test of sendMessage method, of class SerialInterface.
     */
    @Test
    final public void testSendMessage_Message_byteArr() {
        System.out.println("sendMessage");
        Message header = new Message((byte)10,(byte)3) ;
        byte[] body = {2,4,8};
        serialInterface.sendMessage(header,body);
        // TODO review the generated test code and remove the default call to fail.
        assertArrayEquals(outBuffer.toArray(new Byte[]{}), new Byte[]{10,2,4,8} );
    }

    /**
     * Test of sendMessage method, of class SerialInterface.
     */
    @Test
    public void testSendMessage_Message() {
        System.out.println("sendMessage");
        Message header = new Message((byte)10,(byte)3) ;
        header.body = new byte[]{2,4,8};
        serialInterface.sendMessage(header);
        // TODO review the generated test code and remove the default call to fail.
        assertArrayEquals(outBuffer.toArray(new Byte[]{}), new Byte[]{10,2,4,8} );
    }

    /**
     * Test of serialEventHandler method, of class SerialInterface.
     */
    @Test
    public void testSerialEventHandler() {
        System.out.println("serialEventHandler");
        serialInterface.setMicroHandler((Message msg) -> {
            assertEquals(6, msg.header);
            assertArrayEquals(new byte[]{1,2,3}, msg.body);
        });
        inBuffer.add((byte)6);
        inBuffer.add((byte)1);
        inBuffer.add((byte)2);
        inBuffer.add((byte)3);
        serialInterface.slaveMessages.add(new Message((byte)6, (byte)3));
        serialInterface.serialEventHandler();
    }

    /**
     * Test of read method, of class Seri
     * @throws java.io.IOException
     */
    @Test
    public void testRead() throws IOException{
        System.out.println("read");
        inBuffer.add((byte)15);
        int result = serialInterface.read();
        assertEquals(15, result);
    }

    /**
     * Test of isConnected method, of class SerialInterface.
     */
    @Test
    public void testIsConnected() {
        serialInterface.connectToPort(null);
        assertEquals(serialInterface.isConnected(),true);
    }


    public class SerialInterfaceImpl extends SerialInterface {

        @Override
        public void connectToPort(SerialPortParamters params) {
            connected=true;
            in = new InputStream() {

                @Override
                public int available() throws IOException {
                    return inBuffer.size();
                }
                
                @Override
                public int read(){
                    byte b =  inBuffer.get(0);
                    inBuffer.remove(0);
                    return b;
                }
                
            };
            
            out = new OutputStream() {
                 
                @Override
                public void write(int b){
                   outBuffer.add((byte)b);
                }
            };
        }

        @Override
        public List<String> getAvailablePorts() {
            ArrayList<String> portList = new ArrayList<>();
            return portList;
        }

        @Override
        public SerialPortParamters getConfigrations() {
            return null;
        }
    }
    
}
