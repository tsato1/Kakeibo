package com.kakeibo.core.data.constants

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.OnConflictStrategy
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.BuildConfig
import com.kakeibo.R
import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.data.local.entities.CategoryEntity
//import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.util.UtilCategory
import java.util.*

object PrepDB {

    fun initCategoriesTable(db: SupportSQLiteDatabase) {
        val list = ArrayList<TmpCategory>()
        val income = TmpCategory()
        income.code = 0
        income.drawable = R.drawable.ic_category_income
        income.location = 0
        income.eng = "INCOME"
        income.fra = "REV."
        income.ita = "ENTR."
        income.jpn = "収入"
        income.spa = "INGRE"
        list.add(income)
        val commodity = TmpCategory()
        commodity.code = 1
        commodity.drawable = R.drawable.ic_category_comm
        commodity.location = 1
        commodity.eng = "COMM"
        commodity.fra = "ACHAT."
        commodity.ita = "ACQ."
        commodity.jpn = "生活雑貨"
        commodity.spa = "MERCAN"
        list.add(commodity)
        val meal = TmpCategory()
        meal.code = 2
        meal.drawable = R.drawable.ic_category_meal
        meal.location = 2
        meal.eng = "MEAL"
        meal.fra = "ALIM."
        meal.ita = "ALIMEN."
        meal.jpn = "食事"
        meal.spa = "COMIDA"
        list.add(meal)
        val util = TmpCategory()
        util.code = 3
        util.drawable = R.drawable.ic_category_util
        util.location = 3
        util.eng = "UTIL"
        util.fra = "FACTUR"
        util.ita = "UTENZE"
        util.jpn = "水道光熱"
        util.spa = "UTIL"
        list.add(util)
        val health = TmpCategory()
        health.code = 4
        health.drawable = R.drawable.ic_category_health
        health.location = 4
        health.eng = "HEALTH"
        health.fra = "SANTÉ"
        health.ita = "SALUTE"
        health.jpn = "健康"
        health.spa = "SALUD"
        list.add(health)
        val edu = TmpCategory()
        edu.code = 5
        edu.drawable = R.drawable.ic_category_edu
        edu.location = 5
        edu.eng = "EDU"
        edu.fra = "ETUDE"
        edu.ita = "EDUCAZ"
        edu.jpn = "教育"
        edu.spa = "EDU"
        list.add(edu)
        val cloth = TmpCategory()
        cloth.code = 6
        cloth.drawable = R.drawable.ic_category_cloth
        cloth.location = 6
        cloth.eng = "CLOTH"
        cloth.fra = "VETEM."
        cloth.ita = "ABBIG."
        cloth.jpn = "衣服"
        cloth.spa = "VESTID"
        list.add(cloth)
        val trans = TmpCategory()
        trans.code = 7
        trans.drawable = R.drawable.ic_category_trans
        trans.location = 7
        trans.eng = "TRANS"
        trans.fra = "TRANS."
        trans.ita = "TRASP."
        trans.jpn = "交通"
        trans.spa = "TRANS"
        list.add(trans)
        val ent = TmpCategory()
        ent.code = 8
        ent.drawable = R.drawable.ic_category_ent
        ent.location = 12
        ent.eng = "ENT"
        ent.fra = "AMUSE."
        ent.ita = "DIVERT."
        ent.jpn = "娯楽"
        ent.spa = "ENTRET"
        list.add(ent)
        val ins = TmpCategory()
        ins.code = 9
        ins.drawable = R.drawable.ic_category_ins
        ins.location = 13
        ins.eng = "INS"
        ins.fra = "EPARG."
        ins.ita = "RISP."
        ins.jpn = "保険"
        ins.spa = "SEGURO"
        list.add(ins)
        val tax = TmpCategory()
        tax.code = 10
        tax.drawable = R.drawable.ic_category_tax
        tax.location = 14
        tax.eng = "TAX"
        tax.fra = "TAXE"
        tax.ita = "TASSE"
        tax.jpn = "税金"
        tax.spa = "IMPUES"
        list.add(tax)
        val other = TmpCategory()
        other.code = 11
        other.drawable = R.drawable.ic_category_other
        other.location = 15
        other.eng = "OTHER"
        other.fra = "AUTRE"
        other.ita = "ALTRO"
        other.jpn = "他"
        other.spa = "OTROS"
        list.add(other)
        val pet = TmpCategory()
        pet.code = 12
        pet.drawable = R.drawable.ic_category_pet
        pet.location = 8
        pet.eng = "PET"
        pet.fra = "ANIM.C."
        pet.ita = "ANIM.D."
        pet.jpn = "ペット"
        pet.spa = "MSCTA"
        list.add(pet)
        val social = TmpCategory()
        social.code = 13
        social.drawable = R.drawable.ic_category_social
        social.location = 9
        social.eng = "SOCIAL"
        social.fra = "SOCIAL"
        social.ita = "SOCIAL"
        social.jpn = "交際"
        social.spa = "SOCIAL"
        list.add(social)
        val cosme = TmpCategory()
        cosme.code = 14
        cosme.drawable = R.drawable.ic_category_cosme
        cosme.location = 10
        cosme.eng = "COSME"
        cosme.fra = "COSMÉ"
        cosme.ita = "COSME"
        cosme.jpn = "美容"
        cosme.spa = "COSME"
        list.add(cosme)
        val housing = TmpCategory()
        housing.code = 15
        housing.drawable = R.drawable.ic_category_housing
        housing.location = 11
        housing.eng = "HOUSNG"
        housing.fra = "LOGMNT"
        housing.ita = "ABITAT."
        housing.jpn = "居住"
        housing.spa = "VVENDA"
        list.add(housing)
        for (category in list) {
            val values = ContentValues()
            values.put(ConstCategoryDB.COL_CODE, category.code)
            values.put(ConstCategoryDB.COL_NAME, "")
            values.put(ConstCategoryDB.COL_COLOR, 0)
            values.put(ConstCategoryDB.COL_DRAWABLE, category.drawable)
            values.put(ConstCategoryDB.COL_LOCATION, category.location)
            values.put(ConstCategoryDB.COL_SUB_CATEGORIES, 0)
            values.put(ConstCategoryDB.COL_DESCRIPTION, "")
            values.put(ConstCategoryDB.COL_SAVED_DATE, "")
            //            db.insertOrThrow(CategoryDBAdapter.TABLE_NAME, null, values);
            db.insert(ConstCategoryDB.TABLE_NAME_OLD, OnConflictStrategy.REPLACE, values)
            val values2 = ContentValues()
            values2.put(ConstCategoryLanDB.COL_CODE, category.code)
            values2.put(ConstCategoryLanDB.COL_NAME, "")
            values2.put(ConstCategoryLanDB.COL_EN, category.eng)
            values2.put(ConstCategoryLanDB.COL_ES, category.spa)
            values2.put(ConstCategoryLanDB.COL_FR, category.fra)
            values2.put(ConstCategoryLanDB.COL_IT, category.ita)
            values2.put(ConstCategoryLanDB.COL_JA, category.jpn)
            values2.put(ConstCategoryLanDB.COL_SAVED_DATE, "")
            //            db.insertOrThrow(CategoryLanDBAdapter.TABLE_NAME, null, values2);
            db.insert(ConstCategoryLanDB.TABLE_NAME, OnConflictStrategy.REPLACE, values2)
        }
    }

