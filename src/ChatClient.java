import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class ChatClient {
    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.out.println("사용법 : java ChatClient 닉네임");
            return;
        }
        String name = args[0];
        Socket socket = new Socket("127.0.0.1", 9999);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keybord = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        pw.println(name);
        pw.flush();

        InputThread inputThread = new InputThread(br);
        inputThread.start();

        try{
            String line = null;
            while ((line = br.readLine()) != null){
                if("/quit".equals(line))
                        break;
                    pw.println(line);
                    pw.flush();
                System.out.println(line);
            }
        } catch (Exception ex){
            System.out.println("?");
        }

    }
}

class InputThread extends Thread {
    BufferedReader br;

    public InputThread(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void run() {
        try{
            String line = null;
            while ((line = br.readLine()) != null){
                System.out.println(line);
            }
        } catch (Exception ex){
            System.out.println("?");
        }
    }
}