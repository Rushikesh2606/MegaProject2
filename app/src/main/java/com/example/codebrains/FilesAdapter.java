package com.example.codebrains;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private List<FileAttachment> fileAttachments;

    public FilesAdapter(List<FileAttachment> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileAttachment attachment = fileAttachments.get(position);
        holder.filename.setText(attachment.getFilename());
        holder.size.setText(attachment.getSize());
    }

    @Override
    public int getItemCount() {
        return fileAttachments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView filename, size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.tvFilename);
            size = itemView.findViewById(R.id.tvSize);
        }
    }
}