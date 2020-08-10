package longestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class LongestSequence {

    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        SequenceRange result = POOL.invoke(new LongestSequenceTask(arr, 0, arr.length, val, sequentialCutoff));
        return result.longestRange;
    }

    /**
     * Find longest sequence of some integer in an array with given range. Also, give length of sequence if start from
     * low index, otherwise length is 0. And if has sequence reach high index, give that sequence index.
     *
     * @param val a given value
     * @param arr an array
     * @param lo  start index
     * @param hi  end index
     * @return an list with start index and the length of longest sequences, length of sequence start from 0:
     * {longestStartIndex, longestLength, frontLength, lastIndex}
     */
    public static SequenceRange sequence(int val, int[] arr, int lo, int hi) {
        int left = 0;
        int right = 0;
        int longest = 0;
        int length = hi - lo;
        // find length of match sequence on both side
        while ((lo + left < hi) && arr[lo + left] == val) {
            left++;
        }
        while ((hi - 1 - right) >= lo && arr[hi - 1 - right] == val ) {
            right++;
        }
        // find longest length from lo to hi
        int conseq = 0; // current sequence
        for (int i = lo; i < hi; i++) {
            if (arr[i] == val) {
                conseq++;
            } else {
                conseq = 0;
            }
            longest = Math.max(longest, conseq);
        }
        return new SequenceRange(left, right, longest, length);
    }

    private static class LongestSequenceTask extends RecursiveTask<SequenceRange> {
        int[] arr;
        int lo, hi, val, cutoff;

        public LongestSequenceTask(int[] arr, int lo, int hi, int val, int cutoff) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            this.cutoff = cutoff;
        }

        @Override
        protected SequenceRange compute() {
            if (hi - lo <= cutoff) {
                return sequence(val, arr, lo, hi);
            }
            int mid = lo + (hi - lo) / 2;
            LongestSequenceTask leftTask = new LongestSequenceTask(arr, lo, mid, val, cutoff);
            LongestSequenceTask rightTask = new LongestSequenceTask(arr, mid, hi, val, cutoff);
            leftTask.fork();
            SequenceRange rightResult = rightTask.compute();
            SequenceRange leftResult = leftTask.join();

            //    matchingOnLeft: longest matching sequence on the left
            //    matchingOnRight : longest matching sequence on the right
            //    longestRange: longest range of matching sequence
            //    sequenceLength: the length of the entire sequence
            int matchingOnLeft = leftResult.matchingOnLeft;
            int matchingOnRight = rightResult.matchingOnRight;
            int sequenceLength = hi - lo;
            int longestRange;

            // combine left's right and right's
            int combinedLength = leftResult.matchingOnRight + rightResult.matchingOnLeft;
            if (combinedLength == sequenceLength) {
                matchingOnLeft = sequenceLength;
                matchingOnRight = sequenceLength;
                longestRange = sequenceLength;
            } else {
                if (leftResult.matchingOnLeft == leftResult.sequenceLength) {
                    matchingOnLeft += rightResult.matchingOnLeft;
                    longestRange = matchingOnLeft;
                }
                if (rightResult.matchingOnRight == rightResult.sequenceLength) {
                    matchingOnRight += leftResult.matchingOnRight;
                    longestRange = matchingOnRight;
                }
                longestRange = Math.max(leftResult.longestRange, rightResult.longestRange);
                longestRange = Math.max(longestRange, combinedLength);
            }
            // update longest from left.longest right.longest and combined
            longestRange = Math.max(longestRange, combinedLength);
//
//            if (leftResult.sequenceLength > rightResult.sequenceLength) {
//                sequenceLength = leftResult.sequenceLength;
//                longestRange = leftResult.longestRange;
//            } else {
//                sequenceLength = rightResult.sequenceLength;
//                longestRange = rightResult.longestRange;
//            }
            return new SequenceRange(matchingOnLeft, matchingOnRight, longestRange, sequenceLength);
        }
    }

    private static void usage() {
        System.err.println("USAGE: LongestSequence <number> <array> <sequential cutoff>");
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
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