    fun initCategoriesTableRevised(db: SupportSQLiteDatabase) {
        val list = ArrayList<TmpCategory>()
        val income = TmpCategory()
        income.code = 0
        income.color = 1
        income.drawable = R.drawable.ic_category_income
        income.location = 0
        income.eng = "INCOME"
        income.fra = "REV."
        income.ita = "ENTR."
        income.jpn = "収入"
        income.spa = "INGRE"
        income.ara = "الإيرادات"
        income.hin = "आय"
        income.ind = "PENDAPATAN"
        income.kor = "수입"
        income.pol = "DOCHÓD"
        income.por = "RENDA"
        income.rus = "доход"
        income.tur = "GELİR"
        income.vie = "THU NHẬP"
        income.hans = "收入"
        income.hant = "收入"
        list.add(income)
        val commodity = TmpCategory()
        commodity.code = 1
        commodity.color = 0
        commodity.drawable = R.drawable.ic_category_comm
        commodity.location = 1
        commodity.eng = "COMM"
        commodity.fra = "ACHAT."
        commodity.ita = "ACQ."
        commodity.jpn = "生活雑貨"
        commodity.spa = "MERCAN"
        commodity.ara = "السلع"
        commodity.hin = "वस्तु"
        commodity.ind = "KOMODITI"
        commodity.kor = "상품"
        commodity.pol = "TOWAR"
        commodity.por = "MERCADORIA"
        commodity.rus = "товар"
        commodity.tur = "EMTİA"
        commodity.vie = "HÀNG HÓA"
        commodity.hans = "商品"
        commodity.hant = "商品"
        list.add(commodity)
        val meal = TmpCategory()
        meal.code = 2
        meal.color = 0
        meal.drawable = R.drawable.ic_category_meal
        meal.location = 2
        meal.eng = "MEAL"
        meal.fra = "ALIM."
        meal.ita = "ALIMEN."
        meal.jpn = "食事"
        meal.spa = "COMIDA"
        meal.ara = "وجبة"
        meal.hin = "भोजन"
        meal.ind = "MAKAN"
        meal.kor = "식사"
        meal.pol = "POSIŁEK"
        meal.por = "REFEIÇÃO"
        meal.rus = "еда"
        meal.tur = "YEMEK"
        meal.vie = "BỮA ĂN"
        meal.hans = "膳食"
        meal.hant = "膳食"
        list.add(meal)
        val util = TmpCategory()
        util.code = 3
        util.color = 0
        util.drawable = R.drawable.ic_category_util
        util.location = 3
        util.eng = "UTIL"
        util.fra = "FACTUR"
        util.ita = "UTENZE"
        util.jpn = "水道光熱"
        util.spa = "UTIL"
        util.ara = "خدمة"
        util.hin = "उपयोगिता"
        util.ind = "UTIL"
        util.kor = "유용"
        util.pol = "UŻYTECZNOŚĆ"
        util.por = "UTIL"
        util.rus = "Утилита"
        util.tur = "YARAR"
        util.vie = "TIỆN ÍCH"
        util.hans = "效用"
        util.hant = "效用"
        list.add(util)
        val health = TmpCategory()
        health.code = 4
        health.color = 0
        health.drawable = R.drawable.ic_category_health
        health.location = 4
        health.eng = "HEALTH"
        health.fra = "SANTÉ"
        health.ita = "SALUTE"
        health.jpn = "健康"
        health.spa = "SALUD"
        health.ara = "الصحة"
        health.hin = "स्वास्थ्य"
        health.ind = "KESEHATAN"
        health.kor = "건강"
        health.pol = "ZDROWIE"
        health.por = "SAÚDE"
        health.rus = "Здоровье"
        health.tur = "SAĞLIK"
        health.vie = "SỨC KHỎE"
        health.hans = "健康"
        health.hant = "健康"
        list.add(health)
        val edu = TmpCategory()
        edu.code = 5
        edu.color = 0
        edu.drawable = R.drawable.ic_category_edu
        edu.location = 5
        edu.eng = "EDU"
        edu.fra = "ETUDE"
        edu.ita = "EDUCAZ"
        edu.jpn = "教育"
        edu.spa = "EDU"
        edu.ara = "التعليم"
        edu.hin = "शिक्षा"
        edu.ind = "PENDIDIKAN"
        edu.kor = "교육"
        edu.pol = "EDUKACJA"
        edu.por = "EDUCAÇÃO"
        edu.rus = "образование"
        edu.tur = "EĞİTİM"
        edu.vie = "GIÁO DỤC"
        edu.hans = "教育"
        edu.hant = "教育"
        list.add(edu)
        val cloth = TmpCategory()
        cloth.code = 6
        cloth.color = 0
        cloth.drawable = R.drawable.ic_category_cloth
        cloth.location = 6
        cloth.eng = "CLOTH"
        cloth.fra = "VETEM."
        cloth.ita = "ABBIG."
        cloth.jpn = "衣服"
        cloth.spa = "VESTID"
        cloth.ara = "ملابس"
        cloth.hin = "कपड़ा"
        cloth.ind = "PAKAIAN"
        cloth.kor = "의류"
        cloth.pol = "ODZIEŻ"
        cloth.por = "ROUPAS"
        cloth.rus = "Одежда"
        cloth.tur = "GİYİM"
        cloth.vie = "QUẦN ÁO"
        cloth.hans = "衣服"
        cloth.hant = "衣服"
        list.add(cloth)
        val trans = TmpCategory()
        trans.code = 7
        trans.color = 0
        trans.drawable = R.drawable.ic_category_trans
        trans.location = 7
        trans.eng = "TRANS"
        trans.fra = "TRANS."
        trans.ita = "TRASP."
        trans.jpn = "交通"
        trans.spa = "TRANS"
        trans.ara = "نقل"
        trans.hin = "परिवहन"
        trans.ind = "ANGKUTAN"
        trans.kor = "교통"
        trans.pol = "TRANSPORT"
        trans.por = "TRANSPORTE"
        trans.rus = "Транспорт"
        trans.tur = "ULAŞIM"
        trans.vie = "VẬN CHUYỂN"
        trans.hans = "运输"
        trans.hant = "運輸"
        list.add(trans)
        val ent = TmpCategory()
        ent.code = 8
        ent.color = 0
        ent.drawable = R.drawable.ic_category_ent
        ent.location = 12
        ent.eng = "ENT"
        ent.fra = "AMUSE."
        ent.ita = "DIVERT."
        ent.jpn = "娯楽"
        ent.spa = "ENTRET"
        ent.ara = "تسلية"
        ent.hin = "मनोरंजन"
        ent.ind = "HIBURAN"
        ent.kor = "환대"
        ent.pol = "ZABAWA"
        ent.por = "ENTRETENIMENTO"
        ent.rus = "Развлечения"
        ent.tur = "EĞLENCE"
        ent.vie = "SỰ GIẢI TRÍ"
        ent.hans = "娱乐"
        ent.hant = "娛樂"
        list.add(ent)
        val ins = TmpCategory()
        ins.code = 9
        ins.color = 0
        ins.drawable = R.drawable.ic_category_ins
        ins.location = 13
        ins.eng = "INS"
        ins.fra = "EPARG."
        ins.ita = "RISP."
        ins.jpn = "保険"
        ins.spa = "SEGURO"
        ins.ara = "تأمين"
        ins.hin = "बीमा"
        ins.ind = "ASURANSI"
        ins.kor = "보험"
        ins.pol = "UBEZPIECZENIE"
        ins.por = "SEGURO"
        ins.rus = "страхование"
        ins.tur = "SİGORTA"
        ins.vie = "BẢO HIỂM"
        ins.hans = "保险"
        ins.hant = "保險"
        list.add(ins)
        val tax = TmpCategory()
        tax.code = 10
        tax.color = 0
        tax.drawable = R.drawable.ic_category_tax
        tax.location = 14
        tax.eng = "TAX"
        tax.fra = "TAXE"
        tax.ita = "TASSE"
        tax.jpn = "税金"
        tax.spa = "IMPUES"
        tax.ara = "ضريبة"
        tax.hin = "कर"
        tax.ind = "PAJAK"
        tax.kor = "세"
        tax.pol = "PODATEK"
        tax.por = "IMPOSTO"
        tax.rus = "налог"
        tax.tur = "VERGİ"
        tax.vie = "THUẾ"
        tax.hans = "税"
        tax.hant = "稅"
        list.add(tax)
        val other = TmpCategory()
        other.code = 11
        other.color = 0
        other.drawable = R.drawable.ic_category_other
        other.location = 15
        other.eng = "OTHER"
        other.fra = "AUTRE"
        other.ita = "ALTRO"
        other.jpn = "他"
        other.spa = "OTROS"
        other.ara = "آخر"
        other.hin = "अन्य"
        other.ind = "LAIN"
        other.kor = "다른"
        other.pol = "INNY"
        other.por = "OUTRO"
        other.rus = "Другие"
        other.tur = "DİĞER"
        other.vie = "KHÁC"
        other.hans = "其他"
        other.hant = "其他"
        list.add(other)
        val pet = TmpCategory()
        pet.code = 12
        pet.color = 0
        pet.drawable = R.drawable.ic_category_pet
        pet.location = 8
        pet.eng = "PET"
        pet.fra = "ANIM.C."
        pet.ita = "ANIM.D."
        pet.jpn = "ペット"
        pet.spa = "MSCTA"
        pet.ara = "حيوان اليف"
        pet.hin = "पालतू"
        pet.ind = "MEMBELAI"
        pet.kor = "착한 애"
        pet.pol = "ZWIERZĘ DOMOWE"
        pet.por = "ANIMAL"
        pet.rus = "домашнее животное"
        pet.tur = "EVCİL HAYVAN"
        pet.vie = "VẬT NUÔI"
        pet.hans = "宠物"
        pet.hant = "寵物"
        list.add(pet)
        val social = TmpCategory()
        social.code = 13
        social.color = 0
        social.drawable = R.drawable.ic_category_social
        social.location = 9
        social.eng = "SOCIAL"
        social.fra = "SOCIAL"
        social.ita = "SOCIAL"
        social.jpn = "交際"
        social.spa = "SOCIAL"
        social.ara = "اجتماعي"
        social.hin = "सामाजिक"
        social.ind = "SOSIAL"
        social.kor = "사회적인"
        social.pol = "SPOŁECZNY"
        social.por = "SOCIAL"
        social.rus = "Социальное"
        social.tur = "SOSYAL"
        social.vie = "XÃ HỘI"
        social.hans = "社会的"
        social.hant = "社會的"
        list.add(social)
        val cosme = TmpCategory()
        cosme.code = 14
        cosme.color = 0
        cosme.drawable = R.drawable.ic_category_cosme
        cosme.location = 10
        cosme.eng = "COSME"
        cosme.fra = "COSMÉ"
        cosme.ita = "COSME"
        cosme.jpn = "美容"
        cosme.spa = "COSME"
        cosme.ara = "كوزمي"
        cosme.hin = "अंगराग"
        cosme.ind = "COSME"
        cosme.kor = "화장품"
        cosme.pol = "COSME"
        cosme.por = "COSME"
        cosme.rus = "Косме"
        cosme.tur = "COSME"
        cosme.vie = "MỸ PHẨM"
        cosme.hans = "化妆品"
        cosme.hant = "化妝品"
        list.add(cosme)
        val housing = TmpCategory()
        housing.code = 15
        housing.color = 0
        housing.drawable = R.drawable.ic_category_housing
        housing.location = 11
        housing.eng = "HOUSNG"
        housing.fra = "LOGMNT"
        housing.ita = "ABITAT."
        housing.jpn = "居住"
        housing.spa = "VVENDA"
        housing.ara = "إسكان"
        housing.hin = "आवास"
        housing.ind = "PERUMAHAN"
        housing.kor = "주택"
        housing.pol = "MIESZKANIOWY"
        housing.por = "HABITAÇÃO"
        housing.rus = "Корпус"
        housing.tur = "KONUT"
        housing.vie = "NHÀ Ở"
        housing.hans = "住房"
        housing.hant = "住房"
        list.add(housing)
        for (i in 0..15) {
            val values1 = ContentValues()
            values1.put(ConstCategoryDB.COL_CODE, list[i].code)
            values1.put(ConstCategoryDB.COL_COLOR, 0)
            values1.put(ConstCategoryDB.COL_SIGN, 0)
            values1.put(ConstCategoryDB.COL_DRAWABLE, list[i].drawable)
            values1.putNull(ConstCategoryDB.COL_IMAGE)
            values1.put(ConstCategoryDB.COL_PARENT, -1)
            values1.put(ConstCategoryDB.COL_DESCRIPTION, "")
            //            values1.put(CategoryDBAdapter.COL_VAL1, 0);
//            values1.put(CategoryDBAdapter.COL_VAL2, 0);
//            values1.put(CategoryDBAdapter.COL_VAL3, 0);
            values1.put(ConstCategoryDB.COL_SAVED_DATE, "")
            //            db.insertOrThrow(CategoryDBAdapter.TABLE_NAME, null, values1);
            db.insert(ConstCategoryDB.TABLE_NAME_OLD, OnConflictStrategy.REPLACE, values1)
            val values2 = ContentValues()
            values2.put(ConstCategoryLanDB.COL_CODE, list[i].code)
            values2.put(ConstCategoryLanDB.COL_ENG, list[i].eng)
            values2.put(ConstCategoryLanDB.COL_SPA, list[i].spa)
            values2.put(ConstCategoryLanDB.COL_FRA, list[i].fra)
            values2.put(ConstCategoryLanDB.COL_ITA, list[i].ita)
            values2.put(ConstCategoryLanDB.COL_JPN, list[i].jpn)
            values2.put(ConstCategoryLanDB.COL_ARA, list[i].ara)
            values2.put(ConstCategoryLanDB.COL_HIN, list[i].hin)
            values2.put(ConstCategoryLanDB.COL_IND, list[i].ind)
            values2.put(ConstCategoryLanDB.COL_KOR, list[i].kor)
            values2.put(ConstCategoryLanDB.COL_POL, list[i].pol)
            values2.put(ConstCategoryLanDB.COL_POR, list[i].por)
            values2.put(ConstCategoryLanDB.COL_RUS, list[i].rus)
            values2.put(ConstCategoryLanDB.COL_TUR, list[i].tur)
            values2.put(ConstCategoryLanDB.COL_VIE, list[i].vie)
            values2.put(ConstCategoryLanDB.COL_Hans, list[i].hans)
            values2.put(ConstCategoryLanDB.COL_Hant, list[i].hant)
            //            db.insertOrThrow(CategoryLanDBAdapter.TABLE_NAME, null, values2);
            db.insert(ConstCategoryLanDB.TABLE_NAME, OnConflictStrategy.REPLACE, values2)
            val values3 = ContentValues()
            values3.put(ConstCategoryDspDB.COL_LOCATION, list[i].location)
            values3.put(ConstCategoryDspDB.COL_CODE, list[i].code)
            //            db.insertOrThrow(CategoryDspDBAdapter.TABLE_NAME, null, values3);
            db.insert(ConstCategoryDspDB.TABLE_NAME_OLD, OnConflictStrategy.REPLACE, values3)
        }
    }

