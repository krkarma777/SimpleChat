import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    public static void main(String[] args) throws Exception{

        ServerSocket serverSocket = new ServerSocket(9999);
        // 공유객체에서 쓰레드에 안전한 리스트를 만든다.
        List<PrintWriter> outList = Collections.synchronizedList(new ArrayList<>());

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("접속 : " + socket);

            ChatThread chatThread = new ChatThread(socket, outList);
            chatThread.start();
        }
    }
}

class ChatThread extends Thread {
    private Socket socket;
    private List<PrintWriter> outList;
    private  PrintWriter out;
    private BufferedReader in ;

    public ChatThread(Socket socket, List<PrintWriter> outList) {
        this.socket = socket;
        this.outList = outList;
        // 1. socket으로부터 읽어들일 수 있는 객체를 얻는다.
        // 2. socket에게 쓰기 위한 객체를 얻는다. (현재 연결된 클라이언트에게 쓰는 객체)

        try{
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outList.add(out);
        }catch (Exception ex){
        ex.printStackTrace();
        }
    }

    @Override
    public void run() {

        String line = null;
        try{
            while((line = in.readLine()) != null){
                for(int i = 0; i < outList.size(); i++){ // 접속한 모든 클라이언트에게 메시지를 전송한다.
                    PrintWriter o = outList.get(i);
                    o.println(line);
                    o.flush();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally { // 접속이 끊어질때
            try {
                outList.remove(out);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            for (int i = 0 ; i < outList.size(); i++){
                PrintWriter o = outList.get(i);
                o.println("A client has disconnected.");
                // cmd로 테스트중 한글로 "어떤 클라이언트가 접속이 종료되었습니다."하면 utf-8이 아니라 오류떠서 영어로 변경
                // 영어로 바꾸니 잘 실행됨.
                out.flush();
            }

            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 3. 클라이언트가 보낸 메시지를 읽는다.
        // 4. 접속된 모든 클라이언트에게 메시지를 얻는다. (현재 접속된 모든 클라이언트에게 쓸 수 있는 객체가 필요하다.)
    }
}