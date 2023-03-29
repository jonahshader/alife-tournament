package com.csi4999.systems.shop

import com.csi4999.systems.networking.GameClient
import kotlin.math.pow

fun makeShopItems() : List<ShopItem> {
    val items = mutableListOf<ShopItem>()
    // maxSensors
    // might want to set this to 0 sensors initially
    val defaultLevelToCost = { level: Int -> 2f.pow(level).toInt() }
    val user = GameClient.getInstance().user
    items += ShopItem({ level: Int -> level.toFloat() }, defaultLevelToCost, { user.maxSensorsLevel }, { level: Int -> user.maxSensorsLevel = level }, { v: Float -> user.maxSensors = v.toInt() },
        "Max Sensors", "Increases the maximum number of sensors a creature can have at once.", -1)

    // maxTools
    items += ShopItem({ level: Int -> level.toFloat() + 1 }, defaultLevelToCost, { user.maxToolsLevel }, { level: Int -> user.maxToolsLevel = level }, { v: Float -> user.maxTools = v.toInt() },
        "Max Tools", "Increases the maximum number of tools a creature can have at once.", -1)


    return items
}
