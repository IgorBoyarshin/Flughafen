package flughafen;

import himmel.graphics.Window;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Igor on 15-Aug-15.
 */
public class Settings {
    private final String windowTitle;
    private final int windowWidth;
    private final int windowHeight;
    private final int windowAntiAliasing;
    private final boolean windowVsync;
    private final boolean windowFullscreen;
    private final boolean windowWireframe;
    private final boolean windowLog;

    private final int ups;
    private final int rps;

    public Settings(String settingsPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(settingsPath));

        windowTitle = reader.readLine();
        windowWidth = Integer.parseInt(reader.readLine());
        windowHeight = Integer.parseInt(reader.readLine());
        windowAntiAliasing = Integer.parseInt(reader.readLine());
        windowVsync = reader.readLine().equals("true");
        windowFullscreen = reader.readLine().equals("true");
        windowWireframe = reader.readLine().equals("true");
        windowLog = reader.readLine().equals("true");

        reader.readLine();

        ups = Integer.parseInt(reader.readLine());
        rps = Integer.parseInt(reader.readLine());

        reader.close();
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowAntiAliasing() {
        return windowAntiAliasing;
    }

    public boolean getWindowVsync() {
        return windowVsync;
    }

    public boolean getWindowFullscreen() {
        return windowFullscreen;
    }

    public boolean getWindowWireframe() {
        return windowWireframe;
    }

    public boolean getWindowLog() {
        return windowLog;
    }

    public int getUps() {
        return ups;
    }

    public int getRps() {
        return rps;
    }
}
