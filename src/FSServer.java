import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class FSServer {

    private String dir;
    private ArrayList<FSMonitor> clients; // хранит классы, которые будут реализ. интерф.
    private volatile boolean canWork;

    public FSServer(String dir) {
        this.dir = dir;
        clients = new ArrayList<>();
    }

    public void addClient(FSMonitor client) {
        clients.add(client);
    }

    public void removeClien(FSMonitor client) {
        clients.remove(client);
    }

    public void start() {
        canWork = true;
        run();//!!!
    }

    public void stop() {
        canWork = false;
    }

    private void run() {
        try {
            WatchService wath = FileSystems.getDefault().newWatchService(); //слежение за файловой системой
            Paths.get(dir).register(wath,                   //   позволяет приявзть наблюдателя к событием к указанной директории
                    StandardWatchEventKinds.ENTRY_CREATE, //создание
                    StandardWatchEventKinds.ENTRY_DELETE);

            while (canWork) { //реагировать на событие в файловой системе
                WatchKey key = wath.take(); //дискриптор произошедшего события,take - блокирующий метод(не выйдем из метода пока что-то не произойдет)
                for (WatchEvent event : key.pollEvents()) {
                    String fNamne = event.context().toString();//context дополнительные данные(данные сопровождает данные других данных)
                    int kind = 0;
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) // если файл был создан
                        kind = FSMonitor.CREATE;
                    else kind = FSMonitor.REMOVE;
                    for (FSMonitor client : clients) {
                        client.event(fNamne, kind);
                    }
                }
                key.reset(); //закрывает событие
            }
            wath.close();

        } catch (IOException | InterruptedException ex) {

        }
    }
}