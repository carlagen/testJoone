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
import org.joone.net.NeuralNet;


public class testEXor implements NeuralNetListener {

    private static String objectFile = "/tmp/object.txt";

    public static void main(String[] args) {
		new testEXor().restoreNeuralNet("");

	}
    public void restoreNeuralNet(String filename) {
    	NeuralNet nnet = null;
    	try {
	    	FileInputStream stream = new FileInputStream(objectFile);
	    	ObjectInputStream inp = new ObjectInputStream(stream);
	    	nnet = (NeuralNet)inp.readObject();
    	}
    	catch (Exception excp) {
    		excp.printStackTrace();
    		return;
    	}
    	/*
    	* After that, we can restore all the internal variables to manage
    	* the neural network and, finally, we can run it.
    	*/
    	/* The main application registers itself as a NN’s listener */
    	nnet.getMonitor().addNeuralNetListener(this);
    	/* Now we can run the restored net */
        nnet.go(); /* The net starts the training job */
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
