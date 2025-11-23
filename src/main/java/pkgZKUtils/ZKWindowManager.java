package pkgZKUtils;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;

public class ZKWindowManager {

    private static ZKWindowManager instance;
    private long windowID;
    private int width;
    private int height;

    private ZKWindowManager() {
        this.width = 800;
        this.height = 600;
    }

    public static ZKWindowManager getInstance() {
        if (instance == null) {
            instance = new ZKWindowManager();
        }
        return instance;
    }

    public static ZKWindowManager get(int winWidth, int winHeight) {
        if (instance == null) {
            instance = new ZKWindowManager();
            instance.width = winWidth;
            instance.height = winHeight;
            instance.initGlfwWindow();
        }
        return instance;
    }

    public void initGlfwWindow() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);

        windowID = GLFW.glfwCreateWindow(width, height, "GAME OF LIFE ZINA", 0, 0);
        if (windowID == 0L) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwMakeContextCurrent(windowID);
        org.lwjgl.opengl.GL.createCapabilities();

        GL11.glViewport(0, 0, width, height);

        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(windowID);

        enableResizeWindowCallback();
    }

    public void enableResizeWindowCallback() {
        if (windowID != 0L) {
            GLFW.glfwSetFramebufferSizeCallback(windowID, new GLFWFramebufferSizeCallback() {
                @Override
                public void invoke(long window, int width, int height) {
                    if (width > 0 && height > 0) {
                        GL11.glViewport(0, 0, width, height);
                    }
                }
            });
        }
    }

    public void destroyGlfwWindow() {
        if (windowID != 0L) {
            GLFW.glfwDestroyWindow(windowID);
            GLFW.glfwTerminate();
            windowID = 0L;
        }
    }

    public boolean glfwWindowClosed() {
        return windowID == 0L || GLFW.glfwWindowShouldClose(windowID);
    }

    public void swapBuffers() {
        if (windowID != 0L) {
            GLFW.glfwSwapBuffers(windowID);
        }
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public long getWindowID() {
        return windowID;
    }

    public boolean isGlfwWindowClosed() {
        return glfwWindowClosed();
    }
}

