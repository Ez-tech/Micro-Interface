/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.serialinterface.exceptions.ConnectionFailedException;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Aoki-kun
 */
public class RxTxISerial extends SerialInterface implements SerialPortEventListener {

    SerialPort serialPort;

    public RxTxISerial() {
        //buffredTransmitter.setChecksum(false);
    }

    @Override
    public void connectToPort(ConnectionParamters params) throws ConnectionFailedException {
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(params.Port);
            serialPort = (SerialPort) portId.open("Ez-Mill", 2000);
            in = serialPort.getInputStream();
            out = serialPort.getOutputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.setSerialPortParams(params.Baud,
                    params.DataBit,
                    params.StopBit,
                    params.Parity);
            connected = true;
            logger.log(Level.INFO, "Connected to port {0}.", params.Port);
        } catch (Exception ex) {
            throw new ConnectionFailedException(ex);
        }
    }

    @Override
    public List<String> getAvailablePorts() {
        Enumeration portlist = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId;
        ArrayList<String> ports = new ArrayList<>();
        while (portlist.hasMoreElements()) {
            portId = (CommPortIdentifier) portlist.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(portId.getName());
            }
        }
        return ports;
    }

    @Override
    public void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            serialEventHandler();
        }
    }

    @Override
    public ConnectionParamters getConfigrations() {
        ConnectionParamters params = new ConnectionParamters(System.getProperties());
        params.Port = params.getSerialPort() != null ? params.getSerialPort() : "COM10";
        params.Baud = params.getSerialBaud() == 0 ? params.getSerialBaud() : 9600;
        params.Parity = SerialPort.PARITY_NONE;
        params.StopBit = SerialPort.STOPBITS_1;
        params.DataBit = SerialPort.DATABITS_8;
        return params;
    }

    @Override
    public void disconnect() {
        serialPort.close();
    }

}
