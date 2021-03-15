package cs455.threads;

public class TaskQueue {
    final Task[] taskQueue;
    int currentAvailableTask;
    int size;
    int capacity;
    boolean empty;

    // the task queue has an initial capacity which is size*size of the input
    // argument
    public TaskQueue(int cap) {
        this.capacity = cap;
        this.taskQueue = new Task[this.capacity];
        this.currentAvailableTask = 0;
        this.empty = true;
    }

    // simply gets the lock of the taskqueue object and adds tasks to it. Then it
    // simply signals all threads waiting on that tasks are available
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

    // gets a lock on the queue. if it is not empty it returns first available task
    // and notifies all threads waiting on it. Otherwise it will wait itself
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
