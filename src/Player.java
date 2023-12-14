import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Player {
    private String name;
    private Stack<Tile> tiles;
    private String type;
    int id;

    public Player(String name, String type) {
        this.name = name;
        this.tiles = new Stack<>();
        this.type = type;
    }

    public void setTiles(Stack<Tile> tiles) {
        this.tiles = tiles;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Stack<Tile> getTiles() {
        return tiles;
    }
}

