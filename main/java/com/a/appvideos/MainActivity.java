package com.a.appvideos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private StorageReference mStorageRef;
    private FirebaseAuth auth;
    private Button btGravar, btEnviar, btAssistir, btCancelar;
    private EditText etProcesso;
    private VideoView videoView;
    private ProgressBar progressBar;
    private final String TAG = "MainActivity";
    private Uri file;
    private final int REQUEST_GRAVAR = 100;
    private final int REQUEST_ASSISTIR = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btGravar = (Button) findViewById(R.id.btGravar);
        btGravar.setOnClickListener(this);

        btEnviar = (Button) findViewById(R.id.btEnviar);
        btEnviar.setOnClickListener(this);

        btAssistir = (Button) findViewById(R.id.btAssistir);
        btAssistir.setOnClickListener(this);

        btCancelar = (Button) findViewById(R.id.btCancelar);
        btCancelar.setOnClickListener(this);

        etProcesso = (EditText) findViewById(R.id.etProcesso);

        videoView = (VideoView) findViewById(R.id.videoView);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        autenticacao("css01cri@tjrn.jus.br", "Tribunal@123");

    }

    private void autenticacao(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            //FirebaseUser user = auth.getCurrentUser();
                            //updateUI(user);
                            upload();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void upload() {
        if (file != null) {
            StorageReference riversRef = mStorageRef.child(etProcesso.getText().toString() + ".mp4");
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            habilitarBotoes();
                            Toast.makeText(MainActivity.this, "Arquivo " + file.toString() + " enviado.", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(MainActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void enviar() {
        if (auth.getCurrentUser() == null) {
            autenticacao("css01cri@tjrn.jus.br", "Tribunal@123");
        } else {
            upload();
        }
    }

    private void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {
        long tamanho = taskSnapshot.getTotalByteCount();
        long enviado = taskSnapshot.getBytesTransferred();
        long progresso = (100 * enviado) / tamanho;
        progressBar.setProgress((int) progresso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GRAVAR) {
            if (resultCode == RESULT_OK) {
                file = data.getData();
                videoViewPlay();
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == REQUEST_ASSISTIR) {
            if (resultCode == RESULT_OK) {
                file = data.getData();
                videoViewPlay();
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void videoViewPlay() {
        videoView.setVideoURI(file);
        videoView.setMediaController(new MediaController(this));
        videoView.start();
    }

    private void desabilitarBotoes() {
        btEnviar.setEnabled(false);
        btGravar.setEnabled(false);
        btAssistir.setEnabled(false);
        btCancelar.setEnabled(false);
        btEnviar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btGravar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btAssistir.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btCancelar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void habilitarBotoes() {
        btEnviar.setEnabled(true);
        btGravar.setEnabled(true);
        btAssistir.setEnabled(true);
        btCancelar.setEnabled(true);
        btEnviar.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        btGravar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        btAssistir.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        btCancelar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.btGravar) {
                Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, etProcesso.getText());
                startActivityForResult(i, REQUEST_GRAVAR);
            } else if (v.getId() == R.id.btEnviar) {
                enviar();
                desabilitarBotoes();
            } else if (v.getId() == R.id.btAssistir) {
                Intent i = new Intent();
                i.setType("video/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Select Video"), REQUEST_ASSISTIR);
            } else if (v.getId() == R.id.btCancelar) {
                file = null;
                auth.signOut();
                //finish();
                Intent i = new Intent(this, ListVideoActivity.class);
                startActivity(i);
            }
        } catch (Exception e) {
             Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
