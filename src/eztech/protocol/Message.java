/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.protocol;

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
    
}
