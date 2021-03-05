package com.qzl.cloudalbum.internet

data class MyItem(
    val itemName: String,
    val itemType: String,
    val subItems: List<MyItem>,
    val file: MyFileStorage?,
    val hidden: Boolean
) {

    private var checked: Boolean = false

    fun changeCheckedStatus() {
        checked = !checked
    }

    fun getCheckedStatus() = checked

    fun setCheck(isChecked:Boolean) {
        checked = isChecked
    }

}