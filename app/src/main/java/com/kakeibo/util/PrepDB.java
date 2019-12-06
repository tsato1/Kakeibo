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
        income.ar = "الإيرادات";
        income.hi = "आय";
        income.in = "PENDAPATAN";
        income.ko = "수입";
        income.pl = "DOCHÓD";
        income.pt = "RENDA";
        income.ru = "доход";
        income.tr = "GELİR";
        list.add(income);

        KkbCategory commodity = new KkbCategory();
        commodity.code = 1;
        commodity.drawable = R.drawable.ic_category_comm;
        commodity.location = 1;
        commodity.ar = "السلع";
        commodity.hi = "वस्तु";
        commodity.in = "KOMODITI";
        commodity.ko = "상품";
        commodity.pl = "TOWAR";
        commodity.pt = "MERCADORIA";
        commodity.ru = "товар";
        commodity.tr = "EMTİA";
        list.add(commodity);

        KkbCategory meal = new KkbCategory();
        meal.code = 2;
        meal.drawable = R.drawable.ic_category_meal;
        meal.location = 2;
        meal.ar = "وجبة";
        meal.hi = "भोजन";
        meal.in = "MAKAN";
        meal.ko = "식사";
        meal.pl = "POSIŁEK";
        meal.pt = "REFEIÇÃO";
        meal.ru = "еда";
        meal.tr = "YEMEK";
        list.add(meal);

        KkbCategory util = new KkbCategory();
        util.code = 3;
        util.drawable = R.drawable.ic_category_util;
        util.location = 3;
        util.ar = "خدمة";
        util.hi = "उपयोगिता";
        util.in = "UTIL";
        util.ko = "유용";
        util.pl = "UŻYTECZNOŚĆ";
        util.pt = "UTIL";
        util.ru = "Утилита";
        util.tr = "YARAR";
        list.add(util);

        KkbCategory health = new KkbCategory();
        health.code = 4;
        health.drawable = R.drawable.ic_category_health;
        health.location = 4;
        health.ar = "الصحة";
        health.hi = "स्वास्थ्य";
        health.in = "KESEHATAN";
        health.ko = "건강";
        health.pl = "ZDROWIE";
        health.pt = "SAÚDE";
        health.ru = "Здоровье";
        health.tr = "SAĞLIK";
        list.add(health);

        KkbCategory edu = new KkbCategory();
        edu.code = 5;
        edu.drawable = R.drawable.ic_category_edu;
        edu.location = 5;
        edu.ar = "التعليم";
        edu.hi = "शिक्षा";
        edu.in = "PENDIDIKAN";
        edu.ko = "교육";
        edu.pl = "EDUKACJA";
        edu.pt = "EDUCAÇÃO";
        edu.ru = "образование";
        edu.tr = "EĞİTİM";
        list.add(edu);

        KkbCategory cloth = new KkbCategory();
        cloth.code = 6;
        cloth.drawable = R.drawable.ic_category_cloth;
        cloth.location = 6;
        cloth.ar = "ملابس";
        cloth.hi = "कपड़ा";
        cloth.in = "PAKAIAN";
        cloth.ko = "의류";
        cloth.pl = "ODZIEŻ";
        cloth.pt = "ROUPAS";
        cloth.ru = "Одежда";
        cloth.tr = "GİYİM";
        list.add(cloth);

        KkbCategory trans = new KkbCategory();
        trans.code = 7;
        trans.drawable = R.drawable.ic_category_trans;
        trans.location = 7;
        trans.ar = "نقل";
        trans.hi = "परिवहन";
        trans.in = "ANGKUTAN";
        trans.ko = "교통";
        trans.pl = "TRANSPORT";
        trans.pt = "TRANSPORTE";
        trans.ru = "Транспорт";
        trans.tr = "ULAŞIM";
        list.add(trans);

        KkbCategory ent = new KkbCategory();
        ent.code = 8;
        ent.drawable = R.drawable.ic_category_ent;
        ent.location = 12;
        ent.ar = "تسلية";
        ent.hi = "मनोरंजन";
        ent.in = "HIBURAN";
        ent.ko = "환대";
        ent.pl = "ZABAWA";
        ent.pt = "ENTRETENIMENTO";
        ent.ru = "Развлечения";
        ent.tr = "EĞLENCE";
        list.add(ent);

        KkbCategory ins = new KkbCategory();
        ins.code=9;
        ins.drawable = R.drawable.ic_category_ins;
        ins.location = 13;
        ins.ar = "تأمين";
        ins.hi = "बीमा";
        ins.in = "ASURANSI";
        ins.ko = "보험";
        ins.pl = "UBEZPIECZENIE";
        ins.pt = "SEGURO";
        ins.ru = "страхование";
        ins.tr = "SİGORTA";
        list.add(ins);

        KkbCategory tax = new KkbCategory();
        tax.code=10;
        tax.drawable = R.drawable.ic_category_tax;
        tax.location = 14;
        tax.ar = "ضريبة";
        tax.hi = "कर";
        tax.in = "PAJAK";
        tax.ko = "세";
        tax.pl = "PODATEK";
        tax.pt = "IMPOSTO";
        tax.ru = "налог";
        tax.tr = "VERGİ";
        list.add(tax);

        KkbCategory other = new KkbCategory();
        other.code=11;
        other.drawable = R.drawable.ic_category_other;
        other.location = 15;
        other.ar = "آخر";
        other.hi = "अन्य";
        other.in = "LAIN";
        other.ko = "다른";
        other.pl = "INNY";
        other.pt = "OUTRO";
        other.ru = "Другие";
        other.tr = "DİĞER";
        list.add(other);

        KkbCategory pet = new KkbCategory();
        pet.code = 12;
        pet.drawable = R.drawable.ic_category_pet;
        pet.location = 8;
        pet.ar = "حيوان اليف";
        pet.hi = "पालतू";
        pet.in = "MEMBELAI";
        pet.ko = "착한 애";
        pet.pl = "ZWIERZĘ DOMOWE";
        pet.pt = "ANIMAL";
        pet.ru = "домашнее животное";
        pet.tr = "EVCİL HAYVAN";
        list.add(pet);

        KkbCategory social = new KkbCategory();
        social.code = 13;
        social.drawable = R.drawable.ic_category_social;
        social.location = 9;
        social.ar = "اجتماعي";
        social.hi = "सामाजिक";
        social.in = "SOSIAL";
        social.ko = "사회적인";
        social.pl = "SPOŁECZNY";
        social.pt = "SOCIAL";
        social.ru = "Социальное";
        social.tr = "SOSYAL";
        list.add(social);

        KkbCategory cosme = new KkbCategory();
        cosme.code=14;
        cosme.drawable = R.drawable.ic_category_cosme;
        cosme.location = 10;
        cosme.ar = "كوزمي";
        cosme.hi = "अंगराग";
        cosme.in = "COSME";
        cosme.ko = "코스 메";
        cosme.pl = "COSME";
        cosme.pt = "COSME";
        cosme.ru = "Косме";
        cosme.tr = "COSME";
        list.add(cosme);

        KkbCategory housing = new KkbCategory();
        housing.code = 15;
        housing.drawable = R.drawable.ic_category_housing;
        housing.location = 11;
        housing.ar = "إسكان";
        housing.hi = "आवास";
        housing.in = "PERUMAHAN";
        housing.ko = "주택";
        housing.pl = "MIESZKANIOWY";
        housing.pt = "HABITAÇÃO";
        housing.ru = "Корпус";
        housing.tr = "KONUT";
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
