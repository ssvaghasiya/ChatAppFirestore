package com.ChatAppFirestore.model

import java.util.*

class UserStatus {
    var id: String? = null
    var name: String? = null
    var profileImage: kotlin.String? = null
    var lastUpdated: Long = 0
    var statuses = ArrayList<Status>()

    constructor() {}

    constructor(
        name: String?,
        profileImage: String,
        lastUpdated: Long,
        statuses: ArrayList<Status>?
    ) {
        this.name = name
        this.profileImage = profileImage
        this.lastUpdated = lastUpdated
        this.statuses = statuses!!
    }
}