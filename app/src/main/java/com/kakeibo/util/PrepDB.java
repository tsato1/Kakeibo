package com.kakeibo.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kakeibo.KkbCategory;
import com.kakeibo.R;
import com.kakeibo.db.CategoriesDBAdapter;
import com.kakeibo.db.CategoriesLanDBAdapter;

import java.util.ArrayList;

public class PrepDB {
    public static void initCategoriesTable(SQLiteDatabase db) {
        ArrayList<KkbCategory> list = new ArrayList<>();

        KkbCategory income = new KkbCategory();
        income.code = 0;
        income.drawable = R.drawable.ic_category_income;
        income.location = 0;
        income.en = "INCOME";
        income.fr = "REV.";
        income.it = "ENTR.";
        income.jp = "収入";
        income.es = "INGRE";
        list.add(income);

        KkbCategory commodity = new KkbCategory();
        commodity.code = 1;
        commodity.drawable = R.drawable.ic_category_comm;
        commodity.location = 1;
        commodity.en = "COMM";
        commodity.fr = "ACHAT.";
        commodity.it = "ACQ.";
        commodity.jp = "生活雑貨";
        commodity.es = "MERCAN";
        list.add(commodity);

        KkbCategory meal = new KkbCategory();
        meal.code = 2;
        meal.drawable = R.drawable.ic_category_meal;
        meal.location = 2;
        meal.en = "MEAL";
        meal.fr = "ALIM.";
        meal.it = "ALIMEN.";
        meal.jp = "食事";
        meal.es = "COMIDA";
        list.add(meal);

        KkbCategory util = new KkbCategory();
        util.code = 3;
        util.drawable = R.drawable.ic_category_util;
        util.location = 3;
        util.en = "UTIL";
        util.fr = "FACTUR";
        util.it = "UTENZE";
        util.jp = "水道光熱";
        util.es = "UTIL";
        list.add(util);

        KkbCategory health = new KkbCategory();
        health.code = 4;
        health.drawable = R.drawable.ic_category_health;
        health.location = 4;
        health.en = "HEALTH";
        health.fr = "SANTÉ";
        health.it = "SALUTE";
        health.jp = "健康";
        health.es = "SALUD";
        list.add(health);

        KkbCategory edu = new KkbCategory();
        edu.code = 5;
        edu.drawable = R.drawable.ic_category_edu;
        edu.location = 5;
        edu.en = "EDU";
        edu.fr = "ETUDE";
        edu.it = "EDUCAZ";
        edu.jp = "教育";
        edu.es = "EDU";
        list.add(edu);

        KkbCategory cloth = new KkbCategory();
        cloth.code = 6;
        cloth.drawable = R.drawable.ic_category_cloth;
        cloth.location = 6;
        cloth.en = "CLOTH";
        cloth.fr = "VETEM.";
        cloth.it = "ABBIG.";
        cloth.jp = "衣服";
        cloth.es = "VESTID";
        list.add(cloth);

        KkbCategory trans = new KkbCategory();
        trans.code = 7;
        trans.drawable = R.drawable.ic_category_trans;
        trans.location = 7;
        trans.en = "TRANS";
        trans.fr = "TRANS.";
        trans.it = "TRASP.";
        trans.jp = "交通";
        trans.es = "TRANS";
        list.add(trans);

        KkbCategory ent = new KkbCategory();
        ent.code = 8;
        ent.drawable = R.drawable.ic_category_ent;
        ent.location = 12;
        ent.en = "ENT";
        ent.fr = "AMUSE.";
        ent.it = "DIVERT.";
        ent.jp = "娯楽";
        ent.es = "ENTRET";
        list.add(ent);

        KkbCategory ins = new KkbCategory();
        ins.code=9;
        ins.drawable = R.drawable.ic_category_ins;
        ins.location = 13;
        ins.en = "INS";
        ins.fr = "EPARG.";
        ins.it = "RISP.";
        ins.jp = "保険";
        ins.es = "SEGURO";
        list.add(ins);

        KkbCategory tax = new KkbCategory();
        tax.code=10;
        tax.drawable = R.drawable.ic_category_tax;
        tax.location = 14;
        tax.en = "TAX";
        tax.fr = "TAXE";
        tax.it = "TASSE";
        tax.jp = "税金";
        tax.es = "IMPUES";
        list.add(tax);

        KkbCategory other = new KkbCategory();
        other.code=11;
        other.drawable = R.drawable.ic_category_other;
        other.location = 15;
        other.en = "OTHER";
        other.fr = "AUTRE";
        other.it = "ALTRO";
        other.jp = "他";
        other.es = "OTROS";
        list.add(other);

        KkbCategory pet = new KkbCategory();
        pet.code = 12;
        pet.drawable = R.drawable.ic_category_pet;
        pet.location = 8;
        pet.en = "PET";
        pet.fr = "ANIM.C.";
        pet.it = "ANIM.D.";
        pet.jp = "ペット";
        pet.es = "MSCTA";
        list.add(pet);

        KkbCategory social = new KkbCategory();
        social.code = 13;
        social.drawable = R.drawable.ic_category_social;
        social.location = 9;
        social.en = "SOCIAL";
        social.fr = "SOCIAL";
        social.it = "SOCIAL";
        social.jp = "交際";
        social.es = "SOCIAL";
        list.add(social);

        KkbCategory cosme = new KkbCategory();
        cosme.code=14;
        cosme.drawable = R.drawable.ic_category_cosme;
        cosme.location = 10;
        cosme.en = "COSME";
        cosme.fr = "COSMÉ";
        cosme.it = "COSME";
        cosme.jp = "美容";
        cosme.es = "COSME";
        list.add(cosme);

        KkbCategory housing = new KkbCategory();
        housing.code = 15;
        housing.drawable = R.drawable.ic_category_housing;
        housing.location = 11;
        housing.en = "HOUSNG";
        housing.fr = "LOGMNT";
        housing.it = "ABITAT.";
        housing.jp = "居住";
        housing.es = "VVENDA";
        list.add(housing);

        for (KkbCategory category: list) {
            ContentValues values = new ContentValues();
            values.put(CategoriesDBAdapter.COL_CODE, category.getCode());
            values.put(CategoriesDBAdapter.COL_NAME, "");
            values.put(CategoriesDBAdapter.COL_COLOR, 0);
            values.put(CategoriesDBAdapter.COL_DRAWABLE, category.getDrawable());
            values.put(CategoriesDBAdapter.COL_LOCATION, category.getLocation());
            values.put(CategoriesDBAdapter.COL_SUB_CATEGORIES, 0);
            values.put(CategoriesDBAdapter.COL_DESC, "");
            values.put(CategoriesDBAdapter.COL_SAVED_DATE, "");
            db.insertOrThrow(CategoriesDBAdapter.TABLE_CATEGORY, null, values);

            ContentValues values2 = new ContentValues();
            values2.put(CategoriesLanDBAdapter.COL_CODE, category.getCode());
            values2.put(CategoriesLanDBAdapter.COL_NAME, "");
            values2.put(CategoriesLanDBAdapter.COL_EN, category.getEN());
            values2.put(CategoriesLanDBAdapter.COL_ES, category.getES());
            values2.put(CategoriesLanDBAdapter.COL_FR, category.getFR());
            values2.put(CategoriesLanDBAdapter.COL_IT, category.getIT());
            values2.put(CategoriesLanDBAdapter.COL_JA, category.getJA());
            values2.put(CategoriesLanDBAdapter.COL_SAVED_DATE, "");
            db.insertOrThrow(CategoriesLanDBAdapter.TABLE_CATEGORY_LAN, null, values2);
        }
    }

