package com.kakeibo.ui.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Query : Parcelable {

    constructor(arg: Int) {
        Companion.type = arg
    }

    val type: Int
        get() = Companion.type

    var queryCs: Map<Int?, String?>
        get() = Companion.queryCs
        set(queries) {
            Companion.queryCs = queries
        }
    var queryC: String?
        get() = Companion.queryC
        set(query) {
            Companion.queryC = query
        }
    var queryD: String?
        get() = Companion.queryD
        set(query) {
            Companion.queryD = query
        }

    /******** parcel  */
    private constructor(`in`: Parcel) {
        Companion.type = `in`.readInt()
        `in`.readMap(Companion.queryCs, String::class.java.classLoader)
        Companion.queryC = `in`.readString()
        Companion.queryD = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(Companion.type)
        dest.writeMap(Companion.queryCs)
        dest.writeString(Companion.queryC)
        dest.writeString(Companion.queryD)
    }

    companion object {
        const val QUERY_TYPE_NEW = 0
        const val QUERY_TYPE_SEARCH = 1
        private var type: Int = 0
        private var queryCs: Map<Int?, String?> = HashMap()
        private var queryC: String? = null
        private var queryD: String? = null

        @JvmField
        val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<Any?> {
            override fun createFromParcel(`in`: Parcel): Query {
                return Query(`in`)
            }

            override fun newArray(size: Int): Array<Query?> {
                return arrayOfNulls(size)
            }
        }
    }
}