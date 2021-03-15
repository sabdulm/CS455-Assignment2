package cs455.threads;

public class Worker extends Thread {
    // reference of the taskqueue
    final TaskQueue taskQueue;

    public Worker(TaskQueue tq) {
        this.taskQueue = tq;
    }

    // simply calculates the value for a certain index in the output matrix, to
    // decrease time it also computes the output at the diagonally opposite index as
    // well : output[x][y] and output[y][x]
    // after which it counts down the latch to signal a task has completed
    private void doTask(Task task) {
        Integer sum1 = 0, sum2 = 0;
        for (int ind = 0; ind < task.size; ind++) {
            sum1 += (task.input1[task.x_coords][ind] * task.input2[ind][task.y_coords]);
            if (task.x_coords != task.y_coords) {
                sum2 += (task.input1[task.y_coords][ind] * task.input2[ind][task.x_coords]);
            }
        }
        task.output[task.x_coords][task.y_coords] = sum1;
        task.latch.countDown();

        if (task.x_coords != task.y_coords) {
            task.output[task.y_coords][task.x_coords] = sum2;
            task.latch.countDown();
        }
    }

    // worker simply gets a task from the queue and does it. untill it is asked to
    // shutdown
    @Override
    public void run() {
        super.run();
        while (true) {
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
