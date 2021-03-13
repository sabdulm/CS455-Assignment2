package cs455.threads;

import java.util.concurrent.CountDownLatch;

public class Task{
    final public Integer[][] input1, input2, output;
    final public int x_coords, y_coords, size;
    final public boolean done;
    final CountDownLatch latch;


    public Task(Integer[][] in1, Integer[][] in2, Integer[][] out, int x, int y, int s, CountDownLatch l){
        this.input1 = in1;
        this.input2 = in2;
        this.output = out;
        this.x_coords = x;
        this.y_coords = y;
        this.size = s;
        this.done = false;
        this.latch = l;
    }

    public Task(Integer[][] in1, Integer[][] in2, Integer[][] out, int x, int y, int s, CountDownLatch l, boolean d){
        this.input1 = in1;
        this.input2 = in2;
        this.output = out;
        this.x_coords = x;
        this.y_coords = y;
        this.size = s;
        this.done = d;
        this.latch = l;
    }

//    @Override
//    public void run() {
//        Integer sum1 = 0, sum2 = 0;
//
//        for (int ind = 0; ind < this.size; ind++) {
//            sum1 +=  (this.input1[this.x_coords][ind] * this.input2[ind][this.y_coords]);
////            if (this.y_coords != this.x_coords) {
////                sum2 +=  (this.input1[ind][this.x_coords] * this.input2[this.y_coords][ind]);
////            }
//        }
////        System.out.printf("%s completed task %d, %d\n", Thread.currentThread().getName(), task.x_coords, task.y_coords);
//        this.output[this.x_coords][this.y_coords] = sum1;
////        if (this.y_coords != this.x_coords) {
////            this.output[this.y_coords][this.x_coords] = sum2;
////            this.latch.countDown();
////        }
//        this.latch.countDown();
//    }
}
