package com.csi4999.systems.ai;

import java.util.*;

public class SparseBrain implements Brain {
    // TODO: change this (and other systems maybe) to target a number of edges?
    private static final float ADD_EDGE_CHANCE = 0.025f;
    private static final float REMOVE_EDGE_CHANCE = 0.025f;
    private static final float ADD_NEURON_CHANCE = 0.01f;
    private static final float REMOVE_NEURON_CHANCE = 0.01f;
    private static final float MUTATE_WEIGHT_STD = 0.01f;
    private static final float ENERGY_PER_WEIGHT = 0.001f;
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

    // copy constructor
    public SparseBrain(SparseBrain b) {
        neuronValues = b.neuronValues.clone();
        pNeuronValues = b.pNeuronValues.clone();
        weights = b.weights.clone();
        bias = b.bias.clone();
        edges = b.edges.clone();
        output = b.output.clone();
        currentInputSize = b.currentInputSize;
    }


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
     * @param source - the source of the edge
     * @param destination - the destination of the edge
     * @return index of the edge's source in this.edges, -1 if not found
     */
    private int findEdge(int source, int destination) {
        for (int i = 0; i < this.edges.length; i += 2)
            if (this.edges[i] == source && this.edges[i+1] == destination) return i;
        return -1;
    }

    private boolean tryAddEdge(int source, int destination, float weight) {
        if (findEdge(source, destination) == -1) {
            int[] newEdges = new int[edges.length+2];
            float[] newWeights = new float[newEdges.length/2];
            System.arraycopy(edges, 0, newEdges, 0, edges.length);
            System.arraycopy(weights, 0, newWeights, 0, weights.length);
            newEdges[newEdges.length-2] = source;
            newEdges[newEdges.length-1] = destination;
            newWeights[newWeights.length-1] = weight;
            edges = newEdges;
            weights = newWeights;
            return true;
        } else {
            return false;
        }
    }

    private boolean tryRemoveEdge(int source, int destination) {
        // TODO: since we don't care about edge order, instead of doing two arraycopies per array, just swap removed
        // edge with the last edge then copy all over except for last
        int edgePos = findEdge(source, destination);
        if (edgePos == -1) {
            return false;
        } else {
            int[] newEdges = new int[edges.length-2];
            float[] newWeights = new float[newEdges.length/2];
            System.arraycopy(edges, 0, newEdges, 0, edgePos);
            System.arraycopy(edges, edgePos+2, newEdges, edgePos, edges.length - edgePos - 2);
            System.arraycopy(weights, 0, newWeights, 0, edgePos/2);
            System.arraycopy(weights, edgePos/2+1, newWeights, edgePos/2, (edges.length - edgePos - 2)/2);
            edges = newEdges;
            weights = newWeights;
            return true;
        }
    }

    @Override
    public void mutate(float amount, Random rand) {
        float rn = rand.nextFloat();
        if (rn < ADD_EDGE_CHANCE * amount) {
            // add edge
            // pick random edge from {in, hidden} x {hidden, out}
            int src = rand.nextInt(neuronValues.length - output.length);
            int dst = rand.nextInt(currentInputSize, neuronValues.length);
            if (tryAddEdge(src, dst, (float) rand.nextGaussian())) {
//                System.out.println("Added edge: " + src + " -> " + dst);
            } else {
//                System.out.println("Failed to add edge: " + src + " -> " + dst);
            }
        } else if (rn < (ADD_EDGE_CHANCE + REMOVE_EDGE_CHANCE) * amount) {
            // remove edge
            // pick random edge from {in, hidden} x {hidden, out}
            int src = rand.nextInt(neuronValues.length - output.length);
            int dst = rand.nextInt(currentInputSize, neuronValues.length);
            if (tryRemoveEdge(src, dst)) {
//                System.out.println("Removed edge: " + src + " -> " + dst);
            } else {
//                System.out.println("Failed to remove edge: " + src + " -> " + dst);
            }
        } else if (rn < (ADD_EDGE_CHANCE + REMOVE_EDGE_CHANCE + ADD_NEURON_CHANCE) * amount) {
            // add (hidden) neuron
            int hiddenNeurons = neuronValues.length - currentInputSize - output.length;
            int toAdd = rand.nextInt(hiddenNeurons) + currentInputSize;
            insertNeuron(toAdd, (float) rand.nextGaussian());
//            System.out.println("Added neuron " + toAdd);
        } else if (rn < (ADD_EDGE_CHANCE + REMOVE_EDGE_CHANCE + ADD_NEURON_CHANCE + REMOVE_NEURON_CHANCE) * amount) {
            // remove (hidden) neuron
            int hiddenNeurons = neuronValues.length - currentInputSize - output.length;
            if (hiddenNeurons > 0) {
                int toRemove = rand.nextInt(hiddenNeurons) + currentInputSize;
                removeNeuron(toRemove);
//                System.out.println("Removed neuron " + toRemove);
            }
        }

        // mutate weights
        for (int i = 0; i < this.weights.length; i++)
            this.weights[i] += rand.nextGaussian() * MUTATE_WEIGHT_STD;
    }