    fun addMoreCategories(db: SupportSQLiteDatabase) {
        /*** change color column of Income from 0 to 1  */
        val query = "UPDATE " + ConstCategoryDB.TABLE_NAME_OLD +
                " SET " + ConstCategoryDB.COL_COLOR + "=1" +
                " WHERE " + ConstCategoryDB.COL_CODE + "=0;"
        db.execSQL(query)
        val list = ArrayList<TmpCategory>()
        val bonus = TmpCategory()
        bonus.code = 16
        bonus.color = 1
        bonus.drawable = R.drawable.ic_category_bonus
        bonus.ara = "علاوة"
        bonus.eng = "EXTRA"
        bonus.spa = "EXTRA"
        bonus.fra = "SUPPLÉ."
        bonus.hin = "बक्शीश"
        bonus.ind = "TAMBAHAN"
        bonus.ita = "SUPPLE."
        bonus.jpn = "ボーナス"
        bonus.kor = "보너스"
        bonus.pol = "DODATKOWY"
        bonus.por = "ADICIONAL"
        bonus.rus = "допол"
        bonus.tur = "EK"
        bonus.vie = "THÊM"
        bonus.hans = "奖金"
        bonus.hant = "獎金"
        list.add(bonus)
        val allowance = TmpCategory()
        allowance.code = 17
        allowance.color = 1
        allowance.drawable = R.drawable.ic_category_allowance
        allowance.ara = "بدل"
        allowance.eng = "ALLOW"
        allowance.spa = "TOLER."
        allowance.fra = "ALLOC."
        allowance.hin = "भत्ता"
        allowance.ind = "TUNJANGAN"
        allowance.ita = "INDENNITA"
        allowance.jpn = "お小遣い"
        allowance.kor = "수당"
        allowance.pol = "DODATEK"
        allowance.por = "MESADA"
        allowance.rus = "РЕЗЕРВЫ"
        allowance.tur = "ÖDENEK"
        allowance.vie = "PHỤ CẤP"
        allowance.hans = "津贴"
        allowance.hant = "津貼"
        list.add(allowance)
        val inv = TmpCategory()
        inv.code = 18
        inv.color = 1
        inv.drawable = R.drawable.ic_category_in_inv
        inv.ara = "استثمار"
        inv.eng = "INV"
        inv.spa = "INV"
        inv.fra = "INV"
        inv.hin = "निवेश"
        inv.ind = "INV"
        inv.ita = "INV"
        inv.jpn = "投資"
        inv.kor = "투자"
        inv.pol = "INW"
        inv.por = "INV"
        inv.rus = "инвест"
        inv.tur = "YATIRIM"
        inv.vie = "ĐẦU TƯ"
        inv.hans = "投资"
        inv.hant = "投資"
        list.add(inv)
        val rent = TmpCategory()
        rent.code = 19
        rent.color = 1
        rent.drawable = R.drawable.ic_category_in_rent
        rent.ara = "تأجير"
        rent.eng = "RENT"
        rent.spa = "ALQUIL."
        rent.fra = "LOCAC."
        rent.hin = "किराए"
        rent.ind = "SEWA"
        rent.ita = "AFFIT."
        rent.jpn = "家賃"
        rent.kor = "임대"
        rent.pol = "CZYNSZ"
        rent.por = "ALUGAR"
        rent.rus = "аренда"
        rent.tur = "KIRA"
        rent.vie = "THUÊ"
        rent.hans = "房租"
        rent.hant = "房租"
        list.add(rent)
        val ex_exp = TmpCategory()
        ex_exp.code = 20
        ex_exp.color = 0
        ex_exp.drawable = R.drawable.ic_category_expense
        ex_exp.ara = "مصروف"
        ex_exp.eng = "EXPENS"
        ex_exp.spa = "GASTOS"
        ex_exp.fra = "FRAIS"
        ex_exp.hin = "व्यय"
        ex_exp.ind = "BIAYA"
        ex_exp.ita = "SPESE"
        ex_exp.jpn = "支出"
        ex_exp.kor = "비용"
        ex_exp.pol = "KOSZT"
        ex_exp.por = "DESPESA"
        ex_exp.rus = "РАСХОДЫ"
        ex_exp.tur = "GİDER"
        ex_exp.vie = "CHI PHÍ"
        ex_exp.hans = "费用"
        ex_exp.hant = "費用"
        list.add(ex_exp)
        val ex_tele = TmpCategory()
        ex_tele.code = 21
        ex_tele.color = 0
        ex_tele.drawable = R.drawable.ic_category_tele
        ex_tele.ara = "هاتف"
        ex_tele.eng = "TELE"
        ex_tele.spa = "TELÉ"
        ex_tele.fra = "TÉLÉ"
        ex_tele.hin = "फ़ोन"
        ex_tele.ind = "TELE"
        ex_tele.ita = "TELE"
        ex_tele.jpn = "通信"
        ex_tele.kor = "전화"
        ex_tele.pol = "TELE"
        ex_tele.por = "TELE"
        ex_tele.rus = "ТЕЛЕ"
        ex_tele.tur = "TELE"
        ex_tele.vie = "THOẠI"
        ex_tele.hans = "电讯"
        ex_tele.hant = "電訊"
        list.add(ex_tele)
        val ex_inv = TmpCategory()
        ex_inv.code = 22
        ex_inv.color = 0
        ex_inv.drawable = R.drawable.ic_category_ex_inv
        ex_inv.ara = "استثمار"
        ex_inv.eng = "INV"
        ex_inv.spa = "INV"
        ex_inv.fra = "INV"
        ex_inv.hin = "निवेश"
        ex_inv.ind = "INV"
        ex_inv.ita = "INV"
        ex_inv.jpn = "投資"
        ex_inv.kor = "투자"
        ex_inv.pol = "INW"
        ex_inv.por = "INV"
        ex_inv.rus = "инвест"
        ex_inv.tur = "YATIRIM"
        ex_inv.vie = "ĐẦU TƯ"
        ex_inv.hans = "投资"
        ex_inv.hant = "投資"
        list.add(ex_inv)
        val ex_rent = TmpCategory()
        ex_rent.code = 23
        ex_rent.color = 0
        ex_rent.drawable = R.drawable.ic_category_ex_rent
        ex_rent.ara = "تأجير"
        ex_rent.eng = "RENT"
        ex_rent.spa = "ALQUIL."
        ex_rent.fra = "LOCAC."
        ex_rent.hin = "किराए"
        ex_rent.ind = "SEWA"
        ex_rent.ita = "AFFIT."
        ex_rent.jpn = "家賃"
        ex_rent.kor = "임대"
        ex_rent.pol = "CZYNSZ"
        ex_rent.por = "ALUGAR"
        ex_rent.rus = "аренда"
        ex_rent.tur = "KIRA"
        ex_rent.vie = "THUÊ"
        ex_rent.hans = "房租"
        ex_rent.hant = "房租"
        list.add(ex_rent)
        for (c in list) {
            val values = ContentValues()
            values.put(ConstCategoryDB.COL_CODE, c.code)
            values.put(ConstCategoryDB.COL_COLOR, c.color)
            values.put(ConstCategoryDB.COL_DRAWABLE, c.drawable)
            values.putNull(ConstCategoryDB.COL_IMAGE)
            values.put(ConstCategoryDB.COL_DESCRIPTION, "")
            values.put(ConstCategoryDB.COL_SAVED_DATE, "")
            //            db.insertOrThrow(CategoryDBAdapter.TABLE_NAME, null, values);
            db.insert(ConstCategoryDB.TABLE_NAME_OLD, OnConflictStrategy.REPLACE, values)
            val values1 = ContentValues()
            values1.put(ConstCategoryLanDB.COL_CODE, c.code)
            values1.put(ConstCategoryLanDB.COL_ARA, c.ara)
            values1.put(ConstCategoryLanDB.COL_ENG, c.eng)
            values1.put(ConstCategoryLanDB.COL_SPA, c.spa)
            values1.put(ConstCategoryLanDB.COL_FRA, c.fra)
            values1.put(ConstCategoryLanDB.COL_HIN, c.hin)
            values1.put(ConstCategoryLanDB.COL_IND, c.ind)
            values1.put(ConstCategoryLanDB.COL_ITA, c.ita)
            values1.put(ConstCategoryLanDB.COL_JPN, c.jpn)
            values1.put(ConstCategoryLanDB.COL_KOR, c.kor)
            values1.put(ConstCategoryLanDB.COL_POL, c.pol)
            values1.put(ConstCategoryLanDB.COL_POR, c.por)
            values1.put(ConstCategoryLanDB.COL_RUS, c.rus)
            values1.put(ConstCategoryLanDB.COL_TUR, c.tur)
            values1.put(ConstCategoryLanDB.COL_VIE, c.vie)
            values1.put(ConstCategoryLanDB.COL_Hans, c.hans)
            values1.put(ConstCategoryLanDB.COL_Hant, c.hant)
            //            db.insertOrThrow(CategoryLanDBAdapter.TABLE_NAME, null, values1);
            db.insert(ConstCategoryLanDB.TABLE_NAME, OnConflictStrategy.REPLACE, values1)
        }
    }

