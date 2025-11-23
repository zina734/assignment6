package pkgZKUtils;

import java.io.*;
import java.util.*;

public class ZKIntArray {
    public int[][] arrayData;
    public int NUM_ROWS;
    public int NUM_COLS;

    public ZKIntArray(int rows, int cols) {
        this.NUM_ROWS = rows;
        this.NUM_COLS = cols;
        this.arrayData = new int[rows][cols];
    }

    public int[][] loadFile(String path) {
        try (BufferedReader myReader = new BufferedReader(new FileReader(path))) {
            String inputLine;
            int DEFAULT_VALUE = Integer.parseInt(myReader.readLine());
            int MIN_VALUE = DEFAULT_VALUE;
            int MAX_VALUE = DEFAULT_VALUE;
            inputLine = myReader.readLine();
            int[] rowCol = Arrays.stream(inputLine.split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            final int fileRows = rowCol[0];
            final int fileCols = rowCol[1];
            if ((fileRows > arrayData.length) || (fileCols > arrayData[0].length)) {
                try {
                    arrayData = new int[fileRows][fileCols];
                } catch (OutOfMemoryError e) {
                    arrayData = null;
                }
            }
            if (arrayData == null) return null;
            NUM_ROWS = arrayData.length;
            NUM_COLS = arrayData[0].length;

            for (int row = 0; row < arrayData.length; ++row) {
                for (int col = 0; col < arrayData[0].length; ++col) {
                    arrayData[row][col] = DEFAULT_VALUE;
                }
            }

            if (arrayData != null) {
                String line;
                while ((line = myReader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] tokens = line.trim().split("\\s+");
                    int[] readRow = new int[tokens.length];
                    for (int i = 0; i < tokens.length; i++) {
                        readRow[i] = Integer.parseInt(tokens[i]);
                    }

                    int curRow = readRow[0];
                    int readColOffset = 1;
                    int curWriteCol = 0;

                    while (curWriteCol < fileCols && readColOffset < readRow.length) {
                        arrayData[curRow][curWriteCol++] = readRow[readColOffset++];
                    }
                }
            }
            return arrayData;

        } catch (IOException e) {
            e.printStackTrace();
            arrayData = null;
            return null;
        }
    }

    public ZKRCPair[] getNearestNeighborsArray(int row, int col) {
        ArrayList<ZKRCPair> list = new ArrayList<>(8);
        for (int dr = -1; dr <= 1; ++dr) {
            for (int dc = -1; dc <= 1; ++dc) {
                if (dr == 0 && dc == 0) continue;
                int rr = row + dr, cc = col + dc;
                if (rr >= 0 && rr < NUM_ROWS && cc >= 0 && cc < NUM_COLS) {
                    list.add(new ZKRCPair(rr, cc));
                }
            }
        }
        return list.toArray(new ZKRCPair[0]);
    }

    boolean saveToFile(String outputFile, int formatFlag) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            for (int r = 0; r < NUM_ROWS; ++r) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < NUM_COLS; ++c) {
                    if (c > 0) sb.append(' ');
                    sb.append(arrayData[r][c]);
                }
                pw.println(sb.toString());
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    void printArray(String title) {
        if (title != null && !title.isEmpty()) System.out.println(title);
        for (int r = 0; r < NUM_ROWS; ++r) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < NUM_COLS; ++c) {
                if (c > 0) sb.append(' ');
                sb.append(arrayData[r][c]);
            }
            System.out.println(sb.toString());
        }
    }

    private ZKRCPair[] getNextNearestNeighborsArray(int row, int col) {
        ArrayList<ZKRCPair> nnn = new ArrayList<>();
        for (int dr = -2; dr <= 2; ++dr) {
            for (int dc = -2; dc <= 2; ++dc) {
                if (Math.abs(dr) <= 1 && Math.abs(dc) <= 1) continue;
                int rr = row + dr, cc = col + dc;
                if (rr >= 0 && rr < NUM_ROWS && cc >= 0 && cc < NUM_COLS) {
                    nnn.add(new ZKRCPair(rr, cc));
                }
            }
        }
        return nnn.toArray(new ZKRCPair[0]);
    }

    private int[][] getClone() {
        int[][] copy = new int[NUM_ROWS][NUM_COLS];
        for (int r = 0; r < NUM_ROWS; ++r) {
            System.arraycopy(arrayData[r], 0, copy[r], 0, NUM_COLS);
        }
        return copy;
    }

    public int[] randomizViaFisherYatesKnuth(int seedOrMode) {
        int n = NUM_ROWS * NUM_COLS;
        int[] flat = new int[n];
        int k = 0;
        for (int r = 0; r < NUM_ROWS; ++r) {
            for (int c = 0; c < NUM_COLS; ++c) {
                flat[k++] = arrayData[r][c];
            }
        }

        java.util.Random rng = (seedOrMode == 0)
                ? new java.util.Random()
                : new java.util.Random(seedOrMode);

        for (int i = n - 1; i > 0; --i) {
            int j = rng.nextInt(i + 1);
            int t = flat[i];
            flat[i] = flat[j];
            flat[j] = t;
        }
        return flat;
    }

    public void debugCloneAndNeighbors(int row, int col) {
        int[][] cloned = getClone();
        ZKRCPair[] nnn = getNextNearestNeighborsArray(row, col);
        System.out.println("Cloned array sample value at (0,0): " + cloned[0][0]);
        System.out.println("Next-nearest count around (" + row + "," + col + "): " + nnn.length);
    }
}
