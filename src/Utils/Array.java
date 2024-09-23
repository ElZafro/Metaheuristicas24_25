package Utils;

public abstract class Array {

    public static void Swap(final int[] arr, final int i, final int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static int Find(final int[] arr, final int el) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == el) {
                return i;
            }
        }
        return -1;
    }
}
