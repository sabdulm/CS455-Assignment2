package cs455.threads;

public class TaskQueue {
    final Task[] taskQueue;
    int currentAvailableTask;
    final int maxSize;
    boolean empty = true;
    public TaskQueue(int size) {
        this.maxSize = size;
        this.taskQueue = new Task[this.maxSize];
        this.currentAvailableTask = -1;
    }

    public void addTasksToQueue(Task[] tasks) {
        synchronized (this.taskQueue) {
            for (int i = 0; i < tasks.length; i++) {
                this.taskQueue[i] = tasks[i];
            }
            this.currentAvailableTask = 0;
            this.empty = false;
            this.taskQueue.notifyAll();
        }
    }

    public Task getTask() throws InterruptedException {
        Task task;
        synchronized (this.taskQueue) {
            while(this.empty || this.currentAvailableTask == (this.maxSize-1)) {
//                System.out.printf("%s waiting for tasks %d, %d\n", Thread.currentThread().getName(), this.currentAvailableTask, this.maxSize);
                this.taskQueue.wait();
            }
            task = this.taskQueue[this.currentAvailableTask];

            if(this.currentAvailableTask == (this.maxSize-1)) {
                this.empty = true;
            } else {
                this.currentAvailableTask++;
            }
            this.taskQueue.notifyAll();
        }
        return task;
    }

}
