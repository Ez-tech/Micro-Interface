/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eztech.serialinterface.exceptions;

/**
 *
 * @author yami
 */
public class ConnectionFailedException extends Exception{

    public ConnectionFailedException(Throwable cause) {
        super("Connection to controller failed",cause);
    }
    
}
