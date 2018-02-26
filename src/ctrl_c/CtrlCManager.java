package ctrl_c;

import utils.Utils;

/**
 *
 * @author diaz
 */
public class CtrlCManager {

    private final Thread handlerCtrlC;
    private boolean hasBeenInterrupted;
    private CtrlCCallback userCallback;

    public CtrlCManager() {
        hasBeenInterrupted = false;
        handlerCtrlC = new InterruptionHandler();
        Runtime.getRuntime().addShutdownHook(handlerCtrlC); // SIGINT/SIGTERM handler
    }

    public void stop() {
        // this is because the handler is also executed at end of normal termination (on MacOS)
        // but if inside the handler this main thread also continues (to display the best of all solvers)
        // in that case removing the handler raises an execption IllegalStateException because shutdown is in progress
        try {
            Runtime.getRuntime().removeShutdownHook(handlerCtrlC);
        } catch (IllegalStateException ex) {
            Utils.displayMessage(6, "Got an exception in removeShutdownHook: %s", ex);
        }
        handlerCtrlC.interrupt(); // to unblock the sleep in the handler
    }

    public boolean hasBeenInterrupted() {
        return hasBeenInterrupted;
    }

    public void registerCallback(CtrlCCallback callback) {
        userCallback = callback;
    }

    public void unregisterCallback() {
        userCallback = null;
    }


    /*
     * A handler for Ctrl+C (SIGINT and SIGTERM actually).
     * NB: it is also called on normal termination by the JVM on some plateforms (MacOS)
     */
    private class InterruptionHandler extends Thread {

        @Override
        public void run() {
            Utils.displayMessage(4, "=====   IN SIGINT/TERM (CTRL+C) INTERRUPTION HANDLER   =====");
            hasBeenInterrupted = true;
            if (userCallback != null) {
                userCallback.callback();
            }
            try { // let the main thread collects all results and displays the best
                Thread.sleep(10000); // will be interrupted by the main thread
            } catch (InterruptedException ex) {
                Utils.displayMessage(5, "=====   SIGINT/TERM (CTRL+C) END (notified by main which has finished)   =====");
            }
        }
    }
}
