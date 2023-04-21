package salah.elbadaoui.elbadaouisae302;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class ClientHandler implements Runnable {
    //Déclaration de la variable Socket pour gérer la connexion du client
    private Socket socket;
    //Déclaration de la variable PrintWriter pour envoyer des messages au client
    private PrintWriter writer;
    //Constructeur qui prend en paramètre un objet socket
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    //Redéfinition de la méthode run() pour lancer un thread
    @Override
    public void run() {
        try {
            //Création d'un objet InputStreamReader pour lire les données reçues
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            //Création d'un objet BufferedReader pour stocker les données lues
            BufferedReader reader = new BufferedReader(streamReader);
            //Initialisation de la variable writer avec un nouvel objet PrintWriter
            writer = new PrintWriter(socket.getOutputStream(), true);
            String message = null;
            //Boucle infinie pour recevoir les messages en provenance du client
            while (true) {
                message = reader.readLine();
                if (message != null) {
                    //Affichage du message reçu dans la console
                    System.out.println("Received message: " + message);
                    //Envoi d'un message au client pour lui indiquer que le message a bien été reçu
                    writer.println("Server Received: " + message);
                    message = null;
                }
            }
        } catch (IOException e) {
            //Affichage d'un message d'erreur en cas de problème
            System.out.println("error:" + e.getMessage());
        } finally {
            try {
                //Fermeture de la connexion avec le client
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

