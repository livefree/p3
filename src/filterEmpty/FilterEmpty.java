package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import cse332.exceptions.NotYetImplementedException;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    public static int[] filterEmpty(String[] arr) {
        int[] bitset = mapToBitSet(arr);
//        System.out.println(java.util.Arrays.toString(bitset));
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bitset);
//        System.out.println(java.util.Arrays.toString(bitsum));
        int[] result = mapToOutput(arr, bitsum);
        return result;
    }

    public static int[] mapToBitSet(String[] arr) {
        int[] bitset = new int[arr.length];
        POOL.invoke(new MapToBitSet(arr, bitset, 0, arr.length));
        return bitset;
    }

    private static class MapToBitSet extends RecursiveAction {
        int lo, hi;
        int cutoff;
        String[] arr;
        int[] res;

        public MapToBitSet(String[] arr, int[] res, int lo, int hi) {
            this.arr = arr;
            this.res = res;
            this.cutoff = 1;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {
            if (hi - lo <= cutoff) {
                for (int i = lo; i < hi; i++) {
                    if (arr[i] == null || arr[i].isEmpty()) {
                        res[i] = 0;
                    } else {
                        res[i] = 1;
                    }
                }
            } else {
                int mid = lo + (hi - lo) / 2;
                MapToBitSet left = new MapToBitSet(arr, res, lo, mid);
                MapToBitSet right = new MapToBitSet(arr, res, mid, hi);
                left.fork();
                right.compute();
                left.join();
            }
        }
    }

    public static int[] mapToOutput(String[] input, int[] bitsum) {
        int[] output;
        if (bitsum.length == 0) {
            return new int[0];
        } else {
            output = new int[bitsum[bitsum.length - 1]];
        }
        POOL.invoke(new MapToOutput(input, output, bitsum));
        return output;
    }

    private static class MapToOutput extends RecursiveAction {
        String[] input;
        int[] bitsum;
        int[] output;
        int lo, hi;
        int cutoff;

        public MapToOutput(String[] input, int[] output, int[] bitsum) {
            this.input = input;
            this.output = output;
            this.bitsum = bitsum;
            this.lo = 0;
            this.hi = input.length;
            this.cutoff = 1;
        }

        public MapToOutput(String[] input, int[] output, int[] bitsum, int lo, int hi) {
            this.input = input;
            this.output = output;
            this.bitsum = bitsum;
            this.lo = lo;
            this.hi = hi;
            this.cutoff = 1;
        }

        @Override
        protected void compute() {
            if (hi - lo == cutoff) {
                for (int i = lo; i < hi; i++) {
                    if (i == 0 && bitsum[i] > 0) {
                        output[bitsum[i] - 1] = input[i].length();
                    } else if (i > 0 && bitsum[i] > bitsum[i - 1]) {
                        output[bitsum[i] - 1] = input[i].length();
                    }
                }
            } else {
                int mid = lo + (hi - lo) / 2;
                MapToOutput left = new MapToOutput(input, output, bitsum, lo, mid);
                MapToOutput right = new MapToOutput(input, output, bitsum, mid, hi);
                left.fork();
                right.compute();
                left.join();
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}
//
