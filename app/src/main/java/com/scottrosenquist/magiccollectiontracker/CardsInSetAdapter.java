package com.scottrosenquist.magiccollectiontracker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardsInSetAdapter extends RecyclerView.Adapter<CardsInSetAdapter.ViewHolder> {
    private Context viewContext;
    CollectionHelper collectionHelper;
    private SortedList<CardObj> cardsDataset;
    private final LayoutInflater layoutInflater;
    private final int COLOURLESS = 0;
    private final int WHITE = 1;
    private final int BLUE = 2;
    private final int BLACK = 3;
    private final int RED = 4;
    private final int GREEN = 5;
    private final int GOLD = 6;
    private final int ARTIFACT = 7;
    private final int LAND = 8;
    private String setCode;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mainView;
        public ViewHolder(CardView v) {
            super(v);
            mainView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardsInSetAdapter(LayoutInflater layoutInflater, String setCode) {
        this.layoutInflater = layoutInflater;
        viewContext = layoutInflater.getContext();
        collectionHelper = new CollectionHelper(viewContext);
        cardsDataset = new SortedList<>(CardObj.class, sortedListAdapterCallback(this));
        this.setCode = setCode;
    }

    private SortedListAdapterCallback<CardObj> sortedListAdapterCallback(android.support.v7.widget.RecyclerView.Adapter adapter) {
        return new SortedListAdapterCallback<CardObj>(adapter) {
            @Override
            public int compare(CardObj o1, CardObj o2) {
                int number1=0;
                int number2=0;
                try {
                    number1 = Integer.parseInt(o1.getNumber());
                    number2 = Integer.parseInt(o2.getNumber());
                } catch (NumberFormatException e) {
                    if (o1.getNumber().equals("")) {
                        number1 = getColour(o1);
                        number2 = getColour(o2);
                        if (number1 == number2) {
                            number1 = o1.getName().compareToIgnoreCase(o2.getName());
                            number2 = 0;
                        }
                    } else {
                        String[] alphanumeric1 = o1.getNumber().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                        String[] alphanumeric2 = o2.getNumber().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

                        number1 = Integer.parseInt(alphanumeric1[0]);
                        number2 = Integer.parseInt(alphanumeric2[0]);
                    }
                }
                return number1 - number2;
            }

            @Override
            public boolean areContentsTheSame(CardObj oldItem, CardObj newItem) {
                return oldItem.getName().equals(newItem.getName());
            }

            @Override
            public boolean areItemsTheSame(CardObj item1, CardObj item2) {
                return item1.getId().equals(item2.getId());
            }
        };
    }

    private int getColour(CardObj card) {
        int colourOrder = -1;
        if (card.getTypes().get(0).equals("Land")) {
            colourOrder = LAND;
        } else if (card.getTypes().get(0).equals("Artifact") && card.getColours().size() == 0) {
            colourOrder = ARTIFACT;
        } else if (card.getColours().size() > 1) {
            colourOrder = GOLD;
        } else if (card.getColours().size() == 0){
            colourOrder = COLOURLESS;
        } else if (card.getColours().get(0).equals("Green")) {
            colourOrder = GREEN;
        } else if (card.getColours().get(0).equals("Red")) {
            colourOrder = RED;
        } else if (card.getColours().get(0).equals("Black")) {
            colourOrder = BLACK;
        } else if (card.getColours().get(0).equals("Blue")) {
            colourOrder = BLUE;
        } else if (card.getColours().get(0).equals("White")) {
            colourOrder = WHITE;
        }
        return colourOrder;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardsInSetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final CardObj card = cardsDataset.get(position);
        final String cardName = card.getName();
        List<String> cardColours = card.getColours();
        List<String> cardTypes = card.getTypes();

        CardView cardView = (CardView) holder.mainView.findViewById(R.id.card_view);
        RadioButton cardColour = (RadioButton) holder.mainView.findViewById(R.id.card_colour);
        TextView cardNameTextView = ((TextView) holder.mainView.findViewById(R.id.card_name));
        final RadioGroup cardSelection = (RadioGroup) holder.mainView.findViewById(R.id.radioButtons);
        final RadioButton radioButton1 = (RadioButton) holder.mainView.findViewById(R.id.radioButton1);
        final RadioButton radioButton2 = (RadioButton) holder.mainView.findViewById(R.id.radioButton2);
        final RadioButton radioButton3 = (RadioButton) holder.mainView.findViewById(R.id.radioButton3);
        final RadioButton radioButton4 = (RadioButton) holder.mainView.findViewById(R.id.radioButton4);
        final RadioButton radioButton5 = (RadioButton) holder.mainView.findViewById(R.id.radioButton5);

        if (getColour(card) == COLOURLESS) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardColourless)));
        } else if (getColour(card) == WHITE) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardWhite)));
        } else if (getColour(card) == BLUE) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardBlue)));
        } else if (getColour(card) == BLACK) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardBlack)));
        } else if (getColour(card) == RED) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardRed)));
        } else if (getColour(card) == GREEN) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardGreen)));
        } else if (getColour(card) == GOLD) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardGold)));
        } else if (getColour(card) == ARTIFACT) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardArtifact)));
        } else if (getColour(card) == LAND) {
            CompoundButtonCompat.setButtonTintList(cardColour, ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.cardLand)));
        }

        cardNameTextView.setText(cardName);

        cardSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.findViewById(checkedId) != null && group.findViewById(checkedId).isPressed()) {
                    Log.d("scottdebug","Human Interferance");
                    int quantity = 0;
                    boolean foil = false;
                    switch (checkedId) {
                        case R.id.radioButton1:
                            quantity = 1;
                            break;
                        case R.id.radioButton2:
                            quantity = 2;
                            break;
                        case R.id.radioButton3:
                            quantity = 3;
                            break;
                        case R.id.radioButton4:
                            quantity = 4;
                            break;
                        case R.id.radioButton5:
                            quantity = 0;
                            cardSelection.clearCheck();
                            break;
                    }
                    collectionHelper.setCardQuantity(setCode, cardName, quantity, foil);
                    card.setQuantity(quantity);
                }
            }
        });

        cardSelection.clearCheck();
        switch (card.getQuantity()) {
            case 0:
                cardSelection.clearCheck();
                break;
            case 1:
                radioButton1.setChecked(true);
                break;
            case 2:
                radioButton2.setChecked(true);
                break;
            case 3:
                radioButton3.setChecked(true);
                break;
            case 4:
                radioButton4.setChecked(true);
                break;
            default:
                radioButton5.setChecked(true);
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cardsDataset.size();
    }

    public void addCard(CardObj cardObj) {
        cardObj.setQuantity(collectionHelper.getCardQuantity(setCode,cardObj.getName()));
        List<String> ignoreCardsNamed = new ArrayList<>(Arrays.asList("Plains", "Island", "Swamp", "Mountain", "Forest"));
        if (!ignoreCardsNamed.contains(cardObj.getName())) {
            try {
                Integer.parseInt(cardObj.getNumber());
                cardsDataset.add(cardObj);
            } catch (NumberFormatException e) {
                if (cardObj.getNumber().equals("")) {
                    cardsDataset.add(cardObj);
                } else {
                    String[] alphanumeric = cardObj.getNumber().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    if (alphanumeric[1].equals("a")) {
                        cardsDataset.add(cardObj);
                    }
                }
            }
        }
    }

    public void addAllCards(List<CardObj> cards) {
        for (CardObj card : cards) {
            addCard(card);
        }
    }
}