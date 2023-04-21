package salah.elbadaoui.elbadaouisae302;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ICMPActivity extends AppCompatActivity {

    private TextView ipadd; // TextView pour afficher l'adresse IP
    private Button ping; // Bouton pour lancer le ping
    private ListView listePing; // ListView pour afficher les réponses de ping
    private EditText edtIP; // EditText pour saisir l'adresse IP à pinger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icmp);
        ipadd = (TextView)findViewById(R.id.iplocale); // Récupère l'id du TextView pour l'affichage de l'adresse IP
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE); // Instance de WifiManager pour récupérer l'adresse IP
        wifi.setWifiEnabled(false); // Désactive le wifi
        ipadd.setText(Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress())); // Affiche l'adresse IP dans le TextView
        ping = (Button)findViewById(R.id.ping); // Récupère l'id du bouton pour le ping
        listePing = (ListView)findViewById(R.id.listView_ping) ; // Récupère l'id de la ListView pour les réponses de ping
        edtIP = (EditText)findViewById(R.id.edit_ip) ; // Récupère l'id de l'EditText pour saisir l'adresse IP
    }

    public void PING(View view){

        Editable host = edtIP.getText(); // Récupère le texte saisi dans l'EditText
        List<String> listeReponsePing = new ArrayList<String>(); // Liste pour stocker les réponses de ping
        ArrayAdapter<String> adapterListe = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listeReponsePing); // Adapter pour afficher les réponses de ping dans la ListView

        try {

            // Commande pour lancer le ping avec 4 paquets
            String  cmdPing = "ping -c 4 "+host;
            Runtime r = Runtime.getRuntime(); // Obtient l'instance de Runtime pour exécuter la commande
            Process p = r.exec(cmdPing); // Exécute la commande
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream())); // Lit les réponses de la commande
            String inputLinhe;
            Toast.makeText(this,"Commande ping execute",Toast.LENGTH_SHORT).show(); // Affiche un message pour indiquer que la commande a été exécutée
            while((inputLinhe = in.readLine())!= null){ // Boucle pour lire les réponses de la commande
                listeReponsePing.add(inputLinhe); // Ajoute les réponses à la liste
                listePing.setAdapter(adapterListe); // Affiche les réponses dans la ListView
            }

        } catch (Exception e) {
            Toast.makeText(this , "Erreur : "+ e.getMessage().toString(),Toast.LENGTH_SHORT).show(); // Affiche un message d'erreur en cas d'exception
        }
    }
}