// The Master class handles the core program. It creates and initializes all the matrices with the provided size and seed.
// Master also handles the creating of the worker thread pool and the task queue

package cs455.threads;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Master {
    int randomSeed;
    int sizeMatrix;
    int threadPoolSize;

    final int[][] A, B, C, D, X, Y, Z;
    final TaskQueue taskQueue;
    final Worker[] workers;
    final CountDownLatch latchX, latchY, latchZ;

    public Master(Integer tps, Integer sm, Integer rs) {
        this.threadPoolSize = tps;
        this.sizeMatrix = sm;
        this.randomSeed = rs;
        int numIndexes = this.sizeMatrix * this.sizeMatrix;

        // latches for synchronizing stages of the program
        this.latchX = new CountDownLatch(numIndexes);
        this.latchY = new CountDownLatch(numIndexes);
        this.latchZ = new CountDownLatch(numIndexes);

        // initializes the randomizer with the given seed to fill in the initial matrix
        // values
        Random randomizer = new Random(this.randomSeed);

        this.A = this.fillInitialArray(randomizer);
        this.B = this.fillInitialArray(randomizer);
        this.C = this.fillInitialArray(randomizer);
        this.D = this.fillInitialArray(randomizer);
        this.X = new int[this.sizeMatrix][this.sizeMatrix];
        this.Y = new int[this.sizeMatrix][this.sizeMatrix];
        this.Z = new int[this.sizeMatrix][this.sizeMatrix];

        // initialize the task queue

        this.taskQueue = new TaskQueue(numIndexes);

        // initialize workers

        this.workers = new Worker[this.threadPoolSize];
        for (int i = 0; i < this.threadPoolSize; i++) {
            this.workers[i] = new Worker(this.taskQueue);
        }

    }

    // fills in the random values to the array
    int[][] fillInitialArray(Random randomizer) {
        int[][] arr = new int[this.sizeMatrix][this.sizeMatrix];
        for (int i = 0; i < this.sizeMatrix; i++) {
            for (int j = 0; j < this.sizeMatrix; j++) {
                arr[i][j] = randomizer.nextInt();
            }
        }
        return arr;
    }

    // adds tasks to the task pool. firstly creates a list of tasks which is sent to
    // the task pool object to be added
    private void addTaskToPool(int[][] in1, int[][] in2, int[][] out, CountDownLatch latch) {
        Task[] tasks = new Task[this.sizeMatrix * this.sizeMatrix];
        int index = 0;
        for (int i = 0; i < this.sizeMatrix; i++) {
            for (int j = i; j < this.sizeMatrix; j++) {
                Task task = new Task(in1, in2, out, i, j, this.sizeMatrix, latch);
                tasks[index] = task;
                index++;
            }
        }
        this.taskQueue.addTasksToQueue(tasks);
    }

    // utility func to calculate the sum of a matrix
    private int calcSum(int[][] arr) {
        int sum = 0;
        for (int i = 0; i < this.sizeMatrix; i++) {
            for (int j = 0; j < this.sizeMatrix; j++) {
                sum += arr[i][j];
            }
        }
        return sum;
    }

    // signals the worker threads to shutdown
    private void closeWorkers() {
        Task[] tasks = new Task[this.threadPoolSize];
        for (int i = 0; i < this.threadPoolSize; i++) {
            Task task = new Task(null, null, null, 0, 0, 0, null, true);
            tasks[i] = task;
        }
        this.taskQueue.addTasksToQueue(tasks);
    }

    public void main() throws InterruptedException {
        // start the workers to execute their run method
        for (int i = 0; i < this.threadPoolSize; i++) {
            this.workers[i].start();
        }
        System.out.printf("Dimensionality of the square matrices is: %d\n", this.sizeMatrix);
        System.out.printf("The thread pool size has been initialized to: : %d\n\n", this.threadPoolSize);

        System.out.printf("Sum of the elements in input matrix A = %d\n", this.calcSum(this.A));
        System.out.printf("Sum of the elements in input matrix B = %d\n", this.calcSum(this.B));
        System.out.printf("Sum of the elements in input matrix C = %d\n", this.calcSum(this.C));
        System.out.printf("Sum of the elements in input matrix D = %d\n\n", this.calcSum(this.D));

        // for each stage of the program, it adds tasks to the queue and then waits for
        // completion
        // after which the sum and time is printed out

        final long startTimeX = System.currentTimeMillis();
        this.addTaskToPool(this.A, this.B, this.X, this.latchX);

        this.latchX.await();
        final long endTimeX = System.currentTimeMillis();
        final double timeX = (endTimeX - startTimeX) / 1000.0;

        System.out.println("Calculation of matrix X (product of A and B) complete");
        System.out.printf("sum of the elements in X is: %d\n", this.calcSum(this.X));
        System.out.printf("Time to compute matrix X: %f\n\n", timeX);

        final long startTimeY = System.currentTimeMillis();
        this.addTaskToPool(this.C, this.D, this.Y, this.latchY);

        this.latchY.await();
        final long endTimeY = System.currentTimeMillis();
        final double timeY = (endTimeY - startTimeY) / 1000.0;

        System.out.println("Calculation of matrix Y (product of C and D) complete");
        System.out.printf("sum of the elements in Y is: %d\n", this.calcSum(this.Y));
        System.out.printf("Time to compute matrix Y: %f\n\n", timeY);

        final long startTimeZ = System.currentTimeMillis();
        this.addTaskToPool(this.X, this.Y, this.Z, this.latchZ);
        this.latchZ.await();
        final long endTimeZ = System.currentTimeMillis();
        final double timeZ = (endTimeZ - startTimeZ) / 1000.0;

        System.out.println("Calculation of matrix Z (product of X and Y) complete");
        System.out.printf("sum of the elements in Z is: %d\n", this.calcSum(this.Z));
        System.out.printf("Time to compute matrix Z: %f\n\n", timeZ);

        System.out.printf("Cumulative time to compute matrixes X, Y, and Z using a thread pool of size = %d is : %f\n",
                this.threadPoolSize, timeX + timeY + timeZ);

        // simply signal the workers to close
        this.closeWorkers();
    }

}
