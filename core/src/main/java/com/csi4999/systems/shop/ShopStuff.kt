package com.csi4999.systems.shop

import com.csi4999.systems.networking.GameClient
import com.csi4999.systems.networking.packets.UserAccountPacket
import kotlin.math.pow

fun makeShopItems() = makeShopItems(GameClient.getInstance().user)

fun makeShopItems(user: UserAccountPacket) : List<ShopItem> {
    val defaultLevelToCost = { level: Int -> 2f.pow(level).toInt() }
    val componentLevelToCost = { level: Int -> 25}
    val items = mutableListOf<ShopItem>()

    // maxSensors
    // might want to set this to 0 sensors initially
    items += ShopItem({ level: Int -> level.toFloat() }, defaultLevelToCost, { user.maxSensorsLevel }, { level: Int -> user.maxSensorsLevel = level }, { v: Float -> user.maxSensors = v.toInt() },
        "Max Sensors", "Increases the maximum number of sensors a creature can have at once.", -1)

    // maxTools
    items += ShopItem({ level: Int -> level.toFloat() + 1 }, defaultLevelToCost, { user.maxToolsLevel }, { level: Int -> user.maxToolsLevel = level }, { v: Float -> user.maxTools = v.toInt() },
        "Max Tools", "Increases the maximum number of tools a creature can have at once.", -1)

    // mutationRate
    items += ShopItem({ level: Int -> level.toFloat()}, defaultLevelToCost, { user.maxMutationRateLevel }, { level: Int -> user.maxMutationRateLevel = level }, { v: Float -> user.maxMutationRate = v.toInt() },
        "Mutation Rate", "Increases the maximum rate at which a creature mutates.", -1)

    // maxCreaturesPerSecond
    items += ShopItem({ level: Int -> level.toFloat() * 5}, defaultLevelToCost, { user.maxCreaturesPerSecondLevel }, { level: Int -> user.maxCreaturesPerSecondLevel = level }, { v: Float -> user.maxCreaturesPerSecond = v.toInt() },
        "Creatures Per Second", "Increases the maximum rate at which new creatures spawn.", -1)

    // maxInitialCreatures
    items += ShopItem({ level: Int -> level.toFloat() * 50 + 100}, defaultLevelToCost, { user.maxInitialCreaturesLevel }, { level: Int -> user.maxInitialCreaturesLevel = level }, { v: Float -> user.maxInitialCreatures = v.toInt() },
        "Initial Creatures", "Increases the number of creatures at the start of simulation.", -1)

    // maxFood
    items += ShopItem({ level: Int -> level.toFloat() * 100 + 500}, defaultLevelToCost, { user.maxFoodLevel }, { level: Int -> user.maxFoodLevel = level }, { v: Float -> user.maxFood = v.toInt() },
        "Max Food", "Increases the maximum limits for initial food and food target.", -1)

    // Eye
    items += ShopItem({ level: Int -> level.toFloat()}, componentLevelToCost, {user.eyeLevel}, {level: Int -> user.eyeLevel = level}, {v: Float -> user.eyeLevel = v.toInt()}, "Eye Component", "A purchase enables creatures to spawn with eyes that can sense entities in the environment.",
        2)

    // Horn
    items += ShopItem({ level: Int -> level.toFloat()}, componentLevelToCost, {user.hornLevel}, {level: Int -> user.hornLevel = level}, {v: Float -> user.hornLevel = v.toInt()}, "Horn Component", "A purchase enables creatures to spawn with horns that can damage other creatures.",
        2)
    // Gripper
    items += ShopItem({ level: Int -> level.toFloat()}, componentLevelToCost, {user.gripperLevel}, {level: Int -> user.gripperLevel = level}, {v: Float -> user.gripperLevel = v.toInt()}, "Gripper Component", "A purchase enables creatures to spawn with grippers that can manipulate the position of food in the environment.",
        2)


    return items
}
