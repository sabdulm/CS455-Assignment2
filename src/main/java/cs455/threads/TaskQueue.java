package cs455.threads;

public class TaskQueue {
    final Task[] taskQueue;
    int currentAvailableTask;
    int size;
    int capacity;
    boolean empty;

    public TaskQueue(int cap) {
        this.capacity = cap;
        this.taskQueue = new Task[this.capacity];
        this.currentAvailableTask = 0;
        this.empty = true;
    }

    public void addTasksToQueue(Task[] tasks) {

        synchronized (this.taskQueue) {
            for (int i = 0; i < tasks.length; i++) {
                this.taskQueue[i] = tasks[i];
            }
            this.empty = false;
            this.currentAvailableTask = 0;
            this.size = tasks.length;
            this.taskQueue.notifyAll();
        }
    }

    public Task getTask() throws InterruptedException {
        Task task;

        synchronized (this.taskQueue) {

            if (this.empty) {
                this.taskQueue.wait();
            }
            task = this.taskQueue[this.currentAvailableTask];

            if (this.currentAvailableTask == (this.size - 1)) {
                this.empty = true;
            }
            this.currentAvailableTask++;
            this.taskQueue.notifyAll();
        }
        return task;
    }

}
