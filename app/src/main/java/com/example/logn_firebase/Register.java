package com.example.logn_firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    Button btn_register;
    EditText name, email, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageButton boton = findViewById(R.id.imageButton);
        TextView iniciarSesion = findViewById(R.id.iniciarsesion);
        boton.setOnClickListener(l->abrirCamara());
        iniciarSesion.setOnClickListener(l->LoginNow());
    }

    public void LoginNow() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private static final int REQUEST_IMAGE_CAPTURE=2;
    void abrirCamara(){
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle info = data.getExtras();
            Bitmap imagen = (Bitmap) info.get("data");

            ImageView imageView= findViewById(R.id.imageView2);
            imageView.setImageBitmap(imagen);
        }
    }

    public void RegisterCall(View view) {
        name = findViewById(R.id.nombre);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.registrar);
        mAuth = FirebaseAuth.getInstance();

        String nameUser = name.getText().toString().trim();
        String emailUser = email.getText().toString().trim();
        String passUser = password.getText().toString().trim();

        if (nameUser.isEmpty() && emailUser.isEmpty() && passUser.isEmpty()) {
            Toast.makeText(Register.this, "Complete los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(emailUser) || TextUtils.isEmpty(passUser)) {
            Toast.makeText(Register.this, "Ingrese " + (TextUtils.isEmpty(emailUser) ? "un email" : "una contraseña de al menos 6 dígitos"), Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameUser)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            Toast.makeText(Register.this, "Cuenta creada.", Toast.LENGTH_SHORT).show();
                                            // Registro exitoso, redirige al usuario a la siguiente actividad
                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                            startActivity(intent);
                                            finish(); // Opcionalmente, finaliza la actividad actual para evitar que el usuario vuelva atrás
                                        } else {
                                            Toast.makeText(Register.this, "Error al actualizar el perfil.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Si falla el inicio de sesión, muestra un mensaje al usuario
                        Toast.makeText(Register.this, "Falló la autenticación.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}