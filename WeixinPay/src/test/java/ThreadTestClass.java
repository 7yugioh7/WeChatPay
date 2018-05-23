public class ThreadTestClass {

    public static void main(String[] args) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("关闭时执行");
            System.out.println("关闭时执行");
            System.out.println("关闭时执行");
            System.out.println("关闭时执行");
        }));
        logWrite();
    }


    private static void logWrite() throws InterruptedException {
        int count = 0;
        while(true) {
            System.out.println(123123);
            count ++;
            if (count > 10) break;
             Thread.sleep(1000);
        }
    }
}
