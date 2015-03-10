/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

import eztech.protocol.Message;

/**
 *
 * @author Aoki-kun
 */
public interface MicroHandler {

    /**
     *
     * @param msg Slave Message
     */
    void processMicroMessage(Message msg);
}
