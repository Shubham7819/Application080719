package com.example.application080719.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application080719.MainActivity;
import com.example.application080719.PreferenceUtilities;
import com.example.application080719.R;
import com.example.application080719.SoundItem;
import com.example.application080719.Sounds;
import com.example.application080719.ui.main.CommonFragment;

import java.util.ArrayList;

import static com.example.application080719.Sounds.soundIdList;
import static com.example.application080719.Sounds.soundPool;
import static com.example.application080719.Sounds.streamIdList;

public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.SoundViewHolder> {

    private static final String TAG = SoundListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<SoundItem> mSoundList;
    private int id;

    public SoundListAdapter(Context context, ArrayList<SoundItem> itemsList) {
        mContext = context;
        mSoundList = itemsList;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(mContext).inflate(R.layout.sounds_list_item, parent, false);
        return new SoundViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SoundViewHolder holder, int position) {
        final SoundItem currentItem = mSoundList.get(position);

        if (currentItem.isItemPlaying()) {
            holder.imgBtn.setImageResource(R.drawable.ic_pause);
        } else {
            holder.imgBtn.setImageResource(R.drawable.ic_play);
        }

        if (currentItem.isItemSelected()) {
            holder.imgBtn.setBackgroundResource(R.drawable.button_selected_background);
        } else {
            holder.imgBtn.setBackgroundResource(R.drawable.button_background);
        }

        holder.btnTitle.setText(currentItem.getItemTitle());

        holder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = getVarName(currentItem.getItemTitle());

                if (currentItem.isItemLoaded()) {

                    if (currentItem.isItemPlaying()) {

                        soundPool.stop(streamIdList[id]);
                        currentItem.setItemPlaying(false);
                        currentItem.setItemSelected(false);
                        PreferenceUtilities.decrementSoundsPlayingCount(mContext);
                        PreferenceUtilities.decrementSoundsSelectedCount(mContext);
                        Sounds.selectedSoundsList.remove(currentItem);
                        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
                        if (PreferenceUtilities.getSoundsSelectedCount(mContext) > 0) {
                            for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
                                if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                                    Sounds.soundPool.resume(Sounds.streamIdList[i]);
                                    CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                                    PreferenceUtilities.incrementSoundsPlayingCount(mContext);
                                }
                            }
                        } else {
                            MainActivity.menuItemPlay.setTitle(R.string.play);
                            MainActivity.menuItemPlay.setIcon(R.drawable.ic_play);
                        }

                    } else {

                        if (Sounds.allPaused) {

                            if (currentItem.isItemSelected()) {

                                soundPool.stop(streamIdList[id]);
                                currentItem.setItemPlaying(false);
                                currentItem.setItemSelected(false);
                                MainActivity.menuItemPlay.setTitle("Pause");
                                MainActivity.menuItemPlay.setIcon(R.drawable.ic_pause);
                                PreferenceUtilities.decrementSoundsPlayingCount(mContext);
                                PreferenceUtilities.decrementSoundsSelectedCount(mContext);
                                Sounds.selectedSoundsList.remove(currentItem);
                                MainActivity.selectedAudioAdapter.notifyDataSetChanged();

                            } else {

                                Sounds.streamIdList[id] = soundPool.play(soundIdList[id], 1, 1, 0, -1, 1);
                                currentItem.setItemPlaying(true);
                                MainActivity.menuItemPlay.setTitle("Pause");
                                MainActivity.menuItemPlay.setIcon(R.drawable.ic_pause);
                                currentItem.setItemSelected(true);
                                PreferenceUtilities.incrementSoundsPlayingCount(mContext);
                                PreferenceUtilities.incrementSoundsSelectedCount(mContext);
                                Sounds.selectedSoundsList.add(currentItem);
                                MainActivity.selectedAudioAdapter.notifyDataSetChanged();

                            }

                        } else {

                            Sounds.streamIdList[id] = soundPool.play(soundIdList[id], 1, 1, 0, -1, 1);
                            currentItem.setItemPlaying(true);
                            MainActivity.menuItemPlay.setTitle("Pause");
                            MainActivity.menuItemPlay.setIcon(R.drawable.ic_pause);
                            currentItem.setItemSelected(true);
                            PreferenceUtilities.incrementSoundsPlayingCount(mContext);
                            PreferenceUtilities.incrementSoundsSelectedCount(mContext);
                            Sounds.selectedSoundsList.add(currentItem);
                            MainActivity.selectedAudioAdapter.notifyDataSetChanged();

                        }
                        if (PreferenceUtilities.getSoundsSelectedCount(mContext) > 0) {
                            resumeAll();
                        }
                        Sounds.allPaused = false;
                        notifyDataSetChanged();

                    }
                    notifyDataSetChanged();

                } else {
                    soundIdList[id] = soundPool.load(mContext, currentItem.getItemResourceId(), 1);
                    Sounds.selectedSoundsList.add(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSoundList.size();
    }

    class SoundViewHolder extends RecyclerView.ViewHolder {

        ImageButton imgBtn;
        TextView btnTitle;

        SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBtn = itemView.findViewById(R.id.image_btn);
            btnTitle = itemView.findViewById(R.id.btn_title);
        }
    }

    static int getVarName(String soundTitle) {
        switch (soundTitle) {
            case "bells":
                return Sounds.BELLS;

            case "bird":
                return Sounds.BIRD;

            case "clock chimes":
                return Sounds.CLOCK_CHIMES;

            case "farm":
                return Sounds.FARM;

            case "fire":
                return Sounds.FIRE;

            case "flute":
                return Sounds.FLUTE;

            case "music box":
                return Sounds.MUSIC_BOX;

            case "night":
                return Sounds.NIGHT;

            case "rain":
                return Sounds.RAIN;

            case "rainforest":
                return Sounds.RAINFOREST;

            case "river":
                return Sounds.RIVER;

            case "sea":
                return Sounds.SEA;

            case "thunder":
                return Sounds.THUNDER;

            case "waterfall":
                return Sounds.WATERFALL;

            case "wind":
                return Sounds.WIND;

            default:
                return 0;
        }
    }

    private void resumeAll() {
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                soundPool.resume(streamIdList[i]);
            }
        }
    }

}
