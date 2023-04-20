package com.example.filemanager;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{

    private static final String DeleteMenuText = "Удалить";
    private static final String MoveMenuText = "Переместить";
    private static final String RenameMenuText = "Переименовать";
    private static final int REQUEST_CODE_RENAME = 1;

    FileListActivity  _context;
    File[] filesAndFolders;

    public FileAdapter(FileListActivity context, File[] filesAndFolders)
    {
        _context = context;
        this.filesAndFolders = filesAndFolders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(_context).inflate(R.layout.recycle_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileAdapter.ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];

        holder.textView.setText(selectedFile.getName());
        holder.imageView.setImageResource(selectedFile.isDirectory() ? R.drawable.ic_baseline_folder_24 : R.drawable.ic_baseline_insert_drive_file_24);
        holder.itemView.setOnClickListener(v -> setOnClickListener(selectedFile));
        holder.itemView.setOnLongClickListener(v -> setOnLongClickListener(v, selectedFile, position));
    }

    @Override
    public int getItemCount()
    {
        return filesAndFolders.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
        }
    }

    private void setOnClickListener(File selectedFile) {
        if (selectedFile.isDirectory())
        {
            Intent intent = new Intent(_context, FileListActivity.class);
            String path = selectedFile.getAbsolutePath();
            intent.putExtra("path", path);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);

            return;
        }
        try
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String type = "image/*";
            intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(_context.getApplicationContext(), "Cannot open the file", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean setOnLongClickListener(View v, File selectedFile, int position){
        PopupMenu popupMenu = new PopupMenu(_context,v);
        popupMenu.getMenu().add(DeleteMenuText);
        popupMenu.getMenu().add(MoveMenuText);
        popupMenu.getMenu().add(RenameMenuText);

        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getTitle().equals(DeleteMenuText))
            {
                return DeleteFile(selectedFile, position);
            }
            if(item.getTitle().equals(MoveMenuText))
            {
                Toast.makeText(_context.getApplicationContext(),"Перемещено ",Toast.LENGTH_SHORT).show();
            }
            if(item.getTitle().equals(RenameMenuText))
            {
                Intent intentRename = new Intent(_context, RenameFileActivity.class);
                intentRename.putExtra("file", selectedFile);
                _context.startActivity(intentRename);
                notifyItemChanged(position);
                _context.updateStorageFiles();
            }

            return true;
        });

        popupMenu.show();
        return true;
    }

    private boolean DeleteFile(File selectedFile, int position){
        new AlertDialog.Builder(_context)
                .setTitle("Удаление файла")
                .setMessage("Вы уверены, что хотите удалить файл " + selectedFile.getName() + "?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        selectedFile.delete();
                        Toast.makeText(_context.getApplicationContext(),String.format("Файл %s удален", selectedFile.getName()),Toast.LENGTH_SHORT).show();
                        //v.setVisibility(View.GONE);
                        //onBindViewHolder(holder, );
                        notifyItemRemoved(position);
                        _context.updateStorageFiles();
                    }
                })
                .setNegativeButton("Нет", null)
                .show();

        return false;
    }
}
