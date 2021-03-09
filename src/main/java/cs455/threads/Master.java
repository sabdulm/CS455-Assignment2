package cs455.threads;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class Master {
    int randomSeed;
    int sizeMatrix;
    int threadPoolSize;

    final Integer[][] A, B, C, D, X, Y, Z;
    final ArrayList<Task> taskQueue;
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

        this.taskQueue = new ArrayList<>(numIndexes*2 + numIndexes);

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
                arr[i][j] = randomizer.nextInt();
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

        synchronized (this.taskQueue) {
            for (int i = 0; i < this.sizeMatrix; i++) {
                for (int j = i; j < this.sizeMatrix; j++) {
                    Task task = new Task(in1, in2, out, i, j, this.sizeMatrix,latch);
                    this.taskQueue.add(task);
                }
            }
            this.taskQueue.notifyAll();
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

    private void closeWorkers() {
        synchronized (this.taskQueue) {
            for (int i = 0; i < this.threadPoolSize; i++) {
                Task task = new Task(null, null, null, 0, 0, 0, null, true);
                this.taskQueue.add(task);
            }
            this.taskQueue.notifyAll();
        }
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
        final long startTimeX = System.currentTimeMillis();
        this.addTaskToPool(this.A, this.B, this.X, this.latchX);

        this.latchX.await();
        final long endTimeX = System.currentTimeMillis();
        final double timeX = (endTimeX - startTimeX) / 1000.0;


        System.out.println("Calculation of matrix X (product of A and B) complete");
        System.out.printf("sum of the elements in X is: %d\n", this.calcSum(this.X));
        System.out.printf("Time to compute matrix X: %f\n", timeX);

        final long startTimeY = System.currentTimeMillis();
        this.addTaskToPool(this.C, this.D, this.Y, this.latchY);

        this.latchY.await();
        final long endTimeY = System.currentTimeMillis();
        final double timeY = (endTimeY - startTimeY) / 1000.0;

        System.out.println("Calculation of matrix Y (product of C and D) complete");
        System.out.printf("sum of the elements in Y is: %d\n", this.calcSum(this.Y));
        System.out.printf("Time to compute matrix Y: %f\n", timeY);

        final long startTimeZ = System.currentTimeMillis();
        this.addTaskToPool(this.X, this.Y, this.Z, this.latchZ);
        this.latchZ.await();
        final long endTimeZ = System.currentTimeMillis();
        final double timeZ = (endTimeZ - startTimeZ) / 1000.0;

        System.out.println("Calculation of matrix Z (product of X and Y) complete");
        System.out.printf("sum of the elements in Z is: %d\n", this.calcSum(this.Z));
        System.out.printf("Time to compute matrix Z: %f\n", timeZ);

        System.out.printf("Cumulative time to compute matrixes X, Y, and Z using a thread pool of size = %d is : %f\n", this.threadPoolSize,timeX+timeY+timeZ);

        this.closeWorkers();
    }

}
