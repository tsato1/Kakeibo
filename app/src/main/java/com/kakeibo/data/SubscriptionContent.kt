package com.kakeibo.data

import android.os.Parcel
import android.os.Parcelable

class SubscriptionContent : Parcelable {
    val title: String?
    val subtitle: String?
    val description: String?

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(subtitle)
        dest.writeString(description)
    }

    private constructor(input: Parcel) {
        title = input.readString()
        subtitle = input.readString()
        description = input.readString()
    }

    private constructor(
            title: String?,
            subtitle: String?,
            desc: String?
    ) {
        this.title = title
        this.subtitle = subtitle
        description = desc
    }

    override fun toString() =
            "SubscriptionContent{title='$title', subtitle='$subtitle', description='$description'}"

    internal inner class Builder {
        private var title: String? = null
        private var subTitle: String? = null
        private var description: String? = null

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setSubtitle(subtitle: String): Builder {
            this.subTitle = subtitle
            return this
        }

        fun setDescription(description: String?): Builder {
            this.description = description
            return this
        }

        fun build(): SubscriptionContent {
            return SubscriptionContent(title, subTitle, description)
        }
    }

    companion object CREATOR: Parcelable.Creator<SubscriptionContent> {
        override fun createFromParcel(parcel: Parcel): SubscriptionContent {
            return SubscriptionContent(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionContent?> {
            return arrayOfNulls(size)
        }
    }
}