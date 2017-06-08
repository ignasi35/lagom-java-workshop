package com.example.basket.impl.entity;

/**
 *
 */
public class NotEnoughItemsException extends Exception{
    public NotEnoughItemsException(String message) {
        super(message);
    }
}
