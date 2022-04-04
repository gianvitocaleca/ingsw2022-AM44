package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

import java.util.*;

public class View extends Observable implements Observer {

    private Scanner scanner;

    public View() {
        scanner = new Scanner(System.in);
    }

    public void run(GameModel model) {
        System.out.println("Ciao, sono la view");
        scanner.next();


        model.setCharacterTestForMVC();


        model.playCharacter(0);
    }

    private void askForSourceCreature(List<Creature> creatures) {
        System.out.print("Inserisci la creatura dalla sorgente:");
        String s = scanner.next();

        for (Creature c : Creature.values()) {
            if (c.getName().equals(s)) {
                creatures.add(c);
            }
        }

    }

    private int askForIslandIndex() {

        System.out.print("Scegli un'isola:");
        int i = scanner.nextInt();
        //l'isola scelta deve essere una di quelle possibili
        return i;
    }

    private StudentContainer askForDestination() {
        //Chiediamo all'utente di inserire un numero in base alla destinazione scelta
        //Mostriamo le possibili destinazioni all'utente
        return new Cloud(10);
    }

    private int askForMnMovements() {
        System.out.println("Quanti passi in più deve compiere madre natura? (0-2)");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Cagata provided");
            return 1;
        }

    }

    private void askForDestinationCreatures(List<Creature> creatures) {
        System.out.println("Inserisci la creatura dalla destinazione");
        String s = scanner.next();

        for (Creature c : Creature.values()) {
            if (c.getName().equals(s)) {
                creatures.add(c);
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if ((o instanceof Controller) && (arg instanceof Name)) {

            List<Creature> providedSourceCreatures = new ArrayList<>();
            int providedIslandIndex = 0;
            int providedMnMovements = 0;
            StudentContainer providedDestination = null;
            List<Creature> providedDestinationCreatuers = new ArrayList<Creature>();

            for (int i = 0; i < ((Name) arg).getMaxMoves(); i++) {
                if (((Name) arg).isNeedsSourceCreature()) {
                    askForSourceCreature(providedSourceCreatures);
                }
                if (((Name) arg).isNeedsDestination()) {
                    providedDestination = askForDestination();
                }
                if (((Name) arg).isNeedsIslandIndex()) {
                    providedIslandIndex = askForIslandIndex();
                }
                if (((Name) arg).isNeedsMnMovements()) {
                    providedMnMovements = askForMnMovements();
                }
                if (((Name) arg).isNeedsDestinationCreature()) {
                    askForDestinationCreatures(providedDestinationCreatuers);
                }
                //per adesso fa tutto maxmoves volte, bisogna implementare il fatto che un utente possa compiere FINO A maxMoves volte la mossa
            }

            setChanged();
            notifyObservers(new CharactersParameters(providedSourceCreatures, providedIslandIndex, providedMnMovements, providedDestination, providedDestinationCreatuers));
        }
    }
}
