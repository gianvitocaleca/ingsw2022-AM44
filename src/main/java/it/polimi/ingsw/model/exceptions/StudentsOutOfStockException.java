package it.polimi.ingsw.model.exceptions;

public class StudentsOutOfStockException extends Throwable {
    public StudentsOutOfStockException(String message){
        super(message);
    }

    public StudentsOutOfStockException(){
        super();
    }
}
