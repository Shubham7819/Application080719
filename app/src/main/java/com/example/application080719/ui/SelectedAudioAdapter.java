package com.example.application080719.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application080719.MainActivity;
import com.example.application080719.PreferenceUtilities;
import com.example.application080719.R;
import com.example.application080719.SoundItem;
import com.example.application080719.Sounds;

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
                    Sounds.soundPool.pause(Sounds.streamIdList[id]);
                    currentSound.setItemPlaying(false);
                    PreferenceUtilities.decrementSoundsPlayingCount(context);
                    holder.playBackBtn.setImageResource(R.drawable.ic_play);
                    if (PreferenceUtilities.getSoundsPlayingCount(context) == 0) {
                        MainActivity.menuItemPlay.setTitle(R.string.play);
                        MainActivity.menuItemPlay.setIcon(R.drawable.ic_play);
                    }
                } else {
                    Sounds.soundPool.resume(Sounds.streamIdList[id]);
                    currentSound.setItemPlaying(true);
                    PreferenceUtilities.incrementSoundsPlayingCount(context);
                    holder.playBackBtn.setImageResource(R.drawable.ic_pause);
                    MainActivity.menuItemPlay.setTitle(R.string.pause);
                    MainActivity.menuItemPlay.setIcon(R.drawable.ic_pause);
                }
            }
        });

        holder.itemTitle.setText(currentSound.getItemTitle());

        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.soundPool.stop(Sounds.streamIdList[id]);
                currentSound.setItemPlaying(false);
                currentSound.setItemSelected(false);
                PreferenceUtilities.decrementSoundsPlayingCount(context);
                PreferenceUtilities.decrementSoundsSelectedCount(context);
                Sounds.selectedSoundsList.remove(currentSound);
                if (PreferenceUtilities.getSoundsSelectedCount(context) > 0) {
                    Sounds.soundPool.autoResume();
                } else {
                    MainActivity.menuItemPlay.setTitle(R.string.play);
                    MainActivity.menuItemPlay.setIcon(R.drawable.ic_play);
                    MainActivity.selectionBottomSheetDialog.dismiss();
                }
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
        this.context = context;
    }

    private static class ViewHolder {
        private ImageView playBackBtn;
        private TextView itemTitle;
        private ImageView stopBtn;
    }

}
