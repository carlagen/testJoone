package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.joone.engine.Monitor;
import org.joone.engine.Layer;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.learning.*;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;


public class testEXor implements NeuralNetListener {

    private static String objectFile = "/tmp/object.txt";
    private double[][] inputArray = {{0,0}, {0,1}, {1,0}, {1,1}};

    public static void main(String[] args) throws InterruptedException {
		new testEXor().go2();
		Thread.sleep(2000);
		new testEXor().go();

	}
    public NeuralNet restoreNeuralNet(String filename) {
    	NeuralNet nnet = null;
    	try {
	    	FileInputStream stream = new FileInputStream(filename);
	    	ObjectInputStream inp = new ObjectInputStream(stream);
	    	nnet = (NeuralNet)inp.readObject();
    	}
    	catch (Exception excp) {
    		excp.printStackTrace();
    		return nnet;
    	}
    	/*
    	* After that, we can restore all the internal variables to manage
    	* the neural network and, finally, we can run it.
    	*/
    	/* The main application registers itself as a NN’s listener */
    	/*nnet.getMonitor().addNeuralNetListener(this);*/
    	/* Now we can run the restored net */
    	/* nnet.go(); The net starts the training job */
    	return nnet;
    }

    public void go(){
    	// We load the serialized XOR neural net
    	NeuralNet xor = restoreNeuralNet(objectFile);
    	if (xor != null) {
	    	/* We get the first layer of the net (the input layer),
	    	then remove all the input synapses attached to it
	    	and attach a MemoryInputSynapse */
	    	Layer input = xor.getInputLayer();
	    	input.removeAllInputs();
	    	MemoryInputSynapse memInp = new MemoryInputSynapse();
	    	memInp.setFirstRow(1);
	    	memInp.setAdvancedColumnSelector("1,2");
	    	input.addInputSynapse(memInp);
	    	memInp.setInputArray(inputArray);
	    	/* We get the last layer of the net (the output layer),
	    	then remove all the output synapses attached to it
	    	and attach a MemoryOutputSynapse */
	    	Layer output = xor.getOutputLayer();
	    	// Remove all the output synapses attached to it...
	    	output.removeAllOutputs();
	    	//...and attach a MemoryOutputSynapse
	    	MemoryOutputSynapse memOut = new MemoryOutputSynapse();
	    	output.addOutputSynapse(memOut);
	    	// Now we interrogate the net
	    	xor.getMonitor().addNeuralNetListener(this);
	    	/*xor.getMonitor().setTotCicles(1);
	    	xor.getMonitor().setTrainingPatterns(4);
	    	xor.getMonitor().setLearning(false);
	    	xor.start();
	    	xor.getMonitor().Go();*/
	    	xor.go();
	    	for (int i=0; i < 4; ++i) {
		    	// Read the next pattern and print out it
		    	double[] pattern = memOut.getNextPattern();
		    	System.out.println("Output Pattern #"+(i+1)+" = "+pattern[0]);
	    	}
	    	xor.stop();
	    	System.out.println("Finished");
    	}
    }

    public void go2() {
    	NeuralNet xor = restoreNeuralNet(objectFile);
    	xor.getMonitor().addNeuralNetListener(this);
    	/* Now we can run the restored net */
    	xor.go(); /* The net starts the training job */
    }

    public void netStopped(NeuralNetEvent e) {
        System.out.println("Training finished");
    }
    
    public void cicleTerminated(NeuralNetEvent e) {
    }
    
    public void netStarted(NeuralNetEvent e) {
        System.out.println("Training...");
    }
    
    public void errorChanged(NeuralNetEvent e) {
    	Monitor mon = (Monitor)e.getSource();
        /* We want print the results every 200 cycles */
        if (mon.getCurrentCicle() % 200 == 0)
            System.out.println(mon.getCurrentCicle() + " epochs remaining - RMSE = " + mon.getGlobalError());
    }
    
    public void netStoppedError(NeuralNetEvent e,String error) {
    }

    public void saveNeuralNet(String fileName, NeuralNet nnet) {
    	try {
	    	FileOutputStream stream = new FileOutputStream(fileName);
	    	ObjectOutputStream out = new ObjectOutputStream(stream);
	    	out.writeObject(nnet);
	    	out.close();
    	}
    	catch (Exception excp) {
    		excp.printStackTrace();
    	}
   	}
}
