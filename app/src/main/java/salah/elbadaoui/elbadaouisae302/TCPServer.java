package salah.elbadaoui.elbadaouisae302;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class TCPServer implements Runnable {
    public static final String TAG = "MyTCP";
    public static final String RECEIVE_ACTION = "GetTCPReceive";
    public static final String RECEIVE_STRING = "ReceiveString";
    public static final String RECEIVE_BYTES = "ReceiveBytes";
    private int port;
    private boolean ouvert;
    private Context context;
    public ArrayList<ServerSocketThread> socketthread = new ArrayList<>();

    // Constructeur de la classe serveur
    public TCPServer(int port,Context context){
        this.port = port;
        ouvert = true;
        this.context = context;
    }
    // Récupération du premeir état (serveur ouvert)
    public boolean getStatus(){
        return ouvert;
    }
    // Récupération du deuxième état (serveur fermé)
    public void closeServer(){
        ouvert = false;
        //Supression des threads un par un
        for (ServerSocketThread s : socketthread){
            s.isRun = false;
        }
        socketthread.clear();
    }
    // Attendre et Avoir l'autorisation du socket (accept())
    private Socket getSocket(ServerSocket serverSocket){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Mettre à jour l'état du serveur");
            return null;
        }
    }

    @Override
    public void run() {
        try {
            //Démarre le serveur en écoute sur le port
            ServerSocket serverSocket = new ServerSocket(port);
            // Définition du déali  d'attente pour mettre à jour l'état du périphérique connecté
            serverSocket.setSoTimeout(2000);
            while (ouvert){
                Log.e(TAG, "Surveillance de l'entrée de l'appareil...");
                if (!ouvert) break;
                Socket socket = getSocket(serverSocket);
                if (socket != null){
                    // Si le Socket n'est pas nul (0), cela signifie qu'il y a un périphérique connecté
                    new ServerSocketThread(socket,context);
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Surveille le thread d'exécution de la connexion et l'état d'envoi et de réception du périphérique
    public class ServerSocketThread extends Thread{
        private Socket socket;
        private PrintWriter pw;
        private InputStream is;
        private boolean isRun = true;
        private Context context;

        ServerSocketThread(Socket socket, Context context){
            this.socket = socket;
            this.context = context;
            String ip = socket.getInetAddress().toString();
            Log.d(TAG, "Nouveau périphérique détecté, IP: " + ip);

            try {
                socket.setSoTimeout(2000);
                OutputStream os = socket.getOutputStream();
                is = socket.getInputStream();
                pw = new PrintWriter(os,true);
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendData(String msg){
            pw.print(msg);
            pw.flush();
        }

        @Override
        public void run() {
            byte[] buff = new byte[100];
            socketthread.add(this);
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()){
                try {
                    //Vérification si le message a bien été envoyé
                    int rcvLen;
                    if ((rcvLen = is.read(buff)) != -1 ){
                        String string = new String(buff, 0, rcvLen);
                        Log.d(TAG, "Message reçu : " + string);
                        //Réception du message et sa redirection vers l'activité
                        Intent intent = new Intent();
                        intent.setAction(RECEIVE_ACTION);
                        intent.putExtra(RECEIVE_STRING,string);
                        intent.putExtra(RECEIVE_BYTES, buff);
                        context.sendBroadcast(intent);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Sortie de la boucle while ci-dessus = déconnexion
            try {
                socket.close();
                socketthread.clear();
                Log.e(TAG, "Éteindre le serveur");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}