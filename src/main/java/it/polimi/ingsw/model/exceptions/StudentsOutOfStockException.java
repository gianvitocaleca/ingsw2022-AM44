package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when is no more possible to pick new students from the student bucket
 */
public class StudentsOutOfStockException extends Throwable {
    public StudentsOutOfStockException(){
        super();
    }
}
