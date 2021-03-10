package cs455.threads;

import java.util.ArrayList;
import java.util.Vector;

public class Worker extends Thread{
    final Task[] taskQueue;
    int taskIndex;
    public Worker(Task[] tq, int ind){
        this.taskQueue = tq;
        this.taskIndex = ind;
    }

    private Task getTask() throws InterruptedException {
        Task task;
        synchronized (this.taskQueue) {
            while (this.taskIndex == this.taskQueue.length){
                this.taskQueue.wait();
            }
            task = this.taskQueue[this.taskIndex];
            this.taskIndex++;
//            this.taskQueue.removeElementAt(this.taskQueue.size()-1);
            this.taskQueue.notifyAll();
        }
        return task;
    }

    private void doTask(Task task){
        Integer sum1 = 0, sum2 = 0;

        for (int ind = 0; ind < task.size; ind++) {
            sum1 +=  (task.input1[task.x_coords][ind] * task.input2[ind][task.y_coords]);
            if (task.y_coords != task.x_coords) {
                sum2 +=  (task.input1[ind][task.x_coords] * task.input2[task.y_coords][ind]);
            }
        }
        // System.out.printf("%s completed task %d, %d\n", Thread.currentThread().getName(), task.x_coords, task.y_coords);
        task.output[task.x_coords][task.y_coords] = sum1;
        if (task.y_coords != task.x_coords) {
            task.output[task.y_coords][task.x_coords] = sum2;
            task.latch.countDown();
        }
        task.latch.countDown();
    }

    @Override
    public void run() {
        super.run();
        while(true){
            try {
                Task task = this.getTask();
                if (task.done) {
                    return;
                }
                this.doTask(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