    fun initCategoriesTable(db: SQLiteDatabase) {
        val list = ArrayList<TmpCategory>()
        val income = TmpCategory()
        income.code = 0
        income.drawable = R.drawable.ic_category_income
        income.location = 0
        income.eng = "INCOME"
        income.fra = "REV."
        income.ita = "ENTR."
        income.jpn = "収入"
        income.spa = "INGRE"
        list.add(income)
        val commodity = TmpCategory()
        commodity.code = 1
        commodity.drawable = R.drawable.ic_category_comm
        commodity.location = 1
        commodity.eng = "COMM"
        commodity.fra = "ACHAT."
        commodity.ita = "ACQ."
        commodity.jpn = "生活雑貨"
        commodity.spa = "MERCAN"
        list.add(commodity)
        val meal = TmpCategory()
        meal.code = 2
        meal.drawable = R.drawable.ic_category_meal
        meal.location = 2
        meal.eng = "MEAL"
        meal.fra = "ALIM."
        meal.ita = "ALIMEN."
        meal.jpn = "食事"
        meal.spa = "COMIDA"
        list.add(meal)
        val util = TmpCategory()
        util.code = 3
        util.drawable = R.drawable.ic_category_util
        util.location = 3
        util.eng = "UTIL"
        util.fra = "FACTUR"
        util.ita = "UTENZE"
        util.jpn = "水道光熱"
        util.spa = "UTIL"
        list.add(util)
        val health = TmpCategory()
        health.code = 4
        health.drawable = R.drawable.ic_category_health
        health.location = 4
        health.eng = "HEALTH"
        health.fra = "SANTÉ"
        health.ita = "SALUTE"
        health.jpn = "健康"
        health.spa = "SALUD"
        list.add(health)
        val edu = TmpCategory()
        edu.code = 5
        edu.drawable = R.drawable.ic_category_edu
        edu.location = 5
        edu.eng = "EDU"
        edu.fra = "ETUDE"
        edu.ita = "EDUCAZ"
        edu.jpn = "教育"
        edu.spa = "EDU"
        list.add(edu)
        val cloth = TmpCategory()
        cloth.code = 6
        cloth.drawable = R.drawable.ic_category_cloth
        cloth.location = 6
        cloth.eng = "CLOTH"
        cloth.fra = "VETEM."
        cloth.ita = "ABBIG."
        cloth.jpn = "衣服"
        cloth.spa = "VESTID"
        list.add(cloth)
        val trans = TmpCategory()
        trans.code = 7
        trans.drawable = R.drawable.ic_category_trans
        trans.location = 7
        trans.eng = "TRANS"
        trans.fra = "TRANS."
        trans.ita = "TRASP."
        trans.jpn = "交通"
        trans.spa = "TRANS"
        list.add(trans)
        val ent = TmpCategory()
        ent.code = 8
        ent.drawable = R.drawable.ic_category_ent
        ent.location = 12
        ent.eng = "ENT"
        ent.fra = "AMUSE."
        ent.ita = "DIVERT."
        ent.jpn = "娯楽"
        ent.spa = "ENTRET"
        list.add(ent)
        val ins = TmpCategory()
        ins.code = 9
        ins.drawable = R.drawable.ic_category_ins
        ins.location = 13
        ins.eng = "INS"
        ins.fra = "EPARG."
        ins.ita = "RISP."
        ins.jpn = "保険"
        ins.spa = "SEGURO"
        list.add(ins)
        val tax = TmpCategory()
        tax.code = 10
        tax.drawable = R.drawable.ic_category_tax
        tax.location = 14
        tax.eng = "TAX"
        tax.fra = "TAXE"
        tax.ita = "TASSE"
        tax.jpn = "税金"
        tax.spa = "IMPUES"
        list.add(tax)
        val other = TmpCategory()
        other.code = 11
        other.drawable = R.drawable.ic_category_other
        other.location = 15
        other.eng = "OTHER"
        other.fra = "AUTRE"
        other.ita = "ALTRO"
        other.jpn = "他"
        other.spa = "OTROS"
        list.add(other)
        val pet = TmpCategory()
        pet.code = 12
        pet.drawable = R.drawable.ic_category_pet
        pet.location = 8
        pet.eng = "PET"
        pet.fra = "ANIM.C."
        pet.ita = "ANIM.D."
        pet.jpn = "ペット"
        pet.spa = "MSCTA"
        list.add(pet)
        val social = TmpCategory()
        social.code = 13
        social.drawable = R.drawable.ic_category_social
        social.location = 9
        social.eng = "SOCIAL"
        social.fra = "SOCIAL"
        social.ita = "SOCIAL"
        social.jpn = "交際"
        social.spa = "SOCIAL"
        list.add(social)
        val cosme = TmpCategory()
        cosme.code = 14
        cosme.drawable = R.drawable.ic_category_cosme
        cosme.location = 10
        cosme.eng = "COSME"
        cosme.fra = "COSMÉ"
        cosme.ita = "COSME"
        cosme.jpn = "美容"
        cosme.spa = "COSME"
        list.add(cosme)
        val housing = TmpCategory()
        housing.code = 15
        housing.drawable = R.drawable.ic_category_housing
        housing.location = 11
        housing.eng = "HOUSNG"
        housing.fra = "LOGMNT"
        housing.ita = "ABITAT."
        housing.jpn = "居住"
        housing.spa = "VVENDA"
        list.add(housing)
        for (category in list) {
            val values = ContentValues()
            values.put(ConstCategoryDB.COL_CODE, category.code)
            values.put(ConstCategoryDB.COL_NAME, "")
            values.put(ConstCategoryDB.COL_COLOR, 0)
            values.put(ConstCategoryDB.COL_DRAWABLE, category.drawable)
            values.put(ConstCategoryDB.COL_LOCATION, category.location)
            values.put(ConstCategoryDB.COL_SUB_CATEGORIES, 0)
            values.put(ConstCategoryDB.COL_DESCRIPTION, "")
            values.put(ConstCategoryDB.COL_SAVED_DATE, "")
            db.insertOrThrow(ConstCategoryDB.TABLE_NAME_OLD, null, values)
            val values2 = ContentValues()
            values2.put(ConstCategoryLanDB.COL_CODE, category.code)
            values2.put(ConstCategoryLanDB.COL_NAME, "")
            values2.put(ConstCategoryLanDB.COL_EN, category.eng)
            values2.put(ConstCategoryLanDB.COL_ES, category.spa)
            values2.put(ConstCategoryLanDB.COL_FR, category.fra)
            values2.put(ConstCategoryLanDB.COL_IT, category.ita)
            values2.put(ConstCategoryLanDB.COL_JA, category.jpn)
            values2.put(ConstCategoryLanDB.COL_SAVED_DATE, "")
            db.insertOrThrow(ConstCategoryLanDB.TABLE_NAME, null, values2)
        }
    }

