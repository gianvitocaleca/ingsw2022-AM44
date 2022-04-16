package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.view.ViewProxy;

import java.util.*;

public class App {
    public static void main(String[] args) {


        GameModel model = new GameModel(true,new ArrayList<String>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<Color>(Arrays.asList(Color.values())),
                new ArrayList<Wizard>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE))
        );


        ViewProxy viewProxy = new ViewProxy();

        Controller controller = new Controller(model, viewProxy);

        model.addObserver(controller);
        viewProxy.addObserver(controller);

        controller.addObserver(viewProxy);
        model.addObserver(viewProxy);

        viewProxy.run();
    }
}