    @Override
    public float[] run(float[] input) {
        // copy input into previous neuron values
        if (input != null)
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
    public void insertInput(int inputIndex, Random rand) {
        insertNeuron(inputIndex, (float) rand.nextGaussian());
        currentInputSize++;
    }

    @Override
    public void insertOutput(int outputIndex, Random rand) {
        insertNeuron(outputIndex + neuronValues.length - output.length, (float) rand.nextGaussian());
        float[] newOutput = new float[output.length+1];
        System.arraycopy(output, 0, newOutput, 0, output.length);
        newOutput[newOutput.length-1] = 0f;
        output = newOutput;
    }

    @Override
    public void removeInput(int inputIndex) {
        assert(currentInputSize > 0);
        removeNeuron(inputIndex);
        currentInputSize--;
    }

    @Override
    public void removeOutput(int outputIndex) {
        assert(output.length > 1); // can't have zero length array, no outputs doesn't even make sense
        removeNeuron(outputIndex + neuronValues.length - output.length);
        float[] newOutput = new float[output.length-1];
        System.arraycopy(output, 0, newOutput, 0, output.length);
        output = newOutput;
    }

    @Override
    public Brain copy() {
        return new SparseBrain(this);
    }

    private void insertNeuron(int neuronIndex, float neuronBias) {
        float[] newNeurons = new float[neuronValues.length + 1];
        float[] newPNeurons = new float[neuronValues.length + 1];
        float[] newBias = new float[neuronValues.length + 1];
        // copy the first chunk over (up to neuronIndex)
        if (neuronIndex > 0) {
            System.arraycopy(neuronValues, 0, newNeurons, 0, neuronIndex);
            System.arraycopy(pNeuronValues, 0, newPNeurons, 0, neuronIndex);
            System.arraycopy(bias, 0, newBias, 0, neuronIndex);
        }
        // copy the second chunk over (after neuronIndex)
        if (neuronValues.length-neuronIndex > 0) {
            System.arraycopy(neuronValues, neuronIndex, newNeurons, neuronIndex + 1, neuronValues.length - neuronIndex);
            System.arraycopy(pNeuronValues, neuronIndex, newPNeurons, neuronIndex + 1, pNeuronValues.length - neuronIndex);
            System.arraycopy(bias, neuronIndex, newBias, neuronIndex + 1, bias.length - neuronIndex);
        }
        // init
        newNeurons[neuronIndex] = 0f;
        newPNeurons[neuronIndex] = 0f;
        newBias[neuronIndex] = neuronBias;
        // swap
        neuronValues = newNeurons;
        pNeuronValues = newPNeurons;
        bias = newBias;

        // shift down edges >= neuronIndex
        for (int i = 0; i < edges.length; i++)
            if (edges[i] >= neuronIndex) edges[i]++;
    }

    private void removeNeuron(int neuronIndex) {
        float[] newNeurons = new float[neuronValues.length - 1];
        float[] newPNeurons = new float[neuronValues.length - 1];
        float[] newBias = new float[neuronValues.length - 1];
        // copy the first chunk over (up to neuronIndex)
        if (neuronIndex > 0) {
            System.arraycopy(neuronValues, 0, newNeurons, 0, neuronIndex);
            System.arraycopy(pNeuronValues, 0, newPNeurons, 0, neuronIndex);
            System.arraycopy(bias, 0, newBias, 0, neuronIndex);
        }
        // copy the second chunk over (after neuronIndex)
        if (neuronValues.length-neuronIndex > 0) {
            System.arraycopy(neuronValues, neuronIndex + 1, newNeurons, neuronIndex, neuronValues.length - (neuronIndex + 1));
            System.arraycopy(pNeuronValues, neuronIndex + 1, newPNeurons, neuronIndex, pNeuronValues.length - (neuronIndex + 1));
            System.arraycopy(bias, neuronIndex + 1, newBias, neuronIndex, bias.length - (neuronIndex + 1));
        }
        // swap
        neuronValues = newNeurons;
        pNeuronValues = newPNeurons;
        bias = newBias;

        // remove newly invalid edges
        int toDelete = 0;
        for (int i = 0; i < edges.length; i += 2)
            if (edges[i] == neuronIndex || edges[i+1] == neuronIndex) toDelete++;
//        System.out.println("Deleting " + toDelete + " edges.");
//        System.out.println("Edges: " + edges.length);
//        System.out.println("Weights: " + weights.length);
        int[] newEdges = new int[edges.length - toDelete * 2];
        float[] newWeights = new float[newEdges.length / 2];
        // copy over valid edges
        int currentEdge = 0;
        for (int i = 0; i < edges.length; i += 2) {
            if (edges[i] != neuronIndex && edges[i + 1] != neuronIndex) {
                newWeights[currentEdge/2] = weights[i/2];
                newEdges[currentEdge++] = edges[i];
                newEdges[currentEdge++] = edges[i+1];
            }
        }

        // swap
        edges = newEdges;
        weights = newWeights;

        // shift up edges > neuronIndex
        for (int i = 0; i < edges.length; i++)
            if (edges[i] > neuronIndex) edges[i]--;


    }

    @Override
    public float getEnergyConsumption() {
        return weights.length * ENERGY_PER_WEIGHT;
    }
}


