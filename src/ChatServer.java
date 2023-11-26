import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class ChatServer {
    public static void main(String[] args) throws Exception{

        ServerSocket serverSocket = new ServerSocket(9999);
        // 서버 소켓 생성 및 쓰레드에 안전한 리스트 초기화
        List<PrintWriter> outList = Collections.synchronizedList(new ArrayList<>());

        while (true) {
            // 클라이언트의 연결을 기다림
            Socket socket = serverSocket.accept();
            System.out.println("접속 : " + socket);

            // 클라이언트와 통신할 스레드 생성 및 시작
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

        try{
            // 클라이언트와 입출력을 위한 스트림 생성 및 리스트에 추가
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
            // 클라이언트로부터 메시지 수신 및 모든 클라이언트에게 브로드캐스트
            while((line = in.readLine()) != null){
                for(int i = 0; i < outList.size(); i++){
                    PrintWriter o = outList.get(i);
                    o.println(line);
                    o.flush();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try {
                // 연결이 종료된 클라이언트의 출력 스트림 제거
                outList.remove(out);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            // 연결이 종료된 클라이언트를 제외한 모든 클라이언트에게 알림
            for (int i = 0 ; i < outList.size(); i++){
                PrintWriter o = outList.get(i);
                o.println("A client has disconnected.");
                // cmd로 테스트중 한글로 "어떤 클라이언트가 접속이 종료되었습니다."하면 utf-8이 아니라 오류떠서 영어로 변경
                // 영어로 바꾸니 잘 실행됨.
                out.flush();
            }

            try {
                // 소켓 닫기
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}