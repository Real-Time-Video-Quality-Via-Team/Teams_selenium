package org.teams_zoom_automate_caller;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.util.List;


/**
 * This class is a prototype of the selenium automation for starting a video call for teams or
 * zoom web applications.
 * The class holds all the common attributes and methods applying to both zoom and teams.
 */
public abstract class Selenium {

    //-----Web driver for interacting with chrome-----
    protected WebDriver driver ;

    //-----Configuration file will hold the client login details and the friend nickname-----
    protected File config ;

    //-----Client type to know which side is needed (called\answerer)-----
    protected Role role;

    //-----The fields to be loaded from the config file-----
    protected String username, password, teammateName ;

    protected void notifyPython(String message){
        // Notify Python program using a socket
        try (Socket socket = new Socket("localhost", 9999);
             OutputStream out = socket.getOutputStream()) {
            out.write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Selenium(Role role) throws InterruptedException {
        try {
            //-----Vars initialization-----
            this.role = role;
            driver = new ChromeDriver(addOptions());
            // Add a shutdown hook for cleanup
            if (loadConfigurations() != 0) {
                while (createConfigurations() != 0) {
                }
            }
            openApplication();
            login();
            initCall();
            waitForHangup(role);
        } catch (Exception e){
            System.err.println("Selenium process finished");
        }
    }

    /**
     * initCall() method determines whether the call() or the answer() method will be called after the log in.
     */
    private void initCall(){
        switch (role) {
            case CALLER:
                call() ;
                break;
            case ANSWERER:
                answer();
                notifyPython("Start");
                break;
            default:
                System.err.println("Error in client type switch!");
        }
    }

    /**
     * loadConfigurations will handle the loading of the config file, parsing it, and call the create
     * configurations method if the file does not exist.
     */
    protected abstract int loadConfigurations() ;

    /**
     * createConfiguration gets the details from the user input and writes a new config file with those details.
     */
    protected abstract int createConfigurations() ;

    /**
     * openApplication will open the wanted app in the chrome browser.
     */
    protected abstract void openApplication() ;

    protected abstract void login() throws InterruptedException;
    protected abstract String call() ;

    protected abstract void close() ;

    protected abstract String answer() ;

    /**
     * creates options for the chrome driver which enables camera and mic automatically
     * @return options to set to the driver.
     */
    protected ChromeOptions addOptions(){
        ChromeOptions options = new ChromeOptions();
        //Allow camera automaticly
        options.addArguments("use-fake-ui-for-media-stream");
        return options ;
    }

    public void printAllElements() {
        // Get a list of all elements on the page
        List<WebElement> allElements = driver.findElements(By.cssSelector("*"));

        // Loop through the elements
        for (WebElement element : allElements) {
            try {
                // Print the details of each element
                System.out.println("Tag Name: " + element.getTagName());
                System.out.println("Text: " + element.getText());
                System.out.println("ID: " + element.getAttribute("id"));
                System.out.println("Class: " + element.getAttribute("class"));
                System.out.println("Name: " + element.getAttribute("name"));
                System.out.println("Value: " + element.getAttribute("value"));
                System.out.println("----------------------------------");
            } catch (StaleElementReferenceException e) {
                // Catch stale element exception and skip this element
                System.out.println("Stale element reference: Element is no longer attached to the DOM.");
            }
        }
    }
    protected abstract void waitForHangup(Role role) ;
    protected abstract void terminate() ;

}
