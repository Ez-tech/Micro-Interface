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
public class Message implements Comparable<Message> {

    private final byte header;
    private final byte[] body;

    public Message(byte header, byte bodyLength) {
        this.header = header;
        this.body = new byte[bodyLength];
    }

    public Message(byte header, byte... body) {
        this(header, (byte) body.length);
        setBody(body);
    }

    public Message(byte header, int... body) {
        this(header, (byte) body.length);
        setBody(body);
    }

    public void setBody(int... body) {
        for (int i = 0; i < this.body.length; i++) {
            this.body[i] = (byte) body[i];
        }
    }

    public void setBody(byte... body) {
        System.arraycopy(body, 0, this.body, 0, this.body.length);
    }

    public int getBodyLength() {
        return body.length;
    }

    public byte[] getBody() {
        return body;
    }

    public byte getHeader() {
        return header;
    }

    public boolean hasBody() {
        return body != null && body.length > 0;
    }

    @Override
    public String toString() {
        if (hasBody()) {
            return String.format("Header:%d ,Body:%s", header, Arrays.toString(body));
        } else {
            return String.format("Header:%d", header);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.header;
        hash = 37 * hash + Arrays.hashCode(this.body);
        return hash;
    }

    @Override
    public int compareTo(Message o) {
        return this.header - o.header;
    }

}
