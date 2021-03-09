package cs455.threads;

import java.util.ArrayList;
import java.util.Vector;

public class Worker extends Thread{
    final ArrayList<Task> taskQueue;

    public Worker(ArrayList<Task> tq){
        this.taskQueue = tq;
    }

    private Task getTask() throws InterruptedException {
        Task task;
        synchronized (this.taskQueue) {
            while (this.taskQueue.isEmpty()){
                this.taskQueue.wait();
            }
            int ind = this.taskQueue.size() -1;
            task = this.taskQueue.get(ind);
            this.taskQueue.remove(ind);
//            this.taskQueue.removeElementAt(this.taskQueue.size()-1);
            this.taskQueue.notifyAll();
        }
        return task;
    }

    private void doTask(Task task){
        Integer sum = 0;

        for (int ind = 0; ind < task.size; ind++) {
            sum +=  (task.input1[task.x_coords][ind] * task.input2[ind][task.y_coords]);
        }
        // System.out.printf("%s completed task %d, %d\n", Thread.currentThread().getName(), task.x_coords, task.y_coords);
        task.output[task.x_coords][task.y_coords] = sum;
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
