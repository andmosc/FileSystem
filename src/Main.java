public class Main {
    public static void main(String[] args) {
        FSServer server = new FSServer(".");
        server.addClient(new FSMonitor() {
            @Override
            public void event(String fName, int kind) {
                switch (kind) {
                    case FSMonitor.CREATE:
                        System.out.println("File created: " + fName);
                        break;
                    case FSMonitor.REMOVE:
                        System.out.println("File removed: " + fName);
                        break;
                    default:
                        System.out.println("Unknow event");
                }
            }
        });
        server.start();
    }
}