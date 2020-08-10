package longestSequence;

public class SequenceRange {
//    matchingOnLeft: longest matching sequence on the left
//    matchingOnRight : longest matching sequence on the right
    public int matchingOnLeft, matchingOnRight;

//    longestRange: longest range of matching sequence
//    sequenceLength: the length of the entire sequence
    public int longestRange, sequenceLength;

    public SequenceRange(int left, int right, int longest, int length) {
        this.matchingOnLeft = left;
        this.matchingOnRight = right;
        this.longestRange = longest;
        this.sequenceLength = length;
    }
}
