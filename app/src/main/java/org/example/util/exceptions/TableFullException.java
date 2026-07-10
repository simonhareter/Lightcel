package org.example.util.exceptions;

public class TableFullException extends RuntimeException {
   public TableFullException(String message) {
       super(message);
   } 
}
