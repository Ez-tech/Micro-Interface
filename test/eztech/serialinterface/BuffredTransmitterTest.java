/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.serialinterface.exceptions.ConnectionFailedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yami
 */
public class BuffredTransmitterTest {

    ArrayList<Byte> in, out;
    SerialInterface si;
    BufferedTransmitter instance;

    public BuffredTransmitterTest() {
    }

    @Before
    public void setUp() {
        in = new ArrayList<>();
        out = new ArrayList<>();
        si = new SerialInterfaceImpl(in, out);
        try {
            si.connectToPort(null);
        } catch (ConnectionFailedException ex) {
        }
        instance = new BufferedTransmitter(si);
    }

    @Test
    public void testSetAcknowledge() {
        System.out.println("setAcknowledge");
        instance.setAcknowledge(true);
        assertTrue(instance.acknowledged);
    }

    @Test
    public void testSend() throws Exception {
        System.out.println("send");
        instance.send("Hi".getBytes());
        assertArrayEquals("Hi".getBytes(), instance.dataBuffer.get(instance.dataBuffer.size() - 1));
    }

    @Test
    public void testRun() {
    }

    public void initRoutineTest(final byte[] sample, Runnable action) {
        synchronized (si) {
            try {
                instance.send(sample);
                si.wait();
                action.run();
            } catch (InterruptedException ex) {
            }
        }
        while (!out.isEmpty()) {
            int size = sample.length > BufferedTransmitter.BUFFER_MAX_SIZE ? BufferedTransmitter.BUFFER_ACTUAL_MAX_SIZE : out.size();
            List<Byte> outBuffer = new ArrayList<>();
            outBuffer.addAll(out.subList(0, size));
            out.removeAll(outBuffer);
            if (instance.checksum) {
                byte head = outBuffer.remove(0);
                int len = outBuffer.remove(0) << 8 | outBuffer.remove(0);
                int sum = outBuffer.remove(0) << 8 | outBuffer.remove(0);
                assertEquals(head, BufferedTransmitter.MESSAGE_START);
                assertEquals(size - 5, len);
                assertEquals(outBuffer.stream().mapToInt(Byte::intValue).sum(), sum);
            }
            byte[] messageSent = Arrays.copyOfRange(sample, 0, outBuffer.size());
            StringBuilder str = new StringBuilder();
            outBuffer.stream().forEach((b) -> str.append((char) b.byteValue()));
            assertArrayEquals(messageSent, str.toString().getBytes());
        }
    }

    @Test
    public void testRoutineSendingSmallPackageWithCheckSum() {
        final String sample = "test sample 01234";
        instance.checksum = true;
        initRoutineTest(sample.getBytes(), () -> instance.setAcknowledge(true));
        initRoutineTest(sample.getBytes(), () -> {
            out.clear();
            instance.setAcknowledge(false);
        });
    }

    @Test
    public void testRoutineSendingSmallPackageWithNoCheckSum() {
        final String sample = "test sample 01234";
        instance.checksum = false;
        initRoutineTest(sample.getBytes(), () -> instance.setAcknowledge(true));
    }
}