    fun initCategoriesTableRevised(db: SQLiteDatabase) {
        val list = ArrayList<TmpCategory>()
        val income = TmpCategory()
        income.code = 0
        income.color = 1
        income.drawable = R.drawable.ic_category_income
        income.location = 0
        income.eng = "INCOME"
        income.fra = "REV."
        income.ita = "ENTR."
        income.jpn = "収入"
        income.spa = "INGRE"
        income.ara = "الإيرادات"
        income.hin = "आय"
        income.ind = "PENDAPATAN"
        income.kor = "수입"
        income.pol = "DOCHÓD"
        income.por = "RENDA"
        income.rus = "доход"
        income.tur = "GELİR"
        income.vie = "THU NHẬP"
        income.hans = "收入"
        income.hant = "收入"
        list.add(income)
        val commodity = TmpCategory()
        commodity.code = 1
        commodity.color = 0
        commodity.drawable = R.drawable.ic_category_comm
        commodity.location = 1
        commodity.eng = "COMM"
        commodity.fra = "ACHAT."
        commodity.ita = "ACQ."
        commodity.jpn = "生活雑貨"
        commodity.spa = "MERCAN"
        commodity.ara = "السلع"
        commodity.hin = "वस्तु"
        commodity.ind = "KOMODITI"
        commodity.kor = "상품"
        commodity.pol = "TOWAR"
        commodity.por = "MERCADORIA"
        commodity.rus = "товар"
        commodity.tur = "EMTİA"
        commodity.vie = "HÀNG HÓA"
        commodity.hans = "商品"
        commodity.hant = "商品"
        list.add(commodity)
        val meal = TmpCategory()
        meal.code = 2
        meal.color = 0
        meal.drawable = R.drawable.ic_category_meal
        meal.location = 2
        meal.eng = "MEAL"
        meal.fra = "ALIM."
        meal.ita = "ALIMEN."
        meal.jpn = "食事"
        meal.spa = "COMIDA"
        meal.ara = "وجبة"
        meal.hin = "भोजन"
        meal.ind = "MAKAN"
        meal.kor = "식사"
        meal.pol = "POSIŁEK"
        meal.por = "REFEIÇÃO"
        meal.rus = "еда"
        meal.tur = "YEMEK"
        meal.vie = "BỮA ĂN"
        meal.hans = "膳食"
        meal.hant = "膳食"
        list.add(meal)
        val util = TmpCategory()
        util.code = 3
        util.color = 0
        util.drawable = R.drawable.ic_category_util
        util.location = 3
        util.eng = "UTIL"
        util.fra = "FACTUR"
        util.ita = "UTENZE"
        util.jpn = "水道光熱"
        util.spa = "UTIL"
        util.ara = "خدمة"
        util.hin = "उपयोगिता"
        util.ind = "UTIL"
        util.kor = "유용"
        util.pol = "UŻYTECZNOŚĆ"
        util.por = "UTIL"
        util.rus = "Утилита"
        util.tur = "YARAR"
        util.vie = "TIỆN ÍCH"
        util.hans = "效用"
        util.hant = "效用"
        list.add(util)
        val health = TmpCategory()
        health.code = 4
        health.color = 0
        health.drawable = R.drawable.ic_category_health
        health.location = 4
        health.eng = "HEALTH"
        health.fra = "SANTÉ"
        health.ita = "SALUTE"
        health.jpn = "健康"
        health.spa = "SALUD"
        health.ara = "الصحة"
        health.hin = "स्वास्थ्य"
        health.ind = "KESEHATAN"
        health.kor = "건강"
        health.pol = "ZDROWIE"
        health.por = "SAÚDE"
        health.rus = "Здоровье"
        health.tur = "SAĞLIK"
        health.vie = "SỨC KHỎE"
        health.hans = "健康"
        health.hant = "健康"
        list.add(health)
        val edu = TmpCategory()
        edu.code = 5
        edu.color = 0
        edu.drawable = R.drawable.ic_category_edu
        edu.location = 5
        edu.eng = "EDU"
        edu.fra = "ETUDE"
        edu.ita = "EDUCAZ"
        edu.jpn = "教育"
        edu.spa = "EDU"
        edu.ara = "التعليم"
        edu.hin = "शिक्षा"
        edu.ind = "PENDIDIKAN"
        edu.kor = "교육"
        edu.pol = "EDUKACJA"
        edu.por = "EDUCAÇÃO"
        edu.rus = "образование"
        edu.tur = "EĞİTİM"
        edu.vie = "GIÁO DỤC"
        edu.hans = "教育"
        edu.hant = "教育"
        list.add(edu)
        val cloth = TmpCategory()
        cloth.code = 6
        cloth.color = 0
        cloth.drawable = R.drawable.ic_category_cloth
        cloth.location = 6
        cloth.eng = "CLOTH"
        cloth.fra = "VETEM."
        cloth.ita = "ABBIG."
        cloth.jpn = "衣服"
        cloth.spa = "VESTID"
        cloth.ara = "ملابس"
        cloth.hin = "कपड़ा"
        cloth.ind = "PAKAIAN"
        cloth.kor = "의류"
        cloth.pol = "ODZIEŻ"
        cloth.por = "ROUPAS"
        cloth.rus = "Одежда"
        cloth.tur = "GİYİM"
        cloth.vie = "QUẦN ÁO"
        cloth.hans = "衣服"
        cloth.hant = "衣服"
        list.add(cloth)
        val trans = TmpCategory()
        trans.code = 7
        trans.color = 0
        trans.drawable = R.drawable.ic_category_trans
        trans.location = 7
        trans.eng = "TRANS"
        trans.fra = "TRANS."
        trans.ita = "TRASP."
        trans.jpn = "交通"
        trans.spa = "TRANS"
        trans.ara = "نقل"
        trans.hin = "परिवहन"
        trans.ind = "ANGKUTAN"
        trans.kor = "교통"
        trans.pol = "TRANSPORT"
        trans.por = "TRANSPORTE"
        trans.rus = "Транспорт"
        trans.tur = "ULAŞIM"
        trans.vie = "VẬN CHUYỂN"
        trans.hans = "运输"
        trans.hant = "運輸"
        list.add(trans)
        val ent = TmpCategory()
        ent.code = 8
        ent.color = 0
        ent.drawable = R.drawable.ic_category_ent
        ent.location = 12
        ent.eng = "ENT"
        ent.fra = "AMUSE."
        ent.ita = "DIVERT."
        ent.jpn = "娯楽"
        ent.spa = "ENTRET"
        ent.ara = "تسلية"
        ent.hin = "मनोरंजन"
        ent.ind = "HIBURAN"
        ent.kor = "환대"
        ent.pol = "ZABAWA"
        ent.por = "ENTRETENIMENTO"
        ent.rus = "Развлечения"
        ent.tur = "EĞLENCE"
        ent.vie = "SỰ GIẢI TRÍ"
        ent.hans = "娱乐"
        ent.hant = "娛樂"
        list.add(ent)
        val ins = TmpCategory()
        ins.code = 9
        ins.color = 0
        ins.drawable = R.drawable.ic_category_ins
        ins.location = 13
        ins.eng = "INS"
        ins.fra = "EPARG."
        ins.ita = "RISP."
        ins.jpn = "保険"
        ins.spa = "SEGURO"
        ins.ara = "تأمين"
        ins.hin = "बीमा"
        ins.ind = "ASURANSI"
        ins.kor = "보험"
        ins.pol = "UBEZPIECZENIE"
        ins.por = "SEGURO"
        ins.rus = "страхование"
        ins.tur = "SİGORTA"
        ins.vie = "BẢO HIỂM"
        ins.hans = "保险"
        ins.hant = "保險"
        list.add(ins)
        val tax = TmpCategory()
        tax.code = 10
        tax.color = 0
        tax.drawable = R.drawable.ic_category_tax
        tax.location = 14
        tax.eng = "TAX"
        tax.fra = "TAXE"
        tax.ita = "TASSE"
        tax.jpn = "税金"
        tax.spa = "IMPUES"
        tax.ara = "ضريبة"
        tax.hin = "कर"
        tax.ind = "PAJAK"
        tax.kor = "세"
        tax.pol = "PODATEK"
        tax.por = "IMPOSTO"
        tax.rus = "налог"
        tax.tur = "VERGİ"
        tax.vie = "THUẾ"
        tax.hans = "税"
        tax.hant = "稅"
        list.add(tax)
        val other = TmpCategory()
        other.code = 11
        other.color = 0
        other.drawable = R.drawable.ic_category_other
        other.location = 15
        other.eng = "OTHER"
        other.fra = "AUTRE"
        other.ita = "ALTRO"
        other.jpn = "他"
        other.spa = "OTROS"
        other.ara = "آخر"
        other.hin = "अन्य"
        other.ind = "LAIN"
        other.kor = "다른"
        other.pol = "INNY"
        other.por = "OUTRO"
        other.rus = "Другие"
        other.tur = "DİĞER"
        other.vie = "KHÁC"
        other.hans = "其他"
        other.hant = "其他"
        list.add(other)
        val pet = TmpCategory()
        pet.code = 12
        pet.color = 0
        pet.drawable = R.drawable.ic_category_pet
        pet.location = 8
        pet.eng = "PET"
        pet.fra = "ANIM.C."
        pet.ita = "ANIM.D."
        pet.jpn = "ペット"
        pet.spa = "MSCTA"
        pet.ara = "حيوان اليف"
        pet.hin = "पालतू"
        pet.ind = "MEMBELAI"
        pet.kor = "착한 애"
        pet.pol = "ZWIERZĘ DOMOWE"
        pet.por = "ANIMAL"
        pet.rus = "домашнее животное"
        pet.tur = "EVCİL HAYVAN"
        pet.vie = "VẬT NUÔI"
        pet.hans = "宠物"
        pet.hant = "寵物"
        list.add(pet)
        val social = TmpCategory()
        social.code = 13
        social.color = 0
        social.drawable = R.drawable.ic_category_social
        social.location = 9
        social.eng = "SOCIAL"
        social.fra = "SOCIAL"
        social.ita = "SOCIAL"
        social.jpn = "交際"
        social.spa = "SOCIAL"
        social.ara = "اجتماعي"
        social.hin = "सामाजिक"
        social.ind = "SOSIAL"
        social.kor = "사회적인"
        social.pol = "SPOŁECZNY"
        social.por = "SOCIAL"
        social.rus = "Социальное"
        social.tur = "SOSYAL"
        social.vie = "XÃ HỘI"
        social.hans = "社会的"
        social.hant = "社會的"
        list.add(social)
        val cosme = TmpCategory()
        cosme.code = 14
        cosme.color = 0
        cosme.drawable = R.drawable.ic_category_cosme
        cosme.location = 10
        cosme.eng = "COSME"
        cosme.fra = "COSMÉ"
        cosme.ita = "COSME"
        cosme.jpn = "美容"
        cosme.spa = "COSME"
        cosme.ara = "كوزمي"
        cosme.hin = "अंगराग"
        cosme.ind = "COSME"
        cosme.kor = "화장품"
        cosme.pol = "COSME"
        cosme.por = "COSME"
        cosme.rus = "Косме"
        cosme.tur = "COSME"
        cosme.vie = "MỸ PHẨM"
        cosme.hans = "化妆品"
        cosme.hant = "化妝品"
        list.add(cosme)
        val housing = TmpCategory()
        housing.code = 15
        housing.color = 0
        housing.drawable = R.drawable.ic_category_housing
        housing.location = 11
        housing.eng = "HOUSNG"
        housing.fra = "LOGMNT"
        housing.ita = "ABITAT."
        housing.jpn = "居住"
        housing.spa = "VVENDA"
        housing.ara = "إسكان"
        housing.hin = "आवास"
        housing.ind = "PERUMAHAN"
        housing.kor = "주택"
        housing.pol = "MIESZKANIOWY"
        housing.por = "HABITAÇÃO"
        housing.rus = "Корпус"
        housing.tur = "KONUT"
        housing.vie = "NHÀ Ở"
        housing.hans = "住房"
        housing.hant = "住房"
        list.add(housing)
        for (i in 0..15) {
            val values1 = ContentValues()
            values1.put(ConstCategoryDB.COL_CODE, list[i].code)
            values1.put(ConstCategoryDB.COL_COLOR, 0)
            values1.put(ConstCategoryDB.COL_SIGN, 0)
            values1.put(ConstCategoryDB.COL_DRAWABLE, list[i].drawable)
            values1.putNull(ConstCategoryDB.COL_IMAGE)
            values1.put(ConstCategoryDB.COL_PARENT, -1)
            values1.put(ConstCategoryDB.COL_DESCRIPTION, "")
            values1.put(ConstCategoryDB.COL_VAL1, 0)
            values1.put(ConstCategoryDB.COL_VAL2, 0)
            values1.put(ConstCategoryDB.COL_VAL3, 0)
            values1.put(ConstCategoryDB.COL_SAVED_DATE, "")
            db.insertOrThrow(ConstCategoryDB.TABLE_NAME_OLD, null, values1)
            val values2 = ContentValues()
            values2.put(ConstCategoryLanDB.COL_CODE, list[i].code)
            values2.put(ConstCategoryLanDB.COL_ENG, list[i].eng)
            values2.put(ConstCategoryLanDB.COL_SPA, list[i].spa)
            values2.put(ConstCategoryLanDB.COL_FRA, list[i].fra)
            values2.put(ConstCategoryLanDB.COL_ITA, list[i].ita)
            values2.put(ConstCategoryLanDB.COL_JPN, list[i].jpn)
            values2.put(ConstCategoryLanDB.COL_ARA, list[i].ara)
            values2.put(ConstCategoryLanDB.COL_HIN, list[i].hin)
            values2.put(ConstCategoryLanDB.COL_IND, list[i].ind)
            values2.put(ConstCategoryLanDB.COL_KOR, list[i].kor)
            values2.put(ConstCategoryLanDB.COL_POL, list[i].pol)
            values2.put(ConstCategoryLanDB.COL_POR, list[i].por)
            values2.put(ConstCategoryLanDB.COL_RUS, list[i].rus)
            values2.put(ConstCategoryLanDB.COL_TUR, list[i].tur)
            values2.put(ConstCategoryLanDB.COL_VIE, list[i].vie)
            values2.put(ConstCategoryLanDB.COL_Hans, list[i].hans)
            values2.put(ConstCategoryLanDB.COL_Hant, list[i].hant)
            db.insertOrThrow(ConstCategoryLanDB.TABLE_NAME, null, values2)
            val values3 = ContentValues()
            values3.put(ConstCategoryDspDB.COL_LOCATION, list[i].location)
            values3.put(ConstCategoryDspDB.COL_CODE, list[i].code)
            db.insertOrThrow(ConstCategoryDspDB.TABLE_NAME_OLD, null, values3)
        }
    }

