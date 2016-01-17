package app;

/**
 * Created by Loso on 2015-11-09.
 */
public class AppConfig {

        // Server user login url
        public static String URL_LOGIN = "http://eatwit.se/android_login_api/login.php";

        // Server user register url
        public static String URL_REGISTER = "http://eatwit.se/android_login_api/register.php";


        // Server to create new recept
        public static final String URL_SKAPA_RECEPT = "http://eatwit.se/android_recept_api/skapa_recept.php";

        // Server getRecept
        public static final String URL_HAMTA_SPECIFIKT_RECEPT = "http://eatwit.se/android_recept_api/hamta_specifikt_recept.php";

        // Server to update recept
        //public static final String URL_UPPDATERA_RECEPT = "http://eatwit.se/android_recept_api/uppdatera_recept.php";

        // Server to delete recept
        //public static final String URL_TABORT_RECEPT = "http://eatwit.se/android_recept_api/tabort_recept.php";

        // Server to get all recept
        public static String URL_HAMTA_ALLA_RECEPT = "http://eatwit.se/android_recept_api/hamta_alla_recept.php";

        // Server to get all recept
        public static String URL_HAMTA_ALLA_MINA_RECEPT = "http://eatwit.se/android_recept_api/hamta_mina_recept.php";

        public static String URL_AUTO_COMP = "http://eatwit.se/auto_ing.php";

        public static String URL_GET_IMG = "http://eatwit.se/android_pictures/getImage.php?id=";

        //Taggar JSON

        public static final String TAG_RECEPT_NAME = "receptNamn";
        public static final String TAG_SUCCESS = "success";
        public static final String TAG_RECEPT = "recept";
        public static final String TAG_RID = "rid";
        public static final String TAG_NAME = "name";
        public static final String TAG_TYP = "typ";
        public static final String TAG_TID = "tid";
        public static final String TAG_BESKRIVNING = "beskrivning";
        public static final String TAG_TILLAGNING = "tillagning";
        public static final String TAG_INGREDIENSER = "ingredienser";
        public static final String TAG_PORTIONER = "portioner";

    }


