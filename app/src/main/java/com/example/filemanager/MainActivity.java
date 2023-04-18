package com.example.filemanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStorage = findViewById(R.id.storageButton);
        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermissions = checkStoragePermission();

                if(hasPermissions)
                {
                    Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkStoragePermission()
    {
        // Проверяем, предоставлено ли разрешение на доступ к файловому хранилищу
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            // Разрешение уже предоставлено
            Toast.makeText(this, "Разрешение на доступ к хранилищу предоставлено", Toast.LENGTH_SHORT).show();

            return true;
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            // Разрешение еще не предоставлено, но уже было запрошено ранее
            showExplanationDialog();

            return false;
        }

        // Разрешение еще не предоставлено и еще не было запрошено ранее
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS);

        return false;
    }

    private void showExplanationDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Необходимо разрешение");
        builder.setMessage("Для использования функции записи на диск необходимо разрешение на доступ к хранилищу. " +
                "Предоставьте разрешение в настройках приложения");
        builder.setPositiveButton("Предоставить разрешение", (dialog, which) -> {
            // Запрашиваем разрешение на доступ к файловому хранилищу еще раз
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено пользователем
                Toast.makeText(this, "Разрешение на доступ к хранилищу предоставлено", Toast.LENGTH_SHORT).show();

                return;
            }

            // Разрешение не предоставлено пользователем
            Toast.makeText(this, "Разрешение на доступ к хранилищу не предоставлено", Toast.LENGTH_SHORT).show();

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Если пользователь не хочет больше видеть диалог запроса разрешения, но разрешение ему не предоставлено,
                // показываем ему диалог с инструкцией, как вручную предоставить разрешение в настройках приложения
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Необходимо разрешение");
        builder.setMessage("Для использования функции записи необходимо разрешение на доступ к хранилищу. " +
                "Перейдите в настройки приложения и предоставьте разрешение");
        builder.setPositiveButton("Перейти в настройки", (dialog, which) -> {
            // Открываем настройки приложения для ручного предоставления разрешения
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
}