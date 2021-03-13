package cs455.threads;

import java.util.concurrent.CountDownLatch;

public class Task {
    final public Integer[][] input1, input2, output;
    final public int x_coords, y_coords, size;
    final public boolean done;
    final CountDownLatch latch;

    public Task(Integer[][] in1, Integer[][] in2, Integer[][] out, int x, int y, int s, CountDownLatch l) {
        this.input1 = in1;
        this.input2 = in2;
        this.output = out;
        this.x_coords = x;
        this.y_coords = y;
        this.size = s;
        this.done = false;
        this.latch = l;
    }

    public Task(Integer[][] in1, Integer[][] in2, Integer[][] out, int x, int y, int s, CountDownLatch l, boolean d) {
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
