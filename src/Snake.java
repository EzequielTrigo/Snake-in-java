import java.util.LinkedList;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color; // Importa el Color correcto de JavaFX
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import java.util.Random;

public class Snake {
    public LinkedList<Rectangle> snakeList;
    public int length;
    public int nextMovX;
    public int nextMovY;
    public boolean lose;
    public Group snakeGroup;
    public boolean foundFood;
    private int lastMovX;
    private int lastMovY;
    public Circle food;
    public Rectangle snakeHead;
    private boolean[][] matrizDePosiciones;
    private int cantCasillas;

    public Snake(int x, int y){
        this.snakeList= new LinkedList<Rectangle>();
        Rectangle slave2 = new Rectangle(x+1,y+1-40,18,18);
        Rectangle slave1 = new Rectangle(x+1,y+1-20,18,18);
        Rectangle head = new Rectangle(x+1,y+1,18,18);
        this.cantCasillas = 25 * 30;

        slave2.setFill(Color.BLUE);
        slave1.setFill(Color.BLUE);
        head.setFill(Color.BLUE);

        this.snakeList.add(head);
        this.snakeList.add(slave1);
        this.snakeList.add(slave2);

        this.snakeHead =  head;

        this.length=3;
        this.nextMovX = 0;
        this.nextMovY = 0;

        this.lastMovX = 0;
        this.lastMovY = 20;
        this.lose = false;

        this.snakeGroup = new Group();

        snakeGroup.getChildren().add(head);
        snakeGroup.getChildren().add(slave1);
        snakeGroup.getChildren().add(slave2);

        this.matrizDePosiciones= new boolean[30][25];
        for(int i=0; i<30; i++){
            for(int j=0; j<25; j++){
                matrizDePosiciones[i][j] = false;
            }
        }
        for(Rectangle bodySnake : this.snakeList){
            matrizDePosiciones[casillaRectanguloX((int) bodySnake.getX())][casillaRectanguloY((int) bodySnake.getY())] = true;
        }
        createFood();

        this.foundFood = false;
    }

    private void verification(int altNewX, int altNewY, int newX, int newY, BooleanResult booleans){
        for (int i=1; i<length; i++){
            Rectangle prevNode = snakeList.get(i);
            if ((prevNode.getX() == newX) && (prevNode.getY() == newY)){
                booleans.illegalMove = true;  
                booleans.lose = true;
            }else if (((prevNode.getX() == altNewX) && (prevNode.getY() == altNewY))){
                System.out.print("LOSE-----------------");
                booleans.lose = true;      
            }    
        }
    }
    
    public void createFood(){
        Random random = new Random();
        int randX = random.nextInt(30);
        int randY = random.nextInt(25);
        if (matrizDePosiciones[randX][randY]){ //posicion ocupada
            System.out.print("magia en practica -----------------\n");
            int casillerosRestantes = this.cantCasillas - this.length; 
            int indixRan = random.nextInt(casillerosRestantes);
            int indiceX = 0;
            int indiceY = 0;
            outer:
            for(int i = 0; i<30; i++){
                for(int j = 0; j<25; i++){
                    if(!matrizDePosiciones[(randX + i) % 30][(randY + j) % 25]){
                        indiceX = i;
                        indiceY = j;
                        break outer;
                    }
                }
            }
            randX = (randX + indiceX) % 30;
            randY = (randY + indiceY) % 25;
        }
        //randX = 14;
        //randY = 16;
        Circle food = new Circle((randX * 20) + 30,(randY * 20) + 30,7);
        food.setFill(Color.WHITE);
        food.setStroke(Color.GRAY);
        this.food = food;  
        this.snakeGroup.getChildren().add(this.food);  
        //System.out.print("x es: " + randX + "\n" + "y es: " + randY + )
    }

    private int casillaRectanguloX(int x){
        return((x-1)-20) / 20;
    }

    private int casillaRectanguloY(int y){
        return((y-1)-20) / 20;
    }

    private int casillaFoodX(){
        return (((int) this.food.getCenterX() +10-20) / 20) - 1;
    }
    private int casillaFoodY(){
        return (((int) this.food.getCenterY() +10-20) / 20) - 1;
    }

    private boolean casillaOcupada(int x, int y){
        boolean res = false;
        int casillaX;
        int casillaY;
        for (Rectangle rectangle : this.snakeList){
            casillaX = casillaRectanguloX((int) rectangle.getX());
            casillaY = casillaRectanguloY((int) rectangle.getY());

            if (casillaX == x && casillaY == y){
                res = false;
                break;
            }
        }
        return res;
    }

    private boolean isFood(Rectangle newHead){
        return (casillaRectanguloX((int) newHead.getX()) == casillaFoodX()) && (casillaRectanguloY((int) newHead.getY()) == casillaFoodY());
    }

    public Rectangle move(){
        Rectangle toDelete = null;
        Rectangle newHead = null;
        if ((nextMovX != 0) || (nextMovY != 0)){
            Rectangle head = this.snakeList.getFirst();
            int newX = (int) head.getX() + nextMovX;
            int newY = (int) head.getY() + nextMovY;
            int altNewX = (int) head.getX() + lastMovX;
            int altNewY = (int) head.getY() + lastMovY;
  
            Rectangle prevNode = snakeList.get(1);
            if (!((prevNode.getX() == newX) && (prevNode.getY() == newY))){
                newHead = new Rectangle(newX, newY, 18, 18);
                newHead.setFill(Color.BLUE);
                lastMovX = nextMovX;
                lastMovY = nextMovY;
            }else{
                newHead = new Rectangle(altNewX, altNewY, 18, 18);
                newHead.setFill(Color.BLUE);
            };
        }else{
            Rectangle head = this.snakeList.getFirst();
            int altNewX = (int) head.getX() + lastMovX;
            int altNewY = (int) head.getY() + lastMovY;
            BooleanResult booleans = new BooleanResult();

            verification(altNewX, altNewY, altNewY, altNewX, booleans);

            if (altNewX < 20 || altNewX > 620 || altNewY < 20 || altNewY > 520){
                booleans.lose=true;
            }
            if (booleans.lose){
                System.out.print("LOSE-------\n");

                this.lose = booleans.lose;
            }
            newHead = new Rectangle(altNewX, altNewY, 18, 18);
            newHead.setFill(Color.BLUE);
        }
        int newX = (int) newHead.getX();
        int newY = (int) newHead.getY();
        
        if (newX < 20 || newX > 620 || newY < 20 || newY > 520){
            this.lose = true;
        }else if (matrizDePosiciones[casillaRectanguloX((int) newHead.getX())][casillaRectanguloY((int) newHead.getY())] == true){
                this.lose = true;
            }
        
        this.snakeList.addFirst(newHead);
        this.snakeHead=newHead;

        if (!this.lose){   
            if (isFood(newHead)){
                length += 1;
                this.foundFood = true;
                toDelete = this.snakeList.get(length-1);
            }else{
                toDelete = this.snakeList.get(length);
                matrizDePosiciones[casillaRectanguloX((int) toDelete.getX())][casillaRectanguloY((int) toDelete.getY())] = false;
                this.snakeList.removeLast();
            }
            matrizDePosiciones[casillaRectanguloX((int) newHead.getX())][casillaRectanguloY((int) newHead.getY())] = true;
        }
        return toDelete;
    }
}