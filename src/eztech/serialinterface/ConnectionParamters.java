/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import java.util.Properties;

/**
 *
 * @author Aoki-kun
 */
public class ConnectionParamters {

    static final String SERIAL_PORT = "SerialPort";
    static final String SERIAL_BAUD = "SerialBaud";

    static final String ETHERNET_IP = "EthernetIP";
    static final String ETHERNET_PORT = "EthernetPort";

    final Properties systemProperties;

    public ConnectionParamters(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }

    public String Port = "COM1";
    public int Baud = 115200, DataBit = 8, StopBit = 1, Parity = 0;

    public String getSerialPort() {
        return (String) systemProperties.get(SERIAL_PORT);
    }

    public int getSerialBaud() {
        return Integer.parseInt((String) systemProperties.get(SERIAL_BAUD));
    }

    public String getEthernetIP() {
        return (String) systemProperties.get(ETHERNET_IP);
    }

    public int getEthernetPort() {
        return Integer.parseInt((String) systemProperties.get(ETHERNET_PORT));
    }
}
