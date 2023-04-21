package salah.elbadaoui.elbadaouisae302;

//import de librairies nécessaires

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import salah.elbadaoui.elbadaouisae302.R;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static salah.elbadaoui.elbadaouisae302.RecupIP.getLocalIP;

public class UDPActivity extends AppCompatActivity {

    EditText ipdistante, portlocal, messagerecu, entreemessage, portdistant;
    MyBroadcast myBroadcast = new MyBroadcast();
    StringBuffer stringBuffer = new StringBuffer();
    ExecutorService exec = Executors.newCachedThreadPool();
    UDP serveurudp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp);
        // Définition de l'interface d'utilisateur pour l'application
        setBaseUI();
        //Définition de la fonction moniteur UDP
        setReceiveSwitch();
        //Définition de la fonction d'envoi de data
        setSendFunction();
        //Enregistrement d'un receiver' pour assurer le renvoi des messages reçus vers cette activité
        IntentFilter intentFilter = new IntentFilter(UDP.RECEIVE_ACTION);
        registerReceiver(myBroadcast, intentFilter);
    }

    private void setBaseUI() {
        TextView iplocale = findViewById(R.id.iplocale);
        //Récupération de l'adresse IP locale du périphérique par la classe "RecupIP"
        iplocale.setText("IP Locale: " + getLocalIP(this));
        //Vider les informations (messages reçus)
        Button btClear = findViewById(R.id.vider);
        btClear.setOnClickListener(v -> {
            stringBuffer.delete(0,stringBuffer.length());
            messagerecu.setText(stringBuffer);
        });
        ipdistante = findViewById(R.id.ipdistante);
        portdistant = findViewById(R.id.portdistant);
        portlocal = findViewById(R.id.port);
        messagerecu = findViewById(R.id.messagerecu);
        entreemessage = findViewById(R.id.entreemessage);
    }

    private void setReceiveSwitch() {
        ToggleButton switching = findViewById(R.id.switchserver);
        //instanciation du serveur UDP
        //Récupération de l'adresse IP locale du périphérique par la classe "RecupIP"
        //création d'un objet UDP
        serveurudp = new UDP(getLocalIP(this),this);
        //définition d'un bouton pour activer et désactiver la réception des messages UDP
        switching.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //serveur UDP lancé via un thread séparé
                int port = Integer.parseInt(portlocal.getText().toString());
                serveurudp.setPort(port);
                serveurudp.changeServerStatus(true);
                exec.execute(serveurudp);
                portlocal.setEnabled(false);
            } else {
                //boutton désactivé = serveur étéint = réception de messages arrêtée
                serveurudp.changeServerStatus(false);
                portlocal.setEnabled(true);
            }
        });
    }

    private void setSendFunction() {
        //boutton pour envoyer les messages
        Button envoi = findViewById(R.id.button_Send);
        //bouton cliqué => envoi du message rentré par l'utilisateur en utilisant l'objet UDPcréé dasn
        //la méthode setReceiveSwitch()
        envoi.setOnClickListener(v -> {
            String msg = entreemessage.getText().toString();
            String remoteIp = ipdistante.getText().toString();
            int port = Integer.parseInt(portdistant.getText().toString());
            if (msg.length() == 0) return;
            stringBuffer.append("Envoi de ： ").append(msg).append("\n");
            //affichage des mesages envoyés
            messagerecu.setText(stringBuffer);
            exec.execute(()->{
                try {
                serveurudp.send(msg, remoteIp, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                //Reception des messages en retour UDP
                case UDP.RECEIVE_ACTION:
                    String msg = intent.getStringExtra(UDP.RECEIVE_STRING);
                    byte[] bytes = intent.getByteArrayExtra(UDP.RECEIVE_BYTES);
                    stringBuffer.append("Réception de :  ").append(msg).append("\n");
                    messagerecu.setText(stringBuffer);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Fermeture l'écoute de la diffusion générale
        unregisterReceiver(myBroadcast);
    }

}
