package cs455.threads;

import java.util.Vector;

public class Worker extends Thread{
    final Vector<Task> taskQueue;

    public Worker(Vector<Task> tq){
        this.taskQueue = tq;
    }

    private Task getTask() throws InterruptedException {
        Task task;
        synchronized (this.taskQueue) {
            if (this.taskQueue.isEmpty()){
                this.taskQueue.wait();
            }
            task = this.taskQueue.firstElement();
            this.taskQueue.remove(0);
            this.taskQueue.notifyAll();
        }
        return task;
    }

    private void doTask(Task task){
        Integer sum = 0;
        for (int ind = 0; ind < task.size; ind++) {
            sum +=  (task.input1[task.x_coords][ind] * task.input2[ind][task.y_coords]);
        }
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
