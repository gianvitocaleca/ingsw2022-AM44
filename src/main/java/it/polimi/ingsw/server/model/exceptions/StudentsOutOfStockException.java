package it.polimi.ingsw.server.model.exceptions;

public class StudentsOutOfStockException extends Throwable {
    public StudentsOutOfStockException(String message){
        super(message);
    }

    public StudentsOutOfStockException(){
        super();
    }
}
