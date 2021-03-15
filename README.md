# Sheikh Mannan - sheikh.mannan@colostate.edu

# CS455-Assignment2

## files present in the src dir:

-   Master.java - the core functionality of the program
-   MatrixThreads.java - the entry point of the program
-   Task.java - Data Transfer Object which represents a single task
-   TaskQueue.java - implements the task queue
-   Worker.java - implements the worker functionality

## Things to note

-   Usage - `java -cp <jar_file_path> cs455.threads.MatrixThreads thread-pool-size matrix-dimension seed`
    -   threadpoolsize, matrix-dimension and seed are all ints
-   For threadpoolsize (10), matrix-dimension(3000) the program takes about 300 seconds to complete.
