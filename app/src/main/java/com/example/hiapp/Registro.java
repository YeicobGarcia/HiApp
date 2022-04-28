package com.example.hiapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Registro extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    ImageButton imgFoto;
    String fotoCargar;

    Spinner listaGenero;

    EditText txtUserR;
    EditText txtPassR;
    EditText txtName;
    EditText txtApellido;
    EditText txtDPI;
    EditText txtCorreo;

    CheckBox TyCondiciones;
    CheckBox ChekBot;
    ImageButton ImgRecap;
    GoogleApiClient googleApiClient;
    Button btnEnviar;
    ProgressBar progressBar;
    TextView YaMiembro;

    String SiteKey = "6LdMJ30fAAAAAAxx66Jc260Oak6P68wgfozDpRMO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        this.listaGenero = findViewById(R.id.genero);
        this.imgFoto = findViewById(R.id.imgFoto);

        this.txtUserR = findViewById(R.id.username);
        this.txtPassR = findViewById(R.id.password);
        this.txtName = findViewById(R.id.name);
        this.txtApellido = findViewById(R.id.surname);
        this.txtDPI = findViewById(R.id.dpi);
        this.txtCorreo = findViewById(R.id.email);

        this.TyCondiciones = findViewById(R.id.TyCondiciones);
        this.ChekBot = findViewById(R.id.ChekCaptcha);
        this.ImgRecap = findViewById(R.id.ImgRecap);
        this.btnEnviar = findViewById(R.id.btnSignUp);
        this.progressBar = findViewById(R.id.progress);
        this.YaMiembro = findViewById(R.id.YaMiembro);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(Registro.this)
                .build();

        googleApiClient.connect();

        TyCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChekBot.setVisibility(View.VISIBLE);
                ImgRecap.setVisibility(View.VISIBLE);
            }
        });

        ChekBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ChekBot.isChecked()){
                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient,SiteKey)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {
                                    Status status = recaptchaTokenResult.getStatus();
                                    if ((status != null) && status.isSuccess()){
                                        Toast.makeText(getApplicationContext()
                                        ,"Successfully Varified..."
                                        ,Toast.LENGTH_SHORT).show();
                                        ChekBot.setTextColor(Color.GREEN);
                                    }
                                }
                            });
                }
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name, surname, username, password, email, dpi, genero;

                username = String.valueOf(txtUserR.getText());
                password = String.valueOf(txtPassR.getText());
                name = String.valueOf(txtName.getText());
                surname = String.valueOf(txtApellido.getText());
                email = String.valueOf(txtCorreo.getText());
                dpi = String.valueOf(txtDPI.getText());
                genero = String.valueOf(listaGenero.getSelectedItem());

                if(!username.equals("") && !password.equals("") && !name.equals("") && !surname.equals("") && !email.equals("") && !dpi.equals("") && !genero.equals("") && TyCondiciones.isChecked() == true && ChekBot.isChecked() == true) {

                    //Start ProgressBar first (Set visibility VISIBLE)
                    progressBar.setVisibility(View.VISIBLE);
                    handleSSLHandshake();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[7];
                            field[0] = "name";
                            field[1] = "surname";
                            field[2] = "username";
                            field[3] = "password";
                            field[4] = "email";
                            field[5] = "dpi";
                            field[6] = "genero";
                            //Creating array for data
                            String[] data = new String[7];
                            data[0] = name;
                            data[1] = surname;
                            data[2] = username;
                            data[3] = password;
                            data[4] = email;
                            data[5] = dpi;
                            data[6] = genero;

                            PutData putData = new PutData("https://192.168.1.10/LoginRegister/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    //End ProgressBar (Set visibility to GONE)
                                    if(result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Registro.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        YaMiembro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        String generos [] = {"","Masculino" , "Femenino"};

        ArrayAdapter<String> lista = new ArrayAdapter(this,R.layout.genero, generos);
        listaGenero.setAdapter(lista);

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    cargarFoto();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });



    }

    private void cargarFoto(){
        try{
            Intent intento = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intento.setType("image/");
            startActivityForResult(intento.createChooser(intento, "Seleccione una aplicación"), 1);
        } catch(Exception ex){ex.printStackTrace();}}




    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == RESULT_OK){
        try{
                Uri pathFoto = data.getData();
                imgFoto.setImageURI(pathFoto);
                fotoCargar = getRealPathFromURI(pathFoto);
            } catch(Exception ex){
                ex.printStackTrace();
        }
    }
}

    public String getRealPathFromURI(Uri uri) {
        try{
            String[] projection = { MediaStore.Images.Media.DATA };
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }}

    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCertsConfiar en todos los certificados
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}