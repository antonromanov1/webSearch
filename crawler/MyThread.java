package crawler;

interface IMyFunc {
    void func(String s) throws Exception;
}

public class MyThread implements Runnable {
    Thread t;
    IMyFunc a;
    String s;

    MyThread(IMyFunc funcRef, String s) {
        t = new Thread(this, "");
        this.a = funcRef;
        this.s = s;
        t.start();
    }

    public void run() {
        try {
            this.a.func(this.s);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
