package com.kakeibo;

public class Card {
    public final static int TYPE_DATE_RANGE = 0;
    public final static int TYPE_AMOUNT_RANGE = 1;
    public final static int TYPE_CATEGORY = 2;
    public final static int TYPE_MEMO = 3;

    int type;
    int data;

    Card(int type, int data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Card) {
            sameSame = this.type == ((Card) object).type;
        }

        return sameSame;
    }
    // todo hash function needed?
}
