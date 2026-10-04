
package tile;

import main.GamePanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vincent
 */

public class TileManager {
    GamePanel gp;
    
    // Map dimensions and tile sizes
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    
    // Parallel lists for tileset properties
    private List<String> tilesetNames = new ArrayList<>();
    private List<Integer> tilesetFirstGids = new ArrayList<>();
    private List<BufferedImage> tilesetImages = new ArrayList<>();
    private List<Integer> tilesetColumns = new ArrayList<>();

    // Layers data (stores the grid of GIDs for each map layer)
    private List<String> layerNames = new ArrayList<>();
    private List<int[][]> layers = new ArrayList<>();
    private int currentLevel = 0;
    
    private static final String[] LEVEL_FILES = {
    "resources/complete_map/level1.tmx",
    "resources/complete_map/Level2.tmx",
    "resources/complete_map/Level3.tmx"
    };

    public int getLevelCount() {
        return LEVEL_FILES.length;
    }

    public void loadLevel(int level) {
        currentLevel = level;
        loadTmx(LEVEL_FILES[level]);
    }
    
    public TileManager(GamePanel gp) {
        this.gp = gp;
        
        // Load your TMX map file (make sure path matches where your level1.tmx is stored)
        loadTmx("resources/complete_map/level1.tmx");
    }

    public synchronized void loadTmx(String tmxFilePath) {
        try {
            File fXmlFile = new File(tmxFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // 1. Read Map Attributes
            Element mapElement = doc.getDocumentElement();
            this.mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            this.mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
            this.tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));
            this.tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));

            tilesetNames.clear();
            tilesetFirstGids.clear();
            tilesetImages.clear();
            tilesetColumns.clear();
            layerNames.clear();
            layers.clear();
            
            // 2. Parse Tilesets
            NodeList tilesetNodes = doc.getElementsByTagName("tileset");
            for (int i = 0; i < tilesetNodes.getLength(); i++) {
                Element tsElement = (Element) tilesetNodes.item(i);
                int firstGid = Integer.parseInt(tsElement.getAttribute("firstgid"));
                String name = tsElement.getAttribute("name");
                
                Element imageElement = (Element) tsElement.getElementsByTagName("image").item(0);
                String imageSource = imageElement.getAttribute("source"); // e.g., "walls_floor.png"
                int tw = Integer.parseInt(tsElement.getAttribute("tilewidth"));
                
                // Load the corresponding tileset image (adjust path prefix as needed for your project structure)
                BufferedImage sheetImage = ImageIO.read(new File("resources/complete_map/" + imageSource));
                int columns = sheetImage.getWidth() / tw;

                tilesetNames.add(name);
                tilesetFirstGids.add(firstGid);
                tilesetImages.add(sheetImage);
                tilesetColumns.add(columns);
            }

            // 3. Parse Map Layers (CSV format)
            NodeList layerNodes = doc.getElementsByTagName("layer");
            for (int i = 0; i < layerNodes.getLength(); i++) {
                Element layerElement = (Element) layerNodes.item(i);
                String layerName = layerElement.getAttribute("name");
                Element dataElement = (Element) layerElement.getElementsByTagName("data").item(0);
                
                String csvData = dataElement.getTextContent().trim();
                int[][] layerGrid = parseCsvData(csvData, mapWidth, mapHeight);
                
                layerNames.add(layerName);
                layers.add(layerGrid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[][] parseCsvData(String csvData, int width, int height) {
        int[][] grid = new int[height][width];
        String[] rows = csvData.split("\n");
        for (int row = 0; row < rows.length; row++) {
            String[] cols = rows[row].trim().split(",");
            for (int col = 0; col < cols.length; col++) {
                if (col < width && row < height) {
                    grid[row][col] = Integer.parseInt(cols[col].trim());
                }
            }
        }
        return grid;
    }

    /**
     * Finds which tileset owns the given global tile ID (gid) and crops the individual tile image.
     */
    public BufferedImage getTileImageByGid(int gid) {
        if (gid == 0) return null; // 0 means empty tile

        int bestIndex = -1;
        int highestFirstGid = -1;

        // Find the highest firstgid less than or equal to gid
        for (int i = 0; i < tilesetFirstGids.size(); i++) {
            int firstGid = tilesetFirstGids.get(i);
            if (gid >= firstGid && firstGid > highestFirstGid) {
                highestFirstGid = firstGid;
                bestIndex = i;
            }
        }

        if (bestIndex == -1) return null;

        int firstGid = tilesetFirstGids.get(bestIndex);
        int localId = gid - firstGid;
        BufferedImage sheet = tilesetImages.get(bestIndex);
        int columns = tilesetColumns.get(bestIndex);

        int x = (localId % columns) * tileWidth;
        int y = (localId / columns) * tileHeight;

        // Safety check bounds before slicing subimage
        if (x + tileWidth <= sheet.getWidth() && y + tileHeight <= sheet.getHeight()) {
            return sheet.getSubimage(x, y, tileWidth, tileHeight);
        }
        return null;
    }

    /**
     * Draws all layers and tiles onto the screen panel.
     */
    public synchronized void draw(Graphics2D g2) {
        // Loop through every layer in the map
        for (int[][] layerGrid : layers) {
            for (int row = 0; row < mapHeight; row++) {
                for (int col = 0; col < mapWidth; col++) {
                    int gid = layerGrid[row][col];
                    
                    if (gid != 0) {
                        BufferedImage tileImage = getTileImageByGid(gid);
                        if (tileImage != null) {
                            int screenX = col * gp.tileSize;
                            int screenY = row * gp.tileSize;
                            
                            // Draws the tile scaled to your GamePanel's tileSize
                            g2.drawImage(tileImage, screenX, screenY, gp.tileSize, gp.tileSize, null);
                        }
                    }
                }
            }
        }
    }
    // =========================================================================
    // HELPER METHODS REQUIRED BY DoorKey.java & CollisionDetection.java
    // =========================================================================

    public int getLayerCount() {
        return layers.size();
    }

    public String getLayerName(int layerIndex) {
        if (layerIndex >= 0 && layerIndex < layerNames.size()) {
            return layerNames.get(layerIndex);
        }
        return "";
    }

    public int getGid(int layer, int row, int col) {
        if (layer < 0 || layer >= layers.size()) return 0;
        int[][] grid = layers.get(layer);
        if (row < 0 || row >= mapHeight || col < 0 || col >= mapWidth) return 0;
        return grid[row][col];
    }

    public void setGid(int layer, int row, int col, int gid) {
        if (layer >= 0 && layer < layers.size()) {
            int[][] grid = layers.get(layer);
            if (row >= 0 && row < mapHeight && col >= 0 && col < mapWidth) {
                grid[row][col] = gid;
            }
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }
}
