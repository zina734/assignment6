package pkgZKUtils;

import java.io.*;

public class ZKPingPongArray {
    public ZKIntArray cellArrayB;
    public ZKIntArray cellArrayA;
    public ZKIntArray nextCellArray;
    public ZKIntArray liveCellArray;
    private String DEFAULT_OUTPUT_FILE;
    public int DEAD;
    public int MIN_VALUE;
    public int ALIVE;
    public int MAX_VALUE;
    private final int DEFAULT_VALUE;
    public int NUM_ROWS;
    public int NUM_COLS;

    public ZKPingPongArray(int rows, int cols, int defaultValue, int minValue, int maxValue) {
        this.DEFAULT_VALUE = defaultValue;
        this.MIN_VALUE = minValue;
        this.MAX_VALUE = maxValue;
        this.DEAD = 0;
        this.ALIVE = 1;
        this.DEFAULT_OUTPUT_FILE = "ppa_data.txt";
        initArrays(rows, cols);
        setAllCells(DEFAULT_VALUE);
        randomizeViaFisherYatesKnuth();
        printArrayToFile(DEFAULT_OUTPUT_FILE);
        swapLiveAndNext();
        getNNNSum(0, 0);
    }

    public ZKPingPongArray(int rows, int cols) {
        this(rows, cols, 0, 0, 1);
    }

    public void printArray() {
        liveCellArray.printArray("");
    }

    public void randomizeExisting() {
        java.util.Random rng = new java.util.Random();
        for (int r = 0; r < NUM_ROWS; ++r) {
            for (int c = 0; c < NUM_COLS; ++c) {
                nextCellArray.arrayData[r][c] = rng.nextBoolean() ? ALIVE : DEAD;
            }
        }
        save(DEFAULT_OUTPUT_FILE);
    }

    public int[][] loadFile(String path) {
        int[][] ret = liveCellArray.loadFile(path);
        if (ret != null) {
            NUM_ROWS = liveCellArray.NUM_ROWS;
            NUM_COLS = liveCellArray.NUM_COLS;
            if (nextCellArray.NUM_ROWS != NUM_ROWS || nextCellArray.NUM_COLS != NUM_COLS) {
                initArrays(NUM_ROWS, NUM_COLS);
            }
        }
        return ret;
    }

    public int[][] getLiveCellArray() {
        return liveCellArray.arrayData;
    }

    public void setAllCells(int value) {
        for (int r = 0; r < NUM_ROWS; ++r)
            for (int c = 0; c < NUM_COLS; ++c)
                nextCellArray.arrayData[r][c] = value;
    }

    public void randomizeViaFisherYatesKnuth() {
        int[] flat = liveCellArray.randomizViaFisherYatesKnuth(0);
        int k = 0;
        for (int r = 0; r < NUM_ROWS; ++r)
            for (int c = 0; c < NUM_COLS; ++c)
                nextCellArray.arrayData[r][c] = flat[k++];
    }

    protected void initArrays(int rows, int cols) {
        this.NUM_ROWS = rows;
        this.NUM_COLS = cols;
        this.cellArrayA = new ZKIntArray(rows, cols);
        this.cellArrayB = new ZKIntArray(rows, cols);
        this.liveCellArray = cellArrayA;
        this.nextCellArray = cellArrayB;
        for (int r = 0; r < rows; ++r)
            for (int c = 0; c < cols; ++c)
                liveCellArray.arrayData[r][c] = DEFAULT_VALUE;
    }

    public void setCellAlive(int row, int col) {
        setCell(row, col, ALIVE);
    }

    public void save(String outputPath) {
        String target = (outputPath == null || outputPath.isEmpty()) ? DEFAULT_OUTPUT_FILE : outputPath;
        printArrayToFile(target);
        DEFAULT_OUTPUT_FILE = target;
    }

    public void setCellDead(int row, int col) {
        setCell(row, col, DEAD);
    }

    public void setCell(int row, int col, int value) {
        if (row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS) return;
        nextCellArray.arrayData[row][col] = value;
    }

    public void printArrayToFile(String outputPath) {
        liveCellArray.saveToFile(outputPath, 0);
    }

    public void swapLiveAndNext() {
        ZKIntArray tmp = liveCellArray;
        liveCellArray = nextCellArray;
        nextCellArray = tmp;
    }

    public int getNNNSum(int row, int col) {
        int sum = 0;
        ZKRCPair[] nns = liveCellArray.getNearestNeighborsArray(row, col);
        for (ZKRCPair p : nns) {
            sum += liveCellArray.arrayData[p.row()][p.col()];
        }
        return sum;
    }
}


