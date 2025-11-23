package pkgZKRenderEngine;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import pkgZKUtils.ZKWindowManager;
import pkgZKUtils.ZKGoLArray;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static pkgZKRenderEngine.ZKSpotrender.*;

public class ZKRenderer {

    protected ZKWindowManager windowRef;
    protected ZKCamera cameraLocation;
    protected String inputBoardFile;
    protected int numIndicesTotal;
    protected int COLOR_FACTOR;
    protected int vao;
    protected int vbo;
    protected int vpt;
    protected int currentBoardIndex;
    protected int vertexCount;
    protected int indexesCount;
    protected float numCells;
    protected int NUM_TILES;
    protected int MAX_SHADER_OBJECTS;
    protected int sIndex;
    protected ZKShaderObject[] shaderArray;
    protected float[] vertexPositionArray;
    protected float[] SET_SHADER;
    protected int[] my_win;
    protected int FPV;
    protected boolean updateVertexArray;
    protected boolean vptPresent;
    protected ZKGoLArray myGoL;
    protected boolean my_render;
    protected boolean firstRenderingCompleteStatus;
    protected int numRenderingCalls;
    protected int NUM_TRIANGLES;
    protected int NUM_SQUARES;
    protected int setTileWithHeight;
    protected float renderTetraDelay;
    protected float[] renderDataBuffer;
    protected int[] renderIndexArray;
    protected Matrix4f mMatrixLocation;
    protected Matrix4f vMatrixLocation;
    protected Matrix4f pMatrixLocation;
    protected boolean shadersCompiled;
    protected float[] vertexArray;
    protected int SET_TILE_NO_HEIGHT;
    protected int indexBufferIndex;
    protected int[] indexBuffer;
    protected int ebo;
    protected boolean multiColored;
    protected int VEC4ColorsCols;
    protected Vector4f vec4Colors;
    protected ZKShaderObject cs;
    protected int NUM_POINTS;
    protected String vsSource;
    protected String fsSource;
    protected boolean addTriangle;
    protected boolean addSquare;
    protected float[] tileColors = new float[0];

    public ZKRenderer(ZKWindowManager window, ZKCamera camera, String inputBoardFile) {
        this.windowRef = window;
        this.cameraLocation = camera;
        this.inputBoardFile = inputBoardFile;

        this.MAX_SHADER_OBJECTS = 10;
        this.shaderArray = new ZKShaderObject[MAX_SHADER_OBJECTS];
        this.sIndex = 0;

        this.mMatrixLocation = new Matrix4f();
        this.vMatrixLocation = new Matrix4f();
        this.pMatrixLocation = new Matrix4f();

        this.vertexPositionArray = new float[0];
        this.SET_SHADER = new float[0];
        this.my_win = new int[2];

        this.numIndicesTotal = 0;
        this.COLOR_FACTOR = 3;
        this.currentBoardIndex = 0;
        this.vertexCount = 0;
        this.indexesCount = 0;
        this.numCells = 0.0f;
        this.NUM_TILES = 0;
        this.NUM_TRIANGLES = 0;
        this.NUM_SQUARES = 0;
        this.NUM_POINTS = 0;
        this.FPV = 0;
        this.numRenderingCalls = 0;

        this.updateVertexArray = false;
        this.vptPresent = false;
        this.my_render = false;
        this.firstRenderingCompleteStatus = false;
        this.shadersCompiled = false;
        this.multiColored = false;
        this.addTriangle = false;
        this.addSquare = false;

        this.setTileWithHeight = 0;
        this.renderTetraDelay = 0.0f;
        this.SET_TILE_NO_HEIGHT = 0;
        this.indexBufferIndex = 0;
        this.VEC4ColorsCols = 0;
    }

    public void renderScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (windowRef != null) windowRef.pollEvents();

        if (updateVertexArray) {
            initVertexArray(NUM_TILES, tileColors);
            updateVertexArray = false;
        }

        flipVertexArray();
        flipIndexArray();
        renderVertexBuffer();

        if (SLEEP_INTERVAL != 0) {
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException ignored) {}
        }

        if (!windowRef.isGlfwWindowClosed()) {
            renderScene();
        }
    }

    public void initOpenGL() {
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        cs = new ZKShaderObject("vs_0.glsl", "fs_0.glsl");
        cs.compile_shader();
        cs.set_shader_program();
        shadersCompiled = true;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    }

    public void flipVertexArray() {
        if (vertexArray != null && vertexArray.length > 0) {
            renderDataBuffer = Arrays.copyOf(vertexArray, vertexArray.length);
        }
    }

    public void flipIndexArray() {
        if (indexBuffer != null && indexBuffer.length > 0) {
            renderIndexArray = Arrays.copyOf(indexBuffer, indexBuffer.length);
        }
    }

    public void renderVertexBuffer() {
        if (renderDataBuffer == null || renderDataBuffer.length == 0) return;

        if (cs != null && shadersCompiled) {
            cs.set_shader_program();
            Matrix4f identity = new Matrix4f().identity();
            cs.loadMatrix4f("uProjMatrix", identity);
            cs.loadMatrix4f("uViewMatrix", identity);
            cs.loadVector4f("COLOR_FACTOR", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        }

        glBindVertexArray(vao);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(renderDataBuffer.length);
        vertexBuffer.put(renderDataBuffer).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        if (renderIndexArray != null && renderIndexArray.length > 0) {
            IntBuffer indexBuf = BufferUtils.createIntBuffer(renderIndexArray.length);
            indexBuf.put(renderIndexArray).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuf, GL_STATIC_DRAW);
            glDrawElements(GL_TRIANGLES, renderIndexArray.length, GL_UNSIGNED_INT, 0);
        }

        glBindVertexArray(0);
    }

    public void setRenderArray(float[] array) {
        this.vertexArray = array;
        this.updateVertexArray = true;
    }

    public void setVertexColor(float r, float g, float b) {
        if (vec4Colors == null) vec4Colors = new Vector4f();
        vec4Colors.set(r, g, b, 1.0f);
    }

    public float[] getRenderArray() {
        return this.vertexArray;
    }

    public void initVertexArray(int numTiles, float[] tileColors) {
        NUM_TILES = numTiles;
        int verticesPerTile = 4;
        int floatsPerVertex = 6;
        vertexArray = new float[NUM_TILES * verticesPerTile * floatsPerVertex];
        indexBuffer = new int[NUM_TILES * 6];
        updateVertexArray = true;
    }

    public void setRenderDelay(float delay) {
        this.renderTetraDelay = delay;
    }

    public void setupCubeRenderObject(String vsFile, String fsFile) {
        if (sIndex < MAX_SHADER_OBJECTS) {
            shaderArray[sIndex] = new ZKShaderObject(vsFile, fsFile);
            shaderArray[sIndex].compile_shader();
            sIndex++;
        }
    }

    public void setupCharObject(String tex, int width, int height) {}

    public void renderString(String text) {}

    public void renderChar(char c) {}

    public void cleanupGLData() {
        if (vbo != 0) glDeleteBuffers(vbo);
        if (ebo != 0) glDeleteBuffers(ebo);
        if (vao != 0) glDeleteVertexArrays(vao);
        if (cs != null) ZKShaderObject.detach_shader();
    }

    public void initRendering(int rows, int cols) {}
}






