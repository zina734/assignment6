package pkgZKUtils;

public class ZKGoLArray {

    public ZKPingPongArray pingPongArray;
    private int NUM_ROWS;
    private int NUM_COLS;

    public ZKGoLArray(int rows, int cols) {
        this.NUM_ROWS = rows;
        this.NUM_COLS = cols;
        this.pingPongArray = new ZKPingPongArray(rows, cols);
    }

    public void onTickUpdate() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                int liveNeighbors = pingPongArray.getNNNSum(row, col);
                int currentState = pingPongArray.liveCellArray.arrayData[row][col];

                if (currentState == pingPongArray.ALIVE) {
                    if (liveNeighbors == 2 || liveNeighbors == 3) {
                        pingPongArray.setCell(row, col, pingPongArray.ALIVE);
                    } else {
                        pingPongArray.setCell(row, col, pingPongArray.DEAD);
                    }
                } else {
                    if (liveNeighbors == 3) {
                        pingPongArray.setCell(row, col, pingPongArray.ALIVE);
                    } else {
                        pingPongArray.setCell(row, col, pingPongArray.DEAD);
                    }
                }
            }
        }
        pingPongArray.swapLiveAndNext();
    }

    public int[] getNumRowsCols() {
        if (pingPongArray != null) {
            NUM_ROWS = pingPongArray.NUM_ROWS;
            NUM_COLS = pingPongArray.NUM_COLS;
        }
        return new int[] { NUM_ROWS, NUM_COLS };
    }
}