    fun addMoreCategories(db: SQLiteDatabase) {
        /*** change color column of Income from 0 to 1  */
        val query = "UPDATE " + ConstCategoryDB.TABLE_NAME_OLD +
                " SET " + ConstCategoryDB.COL_COLOR + "=1" +
                " WHERE " + ConstCategoryDB.COL_CODE + "=0;"
        db.execSQL(query)
        val list = ArrayList<TmpCategory>()
        val bonus = TmpCategory()
        bonus.code = 16
        bonus.color = 1
        bonus.drawable = R.drawable.ic_category_bonus
        bonus.ara = "علاوة"
        bonus.eng = "EXTRA"
        bonus.spa = "EXTRA"
        bonus.fra = "SUPPLÉ."
        bonus.hin = "बक्शीश"
        bonus.ind = "TAMBAHAN"
        bonus.ita = "SUPPLE."
        bonus.jpn = "ボーナス"
        bonus.kor = "보너스"
        bonus.pol = "DODATKOWY"
        bonus.por = "ADICIONAL"
        bonus.rus = "допол"
        bonus.tur = "EK"
        bonus.vie = "THÊM"
        bonus.hans = "奖金"
        bonus.hant = "獎金"
        list.add(bonus)
        val allowance = TmpCategory()
        allowance.code = 17
        allowance.color = 1
        allowance.drawable = R.drawable.ic_category_allowance
        allowance.ara = "بدل"
        allowance.eng = "ALLOW"
        allowance.spa = "TOLER."
        allowance.fra = "ALLOC."
        allowance.hin = "भत्ता"
        allowance.ind = "TUNJANGAN"
        allowance.ita = "INDENNITA"
        allowance.jpn = "お小遣い"
        allowance.kor = "수당"
        allowance.pol = "DODATEK"
        allowance.por = "MESADA"
        allowance.rus = "РЕЗЕРВЫ"
        allowance.tur = "ÖDENEK"
        allowance.vie = "PHỤ CẤP"
        allowance.hans = "津贴"
        allowance.hant = "津貼"
        list.add(allowance)
        val inv = TmpCategory()
        inv.code = 18
        inv.color = 1
        inv.drawable = R.drawable.ic_category_in_inv
        inv.ara = "استثمار"
        inv.eng = "INV"
        inv.spa = "INV"
        inv.fra = "INV"
        inv.hin = "निवेश"
        inv.ind = "INV"
        inv.ita = "INV"
        inv.jpn = "投資"
        inv.kor = "투자"
        inv.pol = "INW"
        inv.por = "INV"
        inv.rus = "инвест"
        inv.tur = "YATIRIM"
        inv.vie = "ĐẦU TƯ"
        inv.hans = "投资"
        inv.hant = "投資"
        list.add(inv)
        val rent = TmpCategory()
        rent.code = 19
        rent.color = 1
        rent.drawable = R.drawable.ic_category_in_rent
        rent.ara = "تأجير"
        rent.eng = "RENT"
        rent.spa = "ALQUIL."
        rent.fra = "LOCAC."
        rent.hin = "किराए"
        rent.ind = "SEWA"
        rent.ita = "AFFIT."
        rent.jpn = "家賃"
        rent.kor = "임대"
        rent.pol = "CZYNSZ"
        rent.por = "ALUGAR"
        rent.rus = "аренда"
        rent.tur = "KIRA"
        rent.vie = "THUÊ"
        rent.hans = "房租"
        rent.hant = "房租"
        list.add(rent)
        val ex_exp = TmpCategory()
        ex_exp.code = 20
        ex_exp.color = 0
        ex_exp.drawable = R.drawable.ic_category_expense
        ex_exp.ara = "مصروف"
        ex_exp.eng = "EXPENS"
        ex_exp.spa = "GASTOS"
        ex_exp.fra = "FRAIS"
        ex_exp.hin = "व्यय"
        ex_exp.ind = "BIAYA"
        ex_exp.ita = "SPESE"
        ex_exp.jpn = "支出"
        ex_exp.kor = "비용"
        ex_exp.pol = "KOSZT"
        ex_exp.por = "DESPESA"
        ex_exp.rus = "РАСХОДЫ"
        ex_exp.tur = "GİDER"
        ex_exp.vie = "CHI PHÍ"
        ex_exp.hans = "费用"
        ex_exp.hant = "費用"
        list.add(ex_exp)
        val ex_tele = TmpCategory()
        ex_tele.code = 21
        ex_tele.color = 0
        ex_tele.drawable = R.drawable.ic_category_tele
        ex_tele.ara = "هاتف"
        ex_tele.eng = "TELE"
        ex_tele.spa = "TELÉ"
        ex_tele.fra = "TÉLÉ"
        ex_tele.hin = "फ़ोन"
        ex_tele.ind = "TELE"
        ex_tele.ita = "TELE"
        ex_tele.jpn = "通信"
        ex_tele.kor = "전화"
        ex_tele.pol = "TELE"
        ex_tele.por = "TELE"
        ex_tele.rus = "ТЕЛЕ"
        ex_tele.tur = "TELE"
        ex_tele.vie = "THOẠI"
        ex_tele.hans = "电讯"
        ex_tele.hant = "電訊"
        list.add(ex_tele)
        val ex_inv = TmpCategory()
        ex_inv.code = 22
        ex_inv.color = 0
        ex_inv.drawable = R.drawable.ic_category_ex_inv
        ex_inv.ara = "استثمار"
        ex_inv.eng = "INV"
        ex_inv.spa = "INV"
        ex_inv.fra = "INV"
        ex_inv.hin = "निवेश"
        ex_inv.ind = "INV"
        ex_inv.ita = "INV"
        ex_inv.jpn = "投資"
        ex_inv.kor = "투자"
        ex_inv.pol = "INW"
        ex_inv.por = "INV"
        ex_inv.rus = "инвест"
        ex_inv.tur = "YATIRIM"
        ex_inv.vie = "ĐẦU TƯ"
        ex_inv.hans = "投资"
        ex_inv.hant = "投資"
        list.add(ex_inv)
        val ex_rent = TmpCategory()
        ex_rent.code = 23
        ex_rent.color = 0
        ex_rent.drawable = R.drawable.ic_category_ex_rent
        ex_rent.ara = "تأجير"
        ex_rent.eng = "RENT"
        ex_rent.spa = "ALQUIL."
        ex_rent.fra = "LOCAC."
        ex_rent.hin = "किराए"
        ex_rent.ind = "SEWA"
        ex_rent.ita = "AFFIT."
        ex_rent.jpn = "家賃"
        ex_rent.kor = "임대"
        ex_rent.pol = "CZYNSZ"
        ex_rent.por = "ALUGAR"
        ex_rent.rus = "аренда"
        ex_rent.tur = "KIRA"
        ex_rent.vie = "THUÊ"
        ex_rent.hans = "房租"
        ex_rent.hant = "房租"
        list.add(ex_rent)
        for (c in list) {
            val values = ContentValues()
            values.put(ConstCategoryDB.COL_CODE, c.code)
            values.put(ConstCategoryDB.COL_COLOR, c.color)
            values.put(ConstCategoryDB.COL_DRAWABLE, c.drawable)
            values.putNull(ConstCategoryDB.COL_IMAGE)
            values.put(ConstCategoryDB.COL_DESCRIPTION, "")
            values.put(ConstCategoryDB.COL_SAVED_DATE, "")
            db.insertOrThrow(ConstCategoryDB.TABLE_NAME_OLD, null, values)
            val values1 = ContentValues()
            values1.put(ConstCategoryLanDB.COL_CODE, c.code)
            values1.put(ConstCategoryLanDB.COL_ARA, c.ara)
            values1.put(ConstCategoryLanDB.COL_ENG, c.eng)
            values1.put(ConstCategoryLanDB.COL_SPA, c.spa)
            values1.put(ConstCategoryLanDB.COL_FRA, c.fra)
            values1.put(ConstCategoryLanDB.COL_HIN, c.hin)
            values1.put(ConstCategoryLanDB.COL_IND, c.ind)
            values1.put(ConstCategoryLanDB.COL_ITA, c.ita)
            values1.put(ConstCategoryLanDB.COL_JPN, c.jpn)
            values1.put(ConstCategoryLanDB.COL_KOR, c.kor)
            values1.put(ConstCategoryLanDB.COL_POL, c.pol)
            values1.put(ConstCategoryLanDB.COL_POR, c.por)
            values1.put(ConstCategoryLanDB.COL_RUS, c.rus)
            values1.put(ConstCategoryLanDB.COL_TUR, c.tur)
            values1.put(ConstCategoryLanDB.COL_VIE, c.vie)
            values1.put(ConstCategoryLanDB.COL_Hans, c.hans)
            values1.put(ConstCategoryLanDB.COL_Hant, c.hant)
            db.insertOrThrow(ConstCategoryLanDB.TABLE_NAME, null, values1)
        }
    }

