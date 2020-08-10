package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HasOver {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static int CUTOFF;

    public static boolean hasOver(int var, int[] arr, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new HasOverTask(arr, 0, arr.length, var));
    }

    /**
     *
     * @param arr a list contain integers to compare
     * @param lo start index
     * @param hi end index
     * @param var an integer compare to
     * @return Returns true if arr has any elements strictly larger than val.
     */
    public static boolean sequential(int[] arr, int lo, int hi, int var) {
        for(int i = lo; i < hi; i++) {
            if(arr[i] > var) {
                return true;
            }
        }
        return  false;
    }

    private static class HasOverTask extends RecursiveTask<Boolean> {
        int[] arr;
        int lo, hi, var;

        public HasOverTask(int[] arr, int lo, int hi, int var) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.var = var;
        }
        @Override
        protected Boolean compute() {
            if(hi - lo <= CUTOFF) {
                return sequential(arr, lo, hi, var);
            }
            int mid = lo + (hi - lo)/2;

            HasOverTask leftTask = new HasOverTask(arr, lo, mid, var);
            HasOverTask rightTask = new HasOverTask(arr, mid, hi, var);
            leftTask.fork();
            boolean rightResult = rightTask.compute();
            boolean leftResult = leftTask.join();

            return rightResult || leftResult;
        }
    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*",  "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }
}
