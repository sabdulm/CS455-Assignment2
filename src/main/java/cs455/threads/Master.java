package cs455.threads;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class Master {
    int randomSeed;
    int sizeMatrix;
    int threadPoolSize;

    final Integer[][] A, B, C, D, X, Y, Z;
    final Vector<Task> taskQueue;
    final Worker[] workers;
    final CountDownLatch latchX, latchY, latchZ;

    public Master(Integer tps, Integer sm, Integer rs){
        this.threadPoolSize = tps;
        this.sizeMatrix = sm;
        this.randomSeed = rs;
        int numIndexes = this.sizeMatrix*this.sizeMatrix;
        this.latchX = new CountDownLatch(numIndexes);
        this.latchY = new CountDownLatch(numIndexes);
        this.latchZ = new CountDownLatch(numIndexes);

        Random randomizer = new Random(this.randomSeed);

        this.A = this.fillInitialArray(randomizer);
        this.B = this.fillInitialArray(randomizer);
        this.C = this.fillInitialArray(randomizer);
        this.D = this.fillInitialArray(randomizer);
        this.X = new Integer[this.sizeMatrix][this.sizeMatrix];
        this.Y = new Integer[this.sizeMatrix][this.sizeMatrix];
        this.Z = new Integer[this.sizeMatrix][this.sizeMatrix];

        this.taskQueue = new Vector<>(0);

        // initialize workers
        this.workers = new Worker[this.threadPoolSize];
        for (int i = 0; i < this.threadPoolSize; i++) {
            this.workers[i] = new Worker(this.taskQueue);
        }

    }

    Integer[][] fillInitialArray(Random randomizer){
        Integer[][] arr = new Integer[this.sizeMatrix][this.sizeMatrix];
        for (int i = 0; i < this.sizeMatrix; i++) {
            for (int j = 0; j < this.sizeMatrix; j++) {
                arr[i][j] = randomizer.nextInt(30);
            }
        }
        return arr;
    }

    void printArrays(){
        Integer [][][] temp = new Integer[4][this.sizeMatrix][this.sizeMatrix];
        temp[0] = this.A;
        temp[1] = this.B;
        temp[2] = this.C;
        temp[3] = this.D;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.sizeMatrix; j++) {
                for (int k = 0; k < this.sizeMatrix; k++) {
                    System.out.printf("%d ", temp[i][j][k]);
                }
                System.out.println("");
            }
            System.out.println("");
        }

    }

    private void addTaskToPool(Integer[][] in1, Integer[][] in2, Integer[][] out, CountDownLatch latch) {

        synchronized (taskQueue) {
            for (int i = 0; i < this.sizeMatrix; i++) {
                for (int j = 0; j < this.sizeMatrix; j++) {
                    Task task = new Task(in1, in2, out, i, j, latch);
                    taskQueue.add(task);
                }
            }
        }
    }

    private Integer calcSum(Integer[][] arr) {
        Integer sum = 0;
        for (int i = 0; i < this.sizeMatrix; i++) {
            for (int j = 0; j < this.sizeMatrix; j++) {
                sum += arr[i][j];
            }
        }
        return sum;
    }

    public void main() throws InterruptedException {
        System.out.printf("Dimensionality of the square matrices is: %d\n", this.sizeMatrix);
        System.out.printf("The thread pool size has been initialized to: : %d\n", this.threadPoolSize);

        System.out.printf("Sum of the elements in input matrix A = %d\n", this.calcSum(this.A));
        System.out.printf("Sum of the elements in input matrix B = %d\n", this.calcSum(this.B));
        System.out.printf("Sum of the elements in input matrix C = %d\n", this.calcSum(this.C));
        System.out.printf("Sum of the elements in input matrix D = %d\n", this.calcSum(this.D));

        // start all worker threads
        for (int i = 0; i < this.threadPoolSize; i++) {
            this.workers[i].start();
        }

        // fill up task pool with tasks and notify all threads
        this.addTaskToPool(this.A, this.B, this.X, this.latchX);
        final long startTimeX = System.currentTimeMillis();
        taskQueue.notifyAll();
        this.latchX.await();
        final long endTimeX = System.currentTimeMillis();

        System.out.println("Calculation of matrix X (product of A and B) complete");
        System.out.printf("sum of the elements in X is: %d\n", this.calcSum(this.X));
        System.out.printf("Time to compute matrix X: %d\n", (endTimeX-startTimeX)/1000);


        // create latches for stages 1(calc X and Y),2(calc Z)
        // print sum of arrays after each stage
    }

}
