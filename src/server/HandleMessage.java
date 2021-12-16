import java.awt.*;
import java.util.ArrayList;

/**
 * @author Amy Morissey and Pratim Chowdhary
 * A class for handling server and client messages
 */
public class HandleMessage {

    // client method for handling new messages
    public static void clientHandle(String line, Sketch sketch) {
        // split into the parts we need that store different things
        String[] command = line.split("_");
        int id = Integer.parseInt(command[0]);
        String[] args = command[1].split("\\|");
        String action = args[0]; // the action is the first thing after the ID

        // case switch for the various actions
        switch (action) {
            case "delete" -> {
                // check if we have selected a proper element
                if (id == -1) {
                    System.out.println("Nothing chosen to delete");
                    return;
                }
                delete(id, sketch);
                System.out.println("Shape ID " + id + " was deleted");
            }
            case "move" -> {
                // get the paramters from splitting with a space
                String[] params = args[1].split(" ");
                if (id == -1) {
                    System.out.println("Nothing chosen to move");
                    return;
                }
                move(id, params, sketch);
                System.out.println("Shape ID " + id + " was moved");
            }
            case "recolor" -> {
                // get the paramters from splitting with a space
                String[] params = args[1].split(" ");
                if (id == -1) {
                    System.out.println("Nothing chosen to recolor");
                    return;
                }
                recolor(id, params, sketch);
                System.out.println("Shape ID " + id + " was recolored");
            }
            case "add" -> {
                // check which kind of shape we are adding and do the rest accordingly
                if (command[1].contains("polyline")) {
                    // string for the data of all the segments which make up the polyline
                    String segmentData = command[1].split(" ", 2)[1];
                    makePolyline(id, segmentData, sketch);
                    System.out.println("New freehand with ID " + id + " was added");
                } else {
                    // standard attributes of the simpler shapes
                    String[] attributes = args[1].split(" ");
                    String shape = attributes[0];
                    // check which type of shape we are adding
                    switch (shape) {
                        // call their respective functions
                        case "ellipse" -> {
                            makeEllipse(id, attributes, sketch);
                            System.out.println("New ellipse with ID " + id + " was added");
                        }
                        case "rectangle" -> {
                            makeRectangle(id, attributes, sketch);
                            System.out.println("New rectangle with ID " + id + " was added");
                        }
                        case "segment" -> {
                            makeSegment(id, attributes, sketch);
                            System.out.println("New segment with ID " + id + " was added");
                        }
                    }
                }
            }
            // special case for when a new person connects
            case "drawAll" -> {
                // the shapes are seperated by ;
                String[] shapes = args[1].split(";");
                // loop through all the shapes
                for (String shape: shapes) {
                    // get the attributes through spits
                    String[] params = shape.split("#");
                    int shapeID = Integer.parseInt(params[0]);
                    String[] attributes = params[1].split(" ");
                    String shapeType = attributes[0];
                    // draw the shapes accordingly
                    switch (shapeType) {
                        case "polyline" -> makePolyline(shapeID, params[1].split(" ", 2)[1], sketch);
                        case "rectangle" -> makeRectangle(shapeID, attributes, sketch);
                        case "ellipse" -> makeEllipse(shapeID, attributes, sketch);
                        case "segment" -> makeSegment(shapeID, attributes, sketch);
                    }
                }
            }
        }
    }
    // server version of handling new messages
    public static void serverHandle(String line, SketchServer server) {

        //
        if (line.startsWith("add")) {
            // get the command through splitting and the various attributes
            String[] command = line.split("\\|");
            String[] args = command[1].split(" ");
            int id = server.getID(); // get and update the server id
            // check if its polyline or the other simpler shapes
            if (command[1].contains("polyline")) {
                String segmentData = command[1].split(" ", 2)[1];
                makePolyline(id, segmentData, server.getSketch());
            }
            else {
                // which shape
                String shape = args[0];
                // switch statement for shapes
                switch (shape) {
                    case "ellipse" -> makeEllipse(id, args, server.getSketch());
                    case "rectangle" -> makeRectangle(id, args, server.getSketch());
                    case "segment" -> makeSegment(id, args, server.getSketch());
                }
            }
        } else {
            // get the command through splitting and the various attributes
            String[] command = line.split("_");
            int id = Integer.parseInt(command[0]);
            String[] params = command[1].split("\\|");
            String action = params[0];
            String[] args = new String[] {};
            if (params.length > 1) args = params[1].split(" ");
            // check which action we want and call the corresponding function
            switch (action) {
                case "delete" -> delete(id, server.getSketch());
                case "move" -> move(id, args, server.getSketch());
                case "recolor" -> recolor(id, args, server.getSketch());
            }
        }

    }

    // delete id from sketch
    public static void delete(int id, Sketch sketch) {
        sketch.deleteShape(id);
    }
    // recolor id from sketch
    public static void recolor(int id, String[] args, Sketch sketch) {
        Color color = new Color(Integer.parseInt(args[0]));
        sketch.recolorShape(id, color);
    }
    // move id from sketch
    public static void move(int id, String[] args, Sketch sketch) {
        int dx = Integer.parseInt(args[0]);
        int dy = Integer.parseInt(args[1]);
        sketch.moveShape(id, dx, dy);
    }
    // create a new ellipse
    public static void makeEllipse(int id, String[] attributes, Sketch sketch) {
        // parse the message for the params
        int x1 = Integer.parseInt(attributes[1]);
        int y1 = Integer.parseInt(attributes[2]);
        int x2 = Integer.parseInt(attributes[3]);
        int y2 = Integer.parseInt(attributes[4]);
        Color color = new Color(Integer.parseInt(attributes[5]));
        sketch.addShape(id, new Ellipse(x1, y1, x2, y2, color));
    }
    // create a new polyline
    public static void makePolyline(int id, String segmentData, Sketch sketch) {
        // make a new arraylist of segments
        ArrayList<Segment> segments = new ArrayList<>();
        Color segmentColor = new Color(0);
        for (String segData: segmentData.split(":")) {
            // parse each segment data for the params
            String[] attributes = segData.strip().split(" ");
            int x1 = Integer.parseInt(attributes[1]);
            int y1 = Integer.parseInt(attributes[2]);
            int x2 = Integer.parseInt(attributes[3]);
            int y2 = Integer.parseInt(attributes[4]);
            Color color = new Color(Integer.parseInt(attributes[5]));
            segmentColor = color;
            Segment segment = new Segment(x1, y1, x2, y2, color);
            segments.add(segment);
        }
        // finally add the Polyline object
        sketch.addShape(id, new Polyline(segments, segmentColor));
    }
    // create a new rectangle
    public static void makeRectangle(int id, String[] attributes, Sketch sketch) {
        // parse the input for the attributes
        int x1 = Integer.parseInt(attributes[1]);
        int y1 = Integer.parseInt(attributes[2]);
        int x2 = Integer.parseInt(attributes[3]);
        int y2 = Integer.parseInt(attributes[4]);
        Color color = new Color(Integer.parseInt(attributes[5]));
        sketch.addShape(id, new Rectangle(x1, y1, x2, y2, color));
    }
    public static void makeSegment(int id, String[] attributes, Sketch sketch) {
        // parse the input for the attributes
        int x1 = Integer.parseInt(attributes[1]);
        int y1 = Integer.parseInt(attributes[2]);
        int x2 = Integer.parseInt(attributes[3]);
        int y2 = Integer.parseInt(attributes[4]);
        Color color = new Color(Integer.parseInt(attributes[5]));
        sketch.addShape(id, new Segment(x1, y1, x2, y2, color));
    }

}
