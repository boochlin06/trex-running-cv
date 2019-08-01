package com.emotibot.robotvision.game.trexrun.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.model.Player;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
        this.notifyDataSetChanged();
    }

    private List<Player> playerList;

    public RankAdapter(List<Player> playerList) {
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rank, parent, false);
        RankViewHolder viewHolder = new RankViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.txtName.setText(player.getName());
        holder.txtOrder.setText((position + 1) + ".");
        holder.txtScore.setText(player.getScore() + "公尺");
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class RankViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public TextView txtOrder;
        public TextView txtScore;

        public RankViewHolder(View v) {
            super(v);
            txtName = v.findViewById(R.id.txtName);
            txtName.setSelected(true);
            txtOrder = v.findViewById(R.id.txtOrder);
            txtScore = v.findViewById(R.id.txtScore);
        }
    }
}
