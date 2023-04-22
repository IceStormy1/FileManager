package com.example.filemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class FileListActivity extends AppCompatActivity
{
    private File[] files;

    public void updateStorageFiles() {
        setStorageFiles();
    }

    public File[] getStorageFiles(){
        return this.files;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.nofiles_textview);

        setStorageFiles();

        if (files == null || files.length == 0)
        {
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }

        noFilesText.setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(FileListActivity.this, files));

        File file = (File) getIntent().getSerializableExtra("file");

        if(file != null){
            Button btnStorage = findViewById(R.id.buttonMove);
            btnStorage.setVisibility(Button.VISIBLE);

            btnStorage.setOnClickListener(v -> {
                File directory = getFilesDir();
                file.renameTo(new File(directory, file.getName()));

                finish();
            });
        }
    }

    private void setStorageFiles(){
        String path = getIntent().getStringExtra("path");
        File root = new File(path);
        files = root.listFiles();
    }
}