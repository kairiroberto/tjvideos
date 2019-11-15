package com.a.appvideos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListVideoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView lv;
    List<String> strings = new ArrayList<>();
    ArrayAdapter<String> adapter;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        lv = (ListView) findViewById(R.id.lvFiles);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        autenticacao("    ", "    ");
        listar();
    }

    public void listar() {
        for (String s : strings) {
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        }
    }

    private void autenticacao(String email, String password) {
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            final StorageReference storageReference = firebaseStorage.getReference();
                            storageReference.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            for (StorageReference prefix : listResult.getPrefixes()) {
                                                // All the prefixes under listRef.
                                                // You may call listAll() recursively on them.
                                                //tv.setText(tv.getText() + prefix.toString() + "\n\n");
                                            }

                                            for (StorageReference item : listResult.getItems()) {
                                                // All the items under listRef.
                                                download(item.getName());
                                            }
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<ListResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ListResult> task) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Uh-oh, an error occurred!
                                        }
                                    });
                        } else {
                        }
                    }
                });
    }

    public void download(final String file) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();
        // Create a reference with an initial file path and name
        storageRef.child(file).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        strings.add(file + ":" + uri.toString());
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, strings.get(position), Toast.LENGTH_LONG).show();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "PJE-" + strings.get(position).substring(0, strings.get(position).indexOf(":")));
        StringBuffer sb = new StringBuffer();
        sb.append("ARQUIVOS DE VÍDEO DO PJE");
        sb.append("\n\n");
        sb.append("Os vídeos que os OJ estão gravando está sendo enviado diretamente para o e-mail da Secretaria. ");        
        sb.append("\n\n");
        sb.append("LINK: ");
        sb.append(strings.get(position).substring(strings.get(position).indexOf(":")+1));
        share.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(share, "Compartilhar"));
    }
}
