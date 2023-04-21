package salah.elbadaoui.elbadaouisae302;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;


/*Cette classe définit une méthode pour envoyer des paquets de données à en utilisant une socket,
 ainsi qu'une implémentation de l'interface "Runnable" pour démarrer un serveur UDP qui écoute les
 paquets de données entrant et les diffuse

 */
class UDP implements Runnable {
    public static final String TAG = "UDP";
    public static final String RECEIVE_ACTION = "GetUDPReceive";
    public static final String RECEIVE_STRING = "ReceiveString";
    public static final String RECEIVE_BYTES = "ReceiveBytes";

    //port par défaut utilisé pour la communication UDP
    private int port = 8888;
    //Adresse IP du serveur
    private String ipserveur;
    //Etat du serveur (ouvert ou fermé)
    private boolean ouvert;
    // Socket utilisé pour la communication UDP
    private static DatagramSocket datagramsocket = null;
    //Contexte de l'application
    private Context context;

    //Fonction pour changer l'état du serveur (ouvert ou fermé)
    public void changeServerStatus(boolean ouvert) {
        this.ouvert = ouvert;
        if (!ouvert) {
            datagramsocket.close();
            Log.e(TAG, "Serveur actuellement fermé  ");
        }
    }

    //Fonction pour changer le port utilisé pour la communication UDP
    public void setPort(int port){
        this.port = port;
    }

    //Constructeur pour la classe UDP
    public UDP(String ServerIp,Context context) {
        this.context = context;
        this.ipserveur = ServerIp;
        this.ouvert = true;

    }

    //Fonction pour envoyer le message

    public void send(String string, String ipdistante, int portdistant) throws IOException {
        Log.d(TAG, "IP du client：" + ipdistante + ":" + portdistant);
        //récupérer l'adresse IP distanet
        InetAddress inetAddress = InetAddress.getByName(ipdistante);
        //création du socket
        DatagramSocket datagramSocket = new DatagramSocket();
        //envoi du paquet de données
        DatagramPacket dpSend = new DatagramPacket(string.getBytes(), string.getBytes().length, inetAddress, portdistant);
        datagramSocket.send(dpSend);

    }

    //méthode pour démarrer le serveur UDP
    @Override
    public void run() {

        //Définition de l'adresse IP et du port du serveur
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ipserveur, port);
        try {
            //création du socket pour l'écoute des messages entrants
            datagramsocket = new DatagramSocket(inetSocketAddress);
            Log.e(TAG, "Serveur UDP démarré");
        } catch (SocketException e) {
            Log.e(TAG, "Échec lors du démarrage, pour la raison suivante : " + e.getMessage());
            e.printStackTrace();
        }

        byte[] msgRcv = new byte[1024];
        DatagramPacket dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
        // boucle while vérifiant si le serveur est bien ouvert
        while (ouvert) {
            Log.e(TAG, "Serveur UDP :  informations de surveillance..");
            try {
                //pour la réception des paquets
                datagramsocket.receive(dpRcv);
                //conversion en chaîne de caractères
                String string = new String(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());
                Log.d(TAG, "Serveur UDP - informations reçues .. ： " + string);

                // création d'un intent pour l'envoi des données reçues
                Intent intent = new Intent();
                intent.setAction(RECEIVE_ACTION);
                intent.putExtra(RECEIVE_STRING,string);
                intent.putExtra(RECEIVE_BYTES, dpRcv.getData());
                context.sendBroadcast(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
