import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class SortObject{
    int value;
    String a2;

    public SortObject(int value, String a2) {
        this.value = value;
        this.a2 = a2;
    }
}

public class Main {

    static  ArrayList<SortObject> cloneList(ArrayList<SortObject> list){
        ArrayList<SortObject> cloned_list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {                              //i got an error on dev with java's method so i impleneted one
            cloned_list.add(list.get(i));
        }
        return cloned_list;
    }

    static ArrayList<SortObject> createList(int size){
        ArrayList<SortObject> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {                                                    //for creating random valued object list
            SortObject object = new SortObject(new Random().nextInt(10000),"");
            list.add(object);
        }
        return list;
    }

    static void swap(ArrayList<SortObject> list, int x, int y) {
        SortObject temp = list.get(x);
        list.set(x, list.get(y));
        list.set(y, temp);
    }

    static void combSort(ArrayList<SortObject> list){
        int len_gap = list.size();
        float shrink_val = 1.3f;
        boolean swap = false;
        while (len_gap > 1 || swap) {
            if (len_gap > 1) {
                len_gap = (int)(len_gap / shrink_val);
            }
            swap = false;
            for (int i = 0; len_gap + i < list.size(); i++){
                if (list.get(i).value > list.get(i + len_gap).value) {
                    swap(list, i, i + len_gap);
                    swap = true;
                }
            }
        }
    }

    static void gnomeSort(ArrayList<SortObject> list){
        int index = 0;
        while (index < list.size()) {
            if (index == 0)
                index++;
            if (list.get(index).value >= list.get(index - 1).value)
                index++;
            else {
                swap(list, index, index - 1);
                index--;
            }
        }
    }

    static void shakerSort(ArrayList<SortObject> list){
        for (int i = 0; i < list.size()/2; i++) {
            boolean swapped = false;
            for (int j = i; j < list.size() - i - 1; j++) {
                if (list.get(j).value > list.get(j+1).value) {
                    swap(list, j, j+1);
                }
            }
            for (int j = list.size() - 2 - i; j > i; j--) {
                if (list.get(j).value < list.get(j-1).value) {
                    swap(list, j, j-1);
                    swapped = true;
                }
            }
            if(!swapped) break;
        }
    }

    static void stoogeSort(ArrayList<SortObject> list,int l, int h){
        {
            if (l >= h)
                return;
            if (list.get(l).value > list.get(h).value)
            {
                swap(list, l, h);
            }
            if (h-l+1 > 2)
            {
                int t = (h-l+1) / 3;
                stoogeSort(list, l, h-t);
                stoogeSort(list, l+t, h);
                stoogeSort(list, l, h-t);
            }
        }
    }

