/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import java.util.Properties;

/**
 *
 * @author yami
 */
public enum MicroInterfaceType {
    RXTX,
    BULL,
    ETHERNET;

    static final String KEY = "MicroInterface";

    public SerialInterface getInterface() {
        return MicroInterfaceType.getInterface(this);
    }

    private static SerialInterface getInterface(MicroInterfaceType type) {
        switch (type) {
            case RXTX:
                return new RxTxISerial();
            case BULL:
                return new BulldogSerial();
            case ETHERNET:
                return new SocketInterface();
            default:
                throw new AssertionError();
        }
    }

    public static MicroInterfaceType getValueFromProprties(Properties prop) {
        return valueOf(prop.getProperty(KEY).toUpperCase());
    }
}
