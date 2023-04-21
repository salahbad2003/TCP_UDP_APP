package salah.elbadaoui.elbadaouisae302;


//import des librairies necessaires
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;


// Cette classe représente l'activité principale de l'application
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Appeler la méthode de la classe parente
        super.onCreate(savedInstanceState);
        // Définir le layout pour l'activité
        setContentView(R.layout.activity_main);

        //trouver la vue ou le widget aynt l'id TCP et écouter sur son événement
        findViewById(R.id.TCP).setOnClickListener(v->{
            // Lorsque le bouton est cliqué, l'activité TCPActivity démarre
            startActivity(new Intent(this, TCPActivity.class));
        });
        //trouver la vue ou le widget aynt l'id UDP et écouter sur son événement
        findViewById(R.id.UDP).setOnClickListener(v -> {
            // Lorsque le bouton est cliqué, l'activité UDPActivity démarre
            startActivity(new Intent(this, UDPActivity.class));
        });
        //trouver la vue ou le widget ayant l'id icmp et écouter sur son événement
        findViewById(R.id.icmp).setOnClickListener(v -> {
            // Lorsque le bouton est cliqué, l'activité ICMPActivity démarre
            startActivity(new Intent(this, ICMPActivity.class));
        });
    }
}