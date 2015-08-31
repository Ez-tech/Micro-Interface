/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.protocol;

import java.util.Arrays;

/**
 *
 * @author Aoki-kun
 */
public class Message {

    public byte header;
    public byte[] body;
    public byte bodyLength; 

    public Message(byte header, byte bodyLength) {
        this.header = header;
        this.bodyLength = bodyLength;
    }
    
    @Override
    public String toString() {
        if (bodyLength == 0) {
            return String.format("Header:%d", header);
        } else {
            return String.format("Header:%d ,Body:%s", header, Arrays.toString(body));
        }
    }
}
