// Entry Class for the program. Takes in three arguments as input and then calls the Master class which basically handles the rest of the program

package cs455.threads;

public class MatrixThreads {

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.out.println("Invalid number of arguments");
            System.out.println("Usage: java cs455.threads.MatrixThreads thread-pool-size matrix-dimension seed");
            System.out.println("Example: java cs455.threads.MatrixThreads 10 3000 31459");
            return;
        }

        int threadPool = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        int seed = Integer.parseInt(args[2]);

        Master mainRunner = new Master(threadPool, size, seed);
        mainRunner.main();

    }
}
