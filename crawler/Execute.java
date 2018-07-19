package crawler;

public class Execute {
    public static void main(String[] args) {

        try {
            for (int i = 0; i <= Integer.parseInt(args[0]) - 1; i++)
                new MyThread(Functions::indexing, "indexed" + Integer.toString(i) + ".dat");
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Needs an argument");
        }

    }
}
