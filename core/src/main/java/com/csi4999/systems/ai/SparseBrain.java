package com.csi4999.systems.ai;

import com.badlogic.gdx.math.RandomXS128;

import java.util.*;

public class SparseBrain implements Brain{
    // TODO: change this (and other systems maybe) to target a number of edges?
    private static final float ADD_EDGE_CHANCE = 0.025f;
    private static final float REMOVE_EDGE_CHANCE = 0.025f;
    private static final float MUTATE_WEIGHT_STD = 0.01f;
    // TODO: cycles can form that do not impact the output, but through mutations connections can form to these cycles
    // enabling them to impact the output. optimizing these out would improve performance, but these dormant cycles
    // could be "re-enabled" when mutations occur. i think we should "archive" the original brain before pruning these
    // cycles and use that original instance when generating mutations.

    // implementing this as parallel arrays for maximum performance
    private float[] neuronValues;
    private float[] pNeuronValues;
    private float[] weights;
    private float[] bias;
    private int[] edges;
    private float[] output;

    private int currentInputSize;


    public SparseBrain() {} // empty constructor for Kryo

    // Full constructor
    public SparseBrain(int inputs, int outputs, int hiddenNeurons, float inputToHiddenConnectivity,
                       float inputToOutputConnectivity, float hiddenToHiddenConnectivity, float hiddenToOutputConnectivity, Random rand) {
        this.currentInputSize = inputs;
        // calculate number of weights between input, hidden, output using connectivity
        // we have these possible connections: {input, hidden} -> {hidden, output}
        int inputToHiddenWeights = (int) (inputToHiddenConnectivity * inputs * hiddenNeurons);
        int inputToOutputWeights = (int) (inputToOutputConnectivity * inputs * outputs);
        int hiddenToHiddenWeights = (int) (hiddenToHiddenConnectivity * hiddenNeurons * hiddenNeurons);
        int hiddenToOutputWeights = (int) (hiddenToOutputConnectivity * hiddenNeurons * outputs);

        // total number of weights
        int weights = inputToHiddenWeights + inputToOutputWeights + hiddenToHiddenWeights + hiddenToOutputWeights;

        // total number of neurons
        int neurons = inputs + outputs + hiddenNeurons;

        this.neuronValues = new float[neurons];
        this.pNeuronValues = new float[neurons];
        this.bias = new float[neurons];
        this.weights = new float[weights];
        this.edges = new int[weights * 2];
        this.output = new float[outputs];

        // init neuron values to 0
        Arrays.fill(this.neuronValues, 0f);
        Arrays.fill(this.pNeuronValues, 0f);

        // generate initial weights & bias with mean = 0, std = 1
        for (int i = 0; i < this.weights.length; i++)
            this.weights[i] = (float) rand.nextGaussian();
        for (int i = 0; i < this.bias.length; i++)
            this.bias[i] = (float) rand.nextGaussian();

        // to select the initial edges, make four lists of the possible edges
        // then select without replacement from these lists
        List<int[]> inToHid = new ArrayList<>(inputs * hiddenNeurons);
        List<int[]> inToOut = new ArrayList<>(inputs * outputs);
        List<int[]> hidToHid = new ArrayList<>(hiddenNeurons * hiddenNeurons);
        List<int[]> hidToOut = new ArrayList<>(hiddenNeurons * outputs);

        for (int i = 0; i < inputs; i++)
            for (int j = inputs; j < inputs + hiddenNeurons; j++)
                inToHid.add(new int[]{i, j});
        for (int i = 0; i < inputs; i++)
            for (int j = inputs + hiddenNeurons; j < inputs + hiddenNeurons + outputs; j++)
                inToOut.add(new int[]{i, j});
        for (int i = inputs; i < inputs + hiddenNeurons; i++)
            for (int j = inputs; j < inputs + hiddenNeurons; j++)
                hidToHid.add(new int[]{i, j});
        for (int i = inputs; i < inputs + hiddenNeurons; i++)
            for (int j = inputs + hiddenNeurons; j < inputs + hiddenNeurons + outputs; j++)
                hidToOut.add(new int[]{i, j});

        // shuffle
        Collections.shuffle(inToHid, rand);
        Collections.shuffle(inToOut, rand);
        Collections.shuffle(hidToHid, rand);
        Collections.shuffle(hidToOut, rand);

        // take subsets, insert into edges
        int i = 0;
//        inToHid.subList(0, inputToHiddenWeights).forEach(edge -> { this.edges[i++] = edge[0]; }); // bruh no closures :,)
        for (int[] edge : inToHid.subList(0, inputToHiddenWeights)) {
            this.edges[i++] = edge[0];
            this.edges[i++] = edge[1];
        }
        for (int[] edge : inToOut.subList(0, inputToOutputWeights)) {
            this.edges[i++] = edge[0];
            this.edges[i++] = edge[1];
        }
        for (int[] edge : hidToHid.subList(0, hiddenToHiddenWeights)) {
            this.edges[i++] = edge[0];
            this.edges[i++] = edge[1];
        }
        for (int[] edge : hidToOut.subList(0, hiddenToOutputWeights)) {
            this.edges[i++] = edge[0];
            this.edges[i++] = edge[1];
        }
    }