    static void bitonicSwap(ArrayList<SortObject> list, int i, int j, int direction){
        if ( (list.get(i).value > list.get(j).value && direction == 1) ||
                (list.get(i).value < list.get(j).value && direction == 0))
        {
            SortObject temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    static void bitonicMerge(ArrayList<SortObject> list, int low, int cnt, int direction){
        if (cnt>1)
        {
            int k = cnt/2;
            for (int i=low; i<low+k; i++){
                bitonicSwap(list, i,i+k, direction);
            }
            bitonicMerge(list, low, k, direction);
            bitonicMerge(list,low+k, k, direction);
        }
    }

    static void bitonicSort(ArrayList<SortObject> list, int low, int cnt, int direction){
        if (cnt>1)
        {
            int k = cnt/2;
            bitonicSort(list, low, k, 1);
            bitonicSort(list,low+k, k, 0);
            bitonicMerge(list, low, cnt, direction);
        }
    }

    static void print(ArrayList<SortObject> l){
        for (int i = 0; i < l.size(); i++) {
            System.out.print(l.get(i).value + l.get(i).a2+ " ");    //to display the lists in a way that we can check the sort algorithms' stability
        }
        System.out.println("");
    }

    static void sortTimer(String sort, ArrayList<SortObject> list) {
        long start_time;
        long stop_time;
        long elapsed_time_sum;
        ArrayList<SortObject> temp_list;
        if (sort.equals("comb")){                                   //runs every sort algorithm 10 times, sums up the elapsed times and divides them by 10 to find an average
            elapsed_time_sum = 0;                                   //later, displays this time on screen
            for (int i = 0; i < 10; i++) {
                temp_list = cloneList(list);
                start_time = System.nanoTime();
                combSort(temp_list);
                stop_time = System.nanoTime();
                elapsed_time_sum += stop_time - start_time;
            }
            long elapsed_time_avarage = elapsed_time_sum/10;
            System.out.println("Avarage sorting time of " + sort + " sort is: "+elapsed_time_avarage);
        }else if (sort.equals("gnome")){
            elapsed_time_sum = 0;
            for (int i = 0; i < 10; i++) {
                temp_list = cloneList(list);
                start_time = System.nanoTime();
                gnomeSort(temp_list);
                stop_time = System.nanoTime();
                elapsed_time_sum += stop_time - start_time;
            }
            long elapsed_time_avarage = elapsed_time_sum/10;
            System.out.println("Avarage sorting time of " + sort + " sort is: "+elapsed_time_avarage);
        }else if (sort.equals("shaker")){
            elapsed_time_sum = 0;
            for (int i = 0; i < 10; i++) {
                temp_list = cloneList(list);
                start_time = System.nanoTime();
                shakerSort(temp_list);
                stop_time = System.nanoTime();
                elapsed_time_sum += stop_time - start_time;
            }
            long elapsed_time_avarage = elapsed_time_sum/10;
            System.out.println("Avarage sorting time of " + sort + " sort is: "+elapsed_time_avarage);
        }else if (sort.equals("stooge")){
            elapsed_time_sum = 0;
            for (int i = 0; i < 10; i++) {
                temp_list = cloneList(list);
                start_time = System.nanoTime();
                stoogeSort(temp_list, 0, temp_list.size()-1);
                stop_time = System.nanoTime();
                elapsed_time_sum += stop_time - start_time;
            }
            long elapsed_time_avarage = elapsed_time_sum/10;
            System.out.println("Avarage sorting time of " + sort + " sort is: "+elapsed_time_avarage);
        }else if (sort.equals("bitonic")){
            elapsed_time_sum = 0;
            for (int i = 0; i < 10; i++) {
                temp_list = cloneList(list);
                start_time = System.nanoTime();
                bitonicSort(temp_list, 0, temp_list.size(), 1);
                stop_time = System.nanoTime();
                elapsed_time_sum += stop_time - start_time;
            }
            long elapsed_time_avarage = elapsed_time_sum/10;
            System.out.println("Avarage sorting time of " + sort + " sort is: "+elapsed_time_avarage);
        }
    }

    static void tryAllSizes(int[] input_sizes){
        for (int input : input_sizes) {
            ArrayList<SortObject> list = createList(input);
            System.out.println("Sort times for "+input+" inputs on avarage case:");
            ArrayList<SortObject> temp_list = cloneList(list);
            sortTimer("comb", temp_list);
            temp_list = cloneList(list);
            sortTimer("gnome", temp_list);                      //tries the sorting algorithms with every input size on the array
            temp_list = cloneList(list);                            // then tries them again for the worst case
            sortTimer("shaker", temp_list);
            temp_list = cloneList(list);
            sortTimer("stooge", temp_list);
            temp_list = cloneList(list);                            //i don't think that it's true but i used reversely sorted list
            sortTimer("bitonic", temp_list);                    //for every sorting method as the worst case situation
        }
        for (int input : input_sizes) {
            ArrayList<SortObject> list = createList(input);
            gnomeSort(list);
            Collections.reverse(list);
            System.out.println("Sort times for "+input+" inputs on worst case:");
            ArrayList<SortObject> temp_list = cloneList(list);
            sortTimer("comb", temp_list);
            temp_list = cloneList(list);
            sortTimer("gnome", temp_list);
            temp_list = cloneList(list);
            sortTimer("shaker", temp_list);
            temp_list = cloneList(list);
            sortTimer("stooge", temp_list);
            temp_list = cloneList(list);
            sortTimer("bitonic", temp_list);
        }
    }

    static String getSecondValue(ArrayList<SortObject> list){
        String second_vals = "";
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).value == 1){                            //for checking the stability, takes the second values of every object with value 1
                second_vals += list.get(i).a2;
            }
        }
        return second_vals;
    }

    static void stabilityTest(){
        ArrayList<SortObject> list = new ArrayList<>();
        list.add(new SortObject(2,"d"));
        list.add(new SortObject(1,"a"));
        list.add(new SortObject(2,"e"));                //creates a custom list and sorts it then checks if the order of the second values of the objects changes
        list.add(new SortObject(1,"b"));
        list.add(new SortObject(3,"f"));
        list.add(new SortObject(1,"c"));
        list.add(new SortObject(4,"h"));
        list.add(new SortObject(3,"g"));
        list.add(new SortObject(5,"j"));
        list.add(new SortObject(3,"k"));
        list.add(new SortObject(5,"l"));
        list.add(new SortObject(1,"m"));
        list.add(new SortObject(3,"x"));
        list.add(new SortObject(6,"q"));
        list.add(new SortObject(7,"t"));
        list.add(new SortObject(1,"u"));
        ArrayList<SortObject> temp_list = cloneList(list);
        System.out.println("Stability Analysis:");
        System.out.println("Comb sort stability analysis: ");
        String before_sorting = getSecondValue(list);
        combSort(temp_list);
        print(list);
        print(temp_list);
        String after_sorting = getSecondValue(temp_list);
        if (before_sorting.equals(after_sorting)){
            System.out.println("Comb sort is stable\n");
        }else {
            System.out.println("Comb sort is unstable\n");
        }
        temp_list = cloneList(list);
        System.out.println("Gnome sort stability analysis: ");
        gnomeSort(temp_list);
        print(list);
        print(temp_list);
        after_sorting = getSecondValue(temp_list);
        if (before_sorting.equals(after_sorting)){
            System.out.println("Gnome sort is stable\n");
        }else {
            System.out.println("Gnome sort is unstable\n");
        }
        temp_list = cloneList(list);
        System.out.println("Shaker sort stability analysis: ");
        shakerSort(temp_list);
        print(list);
        print(temp_list);
        after_sorting = getSecondValue(temp_list);
        if (before_sorting.equals(after_sorting)){
            System.out.println("Shaker sort is stable\n");
        }else {
            System.out.println("Shaker sort is unstable\n");
        }
        temp_list = cloneList(list);
        System.out.println("Stooge sort stability analysis: ");
        stoogeSort(temp_list, 0, temp_list.size()-1);
        print(list);
        print(temp_list);
        after_sorting = getSecondValue(temp_list);
        if (before_sorting.equals(after_sorting)){
            System.out.println("Stooge sort is stable\n");
        }else {
            System.out.println("Stooge sort is unstable\n");
        }
        temp_list = cloneList(list);
        System.out.println("Bitonic sort stability analysis: ");
        bitonicSort(temp_list, 0, temp_list.size(), 1);
        print(list);
        print(temp_list);
        after_sorting = getSecondValue(temp_list);
        if (before_sorting.equals(after_sorting)){
            System.out.println("Bitonic sort is stable\n");
        }else {
            System.out.println("Bitonic sort is unstable\n");
        }
    }

    public static void main(String[] args) {
        int[] input_sizes = {2,4,8,16,32,64,128,256,512,1024,2048,8192};
        stabilityTest();
        tryAllSizes(input_sizes);
    }
}
