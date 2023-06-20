package com.csi4999.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.csi4999.ALifeApp
import com.csi4999.singletons.CustomAssetManager
import com.csi4999.singletons.ScreenStack
import com.csi4999.systems.creature.sensors.EyeBuilder
import com.csi4999.systems.creature.tools.FlagellaBuilder
import com.csi4999.systems.creature.tools.GripperBuilder
import com.csi4999.systems.creature.tools.HornBuilder
import com.csi4999.systems.creature.tools.MouthBuilder
import com.csi4999.systems.environment.EnvProperties
import com.csi4999.systems.networking.GameClient
import java.util.concurrent.atomic.AtomicBoolean

class GenerationScreen(private val app: ALifeApp) : Screen {
    private val skin: Skin
    private val menuCam: OrthographicCamera
    private val menuViewport: FitViewport
    private val stage: Stage
    private val properties = EnvProperties()

    // Booleans for checking whether the user has these components unlocked
    private var eyeAllowed = false
    private var mouthAllowed = false
    private var flagellaAllowed = false
    private var hornAllowed = false
    private var gripperAllowed = false

    init {
        properties.minSensors = 0
        properties.minTools = 0
        properties.maxSensors = GameClient.getInstance().user.maxSensors
        properties.maxTools = GameClient.getInstance().user.maxTools
        skin = CustomAssetManager.getInstance().manager.get(CustomAssetManager.SKIN_MAIN)
        menuCam = OrthographicCamera()
        menuViewport = FitViewport(1280f, 720f, menuCam)
        menuCam.position[menuCam.viewportWidth / 2, menuCam.viewportHeight / 2] = 0f
        menuCam.update()
        stage = Stage(menuViewport, app.batch)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        val mainTable = Table()
        val slidersTable = Table()
        val componentsTable = Table()
        mainTable.setFillParent(true)
        mainTable.top()
        mainTable.align(Align.center)
        val eyeCheckbox = CheckBox("Eye", skin)
        val mouthCheckbox = CheckBox("Mouth", skin)
        val flagellaCheckbox = CheckBox("Flagella", skin)
        val hornCheckbox = CheckBox("Horn", skin)
        val gripperCheckbox = CheckBox("Gripper", skin)

        // Booleans for checking whether the checkboxes are enabled
        // Creature components are built based on which of these are true
        val eyeEnabled = AtomicBoolean(false)
        val mouthEnabled = AtomicBoolean(false)
        val flagellaEnabled = AtomicBoolean(false)
        val hornEnabled = AtomicBoolean(false)
        val gripperEnabled = AtomicBoolean(false)
        eyeCheckbox.isChecked = false
        mouthCheckbox.isChecked = false
        flagellaCheckbox.isChecked = false
        hornCheckbox.isChecked = false

        // Checking whether the builders for each component are present for user account and automatically enabling


        // Flagella and mouth enabled by default
        flagellaCheckbox.isChecked = true
        flagellaAllowed = true
        flagellaEnabled.set(true)
        mouthCheckbox.isChecked = true
        mouthAllowed = true
        mouthEnabled.set(true)

        // Rest of the components check if the user bought a level
        if (GameClient.getInstance().user.eyeLevel == 2) {
            eyeCheckbox.isChecked = true
            eyeAllowed = true
            eyeEnabled.set(true)
        }
        if (GameClient.getInstance().user.hornLevel == 2) {
            hornCheckbox.isChecked = true
            hornAllowed = true
            hornEnabled.set(true)
        }
        if (GameClient.getInstance().user.gripperLevel == 2) {
            gripperCheckbox.isChecked = true
            gripperAllowed = true
            gripperEnabled.set(true)
        }
        if (!eyeAllowed) eyeCheckbox.setColor(0f, 0f, 0f, .5f)
        if (!mouthAllowed) mouthCheckbox.setColor(0f, 0f, 0f, .5f)
        if (!flagellaAllowed) flagellaCheckbox.setColor(0f, 0f, 0f, .5f)
        if (!hornAllowed) hornCheckbox.setColor(0f, 0f, 0f, .5f)
        if (!gripperAllowed) gripperCheckbox.setColor(0f, 0f, 0f, .5f)
        val user = GameClient.getInstance().user

        // Sliders //
        // Mutation Rate
        val mutationRateSlider = Slider(0f, user.maxMutationRate.toFloat(), 0.1f, false, skin)
        mutationRateSlider.setValue(user.maxMutationRate.toFloat())
        val mutationRateLabel = Label("Mutation Rate", skin)
        val mutationRateValueLabel = Label(user.maxMutationRate.toFloat().toString(), skin)

        // Creatures Per Second
        // Slider max bound might need adjustment
        val creaturesPerSecondSlider = Slider(0f, user.maxCreaturesPerSecond.toFloat(), 1f, false, skin)
        creaturesPerSecondSlider.setValue(user.maxCreaturesPerSecond.toFloat())
        val creaturesPerSecondLabel = Label("Creature Spawn Rate", skin)
        val creaturesPerSecondValueLabel = Label(user.maxCreaturesPerSecond.toString(), skin)

        // Initial Creatures
        val initialCreaturesSlider = Slider(0f, user.maxInitialCreatures.toFloat(), 1f, false, skin)
        initialCreaturesSlider.setValue(user.maxInitialCreatures.toFloat())
        val initialCreaturesLabel = Label("Initial Population", skin)
        val initialCreaturesValueLabel = Label(user.maxInitialCreatures.toString(), skin)

        // Creature Spread
        val creatureDeviationSlider = Slider(128f, 2048f, 1f, false, skin)
        creatureDeviationSlider.setValue(2048f)
        val creatureDeviationLabel = Label("Creature Deviation", skin)
        val creatureDeviationValueLabel = Label(2048.toString(), skin)

        // Initial Food
        val initialFoodSlider = Slider(0f, user.maxFood.toFloat(), 1f, false, skin)
        initialFoodSlider.setValue(user.maxFood.toFloat())
        val initialFoodLabel = Label("Initial Food", skin)
        val initialFoodValueLabel = Label(user.maxFood.toString(), skin)

        // Food Target
        val foodTargetSlider = Slider(0f, user.maxFood.toFloat(), 1f, false, skin)
        foodTargetSlider.setValue(user.maxFood.toFloat())
        val foodTargetLabel = Label("Food Target", skin)
        val foodTargetValueLabel = Label(user.maxFood.toString(), skin)

        // Food Spread
        val foodDeviationSlider = Slider(128f, 2048f, 1f, false, skin)
        foodDeviationSlider.setValue(2048f)
        val foodDeviationLabel = Label("Food Deviation", skin)
        val foodDeviationValueLabel = Label(2048.toString(), skin)

        // Min Sensors
        val minSensorSlider = Slider(0f, user.maxSensors.toFloat(), 1f, false, skin)
        minSensorSlider.setValue(0f)
        val minSensorLabel = Label("Min Sensors", skin)
        val minSensorValueLabel = Label(0.toString(), skin)

        // Max Sensors
        val maxSensorSlider = Slider(0f, user.maxSensors.toFloat(), 1f, false, skin)
        maxSensorSlider.setValue(user.maxSensors.toFloat())
        val maxSensorLabel = Label("Max Sensors", skin)
        val maxSensorValueLabel = Label(user.maxSensors.toString(), skin)

        // Min Tools
        val minToolSlider = Slider(0f, user.maxTools.toFloat(), 1f, false, skin)
        minToolSlider.setValue(properties.minTools.toFloat())
        val minToolLabel = Label("Min Tools", skin)
        val minToolValueLabel = Label(0.toString(), skin)

        // Max Tools
        val maxToolSlider = Slider(properties.minTools.toFloat(), user.maxTools.toFloat(), 1f, false, skin)
        maxToolSlider.setValue(properties.maxTools.toFloat())
        val maxToolLabel = Label("Max Tools", skin)
        val maxToolValueLabel = Label(user.maxTools.toString(), skin)

        // Buttons
        val continueButton = TextButton("Continue", skin)
        val backButton = TextButton("Go Back", skin)

        // Slider listeners
        mutationRateSlider.addListener { event: Event? ->
            val mutationRate = mutationRateSlider.value
            properties.globalMutationRate = mutationRate
            mutationRateValueLabel.setText(((mutationRate * 100).toInt() / 100f).toString())
            false
        }
        creaturesPerSecondSlider.addListener { event: Event? ->
            val creaturesPerSecond = creaturesPerSecondSlider.value.toInt()
            properties.creaturesPerSecond = creaturesPerSecond
            creaturesPerSecondValueLabel.setText(creaturesPerSecond.toString())
            false
        }
        initialCreaturesSlider.addListener { event: Event? ->
            val initialCreatures = initialCreaturesSlider.value.toInt()
            properties.initialCreatures = initialCreatures
            initialCreaturesValueLabel.setText(initialCreatures.toString())
            false
        }
        creatureDeviationSlider.addListener { event: Event? ->
            val creatureDeviation = creatureDeviationSlider.value.toInt()
            properties.creatureSpawnStd = creatureDeviation.toFloat()
            creatureDeviationValueLabel.setText(creatureDeviation.toString())
            false
        }
        initialFoodSlider.addListener { event: Event? ->
            val initialFood = initialFoodSlider.value.toInt()
            if (initialFood > properties.foodTarget) {
                properties.foodTarget = initialFood
                initialFoodValueLabel.setText(initialFood.toString())
                initialFoodSlider.setValue(initialFood.toFloat())
            }
            properties.initialFood = initialFood
            initialFoodValueLabel.setText(initialFood.toString())
            false
        }
        foodTargetSlider.addListener { event: Event? ->
            val foodTarget = foodTargetSlider.value.toInt()
            if (foodTarget > properties.initialFood) {
                properties.initialFood = foodTarget
                foodTargetValueLabel.setText(foodTarget.toString())
                foodTargetSlider.setValue(foodTarget.toFloat())
            }
            properties.foodTarget = foodTarget
            foodTargetValueLabel.setText(foodTarget.toString())
            false
        }
        foodDeviationSlider.addListener { event: Event? ->
            val foodStd = foodDeviationSlider.value.toInt()
            properties.foodSpawnStd = foodStd.toFloat()
            foodDeviationValueLabel.setText(foodStd.toString())
            false
        }
        minSensorSlider.addListener { event: Event? ->
            val minSensors = minSensorSlider.value.toInt()
            if (minSensors > properties.maxSensors) {
                properties.maxSensors = minSensors
                maxSensorValueLabel.setText(minSensors.toString())
                maxSensorSlider.setValue(minSensors.toFloat())
            }
            properties.minSensors = minSensors
            minSensorValueLabel.setText(minSensors.toString())
            false
        }
        maxSensorSlider.addListener { event: Event? ->
            val maxSensors = maxSensorSlider.value.toInt()
            if (maxSensors < properties.minSensors) {
                properties.minSensors = maxSensors
                minSensorValueLabel.setText(maxSensors.toString())
                minSensorSlider.setValue(maxSensors.toFloat())
            }
            properties.maxSensors = maxSensors
            maxSensorValueLabel.setText(maxSensors.toString())
            false
        }
        minToolSlider.addListener { event: Event? ->
            val minTools = minToolSlider.value.toInt()
            if (minTools > properties.maxTools) {
                properties.maxTools = minTools
                maxToolValueLabel.setText(minTools.toString())
                maxToolSlider.setValue(minTools.toFloat())
            }
            properties.minTools = minTools
            minToolValueLabel.setText(minTools.toString())
            false
        }
        maxToolSlider.addListener { event: Event? ->
            val maxTools = maxToolSlider.value.toInt()
            if (maxTools < properties.minTools) {
                properties.minTools = maxTools
                minToolValueLabel.setText(maxTools.toString())
                minToolSlider.setValue(maxTools.toFloat())
            }
            properties.maxTools = maxTools
            maxToolValueLabel.setText(maxTools.toString())
            false
        }

        // Checkbox listeners
        eyeCheckbox.addListener { event: Event? ->
            if (eyeAllowed) {
                eyeEnabled.set(eyeCheckbox.isChecked)
            } else {
                eyeCheckbox.isChecked = false
            }
            false
        }
        mouthCheckbox.addListener { event: Event? ->
            if (mouthAllowed) {
                mouthEnabled.set(mouthCheckbox.isChecked)
            } else {
                mouthCheckbox.isChecked = false
            }
            false
        }
        flagellaCheckbox.addListener { event: Event? ->
            if (flagellaAllowed) {
                flagellaEnabled.set(flagellaCheckbox.isChecked)
            } else {
                flagellaCheckbox.isChecked = false
            }
            false
        }
        hornCheckbox.addListener { event: Event? ->
            if (hornAllowed) {
                hornEnabled.set(hornCheckbox.isChecked)
            } else {
                hornCheckbox.isChecked = false
            }
            false
        }
        gripperCheckbox.addListener { event: Event? ->
            if (gripperAllowed) {
                gripperEnabled.set(gripperCheckbox.isChecked)
            } else {
                gripperCheckbox.isChecked = false
            }
            false
        }

        // Button listeners
        continueButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                // Continue button adds the components all at once based on booleans and goes to next screen
                if (eyeEnabled.get()) {
                    properties.sensorBuilders.add(EyeBuilder())
                }
                if (mouthEnabled.get()) {
                    properties.toolBuilders.add(MouthBuilder())
                }
                if (flagellaEnabled.get()) {
                    properties.toolBuilders.add(FlagellaBuilder())
                }
                if (hornEnabled.get()) {
                    properties.toolBuilders.add(HornBuilder())
                }
                if (gripperEnabled.get()) {
                    properties.toolBuilders.add(GripperBuilder())
                }
                ScreenStack.push(SimScreen(app, GameClient.getInstance().user, properties))
            }
        })
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                ScreenStack.switchTo(MainMenuScreen(app))
            }
        })
        val width = 75f

        // Components column
        componentsTable.row().pad(0f, 0f, 10f, 0f)
        componentsTable.add(flagellaCheckbox).align(Align.left)
        componentsTable.row().pad(0f, 0f, 10f, 0f)
        componentsTable.add(mouthCheckbox).align(Align.left)
        componentsTable.row().pad(0f, 0f, 10f, 0f)
        componentsTable.add(eyeCheckbox).align(Align.left)
        componentsTable.row().pad(0f, 0f, 10f, 0f)
        componentsTable.add(hornCheckbox).align(Align.left)
        componentsTable.row().pad(0f, 0f, 10f, 0f)
        componentsTable.add(gripperCheckbox).align(Align.left)

        // Sliders column
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(mutationRateLabel).align(Align.left)
        slidersTable.add(mutationRateSlider)
        slidersTable.add(mutationRateValueLabel).pad(0f, 15f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(creaturesPerSecondLabel).align(Align.left)
        slidersTable.add(creaturesPerSecondSlider)
        slidersTable.add(creaturesPerSecondValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(initialCreaturesLabel).align(Align.left)
        slidersTable.add(initialCreaturesSlider)
        slidersTable.add(initialCreaturesValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(creatureDeviationLabel).align(Align.left)
        slidersTable.add(creatureDeviationSlider)
        slidersTable.add(creatureDeviationValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(initialFoodLabel).align(Align.left)
        slidersTable.add(initialFoodSlider)
        slidersTable.add(initialFoodValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(foodTargetLabel).align(Align.left)
        slidersTable.add(foodTargetSlider)
        slidersTable.add(foodTargetValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(foodDeviationLabel).align(Align.left)
        slidersTable.add(foodDeviationSlider)
        slidersTable.add(foodDeviationValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(minSensorLabel).align(Align.left)
        slidersTable.add(minSensorSlider)
        slidersTable.add(minSensorValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(maxSensorLabel).align(Align.left)
        slidersTable.add(maxSensorSlider)
        slidersTable.add(maxSensorValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(minToolLabel).align(Align.left)
        slidersTable.add(minToolSlider)
        slidersTable.add(minToolValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        slidersTable.row().pad(0f, 0f, 10f, 10f)
        slidersTable.add(maxToolLabel).align(Align.left)
        slidersTable.add(maxToolSlider)
        slidersTable.add(maxToolValueLabel).pad(0f, 10f, 0f, 0f).align(Align.center).width(width)
        mainTable.add(slidersTable)
        mainTable.add(componentsTable).top()
        mainTable.row().pad(10f, 0f, 10f, 0f)
        mainTable.add(continueButton).width(100f).align(Align.center)
        mainTable.row().pad(0f, 0f, 10f, 0f)
        mainTable.add(backButton).width(100f).align(Align.center)
        stage.addActor(mainTable)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        app.batch.projectionMatrix = menuCam.combined
        app.batch.begin()
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1f)
        app.shapeDrawer.filledRectangle(15f, 15f, menuViewport.worldWidth - 35, menuViewport.worldHeight - 35)
        app.batch.end()
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        menuCam.update()
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        stage.dispose()
    }
}