    public static void addLangsToCategoriesTable(SQLiteDatabase db) {
        ArrayList<KkbCategory> list = new ArrayList<>();

        KkbCategory income = new KkbCategory();
        income.code = 0;
        income.drawable = R.drawable.ic_category_income;
        income.location = 0;
        income.hi = "आय";
        income.in = ""; //todo start from here
        list.add(income);

        KkbCategory commodity = new KkbCategory();
        commodity.code = 1;
        commodity.drawable = R.drawable.ic_category_comm;
        commodity.location = 1;
        commodity.hi = "वस्तु";
        list.add(commodity);

        KkbCategory meal = new KkbCategory();
        meal.code = 2;
        meal.drawable = R.drawable.ic_category_meal;
        meal.location = 2;
        meal.hi = "भोजन";
        list.add(meal);

        KkbCategory util = new KkbCategory();
        util.code = 3;
        util.drawable = R.drawable.ic_category_util;
        util.location = 3;
        util.hi = "उपयोगिता";
        list.add(util);

        KkbCategory health = new KkbCategory();
        health.code = 4;
        health.drawable = R.drawable.ic_category_health;
        health.location = 4;
        health.hi = "स्वास्थ्य";
        list.add(health);

        KkbCategory edu = new KkbCategory();
        edu.code = 5;
        edu.drawable = R.drawable.ic_category_edu;
        edu.location = 5;
        edu.hi = "शिक्षा";
        list.add(edu);

        KkbCategory cloth = new KkbCategory();
        cloth.code = 6;
        cloth.drawable = R.drawable.ic_category_cloth;
        cloth.location = 6;
        cloth.hi = "कपड़ा";
        list.add(cloth);

        KkbCategory trans = new KkbCategory();
        trans.code = 7;
        trans.drawable = R.drawable.ic_category_trans;
        trans.location = 7;
        trans.hi = "परिवहन";
        list.add(trans);

        KkbCategory ent = new KkbCategory();
        ent.code = 8;
        ent.drawable = R.drawable.ic_category_ent;
        ent.location = 12;
        ent.hi = "मनोरंजन";
        list.add(ent);

        KkbCategory ins = new KkbCategory();
        ins.code=9;
        ins.drawable = R.drawable.ic_category_ins;
        ins.location = 13;
        ins.hi = "बीमा";
        list.add(ins);

        KkbCategory tax = new KkbCategory();
        tax.code=10;
        tax.drawable = R.drawable.ic_category_tax;
        tax.location = 14;
        tax.hi = "कर";
        list.add(tax);

        KkbCategory other = new KkbCategory();
        other.code=11;
        other.drawable = R.drawable.ic_category_other;
        other.location = 15;
        other.hi = "अन्य";
        list.add(other);

        KkbCategory pet = new KkbCategory();
        pet.code = 12;
        pet.drawable = R.drawable.ic_category_pet;
        pet.location = 8;
        pet.hi = "पालतू";
        list.add(pet);

        KkbCategory social = new KkbCategory();
        social.code = 13;
        social.drawable = R.drawable.ic_category_social;
        social.location = 9;
        social.hi = "सामाजिक";
        list.add(social);

        KkbCategory cosme = new KkbCategory();
        cosme.code=14;
        cosme.drawable = R.drawable.ic_category_cosme;
        cosme.location = 10;
        cosme.hi = "अंगराग";
        list.add(cosme);

        KkbCategory housing = new KkbCategory();
        housing.code = 15;
        housing.drawable = R.drawable.ic_category_housing;
        housing.location = 11;
        housing.hi = "आवास";
        list.add(housing);

        for (KkbCategory category: list) {
            ContentValues values = new ContentValues();
            values.put(CategoriesLanDBAdapter.COL_CODE, category.getCode());
            values.put(CategoriesLanDBAdapter.COL_NAME, "");
            values.put(CategoriesLanDBAdapter.COL_HI, category.getHI());
            values.put(CategoriesLanDBAdapter.COL_SAVED_DATE, "");
            db.insertOrThrow(CategoriesLanDBAdapter.TABLE_CATEGORY_LAN, null, values);
        }
    }
}
