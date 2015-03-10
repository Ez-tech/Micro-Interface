/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bulldog.core.io.serial.*;
import org.bulldog.core.platform.*;

/**
 *
 * @author Aoki-kun
 */
public class BulldogSerial extends SerialInterface implements SerialDataListener {

    Board board;

    SerialPort serialPort;

    public BulldogSerial() {
        board = Platform.createBoard();
    }

    @Override
    public void connectToPort(SerialPortParamters params) {
        try {
            serialPort = board.getSerialPort(params.Port);
            serialPort.setBaudRate(params.Baud);
            serialPort.setBlocking(false);
            serialPort.open();
            serialPort.addListener(this);
            out = serialPort.getOutputStream();
            in = serialPort.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(BulldogSerial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   @Override
    public List<String> getAvailablePorts() {
       return board.getSerialPorts().stream().map(sp -> sp.getName()).collect(Collectors.toList());
    }

    @Override
    public void onSerialDataAvailable(SerialDataEventArgs sdea) {
        serialEventHandler();
    }

    @Override
    public SerialPortParamters getConfigrations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
