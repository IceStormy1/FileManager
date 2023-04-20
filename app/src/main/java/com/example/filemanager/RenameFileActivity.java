package com.example.filemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class RenameFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_file);

        EditText editTextFilename = findViewById(R.id.edit_text_file_name);
        File file = (File) getIntent().getSerializableExtra("file");
        editTextFilename.setText(file.getName());

        Button btnStorage = findViewById(R.id.button_rename);
        btnStorage.setOnClickListener(v -> {
            EditText editTextFilename1 = findViewById(R.id.edit_text_file_name);
            String newFilename = editTextFilename1.getText().toString();
            File file1 = (File) getIntent().getSerializableExtra("file");
            File newFile = new File(file1.getParent(), newFilename);
            boolean success = file1.renameTo(newFile);
            if (success)
            {
                Toast.makeText(getApplicationContext(), "Файл успешно переименован", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Не удалось переименовать файл", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }
}