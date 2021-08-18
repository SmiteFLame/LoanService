package com.naverfinancial.creditrating.wrapper

class CreditResult {
    var grade : Int
    var isPermit : Boolean

        constructor(grade: Int, isPermit : Boolean) {
                this.grade = grade
                this.isPermit = isPermit
        }
}