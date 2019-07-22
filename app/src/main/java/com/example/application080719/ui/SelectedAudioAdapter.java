package com.example.application080719.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application080719.MainActivity;
import com.example.application080719.R;
import com.example.application080719.SoundItem;
import com.example.application080719.Sounds;
import com.example.application080719.ui.main.CommonFragment;

import java.util.ArrayList;

public class SelectedAudioAdapter extends ArrayAdapter<SoundItem> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.selected_audio_list_item, parent, false);
            holder = new ViewHolder();
            holder.playBackBtn = convertView.findViewById(R.id.selected_item_playback_button);
            holder.itemTitle = convertView.findViewById(R.id.selected_item_title);
            holder.stopBtn = convertView.findViewById(R.id.selected_item_stop_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SoundItem currentSound = getItem(position);
        int id = SoundListAdapter.getVarName(currentSound.getItemTitle());

        if (currentSound.isItemPlaying()) {
            holder.playBackBtn.setImageResource(R.drawable.ic_pause);
        } else {
            holder.playBackBtn.setImageResource(R.drawable.ic_play);
        }

        holder.playBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSound.isItemPlaying()) {
                    Sounds.soundPool.pause(Sounds.streamIdList[id]);
                    currentSound.setItemPlaying(false);
                    holder.playBackBtn.setImageResource(R.drawable.ic_play);
                } else {
                    Sounds.soundPool.resume(Sounds.streamIdList[id]);
                    currentSound.setItemPlaying(true);
                    holder.playBackBtn.setImageResource(R.drawable.ic_pause);
                }
//                CommonFragment.soundListAdapter.notifyDataSetChanged();
            }
        });

        holder.itemTitle.setText(currentSound.getItemTitle());

        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.soundPool.stop(Sounds.streamIdList[id]);
                currentSound.setItemPlaying(false);
                Sounds.soundsPlayingCounter--;
                Sounds.selectedSoundsList.remove(currentSound);
                MainActivity.updateBadgeNumber(Sounds.soundsPlayingCounter);
                if (Sounds.soundsPlayingCounter > 0) {
                    Sounds.soundPool.autoResume();
                } else {
                    MainActivity.menuItemPlay.setTitle(R.string.play);
                    MainActivity.menuItemPlay.setIcon(R.drawable.ic_play);
                }
                currentSound.setItemSelected(false);
                Sounds.allPaused = false;
                MainActivity.selectedAudioAdapter.notifyDataSetChanged();
            }
        });

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return convertView;
    }

    public SelectedAudioAdapter(Activity context, ArrayList<SoundItem> items) {
        super(context, 0, items);
    }

    private static class ViewHolder {
        private ImageView playBackBtn;
        private TextView itemTitle;
        private ImageView stopBtn;
    }

}
