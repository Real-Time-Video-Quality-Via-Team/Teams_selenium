package org.teams_zoom_automate_caller.teams;

import org.teams_zoom_automate_caller.Role;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.teams_zoom_automate_caller.Selenium;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeamsSelenium extends Selenium {

    public TeamsSelenium(Role role) throws InterruptedException {
        super(role);
    }

    protected int loadConfigurations() {
        config = new File("teamsConfig.txt");
        if (!config.exists()) {
            return -1;
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(config));
                username = reader.readLine();
                password = reader.readLine();
                if (username == null || password == null) {
                    return -1;
                }
                if (role == Role.CALLER) {
                    teammateName = reader.readLine();
                    if (teammateName == null) {
                        return -1;
                    }
                }
                return 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected int createConfigurations() {
        try {
            config.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(config));
            JOptionPane.showMessageDialog(null, "Hello, its your first time with Microsoft Teams here, please enter your details so we can connect you on the next times also :-) ");
            username = JOptionPane.showInputDialog("Please enter your mail:");
            // Create a password field
            JPasswordField passwordField = new JPasswordField();
            passwordField.setPreferredSize(new Dimension(200, 25)); // Set preferred size

            // Create a panel to hold the password field
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.add(new JLabel("Enter password:"));
            panel.add(passwordField);

            // Set the preferred size for the panel
            panel.setPreferredSize(new Dimension(250, 80)); // Set preferred size
            passwordField.setFocusable(true);
            // Show the JOptionPane with the password field
            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Password Dialog",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            // Check if the user clicked OK
            if (result == JOptionPane.OK_OPTION) {
                // Retrieve the password from the password field
                char[] tempPass = passwordField.getPassword();
                password = new String(tempPass);

            }

            if (username == null || password == null) {
                return -1;
            } else {
                writer.write(username + "\n");
                writer.write(password + "\n");
            }
            if (role == Role.CALLER) {
                teammateName = JOptionPane.showInputDialog("Please enter your friend's identifier (nickname, mail):");
                if (teammateName == null) {
                    return -1;
                }
                writer.write(teammateName + "\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void openApplication() {
        driver.get("https://teams.microsoft.com/");
    }

    @Override
    public void login() throws InterruptedException {
        passUsernamePage();
        passPasswordPage();
        passKeepLoggedInPage();
    }

    private void passUsernamePage() {
        waitForUsernamePage();
        List<WebElement> userPageElements = getUsernamePageElements();
        fillAndSendUserPage(userPageElements);
    }

    private void waitForUsernamePage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("loginfmt")));
    }

    private void passPasswordPage() {
        waitForPasswordPage();
        List<WebElement> passwordPageElements = getPasswordPageElements();
        fillAndSendPasswordPage(passwordPageElements);
    }

    private void waitForPasswordPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("passwd")));

    }

    private void passKeepLoggedInPage() {
        waitForLogggedInPage();
        driver.findElement(By.id("declineButton")).click();

    }

    private void waitForLogggedInPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("declineButton")));
    }

    private void passMainPage() throws InterruptedException {
        Thread.sleep(10000);
        Random rand = new Random();
        WebElement searchBox = waitForMainPage();

        Actions actions = new Actions(driver);
        actions.moveToElement(searchBox);
        actions.click().perform();
        for (int i = 0; i < teammateName.length(); i++) {
            searchBox.sendKeys(teammateName.substring(i, i + 1));
            Thread.sleep(rand.nextInt(200) + 200);
        }
        Thread.sleep(500);
        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.sendKeys(Keys.ENTER).perform();
    }

    private WebElement waitForFriendPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        // Locate the buttons
        WebElement videoButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[data-tid='chat-call-video-button']")));
        WebElement audioButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[data-tid='chat-call-audio-button']")));

        // Check if the video button is disabled
        String ariaDisabled = videoButton.getAttribute("aria-disabled");

        if ("true".equals(ariaDisabled)) {
            // Return the audio button if the video button is disabled
            return audioButton;
        } else {
            // Return the video button if it is enabled
            return videoButton;
        }
    }

    private boolean waitForRosterButton(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            WebElement peopleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@aria-label='People' or @aria-label='אנשים']")));
            return true ;
        } catch (Exception e) {
            return false ;
        }
    }

    private WebElement waitForMainPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ms-searchux-input")));
    }

    private List<WebElement> getPasswordPageElements() {
        List<WebElement> passwordPageElements = new ArrayList<>();
        passwordPageElements.add(driver.findElement(By.name("passwd")));
        passwordPageElements.add(driver.findElement(By.id("idSIButton9")));
        return passwordPageElements;
    }


    private List<WebElement> getUsernamePageElements() {
        List<WebElement> userPageElements = new ArrayList<>();
        userPageElements.add(driver.findElement(By.name("loginfmt")));
        userPageElements.add(driver.findElement(By.id("idSIButton9")));
        return userPageElements;
    }

    public void fillAndSendUserPage(List<WebElement> userPageElements) {
        userPageElements.get(0).sendKeys(username);
        userPageElements.get(1).click();
    }

    public void fillAndSendPasswordPage(List<WebElement> passwordPageElements) {
        passwordPageElements.get(0).sendKeys(password);
        passwordPageElements.get(1).submit();
    }

    @Override
    public String call() {
        try {
            passMainPage();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement callButton = waitForFriendPage();
        callButton.click();
        return "" ;
    }

    @Override
    public void close() {

    }

    @Override
    protected String answer() {
        WebElement answerButton = waitForCall();
        answerButton.click();
        return "Done";
    }

    private WebElement waitForCall() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));

        // Locate the buttons for video and audio acceptance, considering both English and Hebrew labels
        WebElement answerVideoButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[@aria-label='Accept with video' or @aria-label='קבל עם וידאו']")));
        WebElement answerAudioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[@aria-label='Accept with audio' or @aria-label='קבל עם שמע']")));

        // Check if the video button is aria-disabled
        String isVideoDisabled = answerVideoButton.getAttribute("aria-disabled");

        // Click the video button if it's not disabled, otherwise click the audio button
        if (isVideoDisabled == null || !isVideoDisabled.equals("true")) {
            return answerVideoButton;
        } else {
            return answerAudioButton;
        }
    }
    @Override
    public void waitForHangup(Role role) {
        // Define the locator for the <div> element containing the button by its id
        By hangupButtonDiv = By.id("hangup-button");

        // Set up WebDriverWait (e.g., wait for a maximum of 30 seconds for both conditions)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));

        // First, wait for the hangup button to appear
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(hangupButtonDiv));
        boolean isCallAnswered = false ;
        if(role == Role.CALLER){
            isCallAnswered = waitForRosterButton() ;
            if(isCallAnswered){
                notifyPython("Start");
            }
        }
        if((role == Role.CALLER && isCallAnswered) || role == Role.ANSWERER) {
            // Then, wait for the hangup button to disappear
            wait.until(ExpectedConditions.invisibilityOfElementLocated(hangupButtonDiv));
        }
        // Optionally close the driver or terminate the process if needed
    }
    @Override
    public void terminate(){
        notifyPython("Stop");
        driver.quit();
    }
}
