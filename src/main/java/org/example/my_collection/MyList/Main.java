package org.example.my_collection.MyList;

public class Main {
    public static void main(String[] args) {
        List<String> list = new LinkedList<>();
        list.add("a");
        list.add("b");
        list.remove("a");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
