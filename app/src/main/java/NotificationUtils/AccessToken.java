package NotificationUtils;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


/**
 * Class that connects app
 * with Firebase Messaging Service
 */
public class AccessToken {

    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken(){
        try {
            //that should be private data
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"finance-manager-sanzhar\",\n" +
                    "  \"private_key_id\": \"5c275fa8b0c7bce61b73bbffca20f9c76bf9a406\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDxFcePs+MIl5EU\\nJlZU23t0k5OeFKG465B/5EwirjX1dI1RMIjNyJBUyKixHfK5TXBFcUxPGwRojNsE\\nH9fjum+3kdjIbYMQjJ2CqZN63DKsoIn78Sf3c+uGA/IeLPaL5AIOyiu+lMUERtxC\\n4T8xVpUPsDJAMIxSArbPk0n0kN1/wlXZStma+JlFuWLx4Ha0a68lpdiQ1/li3c39\\n5WZAQB1ovPOYpU/c253/gLLE5rYlfB/IftPbf4j1T0kUmXPTjL9x0/nTqucou33V\\nr/3jSlDF4HUnDtoIS4uCNtce+SdMpg/k5TSSmFUFXOtzRX8tSGSJjWhZAawO9hM5\\n5ERYh3ffAgMBAAECggEAJBD6YjDVeyM+7KvGPeWj33nLG1rCIumixsSURe/tWe4j\\nd2JHumQR1jflUgdFQ5wmeqJgzx4fQmDSzG19x6920m3lkikdSBbb4tXpnqIIjAjB\\npnOaY2Dv0C8Q7ptoEDTTwKYAIKpy8EPa5Gp8IK5vmFbnUvEFslMo9Cl4Zo0G21DL\\nXpsSdNL5ev3ttU1Bz40LyLILPWdcvLSr+fLyapMO0saDHMJvrEWXRwYr9TgcAzZc\\nkeVoIn3VW6jh+ciEYFSc61MHNA36EzXNn/QGuGAOwyUIwOLr0z6iU7I0+1sf4iJN\\nL1AyLxVzPpALy0VqGXEri+sQahg0zlumZpA2teR62QKBgQD7pOETupCjdBlgsPm1\\nb/KVdZYkXqg3URhecPJIxXGRN1b8mifkrexXDf8Z6GTx6Z466XI7FWX6+ZGu8pYx\\nARLpt4iZCa10+h/L7M7Tn+c6sWcwqWn6s8vZBAKQB5izbiGlCMX1mFwuh4AEXgY8\\n7fSlrloAID8KxB8bjYEkb5eM9QKBgQD1QhwgT1/4lmUTW928LBJhSClu+1RIl0zo\\nTBwOZDMmuL1g26cAhyXX4K3Yy/feaikMATqe69bw4Osfdr9F1DMTlglWqh8OWu4m\\nF5elM0YSzVgeORXV7zzJQwOmc3eW3lRkIh/IBrv2nw9HWHosNqj0VD5kIHp0EiiP\\nx/8aofrtAwKBgCqGY27VEUo9/WfkoF4z69esBlJhGKY4cxjKl3cKvrVel7maR3GS\\nnqTbfaegKSbkZtPnzWEErbYq5J6e6hif6NVMKa5K/2AMQMFSZGVI2WeGwTxs9Lbk\\nGWe0EMbMMdbonpDoGaP2OmW2ikCyrvUu9S/mddkdmem02NjsIc3Jed/dAoGAUwiq\\n1RWKfHMOjVtQ2uBg6KaxwG0+v8TZuNvrs8Ogvb9V/nDfPYKEAW6D7R5BMP/oHbRc\\n0GkwQCUl9WUSpOWO1Va6mqjGEuoeLq9WHAwb8UmsR680AVYZ0lnp5nS5TE7Ba5VS\\nBTqicICxf4oo8scetHnBFc0ZKCrtqKAhcjbc6VkCgYEA5B4Ly7B+jKlnsAwKMa0c\\nEcvHT50P59t8xkb9I3hR67p3qL3EHo4BEBpTsMJXqvE8vybYbIVa6dVQn776J31u\\nBaQOgN8/Gy52djsRr5GKJrQiodpxONGUrhR2Q0J4fFC7ltgeqnb/JzjifrDqecks\\nnlwSdFnTvvH728EMxkFN0+g=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-l5j5o@finance-manager-sanzhar.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"111019062829289616943\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-l5j5o%40finance-manager-sanzhar.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Lists.newArrayList(firebaseMessagingScope));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e){
            Log.e("error", e.getMessage());
            return null;
        }
    }
}
