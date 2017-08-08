package ru.uu.voda.voda;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CAH ek on 08.08.2017.
 */

public class RVAdapter extends RecyclerView.Adapter {

    List <Card> cards;

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        CardViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
        }
    }

    RVAdapter(List cards){
        this.cards = cards;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        return new CardViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CardViewHolder cardViewHolder = (CardViewHolder) holder;
        CardView cardView = cardViewHolder.cardView;
        TextView title = (TextView)cardView.findViewById(R.id.title);
        title.setText(cards.get(position).title);
        TextView content = (TextView)cardView.findViewById(R.id.content);
        content.setText(cards.get(position).content);

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}