package it.polimi.ingsw.server.controller.Listeners;

import it.polimi.ingsw.server.CreationHandler;
import it.polimi.ingsw.server.LoginHandler;
import it.polimi.ingsw.server.LoginState;
import it.polimi.ingsw.server.controller.events.*;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class LoginPhaseListener implements EventListener {

    private List<LoginHandler> loginHandlers;

    private LoginState loginState;

    public LoginPhaseListener(LoginState loginState) {
        this.loginHandlers = new ArrayList<>();
        this.loginState = loginState;
    }

    public void addLoginHandler(LoginHandler loginHandler) {
        this.loginHandlers.add(loginHandler);
    }

    public void eventPerformed(LoginEvent evt) {
        LoginHandler currentHandler;
        for (LoginHandler l : loginHandlers) {
            if (l.isMySocket(evt.getSender())) {
                currentHandler = l;

                break;
            }
        }


    }

}
