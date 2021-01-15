//package com.kakeibo.ui.search;
//
//public class Card {
//    /*** for search ***/
//    final static int TYPE_DATE_RANGE = 0;
//    final static int TYPE_AMOUNT_RANGE = 1;
//    final static int TYPE_CATEGORY = 2;
//    final static int TYPE_MEMO = 3;
//
//    int type;
//    int data;
//
//    Card(int type, int data) {
//        this.type = type;
//        this.data = data;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        boolean isSame = false;
//
//        if (object instanceof Card) {
//            isSame = this.type == ((Card) object).type;
//        }
//
//        return isSame;
//    }
//}
