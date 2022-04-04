package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.view.View;

import java.util.*;

public class App {
    public static void main(String[] args) {


        GameModel model = new GameModel(true,new ArrayList<String>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<Color>(Arrays.asList(Color.values())),
                new ArrayList<Wizard>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE))
        );


        View view = new View();

        Controller controller = new Controller(model, view);

        model.addObserver(controller);
        view.addObserver(controller);

        controller.addObserver(view);
        model.addObserver(view);

        view.run(model);
    }
}
