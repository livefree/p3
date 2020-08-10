package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    public static final ForkJoinPool POOL = new ForkJoinPool();

    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        return POOL.invoke(new GetLeftMostIndexTask(needle, haystack, sequentialCutoff, 0, haystack.length));

//        return sequentialCompute(needle, haystack, 0, haystack.length);
    }

    static class GetLeftMostIndexTask extends RecursiveTask<Integer> {
        char[] needle;
        char[] haystack;
        int cutoff;
        int lo;
        int hi;

        public GetLeftMostIndexTask(char[] needle, char[] haystack, int cutoff, int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
            this.cutoff = cutoff;
            this.needle = needle;
            this.haystack = haystack;
        }

        public Integer compute() {
            if (hi - lo <= cutoff || (hi - lo) <= needle.length * 2) {
                return sequentialCompute(needle, haystack, lo, hi);
            }
            int mid = lo + (hi - lo) / 2;
            GetLeftMostIndexTask left = new GetLeftMostIndexTask(needle, haystack, cutoff, lo, mid);
            GetLeftMostIndexTask right = new GetLeftMostIndexTask(needle, haystack, cutoff, mid, hi);
//            GetLeftMostIndexTask middle = new GetLeftMostIndexTask(needle, haystack, cutoff, mid - (needle.length - 1), mid + (needle.length - 1));
            int midVal = sequentialCompute(needle, haystack, (mid- needle.length+1), (mid + needle.length));
//            left.fork();
            right.fork();
//            int midVal = middle.compute();
            int leftVal = left.compute();
            int rightVal = right.join();

            if (leftVal != -1) {
                return leftVal;
            } else if (midVal != -1) {
                return midVal;
            } else {
                return rightVal;
            }
            // scanning the middle region where the word could by skipped by the left's and right's
//            if ((mid - needle.length - 1) > lo && (mid + needle.length - 1) <= hi) {
//                midVal = sequentialCompute(mid - needle.length - 1, mid + needle.length - 1);
//            }

        }

//        public int sequentialCompute(int lo, int hi) {
//            int needle_index = 0;
//            for (int i = lo; i < hi; i++) {
//                // iterate through the entire haystack array
//                if (haystack[i] == needle[needle_index]) {
//                    // if the starting index is found in the needle
//                    needle_index++;
//                } else {
//                    needle_index = 0;
//                }
//                if (needle_index == needle.length) {
//                    return i - needle_index + 1;
//                }
//            }
//            // not found
//            return -1;
//        }
    }

    /**
     * Find the first occurrence of needle in haystack.
     *
     * @param needle   a small string
     * @param haystack a much larger string
     * @return index of the left-most occurrence of needle in haystack if applicable, otherwise return -1.
     */
    public static int sequentialCompute(char[] needle, char[] haystack, int lo, int hi) {
        int needle_index = 0;
        for (int i = lo; i < hi; i++) {
            // iterate through the entire haystack array
            if (haystack[i] == needle[0]) {
                for(int j = 1; (j < needle.length && i + needle.length <= haystack.length); j++) {
                    if(haystack[i+j] != needle[j]) {
                        break;
                    }
                    needle_index = j;
                }
                if(needle_index == needle.length-1){
                    return i;
                }
            } else {
                needle_index = 0;
            }

        }
        // not found
        return -1;
    }

    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
//        char[] needle = needle = "001".toCharArray();
//        char[] haystack = "0000000001".toCharArray();
//        getLeftMostIndex(needle, haystack, 1);
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
