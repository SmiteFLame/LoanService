package com.naverfinancial.creditrating.enumClass

enum class ExceptionEnum() {
    NOT_FOUND_NDI("NOT_FOUND_NDI"),
    NOT_FOUND_USER("NOT_FOUND_USER");

    lateinit var text : String

    constructor(text : String) : this() {
        this.text = text
    }

    override fun toString(): String {
        return this.text
    }
}