package it.polimi.ingsw;

public class StudentsOutOfStockException extends Throwable {
    public StudentsOutOfStockException(String message){
        super(message);
    }

    public StudentsOutOfStockException(){
        super();
    }
}
