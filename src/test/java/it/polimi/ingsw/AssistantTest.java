package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AssistantTest {

    private Value name;

    @BeforeEach
    public void InitializeAssistant() {
        name = Value.values()[new Random().nextInt(Value.values().length)];
    }

    @Test
    void getValue() {
        for (Value val : Value.values()) {
            if (val.equals(name)) {
                assertEquals(val.getValue(), name.getValue());
            }
        }
    }

    @Test
    void getMovements() {
        for (Value val : Value.values()) {
            if (val.equals(name)) {
                assertEquals(val.getMovements(), name.getMovements());
            }
        }
    }
}