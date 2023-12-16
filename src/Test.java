import java.util.Random;

public class Test {
   public static void main(String[] args) {
       Game game = new Game();
       game.addAIPlayer();
       game.addPlayer("Tom");

       game.rollDice();
       System.out.println("Rolled Dice: " + game.rolledDice);
       int testValue = 3;

       if (game.canTake(testValue)) {
           game.pickDie(testValue);
           System.out.println("Picked die " + testValue);
           System.out.println("Rolled Dice after picking: " + game.rolledDice);
           System.out.println("Taken Dice: " + game.takenDice);
       } else {
           System.out.println("Cannot pick die " + testValue);
       }

       if (game.canTake(testValue)) {
           game.pickDie(testValue);
           System.out.println("Picked die " + testValue);
           System.out.println("Rolled Dice after picking: " + game.rolledDice);
           System.out.println("Taken Dice: " + game.takenDice);
       } else {
           System.out.println("Cannot pick die " + testValue);
       }
   }

}