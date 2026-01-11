import java.io.*;
import java.util.*;

public class sweep {
    public static void main(String[] args) throws Exception {
//        for(int i =0 ;i < 1;i++){
//            int size = 10000;
//            for (int j = 0; j < size; j++) {
//                System.out.print((int)(Math.random() * 2));
//            }
//            System.out.println();
//        }
        new sweep().run();
    }

    public void run() throws Exception {
        Scanner f = new Scanner(new File(("sweep.dat")));
//        Scanner f = new Scanner(System.in);
        int times = f.nextInt();
        f.nextLine();
        for (int asdf = 1; asdf <= times; asdf++) {
            String ln = f.next().trim();
            int n = ln.length();
            long[] count = new long[n];
            // the number of stops for each 0 is the number of 1s after it
            for(int i = n-1;i>=0;i--){
                if(ln.charAt(i) == '1') count[i]++;
                if(i + 1 < n) count[i] += count[i+1];
            }

            // System.out.println(Arrays.toString(count));
            
            // we can just check the first and last zero or peace period because of how we build the count array
            int first = ln.indexOf("0");
            int last = ln.lastIndexOf("0");
            if(first != -1 && last != -1){
                System.out.println(Math.abs(count[first] - count[last]));
            }
            else System.out.println(0);
        }
        f.close();
    }
}
