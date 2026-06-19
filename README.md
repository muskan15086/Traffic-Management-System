# 🚦 Traffic Management System

## 📌 Project Overview

The Traffic Management System is a Java Swing-based simulation that demonstrates intelligent traffic signal control at a four-way junction. The system dynamically adjusts green signal durations based on traffic density levels selected by the user.

This project helps visualize how adaptive traffic signal management can reduce congestion and improve traffic flow efficiency.

---

## 🎯 Objectives

- Simulate a four-way traffic junction.
- Dynamically allocate signal timings based on traffic density.
- Visualize vehicle movement under different traffic conditions.
- Demonstrate basic traffic management concepts using Java GUI programming.

---

## ✨ Features

- Four-direction traffic control:
  - North
  - East
  - South
  - West

- Traffic density selection:
  - Low
  - Medium
  - High
  - Very High

- Dynamic traffic signal timing

- Traffic light visualization:
  - 🔴 Red
  - 🟡 Yellow
  - 🟢 Green

- Vehicle movement simulation

- Real-time timer display

- Simulation progress tracking

- Automatic frame capture

- Export simulation frames as images

---

## 🛠 Technologies Used

- Java
- Java Swing
- AWT Graphics
- HashMap Collections
- BufferedImage
- File Handling
- Event-Driven Programming

---

## 📂 Project Structure

```text
Traffic-Management-System/
│
├── TrafficManagementSystem.java
└── README.md
```

After execution, the program generates:

```text
traffic_simulation/
├── frame_0000.png
├── frame_0001.png
├── frame_0002.png
├── ...
└── README.txt
```

---

## 🚥 Traffic Density Logic

The green signal duration is determined by traffic density.

| Density Level | Green Signal Duration |
|--------------|----------------------|
| Low          | 3 Seconds            |
| Medium       | 5 Seconds            |
| High         | 8 Seconds            |
| Very High    | 12 Seconds           |

Higher traffic density receives longer green signal duration.

---

## 🔄 Signal Rotation Sequence

The traffic signals rotate in clockwise order:

```text
NORTH
  ↓
EAST
  ↓
SOUTH
  ↓
WEST
  ↓
NORTH
```

Before changing to the next direction, the signal enters a Yellow state for 2 seconds.

---

## 🚗 Vehicle Simulation

The number of vehicles displayed depends on traffic density.

| Density Level | Number of Vehicles |
|--------------|-------------------|
| Low          | 2                 |
| Medium       | 4                 |
| High         | 7                 |
| Very High    | 12                |

Vehicle movement behavior:

- Green Signal → Vehicles move normally.
- Yellow Signal → Vehicles slow down.
- Red Signal → Vehicles stop.

---

## ▶️ How to Run the Project

### Step 1: Compile

```bash
javac TrafficManagementSystem.java
```

### Step 2: Run

```bash
java TrafficManagementSystem
```

---

## 🖥 How the Simulation Works

1. Launch the application.
2. Select traffic density for each direction.
3. Click **Start Simulation**.
4. Signals change according to traffic density.
5. Vehicles move based on signal status.
6. Simulation runs for 30 seconds.
7. Frames are automatically saved.

---

## 📸 Output Generation

The system captures simulation frames and stores them as PNG images inside the `traffic_simulation` folder.

These frames can later be converted into a video using FFmpeg.

Example command:

```bash
ffmpeg -framerate 30 -i frame_%04d.png -c:v libx264 -pix_fmt yuv420p traffic_simulation.mp4
```

---

## 📊 Applications

- Smart Traffic Management
- Traffic Flow Analysis
- Educational Demonstrations
- Simulation-Based Learning
- Urban Traffic Planning

---

## 🚀 Possible Future Enhancements

- AI-based traffic prediction
- Real-time vehicle detection using cameras
- Emergency vehicle prioritization
- IoT-based traffic monitoring
- Database integration
- Cloud-based analytics dashboard
- Automatic congestion detection

---

## 👩‍💻 Author

**Muskan**  
Computer Science Engineering  
Presidency University, Bangalore

---

## 📖 Note

This project was developed for academic learning, simulation, and demonstration purposes.