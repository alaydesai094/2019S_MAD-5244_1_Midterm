package com.example.sparrow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {
    private final String TAG = "SPARROW";

    // game thread variables
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;

    // drawing variables
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;

    // Screen resolution varaibles
    private int screenWidth;
    private int screenHeight;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;

    // SPRITES
    Sprite player;
    Sprite sparrow;
    Sprite cat;
    Sprite cage;
    Square bullet;
    Square bullet2;


    //Game Variables
    int SQUARE_WIDTH = 20;
    int CAGE_SPEED = 20;
    int CAT_SPEED = 10;

    int updatedY;
    int updatedX;

    int InitialY = 700;
    int InitialX = 100;


    ArrayList<Square> bullets = new ArrayList<Square>();

    // GAME STATS
    int score = 0;
    boolean gameOver = false;

    public GameEngine(Context context, int screenW, int screenH) {
        super(context);

        // intialize the drawing variables
        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        // set screen height and width
        this.screenWidth = screenW;
        this.screenHeight = screenH;

        // setup visible game play area variables
        this.VISIBLE_LEFT = 20;
        this.VISIBLE_TOP = 10;
        this.VISIBLE_RIGHT = this.screenWidth - 20;
        this.VISIBLE_BOTTOM = (int) (this.screenHeight * 0.8);


        // initalize sprites
        this.player = new Sprite(this.getContext(), 100, 700, R.drawable.player64);
        this.sparrow = new Sprite(this.getContext(), 500, 200, R.drawable.bird64);
        this.cat = new Sprite(this.getContext(), 1500, 700, R.drawable.cat64);
        this.cage = new Sprite(this.getContext(), 1500, 100, R.drawable.robot64);
        this.bullet = new Square(context, 100, 700, SQUARE_WIDTH);


    }

    @Override
    public void run() {
        while (gameIsRunning == true) {
            updateGame();    // updating positions of stuff
            redrawSprites(); // drawing the stuff
            controlFPS();
        }
    }

    boolean movingleft = true;

    boolean cagefall = true;


    // Game Loop methods
    public void updateGame() {

        //Random Sparrow position
        Random r = new Random();
        int randX = r.nextInt(this.screenWidth) + 1;
        int randY = r.nextInt(this.screenHeight) + 1;

        // Moving Bird
        this.sparrow.setxPosition(randX - 10);
        this.sparrow.setyPosition(randY - 10);

        //Update Hitbox
        this.sparrow.updateHitbox();

        // Moving Cage
        if (movingleft == true) {

            this.cage.setxPosition(this.cage.getxPosition() + this.CAGE_SPEED);


        } else {
            this.cage.setxPosition(this.cage.getxPosition() - this.CAGE_SPEED);


        }

        // @TODO: Collisn
        if (this.cage.getxPosition() > this.VISIBLE_RIGHT) {
            Log.d(TAG, "Ball reached bottom of screen. Changing direction!");
            movingleft = false;
        }

        if (this.cage.getxPosition() < this.VISIBLE_LEFT) {
            Log.d(TAG, "Ball reached bottom of screen. Changing direction!");
            movingleft = true;
            //this.score = this.score + 1;
        }

        //Upate hitbox
        this.cage.updateHitbox();


        // Moving Cat

        if (movingleft == true) {
            this.cat.setxPosition(this.cat.getxPosition() + this.CAT_SPEED);


        }
        else {
            this.cat.setxPosition(this.cat.getxPosition() - this.CAT_SPEED);


        }

        // @TODO: Collision detection code
        if (this.cat.getxPosition() > this.VISIBLE_RIGHT) {
            Log.d(TAG, "Ball reached bottom of screen. Changing direction!");
            movingleft = false;
        }

        if (this.cat.getxPosition() < this.VISIBLE_LEFT) {
            Log.d(TAG, "Ball reached bottom of screen. Changing direction!");
            movingleft = true;
            //this.score = this.score + 1;
        }

        this.cat.updateHitbox();






        // Moving bullet
        // MAKE BULLET MOVE

        // 1. calculate distance between bullet and enemy
        double a = updatedX- this.bullet.getxPosition();
        double b = updatedY- this.bullet.getyPosition();

        // d = sqrt(a^2 + b^2)

        double d = Math.sqrt((a * a) + (b * b));

        Log.d(TAG, "Distance to enemy: " + d);

        // 2. calculate xn and yn constants
        // (amount of x to move, amount of y to move)
        double xn = (a / d);
        double yn = (b / d);

        // 3. calculate new (x,y) coordinates
        int newX = this.bullet.getxPosition() + (int) (xn * 15);
        int newY = this.bullet.getyPosition() + (int) (yn * 15);

        this.bullet.setxPosition(newX);
        this.bullet.setyPosition(newY);

        //Upate hitbox

        this.bullet.updateHitbox();


        //---------------
        //Colision Detection
        //-------------------

        if (bullet.getHitbox().intersect(cage.getHitbox())) {

            // UPDATE THE cage movement
            this.cage.setyPosition(this.cage.getyPosition() + this.VISIBLE_BOTTOM);
            this.cage.updateHitbox();

            if(this.cage.getyPosition() > this.screenHeight-300){


            }


            }
        }



    public void outputVisibleArea() {
        Log.d(TAG, "DEBUG: The visible area of the screen is:");
        Log.d(TAG, "DEBUG: Maximum w,h = " + this.screenWidth +  "," + this.screenHeight);
        Log.d(TAG, "DEBUG: Visible w,h =" + VISIBLE_RIGHT + "," + VISIBLE_BOTTOM);
        Log.d(TAG, "-------------------------------------");
    }



    public void redrawSprites() {
        if (holder.getSurface().isValid()) {

            // initialize the canvas
            canvas = holder.lockCanvas();
            // --------------------------------

            // set the game's background color
            canvas.drawColor(Color.argb(255,255,255,255));

            // setup stroke style and width
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);

            // --------------------------------------------------------
            // draw boundaries of the visible space of app
            // --------------------------------------------------------
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.argb(255, 0, 128, 0));

            canvas.drawRect(VISIBLE_LEFT, VISIBLE_TOP, VISIBLE_RIGHT, VISIBLE_BOTTOM, paintbrush);
            this.outputVisibleArea();

            // --------------------------------------------------------
            // draw player and sparrow
            // --------------------------------------------------------

            // 1. player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            // 2. sparrow
            canvas.drawBitmap(this.sparrow.getImage(), this.sparrow.getxPosition(), this.sparrow.getyPosition(), paintbrush);

            //3. cat
            canvas.drawBitmap(this.cat.getImage(), this.cat.getxPosition(), this.cat.getyPosition(), paintbrush);

            //4.
            canvas.drawBitmap(this.cage.getImage(), this.cage.getxPosition(), this.cage.getyPosition(), paintbrush);

            //5.Bullet
            paintbrush.setColor(Color.BLACK);
            canvas.drawRect(
                    this.bullet.getxPosition(),
                    this.bullet.getyPosition(),
                    this.bullet.getxPosition() + this.bullet.getWidth(),
                    this.bullet.getyPosition() + this.bullet.getWidth(),
                    paintbrush
            );





            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);

            //hit box on sparrow
            Rect sp = sparrow.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(sp, paintbrush);

            //hit box on cage
            Rect cg = cage.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(cg, paintbrush);

            //hit box on bullet
            Rect bu = bullet.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(bu, paintbrush);

            //hit box on cat
            Rect ct = cat.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(ct, paintbrush);





            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            paintbrush.setTextSize(60);
            paintbrush.setStrokeWidth(5);
            String screenInfo = "Screen size: (" + this.screenWidth + "," + this.screenHeight + ")";
            canvas.drawText(screenInfo, 10, 100, paintbrush);

            // --------------------------------

            if (gameOver == true) {
                paintbrush.setTextSize(60);
                paintbrush.setColor(Color.RED);
                paintbrush.setStrokeWidth(5);
                canvas.drawText("GAME OVER!", 50, 200, paintbrush);
            }

            holder.unlockCanvasAndPost(canvas);
        }

    }

    public void controlFPS() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {

        }
    }


    // Deal with user input
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            // user pushed down on screen

            Log.d(TAG, "The person tapped: (" + event.getX() + "," + event.getY() + ")");
            this.updatedX = (int)event.getX();
            this.updatedY = (int)event.getY();



        }
        else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger
            // for pong, you don't need this, so no code is in here

            this.bullet.setxPosition(this.InitialX);
            this.bullet.setyPosition(this.InitialY);
        }
        return true;
    }

    // Game status - pause & resume
    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {

        }
    }
    public void  resumeGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}
