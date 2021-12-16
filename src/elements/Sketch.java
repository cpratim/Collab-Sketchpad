import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Amy Morissey and Pratim Chowdhary
 * A class to hold the current sketch
 */

public class Sketch {
    // set up the map for IDs to shapes
    public Map<Integer, Shape> IDtoShape;

    public Sketch(){
        IDtoShape = new TreeMap<>();
    }

    // put a new shape in the map
    public synchronized void addShape(int ID, Shape newShape){
        IDtoShape.put(ID, newShape);
    }

    // remove a shape from the map
    public synchronized void deleteShape(int ID){
        if (IDtoShape.containsKey(ID)){
            IDtoShape.remove(ID);
        }

    }

    // recolor one of the shapes
    public synchronized void recolorShape(int ID, Color newColor){
        if (IDtoShape.containsKey(ID)) {
            IDtoShape.get(ID).setColor(newColor);
        }
    }
    // move one of the shapes
    public synchronized void moveShape(int ID, int dx, int dy){
        if (IDtoShape.containsKey(ID)) {
            IDtoShape.get(ID).moveBy(dx, dy);
        }

    }
    // get a shape id from a location
    public synchronized int chosenShapeID(int x, int y){
        int returnID = -1; // default if it doesnt exist
        // sort the keys in ascending order
        ArrayList<Integer> keyset = new ArrayList<>(IDtoShape.keySet());
        keyset.sort(Integer::compareTo);
        // check if any of them have the point
        for(int id: keyset){
            if (IDtoShape.get(id).contains(x,y)) {
                return id;
            }
        }
        return returnID;
    }

    // return the whole map
    public synchronized Map<Integer, Shape> getState() {
        return IDtoShape;
    }

    // draw every single shape
    public synchronized void drawAll(Graphics g) {
        for (int id: IDtoShape.keySet()) {
            IDtoShape.get(id).draw(g);
        }
    }

    // make a message for the current state
    public String allMessage() {
        String message = "";
        // add every shape to it with a # for ID seperation
        for (int id: IDtoShape.keySet()) {
            message += id + "#" + IDtoShape.get(id).toString() + ";";
        }
        return message;
    }

}