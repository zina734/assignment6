package pkgZKUtils;

import java.util.Random;

public class ZKRandomArray {
    public ZKRandomArray() {
        int[] a = getRandomizedIntegerArray(5);
        randomizeIntegerArray(a);
    }

    public void randomizeIntegerArray(int[] inputArray) {
        Random rng = new Random();
        for (int i = inputArray.length - 1; i > 0; --i) {
            int j = rng.nextInt(i + 1);
            int t = inputArray[i];
            inputArray[i] = inputArray[j];
            inputArray[j] = t;
        }
    }

    public int[] getRandomizedIntegerArray(int size) {
        int[] a = new int[size];
        for (int i = 0; i < size; ++i) a[i] = i;
        randomizeIntegerArray(a);
        return a;
    }
}