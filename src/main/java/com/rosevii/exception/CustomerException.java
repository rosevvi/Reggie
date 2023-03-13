package com.rosevii.exception;

/**
 * @author: rosevvi
 * @date: 2023/3/7 15:53
 * @version: 1.0
 * @description:
 */
public class CustomerException extends RuntimeException{
    public CustomerException(String msg){
        super(msg);
    }
}
