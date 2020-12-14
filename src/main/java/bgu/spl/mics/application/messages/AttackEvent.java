package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AttackEvent implements Event<Boolean> {
	private int duration;
	private List<Integer> serials;


    public AttackEvent(Attack attack)
    {
        duration = attack.getDuration();
        serials = sortSerials(attack.getSerials());
    }

    private List<Integer> sortSerials(List<Integer> serialNumbers) {
        int[] arr = new int[serialNumbers.size()];
        int n = arr.length;
        List<Integer> ListOutput = new LinkedList<>();

        int index = 0;
        for (int num: serialNumbers){
            arr[index] = num;
            index++;
        }

        radixSort(arr, n);
        // copy the output array to List<int>
        for (int i =0; i< n; i++){
            ListOutput.add(arr[i]);
        }
        return ListOutput;

    }

    private void radixSort(int[] arr, int n) {
        int max = getMax(arr,n);

        for (int i=1; max/i >0; i = i* 10 )
            countSort(arr,n,i);
    }

    private void countSort(int[] arr, int n, int exp) {
        int[] output = new int[n];
        int i;
        int[] count = new int[10];


        for (i=0; i<n; i++)
            count[i] = 0;

        //store count of occurrences in count[]
        for (i=0; i<n; i++)
            count[(arr[i]/exp) % 10]++;

        // make that count will contain the right index of each number as it supposed to appear in output
        for (i=1; i<10; i++)
            count[i] = count[i] + count [i-1];

        // build the output array
        for (i = n-1; i>= 0; i--){
            output[count[(arr[i]/exp) % 10 ]-1] = arr[i];
            count[(arr[i]/exp) % 10]--;
        }

        for (i=0; i< n; i++)
            arr[i] = output[i];

    }

    private int getMax(int[] arr, int n) {
        int max = arr[0];
        for(int i=1; i< arr.length; i++){
            if (arr[i] > max)
                max = arr[i];
        }
        return max;
    }

    public int getDuration(){return duration;}

    public List<Integer> getSerials(){return serials;}

}
