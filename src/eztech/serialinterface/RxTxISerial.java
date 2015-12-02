/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aoki-kun
 */
public class RxTxISerial extends SerialInterface implements SerialPortEventListener {

    SerialPort serialPort;

    public RxTxISerial() {
        msgbundelled = false;
        msgBuffered = false;
        buffredTransmitter.setChecksum(false);
    }

    @Override
    public void connectToPort(SerialPortParamters params) {
        CommPortIdentifier portId = getPortID(params.Port);
        if (portId != null) {
            try {
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
            } catch (TooManyListenersException | IOException | PortInUseException | UnsupportedCommOperationException ex) {
                Logger.getLogger(RxTxISerial.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        } else {
            logger.log(Level.SEVERE, "port {0} not found.", params.Port);
        }
    }

    private CommPortIdentifier getPortID(String port) {
        // parse ports and if the default port is found, initialized the reader
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId;
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(port)) {
                    System.out.println("Found port: " + port);
                    return portId;
                }
            }
        }
        return null;
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
    public SerialPortParamters getConfigrations() {
        SerialPortParamters params = new SerialPortParamters();
        params.Port = System.getenv("PORT") != null ? System.getenv("PORT") : "COM10";
        params.Baud = System.getenv("BAUD") != null ? Integer.parseInt(System.getenv("BAUD")) : 9600;
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