    fun prepCategoryStatuses(): List<CategoryEntity> {
        val out: MutableList<CategoryEntity> = ArrayList()
        out.add(CategoryEntity(1, 0, "INCOME", UtilCategory.CATEGORY_COLOR_INCOME, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_income", null, -1, "", ""))
        out.add(CategoryEntity(2, 1, "COMM", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_comm", null, -1, "", ""))
        out.add(CategoryEntity(3, 2, "MEAL", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_meal", null, -1, "", ""))
        out.add(CategoryEntity(4, 3, "UTIL", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_util", null, -1, "", ""))
        out.add(CategoryEntity(5, 4, "HEALTH", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_health", null, -1, "", ""))
        out.add(CategoryEntity(6, 5, "EDU", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_edu", null, -1, "", ""))
        out.add(CategoryEntity(7, 6, "CLOTH", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_cloth", null, -1, "", ""))
        out.add(CategoryEntity(8, 7, "TRANS", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_trans", null, -1, "", ""))
        out.add(CategoryEntity(9, 8, "ENT", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_ent", null, -1, "", ""))
        out.add(CategoryEntity(10, 9, "INS", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_ins", null, -1, "", ""))
        out.add(CategoryEntity(11, 10, "TAX", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_tax", null, -1, "", ""))
        out.add(CategoryEntity(12, 11, "OTHER", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_other", null, -1, "", ""))
        out.add(CategoryEntity(13, 12, "PET", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_pet", null, -1, "", ""))
        out.add(CategoryEntity(14, 13, "SOCIAL", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_social", null, -1, "", ""))
        out.add(CategoryEntity(15, 14, "COSME", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_cosme", null, -1, "", ""))
        out.add(CategoryEntity(16, 15, "HOUSNG", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_housing", null, -1, "", ""))
        out.add(CategoryEntity(17, 16, "EXTRA", UtilCategory.CATEGORY_COLOR_INCOME, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_bonus", null, -1, "", ""))
        out.add(CategoryEntity(18, 17, "ALLOW", UtilCategory.CATEGORY_COLOR_INCOME, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_allowance", null, -1, "", ""))
        out.add(CategoryEntity(19, 18, "INV", UtilCategory.CATEGORY_COLOR_INCOME, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_in_inv", null, -1, "", ""))
        out.add(CategoryEntity(20, 19, "RENT", UtilCategory.CATEGORY_COLOR_INCOME, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_in_rent", null, -1, "", ""))
        out.add(CategoryEntity(21, 20, "EXPENS", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_expense", null, -1, "", ""))
        out.add(CategoryEntity(22, 21, "TELE", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_tele", null, -1, "", ""))
        out.add(CategoryEntity(23, 22, "INV", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_ex_inv", null, -1, "", ""))
        out.add(CategoryEntity(24, 23, "RENT", UtilCategory.CATEGORY_COLOR_EXPENSE, UtilCategory.CATEGORY_SIGN_LOW, "ic_category_ex_rent", null, -1, "", ""))
        out.toMutableList()
        return out
    }

    //    public static List<CategoryLanStatus> prepLanCategoryStatuses() {
    //        List<CategoryLanStatus> out = new ArrayList<>();
    //        out.add(new CategoryLanStatus(1, 0, "الإيرادات", "INCOME", "INGRE", "REV.", "आय", "ENTR.", "ENTR.", "収入", "수입", "DOCHÓD", "RENDA", "доход", "GELİR", "THU NHẬP", "收入", "收入"));
    //        out.add(new CategoryLanStatus(2, 1, "السلع", "COMM", "MERCAN", "ACHAT.", "वस्तु", "KOMODITI", "ACQ.", "生活雑貨", "상품", "TOWAR", "MERCADORIA", "товар", "EMTİA", "HÀNG HÓA", "商品", "商品"));
    //        out.add(new CategoryLanStatus(3, 2, "وجبة", "MEAL", "COMIDA", "ALIM.", "भोजन", "MAKAN", "ALIMEN.", "食事", "식사", "POSIŁEK", "REFEIÇÃO", "еда", "YEMEK", "BỮA ĂN", "膳食", "膳食"));
    //        out.add(new CategoryLanStatus(4, 3, "خدمة", "UTIL", "UTIL", "FACTUR", "उपयोगिता", "UTIL", "UTENZE", "水道光熱", "유용", "UŻYTECZNOŚĆ", "UTIL", "Утилита", "YARAR", "TIỆN ÍCH", "效用", "效用"));
    //        out.add(new CategoryLanStatus(5, 4, "الصحة", "HEALTH", "SALUD", "SANTÉ", "स्वास्थ्य", "KESEHATAN", "SALUTE", "健康", "건강", "ZDROWIE", "SAÚDE", "Здоровье", "SAĞLIK", "SỨC KHỎE", "健康", "健康"));
    //        out.add(new CategoryLanStatus(6, 5, "التعليم", "EDU", "EDU", "ETUDE", "शिक्षा", "PENDIDIKAN", "EDUCAZ", "教育", "교육", "EDUKACJA", "EDUCAÇÃO", "образование", "EĞİTİM", "GIÁO DỤC", "教育", "教育"));
    //        out.add(new CategoryLanStatus(7, 6, "ملابس", "CLOTH", "VESTID", "VETEM.", "कपड़ा", "PAKAIAN", "ABBIG.", "衣服", "의류", "ODZIEŻ", "ROUPAS", "Одежда", "GİYİM", "QUẦN ÁO", "衣服", "衣服"));
    //        out.add(new CategoryLanStatus(8, 7, "نقل", "TRANS", "TRANS", "TRANS.", "परिवहन", "ANGKUTAN", "TRASP.", "交通", "교통", "TRANSPORT", "TRANSPORTE", "Транспорт", "ULAŞIM", "VẬN CHUYỂN", "运输", "運輸"));
    //        out.add(new CategoryLanStatus(9, 8, "تسلية", "ENT", "ENTRET", "AMUSE.", "मनोरंजन", "HIBURAN", "DIVERT.", "娯楽", "환대", "ZABAWA", "ENTRETENIMENTO", "Развлечения", "EĞLENCE", "SỰ GIẢI TRÍ", "娱乐", "娛樂"));
    //        out.add(new CategoryLanStatus(10, 9, "تأمين", "INS", "SEGURO", "EPARG.", "बीमा", "ASURANSI", "RISP.", "保険", "보험", "UBEZPIECZENIE", "SEGURO", "страхование", "SİGORTA", "BẢO HIỂM", "保险", "保險"));
    //        out.add(new CategoryLanStatus(11, 10, "ضريبة", "TAX", "IMPUES", "TAXE", "कर", "PAJAK", "TASSE", "税金", "세", "PODATEK", "IMPOSTO", "налог", "VERGİ", "THUẾ", "税", "稅"));
    //        out.add(new CategoryLanStatus(12, 11, "آخر", "OTHER", "OTROS", "AUTRE", "अन्य", "LAIN", "ALTRO", "他", "다른", "INNY", "OUTRO", "Другие", "DİĞER", "KHÁC", "其他", "其他"));
    //        out.add(new CategoryLanStatus(13, 12, "حيوان اليف", "PET", "MSCTA", "ANIM.C.", "पालतू", "MEMBELAI", "ANIM.D.", "ペット", "착한 애", "ZWIERZĘ DOMOWE", "ANIMAL", "домашнее животное", "EVCİL HAYVAN", "VẬT NUÔI", "宠物", "寵物"));
    //        out.add(new CategoryLanStatus(14, 13, "اجتماعي", "SOCIAL", "SOCIAL", "SOCIAL", "सामाजिक", "SOSIAL", "SOCIAL", "交際", "사회적인", "SPOŁECZNY", "SOCIAL", "Социальное", "SOSYAL", "XÃ HỘI", "社会的", "社會的"));
    //        out.add(new CategoryLanStatus(15, 14, "كوزمي", "COSME", "COSME", "COSMÉ", "अंगराग", "COSME", "COSME", "美容", "화장품", "COSME", "COSME", "Косме", "COSME", "MỸ PHẨM", "化妆品", "化妝品"));
    //        out.add(new CategoryLanStatus(16, 15, "إسكان", "HOUSNG", "VVENDA", "LOGMNT", "आवास", "PERUMAHAN", "ABITAT.", "居住", "주택", "MIESZKANIOWY", "HABITAÇÃO", "Корпус", "KONUT", "NHÀ Ở", "住房", "住房"));
    //        out.add(new CategoryLanStatus(17, 16, "علاوة", "EXTRA", "EXTRA", "SUPPLÉ.", "बक्शीश", "TAMBAHAN", "SUPPLE.", "ボーナス", "보너스", "DODATKOWY", "ADICIONAL", "допол", "EK", "THÊM", "奖金", "獎金"));
    //        out.add(new CategoryLanStatus(18, 17, "بدل", "ALLOW", "TOLER.", "ALLOC.", "भत्ता", "TUNJANGAN", "INDENNITA", "小遣い", "수당", "DODATEK", "MESADA", "РЕЗЕРВЫ", "ÖDENEK", "PHỤ CẤP", "津贴", "津貼"));
    //        out.add(new CategoryLanStatus(19, 18, "استثمار", "INV", "INV", "INV", "निवेश", "INV", "INV","投資", "투자", "INW", "INV", "инвест", "YATIRIM", "ĐẦU TƯ", "投资", "投資"));
    //        out.add(new CategoryLanStatus(20, 19, "تأجير", "RENT", "ALQUIL.", "LOCAC.", "किराए", "SEWA", "AFFIT.", "家賃", "임대", "CZYNSZ", "ALUGAR", "аренда", "KIRA", "THUÊ", "房租", "房租"));
    //        out.add(new CategoryLanStatus(21, 20, "مصروف", "EXPENS", "GASTOS", "FRAIS", "व्यय", "BIAYA", "SPESE", "支出", "비용", "KOSZT", "DESPESA", "РАСХОДЫ", "GİDER", "CHI PHÍ", "费用", "費用"));
    //        out.add(new CategoryLanStatus(22, 21, "هاتف", "TELE", "TELÉ", "TÉLÉ", "फ़ोन", "TELE", "TELE", "通信", "전화", "TELE", "TELE", "ТЕЛЕ", "TELE", "THOẠI", "电讯", "電訊"));
    //        out.add(new CategoryLanStatus(23, 22, "استثمار", "INV", "INV", "INV", "निवेश", "INV", "INV", "投資", "투자", "INW", "INV", "инвест", "YATIRIM", "ĐẦU TƯ", "投资", "投資"));
    //        out.add(new CategoryLanStatus(24, 23, "تأجير", "RENT", "ALQUIL.", "LOCAC.", "किराए", "SEWA", "AFFIT.", "家賃", "임대", "CZYNSZ", "ALUGAR", "аренда", "KIRA", "THUÊ", "房租", "房租"));
    //        return out;
    //    }

    fun prepDspCategoryStatuses(): List<CategoryDspEntity> {
        val out: MutableList<CategoryDspEntity> = ArrayList()
        out.add(CategoryDspEntity(1, 0, 0))
        out.add(CategoryDspEntity(2, 1, 1))
        out.add(CategoryDspEntity(3, 2, 2))
        out.add(CategoryDspEntity(4, 3, 3))
        out.add(CategoryDspEntity(5, 4, 4))
        out.add(CategoryDspEntity(6, 5, 5))
        out.add(CategoryDspEntity(7, 6, 6))
        out.add(CategoryDspEntity(8, 7, 7))
        out.add(CategoryDspEntity(9, 12, 8))
        out.add(CategoryDspEntity(10, 13, 9))
        out.add(CategoryDspEntity(11, 14, 10))
        out.add(CategoryDspEntity(12, 15, 11))
        out.add(CategoryDspEntity(13, 8, 12))
        out.add(CategoryDspEntity(14, 9, 13))
        out.add(CategoryDspEntity(15, 10, 14))
        out.add(CategoryDspEntity(16, 11, 15))
        return out
    }

//    fun initKkbAppTable() : KkbAppEntity {
//        return KkbAppEntity(
//            1,
//            "",
//            "",
//            "",
//            BuildConfig.versionDB,
//            -1,
//            0,
//            "",
//            "",
//            "")
//    }
}