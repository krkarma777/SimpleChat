import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatThread extends Thread {

    private String name;
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
    List<ChatThread> list;
    public ChatThread(Socket socket, List<ChatThread> list) throws Exception{
        this.socket = socket;
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.br = br;
        this.pw = pw;
        this.name = br.readLine();
        this.list = list;
        this.list.add(this);

    }

    public void sendMessage(String msg){
        pw.println(msg);
        pw.flush();
    }


    @Override
    public void run() {
        try {
            String line = null;
            broadcast(name + "님이 연결되었습니다.",false);

            while ((line = br.readLine()) !=null){
                broadcast(name + " : " + line, true);
            }

        } catch (Exception ex){
            broadcast(name + "님의 연결이 종료되었습니다.",false);
            this.list.remove(this);
        }
    }

    private void broadcast(String msg, boolean includeMe){
        List<ChatThread> chatThreads = new ArrayList<>(this.list);

        try {
            for(int i = 0 ; i < chatThreads.size(); i++){
                ChatThread ct = chatThreads.get(i);
                if(!includeMe){
                    if (ct == this){
                        continue;
                    }
                }
                ct.sendMessage(msg);
            }
        } catch (Exception ex){
            System.out.println("??");
        }

    }
}