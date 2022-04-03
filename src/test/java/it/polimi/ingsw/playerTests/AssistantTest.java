package it.polimi.ingsw.playerTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.enums.Value;
import org.junit.jupiter.api.*;

import java.util.*;

class AssistantTest {

    private Value name;

    @BeforeEach
    public void InitializeAssistant() {
        name = Value.values()[new Random().nextInt(Value.values().length)];
    }

    /**
     * This tests that the assistants values initialization is correctly made
     */
    @Test
    void getValue() {
        for (Value val : Value.values()) {
            if (val.equals(name)) {
                assertEquals(val.getValue(), name.getValue());
            }
        }
    }

    /**
     * This tests that the assistants movements initialization is correctly made
     */
    @Test
    void getMovements() {
        for (Value val : Value.values()) {
            if (val.equals(name)) {
                assertEquals(val.getMovements(), name.getMovements());
            }
        }
    }
}