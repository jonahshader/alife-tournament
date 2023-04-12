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
    private lateinit var levelLabel: Label
    private lateinit var nameLabel: Label

    fun makeComponent(skin: Skin, description: Label, cost: Label, purchaseButton: Button, sellButton: Button, currencyLabel: Label): Table {
        val componentTable = Table()
        componentTable.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                description.setText(desc)
                cost.setText("Cost: " + levelToPrice(retrieveLevel() + 1).toString())
                purchaseButton.clearListeners()
                purchaseButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        tryPurchase()
                        currencyLabel.setText("Money: " + GameClient.getInstance().user.money)
                        cost.setText("Cost: " + levelToPrice(retrieveLevel() + 1).toString())
                    }
                })
                sellButton.clearListeners()
                sellButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        trySell()
                        currencyLabel.setText("Money: " + GameClient.getInstance().user.money)
                        cost.setText("Cost: " + levelToPrice(retrieveLevel() + 1).toString())
                    }
                })
            }
        })
        nameLabel = Label(name, skin)
        levelLabel = Label("Level: " + retrieveLevel().toString(), skin)
        nameLabel.setAlignment(Align.center)
        levelLabel.setAlignment(Align.center)


        componentTable.row().size(175f, 35f)
        componentTable.add(nameLabel)
        componentTable.row().size(175f, 35f)
        componentTable.add(levelLabel)
        return componentTable
    }

    fun initUserAccountValue() {
        setLevel(1)
        setValue(levelToValue(1))
    }

    fun tryPurchase() {
        val cost = levelToPrice(retrieveLevel() + 1)
        if (cost <= GameClient.getInstance().user.money && (retrieveLevel() < maxLevel  || maxLevel < 0)) {
            // Buy
            GameClient.getInstance().user.money -= cost // decrement money
            setLevel(retrieveLevel() + 1) // increment level
            setValue(levelToValue(retrieveLevel()))
            levelLabel.setText("Level: " + retrieveLevel().toString()) // update label
            GameClient.getInstance().client.sendTCP(GameClient.getInstance().user) // send updated user to server
        } else {
            // can't purchase. do something
            println("Not enough money!")
        }
        updateColor()
    }

    fun trySell() {
        val level = retrieveLevel()
        if (level > 1) {
            // Sell
            GameClient.getInstance().user.money += levelToPrice(retrieveLevel())
            setLevel(retrieveLevel() - 1)
            setValue(levelToValue(retrieveLevel()))
            levelLabel.setText("Level: " + retrieveLevel().toString()) // update label
            GameClient.getInstance().client.sendTCP(GameClient.getInstance().user) // send updated user to server
        } else {
            // can't keep selling
            println("Can't sell something you don't have")
        }
        updateColor()
    }

    private fun updateColor() {
        val level = retrieveLevel()
        if (level == maxLevel) {
            nameLabel.setColor(1f, 1f, 1f, .5f)
            levelLabel.setColor(1f, 1f, 1f, .5f)
        } else {
            nameLabel.setColor(1f, 1f, 1f, 1f)
            levelLabel.setColor(1f, 1f, 1f, 1f)
        }
    }
}
