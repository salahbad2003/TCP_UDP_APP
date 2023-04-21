package salah.elbadaoui.elbadaouisae302;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static salah.elbadaoui.elbadaouisae302.RecupIP.getLocalIP;


public class TCPActivity extends AppCompatActivity {

    EditText ipdistante, portlocal, msgrecu, edInputBox, portdistant;
    ToggleButton coclient, startsrv, srv;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switching;
    ExecutorService executor = Executors.newCachedThreadPool();
    MyBroadcast myBroadcast = new MyBroadcast();
    StringBuffer stringBuffer = new StringBuffer();
    TCPServer serveurTCP;
    TCPClient clientTCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);
        // Définir l'interface utilisateur de base
        setBaseUI();
        //Définir la fonction du serveur TCP
        setServerSwitch();
        //Définir la fonction client TCP
        setClientSwitch();
        //Définir la fonction d'envoi de données (serveur et client)
        setSendFunction();
        // Enregistre la diffusion, recoit les données renvoyées par l'appareil de l'autre partie
        IntentFilter intentFilter = new IntentFilter(TCPServer.RECEIVE_ACTION);
        registerReceiver(myBroadcast, intentFilter);
    }

    private void setBaseUI() {
        TextView tvLocalIp = findViewById(R.id.iplocale);
        tvLocalIp.setText("IP du mobile : " + getLocalIP(this));
        Button btClear = findViewById(R.id.button_clear);
        btClear.setOnClickListener(v -> {
            stringBuffer.delete(0, stringBuffer.length());
            msgrecu.setText(stringBuffer);
        });
        startsrv = findViewById(R.id.server);
        coclient = findViewById(R.id.client);
        ipdistante = findViewById(R.id.ipdistante);
        portdistant = findViewById(R.id.portdistant);
        portlocal = findViewById(R.id.port);
        msgrecu = findViewById(R.id.messagerecu);
        edInputBox = findViewById(R.id.entreemessage);
        switching = findViewById(R.id.switchmode);
        switching.setChecked(false);

        //Définition de la fonction pour le changement de mode du switchbutton
        switching.setOnCheckedChangeListener((buttonView, isChecked) -> {
            startsrv.setEnabled(!isChecked);
            coclient.setEnabled(isChecked);
            if (!isChecked) {
                if (clientTCP != null && clientTCP.getStatus()) {
                    clientTCP.closeClient();
                    coclient.setChecked(false);
                }
                switching.setText("Serveur");
            } else {
                if (serveurTCP != null && serveurTCP.getStatus()) {
                    serveurTCP.closeServer();
                    srv.setChecked(false);
                }
                switching.setText("Client");
            }
        });
    }

    //Définition de la fonction serveur TCP
    private void setServerSwitch() {
        srv = findViewById(R.id.server);
        ExecutorService exec = Executors.newCachedThreadPool();
        srv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int port = Integer.parseInt(portlocal.getText().toString());
                serveurTCP = new TCPServer(port, this);
                exec.execute(serveurTCP);
                portlocal.setEnabled(false);
            } else {
                serveurTCP.closeServer();
                portlocal.setEnabled(true);
            }
        });

    }
    //Définition de  la fonction client TCP
    private void setClientSwitch() {
        String remoteIp = ipdistante.getText().toString();
        int remotePort = Integer.parseInt(portdistant.getText().toString());
        coclient.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clientTCP = new TCPClient(remoteIp, remotePort, this);
                executor.execute(clientTCP);
                ipdistante.setEnabled(false);
                portdistant.setEnabled(false);

            } else {
                clientTCP.closeClient();
                ipdistante.setEnabled(true);
                portdistant.setEnabled(true);
            }
        });


    }
    //Définition de la fonction pour l'envoi des messages
    private void setSendFunction() {
        Button envoi = findViewById(R.id.envoi);
        envoi.setOnClickListener(v -> {
            String text = edInputBox.getText().toString();
            if (switching.isChecked()) {
                //Lorsque le switch est en mode Client
                if (clientTCP == null)return;
                if (text.length() == 0 || !clientTCP.getStatus()) return;
                executor.execute(() -> clientTCP.send(text));
            } else {
                //Lorsque le switch est en mode serveur
                if (serveurTCP == null)return;
                if (text.length() == 0 || !serveurTCP.getStatus()) return;
                // L'expression Lambda ici est équivalente à la section de commentaire ci-dessous
                if (serveurTCP.socketthread.size() == 0) return;
                executor.execute(() -> serveurTCP.socketthread.get(0).sendData(text));
            }
        });
    }
    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            //Reception des messages de retour
            switch (action) {
                case TCPServer.RECEIVE_ACTION:
                    String msg = intent.getStringExtra(TCPServer.RECEIVE_STRING);
                    byte[] bytes = intent.getByteArrayExtra(TCPServer.RECEIVE_BYTES);
                    stringBuffer.append("Réception du message： ").append(msg).append("\n");
                    msgrecu.setText(stringBuffer);
                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Annule l'écoute de la diffusion
        unregisterReceiver(myBroadcast);
    }
}