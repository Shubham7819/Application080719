package com.example.application080719.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application080719.MeditationExercise;
import com.example.application080719.R;

import java.util.ArrayList;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder> {

    private Context mContext;
    private ArrayList<MeditationExercise> mExercisesList;

    public ExerciseListAdapter(Context context, ArrayList<MeditationExercise> exercisesList) {
        mContext = context;
        mExercisesList = exercisesList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(mContext).inflate(R.layout.exercise_list_item, parent, false);
        return new ExerciseViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExerciseViewHolder holder, int position) {
        MeditationExercise currentExercise = mExercisesList.get(position);
        holder.titleTV.setText(currentExercise.getExerciseTitle());
        holder.guidesTV.setText(currentExercise.getExerciseGuides());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.titleTV.setTypeface(null, Typeface.BOLD);
                holder.guidesTV.setTypeface(null, Typeface.BOLD);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExercisesList.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        TextView guidesTV;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.exercise_title_tv);
            guidesTV = itemView.findViewById(R.id.exercise_guides_tv);
        }
    }
}
