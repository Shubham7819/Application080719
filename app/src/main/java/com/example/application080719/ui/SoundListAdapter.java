package com.example.application080719.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application080719.R;
import com.example.application080719.SoundItem;
import com.example.application080719.Sounds;

import java.util.ArrayList;

import static com.example.application080719.Sounds.soundIdList;
import static com.example.application080719.Sounds.soundPool;
import static com.example.application080719.Sounds.streamIdList;

public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.SoundViewHolder> {

    private final String TAG = SoundListAdapter.class.getSimpleName();
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
        holder.imgBtn.setImageResource(R.drawable.ic_play);
        holder.btnTitle.setText(currentItem.getItemTitle());

        holder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = getVarName(currentItem.getItemTitle());
                Log.v(TAG, "On Click called, id: " + id);
//                holder.imgBtn.setBackgroundResource(R.drawable.button_selected_background);
                Log.v(TAG, currentItem.getItemTitle() + " is item loaded " + isItemLoaded(currentItem.getItemTitle()));
                if (isItemLoaded(currentItem.getItemTitle())) {
                    if (isAudioPlaying(currentItem.getItemTitle())) {

                        soundPool.pause(streamIdList[id]);
                        setAudioPlaying(currentItem.getItemTitle(), false);
                        Toast.makeText(mContext, "paused", Toast.LENGTH_SHORT).show();
                        holder.imgBtn.setImageResource(R.drawable.ic_play);
                    } else {

                        soundPool.resume(Sounds.streamIdList[id]);
                        setAudioPlaying(currentItem.getItemTitle(), true);
                        Toast.makeText(mContext, "playing", Toast.LENGTH_SHORT).show();
                        holder.imgBtn.setImageResource(R.drawable.ic_pause);
                    }
                } else {
                    soundIdList[id] = soundPool.load(mContext, currentItem.getItemResourceId(), 1);
                    holder.imgBtn.setImageResource(R.drawable.ic_pause);
//                holder.imgBtn.setBackgroundResource(R.drawable.button_selected_background);
//                    Toast.makeText(mContext, "play now", Toast.LENGTH_SHORT).show();
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

    private int getVarName(String soundTitle) {
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

    private boolean isItemLoaded(String soundTitle) {
        Log.v(TAG, "isItemLoaded Method called, soundTitle: " + soundTitle);
        switch (soundTitle) {
            case "bells":
                Log.v(TAG, "bells case" + Sounds.isBellsSoundLoaded);
                return Sounds.isBellsSoundLoaded;

            case "bird":
                Log.v(TAG, "bird case");
                return Sounds.isBirdSoundLoaded;

            case "clock chimes":
                Log.v(TAG, "clock case");
                return Sounds.isClockSoundLoaded;

            case "farm":
                Log.v(TAG, "farm case");
                return Sounds.isFarmSoundLoaded;

            case "fire":
                Log.v(TAG, "fire case");
                return Sounds.isFireSoundLoaded;

            case "flute":
                Log.v(TAG, "flute case");
                return Sounds.isFluteSoundLoaded;

            case "music box":
                Log.v(TAG, "music case");
                return Sounds.isMusicBoxSoundLoaded;

            case "night":
                Log.v(TAG, "night case");
                return Sounds.isNightSoundLoaded;

            case "rain":
                Log.v(TAG, "rain case");
                return Sounds.isRainSoundLoaded;

            case "rainforest":
                Log.v(TAG, "rainforest case");
                return Sounds.isRainForestSoundLoaded;

            case "river":
                Log.v(TAG, "river case");
                return Sounds.isRiverSoundLoaded;

            case "sea":
                Log.v(TAG, "sea case");
                return Sounds.isSeaSoundLoaded;

            case "thunder":
                Log.v(TAG, "thunder case");
                return Sounds.isThunderSoundLoaded;

            case "waterfall":
                Log.v(TAG, "waterfall case");
                return Sounds.isWaterfallSoundLoaded;

            case "wind":
                Log.v(TAG, "wind case");
                return Sounds.isWindSoundLoaded;

            default:
                Log.v(TAG, "default case");
                return false;
        }
    }

    private void setAudioPlaying(String soundTitle, boolean b) {
        switch (soundTitle) {
            case "bells":
                Sounds.isBellsSoundPlaying = b;
                break;

            case "bird":
                Sounds.isBirdSoundPlaying = b;
                break;

            case "clock chimes":
                Sounds.isClockSoundPlaying = b;
                break;

            case "farm":
                Sounds.isFarmSoundPlaying = b;
                break;

            case "fire":
                Sounds.isFireSoundPlaying = b;
                break;

            case "flute":
                Sounds.isFluteSoundPlaying = b;
                break;

            case "music box":
                Sounds.isMusicBoxSoundPlaying = b;
                break;

            case "night":
                Sounds.isNightSoundPlaying = b;
                break;

            case "rain":
                Sounds.isRainSoundPlaying = b;
                break;

            case "rainforest":
                Sounds.isRainForestSoundPlaying = b;
                break;

            case "river":
                Sounds.isRiverSoundPlaying = b;
                break;

            case "sea":
                Sounds.isSeaSoundPlaying = b;
                break;

            case "thunder":
                Sounds.isThunderSoundPlaying = b;
                break;

            case "waterfall":
                Sounds.isWaterfallSoundPlaying = b;
                break;

            case "wind":
                Sounds.isWindSoundPlaying = b;
                break;

            default:
                break;
        }
    }

    private boolean isAudioPlaying(String soundTitle) {
        switch (soundTitle) {
            case "bells":
//                Log.v(TAG, "bells case" + Sounds.isBellsSoundLoaded);
                return Sounds.isBellsSoundPlaying;

            case "bird":
//                Log.v(TAG, "bird case");
                return Sounds.isBirdSoundPlaying;

            case "clock chimes":
//                Log.v(TAG, "clock case");
                return Sounds.isClockSoundPlaying;

            case "farm":
//                Log.v(TAG, "farm case");
                return Sounds.isFarmSoundPlaying;

            case "fire":
                Log.v(TAG, "fire case");
                return Sounds.isFireSoundPlaying;

            case "flute":
                Log.v(TAG, "flute case");
                return Sounds.isFluteSoundPlaying;

            case "music box":
                Log.v(TAG, "music case");
                return Sounds.isMusicBoxSoundPlaying;

            case "night":
                Log.v(TAG, "night case");
                return Sounds.isNightSoundPlaying;

            case "rain":
                Log.v(TAG, "rain case");
                return Sounds.isRainSoundPlaying;

            case "rainforest":
                Log.v(TAG, "rainforest case");
                return Sounds.isRainForestSoundPlaying;

            case "river":
                Log.v(TAG, "river case");
                return Sounds.isRiverSoundPlaying;

            case "sea":
                Log.v(TAG, "sea case");
                return Sounds.isSeaSoundPlaying;

            case "thunder":
                Log.v(TAG, "thunder case");
                return Sounds.isThunderSoundPlaying;

            case "waterfall":
                Log.v(TAG, "waterfall case");
                return Sounds.isWaterfallSoundPlaying;

            case "wind":
                Log.v(TAG, "wind case");
                return Sounds.isWindSoundPlaying;

            default:
                Log.v(TAG, "default case");
                return false;
        }
    }
}
