import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color; // Importa el Color correcto de JavaFX
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.application.Platform;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

import javafx.animation.AnimationTimer;
//import javafx.scene.*;

public class SnakeGame extends Application {
    public Snake snake;
    public Label pressEnter;
    public String[] clocks = {"|","/","-","\\"};
    public int tics = 0;
    //public Semaphore start;
    @Override
    
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        Label clock = new Label("|");
        clock.setStyle("-fx-text-fill: white;");
        clock.setLayoutX(200);
        root.getChildren().add(clock);
        Rectangle background = new Rectangle(19,19, 600+2, 500+2);
        background.setFill(Color.WHITE);
        Rectangle black = new Rectangle(20, 20, 600, 500); // lose zone= x<20 or x>620 or y<20 or y>520
        black.setFill(Color.BLACK);
        root.getChildren().add(background);
        root.getChildren().add(black);
        
        Semaphore start = new Semaphore(1);
        start(root);
        
        try {
            start.acquire();
            System.out.print("padre  con semaforo");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AtomicBoolean gamePlaying= new AtomicBoolean(false);
        
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP){
                //mover arriba
                this.snake.nextMovY = -20;
                this.snake.nextMovX = 0;
            } else if (event.getCode() == KeyCode.DOWN) {
                //movy = 20; // Mover hacia abajo
                this.snake.nextMovY = 20;
                this.snake.nextMovX = 0;
            } else if (event.getCode() == KeyCode.LEFT) {
                //movx = (-20); // Mover hacia la izquierda
                this.snake.nextMovX = -20;
                this.snake.nextMovY = 0;
            } else if (event.getCode() == KeyCode.RIGHT) {
                //movx = 20; // Mover hacia la derecha
                this.snake.nextMovX = 20;
                this.snake.nextMovY = 0;
            } else if (event.getCode() == KeyCode.ENTER){
                if (!gamePlaying.get()){
                    
                    start.release();
                    System.out.print("Enter \n");
                    
                } 
            }
        });

        new Thread(() -> {
            while(true){
                try {
                    start.acquire();
                    gamePlaying.set(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    root.getChildren().remove(this.pressEnter);
                    this.pressEnter = null;
                });
                while(!snake.lose){
                    long started = System.nanoTime();
                    LockSupport.parkNanos(150_000_000); // Pausa de 150 milisegundos (150 millón de nanosegundos)
                    long end = System.nanoTime();  

                    Rectangle toDelete = snake.move();
                    if (toDelete != null){
                        Platform.runLater(() -> {
                            this.tics += 1;
                            clock.setText(clocks[tics % 4]);
                            this.snake.snakeGroup.getChildren().remove(toDelete);
                            
                            if (this.snake.foundFood){
                                this.snake.snakeGroup.getChildren().remove(this.snake.food);
                                this.snake.createFood();
                                this.snake.foundFood = false;
                            }else{
                                this.snake.snakeGroup.getChildren().remove(toDelete);
                            }
                            this.snake.snakeGroup.getChildren().add(snake.snakeHead); 
                        });
                    }
                }  

                start.release();
                System.out.print("Semaforo liberado\n");
                gamePlaying.set(false);
                Platform.runLater(() -> {
                    root.getChildren().remove(this.snake.snakeGroup);
                    System.out.print("limpieza Realizada\n");  
                    try {
                        System.out.print("padre pide semaforo\n");
                        start.acquire();  
                        start(root);
                        System.out.print("Semaforo adquirido por el padre\n");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }         
                });
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake by Eze");
        primaryStage.show();
    }
    
    public void start(Group root){
        this.pressEnter = new Label("Press enter to play ");
        pressEnter.setStyle("-fx-text-fill: white;-fx-font-size: 20 px");
        pressEnter.setLayoutX(220);
        pressEnter.setLayoutY(195);
        root.getChildren().add(pressEnter);

        this.snake = new Snake(300, 260);
        root.getChildren().add(this.snake.snakeGroup);
    }
    public static void main(String[] args) {
        launch(args);
    }
}


