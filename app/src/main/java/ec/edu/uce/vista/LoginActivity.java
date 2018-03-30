package ec.edu.uce.vista;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.edu.uce.controlador.Metodos;
import ec.edu.uce.movilidaduce1.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final Metodos m = new Metodos();


    private EditText email, password;
    private Button sign_in_register;
    private RequestQueue requestQueue;
    private static final String URL = "https://movilidad.000webhostapp.com/login/login.php";
    private StringRequest request;
    boolean cancel = false;
    View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        sign_in_register = (Button) findViewById(R.id.sign_in_register);

        requestQueue = Volley.newRequestQueue(this);

        //comprobar archivo
        String sFichero = "/data/data/" + getPackageName() + "/shared_prefs/PreferenciasDeUsuario.xml";
        File fichero = new File(sFichero);

        if (fichero.exists()) {
              System.out.println("El fichero " + sFichero + " existe"+getBaseContext());
            //  mEmailView.setText(m.cargarPreferencias(getBaseContext()).toString());
            Toast.makeText(getApplicationContext(), "SUCCESS " + sFichero, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), GPSActivity.class);
            startActivity(intent);
            finish();

        } else {
            sign_in_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final String emailvalido = email.getText().toString();
                    if (emailvalido.contains("@uce.edu.ec")) {
                        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.names().get(0).equals("success")) {
                                        Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                        //m.guardarPreferencias(getBaseContext(), email.getText().toString());
                                        m.guardarPreferencias(getBaseContext(),emailvalido);
                                        startActivity(new Intent(getApplicationContext(), GPSActivity.class));
                                        finish();
                                    } else {
                                        password.setError(getString(R.string.error_invalid_email));
                                        focusView = password;
                                        cancel = true;
                                        Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                                    }
                                    if (cancel) {
                                        // There was an error; don't attempt login and focus the first
                                        // form field with an error.
                                        focusView.requestFocus();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> hashMap = new HashMap<String, String>();
                                hashMap.put("email", email.getText().toString());
                                hashMap.put("password", password.getText().toString());

                                return hashMap;
                            }
                        };

                        requestQueue.add(request);
                    } else {
                        email.setError(getString(R.string.error_invalid_email));
                        focusView = email;
                        cancel = true;
                    }
                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    }
                }

            });


        }
    }
}

