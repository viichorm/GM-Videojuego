package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Nave4 {

    private boolean destruida = false;
    private int vidas = 3;
    private float xVel = 0;
    private float yVel = 0;
    private float acceleration = 0.1f;
    private float deceleration = 0.98f; // Factor de desaceleración
    private float maxSpeed = 5f; // Velocidad máxima
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;

    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
        sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        this.txBala = txBala;
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setBounds(x, y, 45, 45);
    }

    public void draw(SpriteBatch batch, PantallaJuego juego) {
        float x = spr.getX();
        float y = spr.getY();

        if (!herido) {
            // Movimiento fluido con aceleración
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                xVel -= acceleration; // Flecha izquierda o 'A'
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                xVel += acceleration; // Flecha derecha o 'D'
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                yVel += acceleration; // Flecha arriba o 'W'
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                yVel -= acceleration; // Flecha abajo o 'S'
            }

            // Aplicar desaceleración para suavizar el movimiento
            xVel *= deceleration;
            yVel *= deceleration;

            // Limitar la velocidad máxima
            xVel = MathUtils.clamp(xVel, -maxSpeed, maxSpeed);
            yVel = MathUtils.clamp(yVel, -maxSpeed, maxSpeed);

            // Que se mantenga dentro de los bordes de la ventana
            if (x + xVel < 0 || x + xVel + spr.getWidth() > Gdx.graphics.getWidth()) {
                xVel = 0; // Detener el movimiento en el borde
            }
            if (y + yVel < 0 || y + yVel + spr.getHeight() > Gdx.graphics.getHeight()) {
                yVel = 0; // Detener el movimiento en el borde
            }

            // Actualizar posición
            spr.setPosition(x + xVel, y + yVel);
            spr.draw(batch);
        } else {
            // Efecto de estar herido
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch);
            spr.setX(x);
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }

        // Disparo
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Bullet bala = new Bullet(spr.getX() + spr.getWidth() / 2 - 5, spr.getY() + spr.getHeight() - 5, 0, 3, txBala);
            juego.agregarBala(bala);
            soundBala.play();
        }
    }

    public boolean checkCollision(Ball2 b) {
        if (!herido && b.getArea().overlaps(spr.getBoundingRectangle())) {
            // Rebote
            xVel = -xVel;
            yVel = -yVel;
            b.setXSpeed(-b.getXSpeed());
            b.setySpeed(-b.getySpeed());

            // Actualizar vidas y herir
            vidas--;
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            sonidoHerido.play();
            if (vidas <= 0)
                destruida = true;
            return true;
        }
        return false;
    }

    public boolean estaDestruido() {
        return !herido && destruida;
    }

    public boolean estaHerido() {
        return herido;
    }

    public int getVidas() {
        return vidas;
    }

    public int getX() {
        return (int) spr.getX();
    }

    public int getY() {
        return (int) spr.getY();
    }

    public void setVidas(int vidas2) {
        vidas = vidas2;
    }
}
