/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface;

/**
 *
 * @author Aoki-kun
 */
    public class SerialPortParamters{
       public String Port = "COM1";
       public int Baud =115200 ,DataBit =8,StopBit =1,Parity =1;

    @Override
    public String toString() {
        return String.format("Port:%s, Baud:%s",Port,Baud);
    }
       
    }