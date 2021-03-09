package cs455.threads;

import java.util.Vector;

public class Worker extends Thread{
    final Vector<Task> taskQueue;

    public Worker(Vector<Task> tq){
        this.taskQueue = tq;
    }

    @Override
    public void run() {
        super.run();
        while(true){

        }
    }
}
