package pkgZKRenderEngine;

import org.joml.Vector4f;
import pkgZKUtils.ZKGoLArray;
import pkgZKUtils.ZKWindowManager;
import pkgZKUtils.ZKPingPongArray;

import static org.lwjgl.opengl.GL11.*;
import static pkgZKRenderEngine.ZKSpotrender.SLEEP_INTERVAL;

public class ZKCARenderer extends ZKRenderer {

    public float[] BG_COLOR;
    public Vector4f BOARDCOLOR;
    public ZKGoLArray golBoard;
    public ZKWindowManager curWM;
    public int VPT;
    public boolean isGoLinitialized;

    public ZKCARenderer(ZKWindowManager window, ZKCamera camera, String inputBoardFile) {
        super(window, camera, inputBoardFile);
        this.curWM = window;
        this.BG_COLOR = new float[]{0.8f, 0.4f, 0.7f};
        this.BOARDCOLOR = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
        this.VPT = 0;
        this.isGoLinitialized = false;
        glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], 1.0f);
    }

    public void generateLCVertexArray() {
        if (!isGoLinitialized || golBoard == null || golBoard.pingPongArray == null) return;

        int[] dims = golBoard.getNumRowsCols();
        int rows = dims[0];
        int cols = dims[1];

        int liveCellCount = 0;
        ZKPingPongArray ppa = golBoard.pingPongArray;

        if (ppa.liveCellArray != null && ppa.liveCellArray.arrayData != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (ppa.liveCellArray.arrayData[r][c] == ppa.ALIVE) {
                        liveCellCount++;
                    }
                }
            }
        }

        if (liveCellCount == 0) {
            this.vertexArray = new float[0];
            this.indexBuffer = new int[0];
            this.renderDataBuffer = new float[0];
            this.renderIndexArray = new int[0];
            return;
        }

        float[] vertices = new float[liveCellCount * 4 * 3];
        int[] indices = new int[liveCellCount * 6];

        int vertexIndex = 0;
        int indexOffset = 0;
        int quadIndex = 0;

        float gridScale = 0.8f;
        float cellWidth = (2.0f * gridScale) / cols;
        float cellHeight = (2.0f * gridScale) / rows;
        float startX = -gridScale;
        float startY = gridScale;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (ppa.liveCellArray.arrayData[r][c] == ppa.ALIVE) {

                    float x = startX + c * cellWidth;
                    float y = startY - r * cellHeight;

                    float padding = cellWidth * 0.1f;
                    float cellW = cellWidth - padding;
                    float cellH = cellHeight - padding;

                    vertices[vertexIndex++] = x;
                    vertices[vertexIndex++] = y - cellH;
                    vertices[vertexIndex++] = 0.0f;

                    vertices[vertexIndex++] = x + cellW;
                    vertices[vertexIndex++] = y - cellH;
                    vertices[vertexIndex++] = 0.0f;

                    vertices[vertexIndex++] = x + cellW;
                    vertices[vertexIndex++] = y;
                    vertices[vertexIndex++] = 0.0f;

                    vertices[vertexIndex++] = x;
                    vertices[vertexIndex++] = y;
                    vertices[vertexIndex++] = 0.0f;

                    int base = quadIndex * 4;
                    indices[indexOffset++] = base;
                    indices[indexOffset++] = base + 1;
                    indices[indexOffset++] = base + 2;
                    indices[indexOffset++] = base;
                    indices[indexOffset++] = base + 2;
                    indices[indexOffset++] = base + 3;

                    quadIndex++;
                }
            }
        }

        this.vertexArray = vertices;
        this.indexBuffer = indices;
        this.renderDataBuffer = vertices;
        this.renderIndexArray = indices;
        this.updateVertexArray = true;
        this.vec4Colors = BOARDCOLOR;
    }

    @Override
    public void renderScene() {
        glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (curWM != null) curWM.pollEvents();
        if (isGoLinitialized) generateLCVertexArray();
        if (vertexArray != null && vertexArray.length > 0) renderVertexBuffer();
        if (curWM != null) curWM.swapBuffers();

        try {
            Thread.sleep(SLEEP_INTERVAL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (isGoLinitialized && golBoard != null) golBoard.onTickUpdate();
        if (!curWM.isGlfwWindowClosed()) renderScene();
    }

    @Override
    public void initRendering(int rows, int cols) {
        golBoard = new ZKGoLArray(rows, cols);

        if (inputBoardFile != null && !inputBoardFile.isEmpty()) {
            if (golBoard.pingPongArray != null) {
                int[][] loaded = golBoard.pingPongArray.loadFile(inputBoardFile);
                if (loaded == null) {
                    golBoard.pingPongArray.randomizeExisting();
                }
            }
        } else {
            if (golBoard.pingPongArray != null) {
                golBoard.pingPongArray.randomizeExisting();
            }
        }

        isGoLinitialized = true;
        generateLCVertexArray();
    }
}











