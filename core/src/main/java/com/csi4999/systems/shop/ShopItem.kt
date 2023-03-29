package com.csi4999.systems.shop

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.csi4999.systems.networking.GameClient

class ShopItem(
    private val levelToValue: (Int) -> Float,
    private val levelToPrice: (Int) -> Int,
    private val retrieveLevel: () -> Int,
    private val setLevel: (Int) -> Unit,
    private val setValue: (Float) -> Unit,
    private val name: String,
    private val desc: String,
    private val maxLevel: Int
) {
    private var levelLabel: Label? = null

    fun makeComponent(skin: Skin, description: Label, cost: Label, purchaseButton: Button, sellButton: Button): Table {
        val componentTable = Table()
        componentTable.setSize(100f, 100f)
        componentTable.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                description.setText(desc)
                cost.setText("Cost: " + levelToPrice(retrieveLevel() + 1).toString())
                purchaseButton.clearListeners()
                purchaseButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        tryPurchase()
                    }
                })
                sellButton.clearListeners()
                sellButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        trySell()
                    }
                })
            }
        })
        val nameLabel = Label(name, skin)
        levelLabel = Label(retrieveLevel().toString(), skin)
        componentTable.row()
        componentTable.add(nameLabel).align(Align.center)
        componentTable.row()
        componentTable.add(levelLabel).align(Align.left)
        return componentTable
    }

    fun tryPurchase() {
        val cost = levelToPrice(retrieveLevel() + 1)
        if (cost <= GameClient.getInstance().user.money && (retrieveLevel() < maxLevel  || maxLevel < 0)) {
            // Buy
            GameClient.getInstance().user.money -= cost // decrement money
            setLevel(retrieveLevel() + 1) // increment level
            levelLabel!!.setText(retrieveLevel().toString()) // update label
            setValue(levelToValue(retrieveLevel()))
            GameClient.getInstance().client.sendTCP(GameClient.getInstance().user) // send updated user to server
        } else {
            // can't purchase. do something
            println("Not enough money!")
        }
    }

    fun trySell() {
        val level = retrieveLevel()
        if (level > 0) {
            // Sell
            GameClient.getInstance().user.money += levelToPrice(retrieveLevel())
            setLevel(retrieveLevel() - 1)
            setValue(levelToValue(retrieveLevel()))
            GameClient.getInstance().client.sendTCP(GameClient.getInstance().user) // send updated user to server
        } else {
            // can't keep selling
            println("Can't sell something you don't have")
        }
    }
}