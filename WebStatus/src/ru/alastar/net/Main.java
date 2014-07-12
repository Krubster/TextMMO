package ru.alastar.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ru.alastar.main.Plugin;

public class Main extends Plugin
{ 
    @Override
    public void OnLoad(){
    ServerSocket ss;
    try
    {
        ss = new ServerSocket(8080);

    while (true) {
        Socket s = ss.accept();
        System.err.println("webClient accepted");
        new Thread(new SocketProcessor(s)).start();
      } 
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
       catch (Throwable e)
      {
        e.printStackTrace();
      }
   } 
  private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            s.getOutputStream();   
        }

        public void run() {
            try {
                String request = readInputHeaders();
                ru.alastar.main.Main.Log("[WEBSERVER]",request);
                writeResponse("");
            } catch (Throwable t) {
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: Server/2009-09-09\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }
        
        private String readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while(true) {
                String s = br.readLine();
                if(s == null || s.trim().length() == 0) {
                    return s;
                }
            }
        }
    }
}
