package flughafen;

/**
 * Created by Igor on 16-Aug-15.
 */
public class Loader {
    private Thread loadingThread;

    private volatile boolean finished;

    public Loader() {
        finished = false;

        loadingThread = new Thread(() -> {
            // Load here

            finished = true;
        });
    }

    public void launch() {
        loadingThread.start();
    }

    public boolean isFinished() {
        return finished;
    }
}
