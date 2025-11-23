package pkgDriver;
import org.joml.Vector3f;

import static pkgDriver.ZKSpot.FRUSTUM_BOTTOM;
import static pkgDriver.ZKSpot.FRUSTUM_LEFT;
import static pkgDriver.ZKSpot.FRUSTUM_RIGHT;
import static pkgDriver.ZKSpot.FRUSTUM_TOP;
import static pkgDriver.ZKSpot.WIN_HEIGHT;
import static pkgDriver.ZKSpot.WIN_WIDTH;
import static pkgDriver.ZKSpot.Z_FAR;
import static pkgDriver.ZKSpot.Z_NEAR;
import pkgZKRenderEngine.ZKCARenderer;
import pkgZKRenderEngine.ZKCamera;
import pkgZKRenderEngine.ZKRenderer;
import pkgZKUtils.ZKWindowManager;
public class Driver {
    public static void main(String[] args) {
        ZKWindowManager my_win = ZKWindowManager.get(WIN_WIDTH, WIN_HEIGHT);
        int NUM_ROWS = 19, NUM_COLS = 22;
        float[] camParams = {FRUSTUM_LEFT, FRUSTUM_RIGHT, FRUSTUM_BOTTOM,
                FRUSTUM_TOP, Z_NEAR, Z_FAR };
        final Vector3f myCameraLocation = new Vector3f(0, 0, 0.0f);
        ZKCamera myCamera = new ZKCamera(camParams, myCameraLocation);
        ZKRenderer currentScene = new ZKCARenderer(my_win, myCamera, "gol_input_1.txt");
        currentScene.initOpenGL();
        currentScene.initRendering(NUM_ROWS, NUM_COLS);
        currentScene.renderScene();
        my_win.destroyGlfwWindow();
    } // public static void main(String[] args)
} // public class Driver