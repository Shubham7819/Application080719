package com.example.application080719.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application080719.R;
import com.example.application080719.dto.MeditationExercise;
import com.example.application080719.ui.main.MeditationActivity;

import java.util.ArrayList;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder> {

    public static final String EXERCISE_NAME = "exercise name";
    public static final String EXERCISE_GUIDES = "exercise guides";
    public static final String EXERCISE_RESOURCE_ID = "exercise resource id";
    private Context mContext;
    private ArrayList<MeditationExercise> mExercisesList;

    public ExerciseListAdapter(Context context, ArrayList<MeditationExercise> exercisesList) {
        mContext = context;
        mExercisesList = exercisesList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(mContext).inflate(R.layout.exercise_list_item, parent
                , false);
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
                Intent i = new Intent(mContext, MeditationActivity.class);
                i.putExtra(EXERCISE_NAME, currentExercise.getExerciseTitle());
                i.putExtra(EXERCISE_GUIDES, currentExercise.getExerciseGuides());
                i.putExtra(EXERCISE_RESOURCE_ID, currentExercise.getExerciseAudioResourceId());
                mContext.startActivity(i);
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
