package salah.elbadaoui.elbadaouisae302;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

class TCPClient implements Runnable {
    //Déclaration de la variable TAG pour utiliser les constantes de la classe TCPServer
    private String TAG = TCPServer.TAG;
    //Déclaration de la variable PrintWriter pour envoyer des messages au serveur
    private PrintWriter printwriter;
    //Déclaration de la variable InputStream pour lire les données reçues
    private InputStream inputstream;
    //Déclaration de la variable DataInputStream pour stocker les données lues
    private DataInputStream datainputstream;
    //Déclaration des variables pour stocker l'adresse IP et le port du serveur
    private String ipserveur;
    private int portserveur;
    //Variable pour vérifier si le client est en cours d'exécution
    private boolean running = true;
    //Déclaration de la variable Socket pour gérer la connexion au serveur
    private Socket socket;
    //Déclaration de la variable Context pour pouvoir utiliser les fonctionnalités du système d'exploitation
    private Context context;



    //Constructeur qui prend en paramètres l'adresse IP, le port et un objet context
    public TCPClient(String ip , int port,Context context){
        this.ipserveur = ip;
        this.portserveur = port;
        this.context = context;
    }
    //Méthode pour récupérer l'état de connexion
    public boolean getStatus(){
        return running;
    }
    //Méthode pour fermer la connexion avec le serveur
    public void closeClient(){
        running = false;
    }
    //Méthode pour envoyer un message au format byte[] au serveur
    public void send(byte[] msg){
        try {
            //Création d'un objet OutputStream pour envoyer les données
            OutputStream outputStream = socket.getOutputStream();
            //Envoi des données
            outputStream.write(msg);
            //Vider le buffer
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Méthode pour envoyer un message au format String au serveur
    public void send(String msg){
        //Envoi du message
        printwriter.print(msg);
        //Vider le buffer
        printwriter.flush();
    }
    //Redéfinition de la méthode run() pour lancer un thread
    @Override
    public void run() {
        //Déclaration d'un tableau de bytes pour stocker les données reçues
        byte[] buff = new byte[100];
        try {
            //Création d'un nouvel objet Socket qui pointe vers l'adresse IP et le port spécifiés
            socket = new Socket(ipserveur,portserveur);
            //Définir le temps d'attente pour lire les données
            socket.setSoTimeout(5000);
            // Initialisation de la variable printwriter avec un nouvel objet PrintWriter
            printwriter = new PrintWriter(socket.getOutputStream(),true);
            //Initialisation de la variable inputstream avec un nouvel objet InputStream
            inputstream = socket.getInputStream();
            //Initialisation de la variable datainputstream avec un nouvel objet DataInputStream
            datainputstream = new DataInputStream(inputstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Boucle infinie pour recevoir les messages en provenance du serveur
        while (running){
            try {
                //Lecture des données reçues
                int rcvLen = datainputstream.read(buff);
                //Conversion des données en String
                String rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                Log.d(TAG, "Message reçu : "+ rcvMsg);
                //Création d'un objet Intent pour envoyer un message à l'application
                Intent intent =new Intent();
                intent.setAction(TCPServer.RECEIVE_ACTION);
                intent.putExtra(TCPServer.RECEIVE_STRING, rcvMsg);
                context.sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //Fermeture des flux et de la connexion
            printwriter.close();
            inputstream.close();
            datainputstream.close();
            //Fermeture de la socket
            socket.close();
            Log.d(TAG, "Fermer le client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
