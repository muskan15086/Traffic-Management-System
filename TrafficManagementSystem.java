

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class TrafficManagementSystem extends JFrame {
    // Constants
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int ROAD_WIDTH = 100;
    private static final int SIGNAL_SIZE = 30;
    private static final int SIMULATION_SECONDS = 30;
    private static final int FRAMES_PER_SECOND = 30;
    
    // Traffic directions
    private enum Direction {
        NORTH, EAST, SOUTH, WEST
    }
    
    // Traffic light states
    private enum SignalState {
        RED, YELLOW, GREEN
    }
    
    // Traffic density levels
    private enum DensityLevel {
        LOW(1, "Low"), 
        MEDIUM(2, "Medium"), 
        HIGH(3, "High"), 
        VERY_HIGH(4, "Very High");
        
        private final int value;
        private final String label;
        
        DensityLevel(int value, String label) {
            this.value = value;
            this.label = label;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getLabel() {
            return label;
        }
    }
    
    // Traffic data
    private Map<Direction, DensityLevel> trafficDensity;
    private Map<Direction, SignalState> signalStates;
    private Direction currentGreenDirection;
    private int currentTimer;
    private int elapsedSimulationTime;
    private boolean isSimulationRunning;
    private BufferedImage[] frames;
    private int frameCount;
    
    // UI Components
    private JPanel simulationPanel;
    private JButton startButton;
    private JComboBox<String>[] densitySelectors;
    private JLabel timerLabel;
    private JLabel simulationTimeLabel;
    private Timer simulationTimer;
    private JProgressBar progressBar;
    
    @SuppressWarnings("unchecked")
    public TrafficManagementSystem() {
        setTitle("Traffic Management System - Presidency College Junction");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize data structures
        trafficDensity = new HashMap<>();
        signalStates = new HashMap<>();
        
        // Initialize all signals to RED except NORTH which starts as GREEN
        for (Direction dir : Direction.values()) {
            signalStates.put(dir, SignalState.RED);
            trafficDensity.put(dir, DensityLevel.MEDIUM); // Default density
        }
        currentGreenDirection = Direction.NORTH;
        signalStates.put(currentGreenDirection, SignalState.GREEN);
        currentTimer = 0;
        elapsedSimulationTime = 0;
        isSimulationRunning = false;
        
        // Set up frames for recording
        frames = new BufferedImage[SIMULATION_SECONDS * FRAMES_PER_SECOND];
        frameCount = 0;
        
        // Create UI components
        createUIComponents();
        
        // Set up layout
        setLayout(new BorderLayout());
        
        // Traffic density controls panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Traffic Density Controls"));
        
        // Create density selectors
        densitySelectors = new JComboBox[Direction.values().length];
        int i = 0;
        for (Direction dir : Direction.values()) {
            controlPanel.add(new JLabel(dir.name() + " Traffic Density:"));
            
            String[] densityOptions = new String[DensityLevel.values().length];
            for (int j = 0; j < DensityLevel.values().length; j++) {
                densityOptions[j] = DensityLevel.values()[j].getLabel();
            }
            
            densitySelectors[i] = new JComboBox<>(densityOptions);
            densitySelectors[i].setSelectedIndex(1); // Default to MEDIUM
            controlPanel.add(densitySelectors[i]);
            i++;
        }
        
        // Add start button
        startButton = new JButton("Start Simulation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSimulationRunning) {
                    startSimulation();
                }
            }
        });
        controlPanel.add(startButton);
        
        // Add timer label
        timerLabel = new JLabel("Current Signal Timer: 0s");
        controlPanel.add(timerLabel);
        
        // Add progress bar
        progressBar = new JProgressBar(0, SIMULATION_SECONDS);
        progressBar.setStringPainted(true);
        progressBar.setString("Simulation Progress");
        
        // Create simulation panel
        simulationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawJunction((Graphics2D) g);
            }
        };
        simulationPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - 200));
        simulationPanel.setBackground(Color.WHITE);
        
        // Simulation time label
        simulationTimeLabel = new JLabel("Simulation Time: 0/" + SIMULATION_SECONDS + " seconds");
        
        // Add components to frame
        add(simulationPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(simulationTimeLabel, BorderLayout.NORTH);
        southPanel.add(progressBar, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
        
        // Set up simulation timer
        simulationTimer = new Timer(1000 / FRAMES_PER_SECOND, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSimulation();
            }
        });
        
        // Make frame visible
        setVisible(true);
    }
    
    private void createUIComponents() {
        // This method can be used for additional UI component setup
    }
    
    private void startSimulation() {
        // Update traffic density based on user input
        for (int i = 0; i < Direction.values().length; i++) {
            Direction dir = Direction.values()[i];
            int selectedIndex = densitySelectors[i].getSelectedIndex();
            trafficDensity.put(dir, DensityLevel.values()[selectedIndex]);
        }
        
        // Reset simulation state
        for (Direction dir : Direction.values()) {
            signalStates.put(dir, SignalState.RED);
        }
        currentGreenDirection = Direction.NORTH;
        signalStates.put(currentGreenDirection, SignalState.GREEN);
        currentTimer = 0;
        elapsedSimulationTime = 0;
        frameCount = 0;
        
        // Update UI state
        isSimulationRunning = true;
        startButton.setEnabled(false);
        for (JComboBox<String> selector : densitySelectors) {
            selector.setEnabled(false);
        }
        
        // Start timer
        simulationTimer.start();
    }
    
    private void updateSimulation() {
        // Capture current frame
        if (frameCount < frames.length) {
            BufferedImage frameImage = new BufferedImage(simulationPanel.getWidth(), 
                                                       simulationPanel.getHeight(), 
                                                       BufferedImage.TYPE_INT_RGB);
            Graphics2D g = frameImage.createGraphics();
            simulationPanel.paint(g);
            g.dispose();
            frames[frameCount++] = frameImage;
        }
        
        // Update timer
        currentTimer++;
        
        // One second has passed in simulation time
        if (currentTimer % FRAMES_PER_SECOND == 0) {
            elapsedSimulationTime++;
            simulationTimeLabel.setText("Simulation Time: " + elapsedSimulationTime + "/" + SIMULATION_SECONDS + " seconds");
            progressBar.setValue(elapsedSimulationTime);
            
            // Check if signal change is needed
            int greenDuration = calculateSignalDuration(currentGreenDirection);
            if (currentTimer / FRAMES_PER_SECOND >= greenDuration) {
                // Change signal to yellow first
                signalStates.put(currentGreenDirection, SignalState.YELLOW);
                
                // After 2 seconds of yellow, change to next direction
                if (currentTimer / FRAMES_PER_SECOND >= greenDuration + 2) {
                    signalStates.put(currentGreenDirection, SignalState.RED);
                    currentGreenDirection = getNextDirection(currentGreenDirection);
                    signalStates.put(currentGreenDirection, SignalState.GREEN);
                    currentTimer = 0; // Reset timer for new green signal
                }
            }
            
            // Update timer label
            timerLabel.setText("Current Signal Timer: " + (currentTimer / FRAMES_PER_SECOND) + "s");
        }
        
        // Repaint junction
        simulationPanel.repaint();
        
        // End simulation after specified duration
        if (elapsedSimulationTime >= SIMULATION_SECONDS) {
            endSimulation();
        }
    }
    
    private void endSimulation() {
        simulationTimer.stop();
        isSimulationRunning = false;
        startButton.setEnabled(true);
        for (JComboBox<String> selector : densitySelectors) {
            selector.setEnabled(true);
        }
        
        // Generate video from frames
        saveVideoFrames();
        
        JOptionPane.showMessageDialog(this, 
                                     "Simulation completed! Video frames saved to 'traffic_simulation' folder.",
                                     "Simulation Complete", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveVideoFrames() {
        try {
            // Create directory for frames
            File outputDir = new File("traffic_simulation");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }
            
            // Save each frame
            for (int i = 0; i < frameCount; i++) {
                File outputFile = new File(outputDir, String.format("frame_%04d.png", i));
                ImageIO.write(frames[i], "png", outputFile);
            }
            
            // Create a README file with instructions to convert frames to video
            File readme = new File(outputDir, "README.txt");
            java.io.PrintWriter writer = new java.io.PrintWriter(readme);
            writer.println("To convert these frames to a video, use FFmpeg with the following command:");
            writer.println("ffmpeg -framerate 30 -i frame_%04d.png -c:v libx264 -pix_fmt yuv420p traffic_simulation.mp4");
            writer.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                                         "Error saving frames: " + e.getMessage(),
                                         "Error", 
                                         JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int calculateSignalDuration(Direction direction) {
        // Calculate signal duration based on traffic density
        // Higher density = longer green light
        DensityLevel density = trafficDensity.get(direction);
        
        switch (density) {
            case LOW:
                return 3; // 3 seconds for low traffic
            case MEDIUM:
                return 5; // 5 seconds for medium traffic
            case HIGH:
                return 8; // 8 seconds for high traffic
            case VERY_HIGH:
                return 12; // 12 seconds for very high traffic
            default:
                return 5;
        }
    }
    
    private Direction getNextDirection(Direction current) {
        // Get next direction in clockwise order
        switch (current) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default:
                return Direction.NORTH;
        }
    }
    
    private void drawJunction(Graphics2D g) {
        int centerX = simulationPanel.getWidth() / 2;
        int centerY = simulationPanel.getHeight() / 2;
        
        // Draw background (green area)
        g.setColor(new Color(100, 200, 100));
        g.fillRect(0, 0, simulationPanel.getWidth(), simulationPanel.getHeight());
        
        // Draw roads
        g.setColor(Color.DARK_GRAY);
        // Horizontal road
        g.fillRect(0, centerY - ROAD_WIDTH/2, simulationPanel.getWidth(), ROAD_WIDTH);
        // Vertical road
        g.fillRect(centerX - ROAD_WIDTH/2, 0, ROAD_WIDTH, simulationPanel.getHeight());
        
        // Draw road markings
        g.setColor(Color.WHITE);
        // Draw dashed lines on roads
        drawDashedLine(g, 0, centerY, simulationPanel.getWidth(), centerY);
        drawDashedLine(g, centerX, 0, centerX, simulationPanel.getHeight());
        
        // Draw junction name
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Presidency College Junction", centerX - 120, 30);
        
        // Draw traffic signals
        drawTrafficSignal(g, centerX - ROAD_WIDTH/2 - SIGNAL_SIZE - 5, centerY - ROAD_WIDTH/2 - 5, Direction.NORTH);
        drawTrafficSignal(g, centerX + ROAD_WIDTH/2 + 5, centerY - ROAD_WIDTH/2 - 5, Direction.EAST);
        drawTrafficSignal(g, centerX + ROAD_WIDTH/2 + 5, centerY + ROAD_WIDTH/2 + 5, Direction.SOUTH);
        drawTrafficSignal(g, centerX - ROAD_WIDTH/2 - SIGNAL_SIZE - 5, centerY + ROAD_WIDTH/2 + 5, Direction.WEST);
        
        // Draw traffic (cars)
        drawTraffic(g, Direction.NORTH, centerX - ROAD_WIDTH/4, centerY - ROAD_WIDTH/2, centerX - ROAD_WIDTH/4, 0);
        drawTraffic(g, Direction.EAST, centerX + ROAD_WIDTH/2, centerY - ROAD_WIDTH/4, simulationPanel.getWidth(), centerY - ROAD_WIDTH/4);
        drawTraffic(g, Direction.SOUTH, centerX + ROAD_WIDTH/4, centerY + ROAD_WIDTH/2, centerX + ROAD_WIDTH/4, simulationPanel.getHeight());
        drawTraffic(g, Direction.WEST, centerX - ROAD_WIDTH/2, centerY + ROAD_WIDTH/4, 0, centerY + ROAD_WIDTH/4);
        
        // Draw legend
        drawLegend(g);
    }
    
    private void drawDashedLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        // Create dashed stroke
        float[] dashPattern = {10, 10};
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(new BasicStroke(1)); // Reset stroke
    }
    
    private void drawTrafficSignal(Graphics2D g, int x, int y, Direction direction) {
        // Draw signal box
        g.setColor(Color.BLACK);
        g.fillRect(x, y, SIGNAL_SIZE, 3 * SIGNAL_SIZE);
        
        // Draw signal lights
        SignalState state = signalStates.get(direction);
        
        // Red light
        g.setColor(state == SignalState.RED ? Color.RED : Color.GRAY);
        g.fillOval(x + 5, y + 5, SIGNAL_SIZE - 10, SIGNAL_SIZE - 10);
        
        // Yellow light
        g.setColor(state == SignalState.YELLOW ? Color.YELLOW : Color.GRAY);
        g.fillOval(x + 5, y + SIGNAL_SIZE + 5, SIGNAL_SIZE - 10, SIGNAL_SIZE - 10);
        
        // Green light
        g.setColor(state == SignalState.GREEN ? Color.GREEN : Color.GRAY);
        g.fillOval(x + 5, y + 2 * SIGNAL_SIZE + 5, SIGNAL_SIZE - 10, SIGNAL_SIZE - 10);
        
        // Draw direction label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString(direction.name().substring(0, 1), x + SIGNAL_SIZE/2 - 3, y + 3 * SIGNAL_SIZE + 15);
        
        // Show traffic density
        DensityLevel density = trafficDensity.get(direction);
        g.drawString(density.getLabel(), x - 5, y + 3 * SIGNAL_SIZE + 30);
    }
    
    private void drawTraffic(Graphics2D g, Direction direction, int x1, int y1, int x2, int y2) {
        DensityLevel density = trafficDensity.get(direction);
        SignalState signal = signalStates.get(direction);
        
        // Number of cars based on density
        int numCars;
        switch (density) {
            case LOW:
                numCars = 2;
                break;
            case MEDIUM:
                numCars = 4;
                break;
            case HIGH:
                numCars = 7;
                break;
            case VERY_HIGH:
                numCars = 12;
                break;
            default:
                numCars = 4;
        }
        
        // Calculate spacing
        int totalDistance = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        int spacing = totalDistance / (numCars + 1);
        
        // Calculate unit vector for direction
        double dx = (x2 - x1) / (double) totalDistance;
        double dy = (y2 - y1) / (double) totalDistance;
        
        // Draw cars
        g.setColor(new Color(50, 50, 200)); // Blue for cars
        
        // Movement factor based on signal state
        double movementFactor = 0;
        if (signal == SignalState.GREEN) {
            movementFactor = 0.2; // Move forward when green
        } else if (signal == SignalState.YELLOW) {
            movementFactor = 0.05; // Slow down when yellow
        }
        
        // Current animation offset based on timer
        int offset = (int) (currentTimer * movementFactor) % spacing;
        
        for (int i = 1; i <= numCars; i++) {
            int pos = i * spacing - offset;
            if (pos <= totalDistance) {
                int carX = (int) (x1 + pos * dx - 10);
                int carY = (int) (y1 + pos * dy - 5);
                
                // Draw car
                g.fillRect(carX, carY, 20, 10);
            }
        }
    }
    
    private void drawLegend(Graphics2D g) {
        int legendX = 20;
        int legendY = simulationPanel.getHeight() - 100;
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Traffic Density Legend:", legendX, legendY);
        
        for (int i = 0; i < DensityLevel.values().length; i++) {
            DensityLevel level = DensityLevel.values()[i];
            g.drawString(level.getLabel(), legendX, legendY + 20 + i * 15);
        }
        
        // Current green direction
        g.drawString("Current Signal: " + currentGreenDirection + " is " + 
                    signalStates.get(currentGreenDirection), legendX + 150, legendY);
    }
    
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrafficManagementSystem();
            }
        });
    }
}