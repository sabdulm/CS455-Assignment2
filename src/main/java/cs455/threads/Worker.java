package cs455.threads;


public class Worker extends Thread{
    final TaskQueue taskQueue;

    public Worker(TaskQueue tq){
        this.taskQueue = tq;
    }


    private void doTask(Task task){
        Integer sum1 = 0, sum2 = 0;

        for (int ind = 0; ind < task.size; ind++) {
            sum1 +=  (task.input1[task.x_coords][ind] * task.input2[ind][task.y_coords]);
            if (task.y_coords != task.x_coords) {
                sum2 +=  (task.input1[ind][task.x_coords] * task.input2[task.y_coords][ind]);
            }
        }
//        System.out.printf("%s completed task %d, %d\n", Thread.currentThread().getName(), task.x_coords, task.y_coords);
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
                Task task = this.taskQueue.getTask();
                if (task != null) {
                    if (task.done) {
                        return;
                    }
                    this.doTask(task);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
