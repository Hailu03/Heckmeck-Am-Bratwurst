import java.util.*;
import java.util.stream.Collectors;

public class Game {
   private List<Player> players;
   public List<Integer> rolledDice;
   public List<Integer> takenDice;
   private List<Tile> grill;
   private gameState state;
   int activePLayer; //index of the current player
   private boolean hasRolled; // used to check if the player has rolled the dice to ensure that the player can't roll multiple times in a round

   public enum gameState{
       INPUT_PLAYERS,
       GAME,
       GAME_OVER
   }
   public Game() {
       this.players = new ArrayList<>();
       this.rolledDice = new ArrayList<>();
       this.takenDice = new ArrayList<>();
       this.grill = new ArrayList<>();
       this.state = gameState.INPUT_PLAYERS;
       this.activePLayer = -1;
       this.hasRolled = false;
   }


   public static String aiNameGenerate() {
       String[] PREFIXES = {"Ch", "K", "M", "Tr", "Sp"};
       String[] MIDDLES = {"a", "e", "u", "i", "o"};
       String[] ENDS = {"ft", "ht", "ck", "son", "n"};

       Random random = new Random();
       String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
       String middle = MIDDLES[random.nextInt(MIDDLES.length)];
       String end = ENDS[random.nextInt(ENDS.length)];
       return prefix + middle + end;
   }
   public void addPlayer(String name) {
       players.add(new Player(name, "human"));
       System.out.print("Successfully add: " + name);
   }

   public void addAIPlayer() {
       String aiName = aiNameGenerate();
       players.add(new Player(aiName, "ai"));
       System.out.println("Successfully add: " + aiName);
   }

   //roll 1 die
   public int roll(){
       Random random = new Random();
       return random.nextInt(6) + 1;
   }

   //roll 8 dice nad update the rolledDice list
   public void rollDice() {
       this.rolledDice.clear();
       for (int i = 0; i < 8 - this.takenDice.size(); i++) {
           this.rolledDice.add(roll());
       }
       Collections.sort(this.rolledDice);
   }

   //check if the dice value has been taken in the previous round
   public boolean hasTaken(int value) {
       return this.takenDice.contains(value);
   }

   //check if the dice set can be taken
   public boolean canTake(int value) {
       return !this.hasTaken(value);
   }


   //check if worm has been taken
   public boolean wormTaken(){
       return hasTaken(6);
   }

   public void resetGame(){
       Collections.shuffle(players);
       for(int i = 0; i < players.size(); i++){
           players.get(i).setTiles(new Stack<>());
           players.get(i).setId(i);
       }
       activePLayer = 0;
       this.grill.clear();
       for(int i = 21; i <= 36; i++){
           this.grill.add(new Tile(i, (int) Math.ceil(((double) (i - 20) / 4))));
       }
       this.newRound();
   }

   public void startAgain(){
       this.players.clear();
       this.state = gameState.INPUT_PLAYERS;
   }

   public void startGame(){
       this.resetGame();
       this.state = gameState.GAME;
   }

   public void newRound(){
       takenDice.clear();
       rollDice();
   }

   //change turn to the next player
   public void nextTurn() {
       activePLayer = (activePLayer + 1) % players.size();
       newRound();

       if (gameOver()) {
           // Sorting players based on points in descending order
           players.sort(Comparator.comparingInt(this::getPoints).reversed());
           state = gameState.GAME_OVER;
       }
   }

   public void playerPickDie(int v) {
       if (!playerTurn()) {
           return;
       }
       pickDie(v);
   }

   public void pickDie(int v) {
       if (!canTake(v)) {
           return;
       }

       List<Integer> matchingDice = rolledDice.stream()
               .filter(die -> die == v)
               .toList();
       List<Integer> nonMatchingDice = rolledDice.stream()
               .filter(die -> die != v)
               .collect(Collectors.toList());

       if (matchingDice.isEmpty()) {
           return;
       }

       takenDice.addAll(matchingDice);
       rolledDice = nonMatchingDice;
       Collections.sort(takenDice);
       hasRolled = false;
   }

   //logic to emulate fail turn scenario, return tile on the player's stack to the grill and flip the last tile on the grill
   public void failTurn() {
       Player currentPlayer = players.get(activePLayer);
       if (!currentPlayer.getTiles().isEmpty()) {
           Tile worm = currentPlayer.getTiles().removeFirst();
           grill.add(worm);

           // Sorting the grill based on tile number
           grill.sort(Comparator.comparing(Tile::getNumber));

           // Only remove the topmost worm from the grill if it wasn't just returned
           if (grill.getLast().getNumber() != worm.getNumber()) {
               grill.removeLast();
           }
       }
       nextTurn();
   }

   public int getPoints(Player player) {
       return player.getTiles().stream()
               .mapToInt(Tile::getValue)
               .sum();
   }

   public Player currentPlayer(){
       return this.players.get(activePLayer);
   }

   public boolean gameOver(){
       return this.grill.isEmpty();
   }

   public int currentValue() {
       int sum = 0;
       for (Integer v : takenDice) {
           sum += (v == 6 ? 5 : v);
       }
       return sum;
   }

   //logic for the can stop button
   public boolean canStop() {
       if (!playerTurn()) {
           return false;
       }
       if (hasRolled) {
           return false;
       }
       if (!wormTaken()) {
           return false;
       }

       return canPickTile();
   }
   //check if there are tiles players can get after rolling 8 dice
   private boolean canPickTile() {
       int v = currentValue();
       for (Player player : players) {
           if (players.indexOf(player) == activePLayer) {
               continue;
           }
           if (!player.getTiles().isEmpty() && player.getTiles().getFirst().getNumber() == v) {
               return true; // A valid worm is found, can stop
           }
       }

       // Check if a valid worm is available in the grill
       return grill.stream().anyMatch(e -> e.getNumber() <= v);
   }

   public boolean failedTurn() {
       if (!playerTurn()) {
           return false;
       }
       if (rolledDice.isEmpty() && !wormTaken()) {
           return true;
       }
       if (!hasRolled) {
           return false;
       }
       return rolledDice.stream().noneMatch(this::canTake);
   }


   public boolean canRoll() {
       return playerTurn() && !hasRolled && !rolledDice.isEmpty();
   }

   //check if this is the player turn (in case there are bot players)
   private boolean playerTurn() {
       return currentPlayer().getType().equals("human");
   }

   //function to choose the tile on the grill or other players hand based on the sum value of dice
   public Tile pickTile(boolean remove) {
       int v = currentValue(); //check the current sum value of the dice
       for (int i = 0; i < players.size(); i++) {
           if (i == activePLayer) {
               continue;
           }
           Player player = players.get(i);
           if (!player.getTiles().isEmpty() && player.getTiles().getFirst().getNumber() == v) {
               Tile worm = player.getTiles().getFirst();
               if (remove) {
                   player.getTiles().removeFirst();
               }
               return worm;
           }
       }
       return null; // Return null if no matching worm is found
   }
}
