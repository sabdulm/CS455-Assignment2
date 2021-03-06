package cs455.threads;

import java.util.concurrent.CountDownLatch;

// Simply a data transfer object (DTO) used to signify a single task(the value of a single index in the output array)
public class Task {
    final public int[][] input1, input2, output;
    final public int x_coords, y_coords, size;
    final public boolean done;
    final CountDownLatch latch;

    public Task(int[][] in1, int[][] in2, int[][] out, int x, int y, int s, CountDownLatch l) {
        this.input1 = in1;
        this.input2 = in2;
        this.output = out;
        this.x_coords = x;
        this.y_coords = y;
        this.size = s;
        this.done = false;
        this.latch = l;
    }

    public Task(int[][] in1, int[][] in2, int[][] out, int x, int y, int s, CountDownLatch l, boolean d) {
        this.input1 = in1;
        this.input2 = in2;
        this.output = out;
        this.x_coords = x;
        this.y_coords = y;
        this.size = s;
        this.done = d;
        this.latch = l;
    }
}