    /**
     *
     * @param source - the source of the edge
     * @param destination - the destination of the edge
     * @return index of the edge's source in this.edges, -1 if not found
     */
    private int findEdge(int source, int destination) {
        for (int i = 0; i < this.edges.length; i += 2)
            if (this.edges[i] == source && this.edges[i+1] == destination) return i;
        return -1;
    }

    @Override
    public void mutate(float amount, Random rand) {
        float rn = rand.nextFloat();
        if (rn < ADD_EDGE_CHANCE * amount) {
            // TODO add edge
        } else if (rn < ADD_EDGE_CHANCE * amount + REMOVE_EDGE_CHANCE + amount) {
            // TODO remove edge
        }

        // mutate weights
        for (int i = 0; i < this.weights.length; i++)
            this.weights[i] += rand.nextGaussian() * MUTATE_WEIGHT_STD;
    }

    @Override
    public float[] run(float[] input) {
        // copy input into previous neuron values
        System.arraycopy(input, 0, pNeuronValues, 0, input.length);
        // compute current neuron values
        Arrays.fill(neuronValues, 0f);
        // perform sparse vector-matrix multiply thingy
        for (int i = 0; i < edges.length; i += 2)
            neuronValues[edges[i+1]] += pNeuronValues[edges[i]] * weights[i/2];
        // add bias
        for (int i = 0; i < neuronValues.length; i++)
            neuronValues[i] += bias[i];

        // apply activation function
        for (int i = 0; i < neuronValues.length; i++)
            neuronValues[i] = (float) Math.tanh(neuronValues[i]);

        // copy output neurons to output
        System.arraycopy(this.neuronValues, neuronValues.length - output.length, output, 0, output.length);

        // swap current and previous neurons
        float[] temp = this.neuronValues;
        this.neuronValues = this.pNeuronValues;
        this.pNeuronValues = temp;

        return output;
    }

    @Override
    public void resizeInput(int newInputSize) {
        // weights are the only thing that don't change
        if (currentInputSize != newInputSize) {
            int delta = newInputSize - currentInputSize;
            float[] newNeurons = new float[neuronValues.length + delta];
            float[] newPNeurons = new float[neuronValues.length + delta];
            float[] newBias = new float[neuronValues.length + delta];
            Arrays.fill(newNeurons, 0f);
            Arrays.fill(newPNeurons, 0f);
            Arrays.fill(newBias, 0f); // new neurons are only inputs, so we don't care about initializing bias with gaussian

            System.arraycopy(neuronValues, 0, newNeurons, 0, currentInputSize);
            System.arraycopy(neuronValues, 0, newPNeurons, 0, currentInputSize);
            System.arraycopy(bias, 0, newBias, 0, bias.length);

            System.arraycopy(neuronValues, currentInputSize, newNeurons, newInputSize, neuronValues.length - currentInputSize);
            System.arraycopy(neuronValues, currentInputSize, newPNeurons, newInputSize, neuronValues.length - currentInputSize);
            System.arraycopy(bias, currentInputSize, newBias, newInputSize, bias.length - currentInputSize);

            if (newInputSize > currentInputSize) {
                // shift down edges that are greater than or equal to currentInputSize
                for (int i = 0; i < edges.length; i++) {
                    if (edges[i] >= currentInputSize) edges[i] += delta;
                }
            } else {
                // delete edges that are greater than or equal to new
                // first, count the number of edges that need to be deleted
                int toDelete = 0;
                for (int i = 0; i < edges.length; i += 2) {
                    if ((edges[i] < currentInputSize && edges[i] >= newInputSize) || (edges[i+1] < currentInputSize && edges[i+1] >= newInputSize)) toDelete++;
                }
                int[] newEdges = new int[edges.length - toDelete * 2];
                float[] newWeights = new float[newEdges.length/2];
                // copy over to new arrays
                int index = 0;
                for (int i = 0; i < edges.length; i += 2) {
                    // if we are keeping this edge,
                    if ((edges[i] >= currentInputSize || edges[i] < newInputSize) && (edges[i + 1] >= currentInputSize || edges[i + 1] < newInputSize)) {
                        newEdges[index++] = edges[i];
                        newEdges[index++] = edges[i+1];
                        newWeights[index/2] = weights[i/2];
                    }
                }
                // swap in new arrays
                edges = newEdges;
                weights = newWeights;
            }
            currentInputSize = newInputSize;
        }
    }

    @Override
    public void resizeOutput(int newOutputSize) {

    }
}
