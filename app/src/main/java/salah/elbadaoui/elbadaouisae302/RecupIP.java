package salah.elbadaoui.elbadaouisae302;

//import de librairies et dépendances nécessaires
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import static android.content.Context.WIFI_SERVICE;

// Définition d'une classe
public class RecupIP {

    //L'annotation @SuppressLint("DefaultLocale") est utilisée pour supprimer l'avertissement Lint
    // généré lorsque la méthode String.format est utilisée avec des lettres minuscules dans la
    // chaîne de format.

    @SuppressLint("DefaultLocale")

    // définition d'une méthode getLocalIP prend un objet de type Context en entrée
    //cette méthode va permettre de récupérer l'IP locale

    public static String getLocalIP(Context context) {
        // utilisation de la classe  Wifi Manager pour  récupérer une instance du service Wifi depuis le système.
        // Ce service permet l'accès aux informations wifi du périphérique afin d'obtenir l'IP locale du mobile par la suite.
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        // utilisation de la classe WifiInfo pour récupérer l'adresse IP de l'appreil, l'adresse IP est un entier
        //mais cette classe le convertit en chaîne de caractères.
        WifiInfo info = wifiManager.getConnectionInfo();
        int ipAddress = info.getIpAddress();

        //cette méthode  retourne l'adresse IP en tant que chaîne de caractères
        return String.format("%d.%d.%d.%d"
                , ipAddress & 0xff
                , ipAddress >> 8 & 0xff
                , ipAddress >> 16 & 0xff
                , ipAddress >> 24 & 0xff);
    }
}
