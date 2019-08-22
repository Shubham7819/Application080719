package com.example.application080719.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application080719.PreferenceUtilities;
import com.example.application080719.R;
import com.example.application080719.dto.SoundItem;
import com.example.application080719.dto.Sounds;
import com.example.application080719.ui.main.MainActivity;

import java.util.ArrayList;

public class SelectedAudioAdapter extends ArrayAdapter<SoundItem> {

    private Context context;

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
                    MainActivity.playerService.pauseAudio(Sounds.streamIdList[id]);
                    currentSound.setItemPlaying(false);
                    PreferenceUtilities.decrementSoundsPlayingCount(context);
                    holder.playBackBtn.setImageResource(R.drawable.ic_play);
                } else {
                    MainActivity.playerService.resumeAudio(Sounds.streamIdList[id]);
                    currentSound.setItemPlaying(true);
                    PreferenceUtilities.incrementSoundsPlayingCount(context);
                    holder.playBackBtn.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        holder.itemTitle.setText(currentSound.getItemTitle());

        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.playerService.stopAudio(Sounds.streamIdList[id]);
                currentSound.setItemPlaying(false);
                currentSound.setItemSelected(false);
                PreferenceUtilities.decrementSoundsPlayingCount(context);
                PreferenceUtilities.decrementSoundsSelectedCount(context);
                Sounds.selectedSoundsList.remove(currentSound);
                if (PreferenceUtilities.getSoundsSelectedCount(context) > 0) {
                    MainActivity.playerService.getPlayer().autoResume();
                } else {
                    MainActivity.selectionBottomSheetDialog.dismiss();
                }
                Sounds.allPaused = false;
                MainActivity.selectedAudioAdapter.notifyDataSetChanged();
            }
        });

        // Return the whole list item layout (containing 2 ImageView and an TextViews)
        // so that it can be shown in the ListView
        return convertView;
    }

    public SelectedAudioAdapter(Activity context, ArrayList<SoundItem> items) {
        super(context, 0, items);
        this.context = context;
    }

    private static class ViewHolder {
        private ImageView playBackBtn;
        private TextView itemTitle;
        private ImageView stopBtn;
    }

}
