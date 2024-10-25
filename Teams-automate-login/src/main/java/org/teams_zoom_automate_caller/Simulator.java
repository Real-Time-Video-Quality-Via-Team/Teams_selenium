package org.teams_zoom_automate_caller;

import org.teams_zoom_automate_caller.teams.TeamsSelenium;

import javax.swing.*;

public class Simulator {
    private Selenium selenium ;
    public Simulator() throws InterruptedException {
        Role role = getRole() ;
        selenium = new TeamsSelenium(role) ;
    }


    //-----Get from the user the wanted role (Caller / Answerer)-------
    private Role getRole(){
        Object[] options = {"Caller", "Answerer"};
        int option = JOptionPane.showOptionDialog(null, "Please choose platform:", "Simulator", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) ;
        if(option == 0){
            return Role.CALLER ;
        }else{
            return Role.ANSWERER ;
        }
    }
}
