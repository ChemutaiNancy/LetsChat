package com.chemutai.letschat.Matches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.chemutai.letschat.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {

    private List<MatchesObject> matchesList;
    private Context mContext;

    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rvc = new MatchesViewHolders((layoutView));
        return rvc;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mMatchName.setText(matchesList.get(position).getName());
        if (!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(mContext).load(matchesList.get(position).getProfileImageUrl()).into(holder.mMatchImage);
        }
    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}
